package com.teamside.project.alpha.group.service;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.model.constant.KeepitConstant;
import com.teamside.project.alpha.common.msg.MsgService;
import com.teamside.project.alpha.common.msg.enumurate.MQExchange;
import com.teamside.project.alpha.common.msg.enumurate.MQRoutingKey;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.domain.daily.model.dto.DailyDto;
import com.teamside.project.alpha.group.domain.review.model.dto.ReviewDto;
import com.teamside.project.alpha.group.model.constant.GroupConstant;
import com.teamside.project.alpha.group.model.dto.GroupDto;
import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.group.model.entity.GroupMemberMappingEntity;
import com.teamside.project.alpha.group.model.enumurate.GroupMemberStatus;
import com.teamside.project.alpha.group.model.enumurate.MyGroupType;
import com.teamside.project.alpha.group.repository.GroupRepository;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import com.teamside.project.alpha.member.repository.MemberRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final MsgService msgService;
    private final MemberRepo memberRepository;




    @Override
    @Transactional
    public String createGroup(GroupDto group) throws CustomException {
        if (groupRepository.countByMasterMidAndIsDelete(CryptUtils.getMid(), false) >= GroupConstant.GROUP_MAKE_POSSIBLE_COUNT) {
            throw new CustomException(ApiExceptionCode.CAN_NOT_CREATE_GROUP);
        }
        isExistGroupName(group.getName());
        GroupEntity groupEntity = new GroupEntity(group);

        GroupEntity newGroupEntity = groupRepository.save(groupEntity);
        newGroupEntity.addMember(newGroupEntity.getMasterMid());

        return newGroupEntity.getGroupId();
    }

    @Override
    public void updateGroup(String groupId, GroupDto groupDto) throws CustomException {
        GroupEntity groupEntity = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));

        groupEntity.checkGroupStatus();

        if (!groupEntity.getName().equals(groupDto.getName())) isExistGroupName(groupDto.getName());
        groupEntity.checkGroupMaster();

        groupEntity.updateGroup(groupDto);

        groupRepository.save(groupEntity);
    }

    @Override
    public void isExistGroupName(String groupName) throws CustomException {
        if (groupRepository.existsByName(groupName)) {
            throw new CustomException(ApiExceptionCode.DUPLICATE_NAME);
        }
    }
    @Override
    @Transactional
    public void deleteGroup(String groupId) throws CustomException {
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomException(ApiExceptionCode.GROUP_NOT_FOUND));
        group.checkGroupMaster();

        group.deleteGroup();

        Map<String, String> data = new HashMap<>();
        data.put("groupId", groupId);
        msgService.publishMsg(MQExchange.KPS_EXCHANGE, MQRoutingKey.GROUP_DELETE, data);

    }

    @Override
    @Transactional
    public GroupDto.GroupInfoDto selectGroup(String groupId) {
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));

        GroupDto.GroupInfoDto result = groupRepository.selectGroup(groupId);

        GroupDto.GroupInfoDto.MembersDto master = result.getMembers().stream().filter(m -> m.getMid().equals(group.getMasterMid())).findAny().orElseThrow();
        result.getMembers().remove(master);
        result.getMembers().add(0, master);

        GroupDto.GroupInfoDto.MembersDto me = result.getMembers().stream().filter(m -> m.getMid().equals(CryptUtils.getMid())).findAny().orElse(null);
        if (me != null && !master.equals(me)) {
            result.getMembers().remove(me);
            result.getMembers().add(1, me);
        }

        // block
        List<String> blocks = getBlockTarget();
        result.filterBlocks(blocks);

        return result;
    }

    @Override
    public GroupDto.ResponseSearchGroupDto selectGroups(Long lastGroupId, Long pageSize) {
        List<GroupDto.SearchGroupDto> groupList = groupRepository.selectGroups(lastGroupId, pageSize);
        Long responseLastGroupId = groupList.size() == pageSize ? groupList.get((int) (pageSize-1L)).getGroupSeq() : null;

        return new GroupDto.ResponseSearchGroupDto(null, responseLastGroupId, groupList);
    }

    @Override
    public GroupDto.ResponseSearchGroupDto searchGroup(Long lastGroupSeq, Long pageSize, String search) {
        List<GroupDto.SearchGroupDto> groupList = groupRepository.searchGroup(lastGroupSeq, pageSize, search.replaceAll(KeepitConstant.REGEXP_EMOJI, ""));
        Long totalCount = lastGroupSeq == null ? groupRepository.countByNameContainingAndIsDelete(search, false) : null;
        Long responseLastGroupId = groupList.size() == pageSize ? groupList.get((int) (pageSize-1L)).getGroupSeq() : null;

        return new GroupDto.ResponseSearchGroupDto(totalCount, responseLastGroupId, groupList);
    }

    @Override
    public List<GroupDto.SearchGroupDto> random() {
        return groupRepository.random();
    }

    @Override
    @Transactional
    public void joinGroup(String groupId, String password) throws CustomException {
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomException(ApiExceptionCode.GROUP_NOT_FOUND));
        group.checkGroupStatus();
        group.checkJoinPossible(group, password);

        String mid = CryptUtils.getMid();
        // 내가 참여한 그룹 갯수 (status - join)
        long joinGroupCount = groupRepository.countJoinGroup(mid);

        if (joinGroupCount >= GroupConstant.MEMBER_JOIN_POSSIBLE_COUNT) {
            throw new CustomException(ApiExceptionCode.CAN_NOT_PARTICIPANT);
        }
        group.addMember(mid);
        Map<String, String> data = new HashMap<>();
        data.put("senderMid", mid);
        data.put("groupId", groupId);
        msgService.publishMsg(MQExchange.KPS_EXCHANGE, MQRoutingKey.GROUP_JOIN, data);
    }

    @Override
    @Transactional
    public void leaveGroup(String groupId) throws CustomException {
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomException(ApiExceptionCode.GROUP_NOT_FOUND));

        group.leaveGroup();
    }

    @Override
    public GroupDto.ResponseMyGroupDto selectMyGroups(MyGroupType type) {
        if (type.equals(MyGroupType.NULL)) {
            throw new CustomRuntimeException(ApiExceptionCode.INVALID_GROUP_TYPE);
        }

        String mId = CryptUtils.getMid();
        List<GroupDto.MyGroupDto> myGroups = groupRepository.selectMyGroups(mId, type);

        List<GroupDto.MyGroupDto> favoriteGroups = myGroups.stream()
                .filter(group -> group.getFavorite())
                .sorted(Comparator.comparing(group -> group.getOrd()))
                .collect(Collectors.toList());

        List<GroupDto.MyGroupDto> groupList = type.equals(MyGroupType.FAVORITE) ? null : myGroups.stream()
                .filter(group -> !group.getFavorite())
                .sorted(Comparator.comparing(group -> group.getName()))
                .collect(Collectors.toList());

        GroupDto.ResponseMyGroupDto response = new GroupDto.ResponseMyGroupDto(favoriteGroups, groupList);

        return response;
    }

    @Override
    @Transactional
    public void editFavorite(String groupId) throws CustomException {
        String mId = CryptUtils.getMid();

        GroupMemberMappingEntity groupMemberMapping = groupRepository.selectGroupMemberMappingEntity(mId, groupId)
                        .orElseThrow(() -> new CustomException(ApiExceptionCode.GROUP_MEMBER_NOT_FOUND));

        Boolean isFavorite = !groupMemberMapping.getFavorite();
        Integer ord;

        if (isFavorite) {
            Optional<Integer> ordEntity = groupRepository.selectLatestFavoriteOrd(mId);
            ord = ordEntity.map(o -> o + 1).orElse(1);
        } else {
            ord = null;
        }

        groupMemberMapping.updateOrdAndFavorite(ord, isFavorite);
    }

    @Override
    public void inviteMember(String groupId, String memberId)  {

    }

    @Override
    @Transactional
    public void updateOrd(GroupDto.RequestUpdateOrdDto request) throws CustomException {
        String mId = CryptUtils.getMid();
        List<GroupMemberMappingEntity> targetList = groupRepository.selectFavoriteMappingGroups(mId);

        validateRequest(request.getGroupList(), targetList);

        targetList.stream().forEach(groupMemberMappingEntity -> {
            for (GroupDto.MyGroupDto dto : request.getGroupList()) {
                if (groupMemberMappingEntity.getGroupId().equals(dto.getGroupId())) {
                    groupMemberMappingEntity.updateOrd(dto.getOrd());
                }
            }
        });
    }

    public void validateRequest(List<GroupDto.MyGroupDto> request, List<GroupMemberMappingEntity> targetList) throws CustomException {
        if (request.size() != targetList.size()) {
            throw new CustomException(ApiExceptionCode.INVALID_GROUP_REQUEST);
        }

        Integer ordCount = request.stream()
                .map(dto -> dto.getOrd())
                .distinct()
                .collect(Collectors.toList())
                .size();

        if (ordCount != targetList.size()) {
            throw new CustomException(ApiExceptionCode.DUPLICATE_ORD);
        }

        List<String> ids = request.stream()
                .map(dto -> dto.getGroupId())
                .collect(Collectors.toList());

        List<GroupMemberMappingEntity> filteredList = targetList.stream()
                .filter(target -> !ids.contains(target.getGroupId()))
                .collect(Collectors.toList());

        if (filteredList.size() > 0) {
            throw new CustomException(ApiExceptionCode.GROUP_IS_NOT_MATCH);
        }
    }

    @Override
    public List<GroupDto.SearchGroupDto> statGroups(String referralType, String category) {
        List<GroupDto.SearchGroupDto> groupList;

        if (referralType.equals("RECOM")) {
            groupList = groupRepository.random();
        } else {
            groupList = groupRepository.statGroups(referralType, category);
        }
        return groupList;
    }

    @Override
    public GroupDto.GroupMemberProfileDto groupMemberProfile(String groupId, String memberId) {
        MemberEntity member = memberRepository.findByMid(memberId).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.MEMBER_NOT_FOUND));
        member.checkWithdrawal();
        return groupRepository.groupMemberProfile(groupId, memberId);
    }

    @Override
    @Transactional
    public ReviewDto.ResponseSelectReviewsInGroup selectReviewsInGroup(String groupId, String targetMid, Long pageSize, Long seq) {
        List<String> blocks = getBlockTarget();

        List<ReviewDto.SelectReviewsInGroup> reviewsInGroup = groupRepository.selectReviewsInGroup(groupId, targetMid, pageSize, seq, blocks);
        Long responseLastGroupId = reviewsInGroup.size() == pageSize ? reviewsInGroup.get(reviewsInGroup.size()-1).getReview().getReviewSeq() : null;
        GroupEntity groupEntity = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));

        Long reviewCount = groupEntity.getReviewEntities().stream()
                .filter(d -> !d.getIsDelete())
                .filter((blocks != null && blocks.size() > 0) ? d -> !blocks.contains(d.getMasterMid()) : d -> true)
                .filter(targetMid != null ? d -> d.getMasterMid().equals(targetMid) : d -> true)
                .count();
        return new ReviewDto.ResponseSelectReviewsInGroup(reviewsInGroup, responseLastGroupId, reviewCount);
    }

    @Override
    @Transactional
    public DailyDto.ResponseDailyInGroup selectDailyInGroup(String groupId, String targetMid, Long pageSize, Long lastDailySeq) {
        List<String> blocks = getBlockTarget();

        List<DailyDto.DailyInGroup> dailyInGroup = groupRepository.selectDailyInGroup(groupId, targetMid, pageSize, lastDailySeq, blocks);
        Long responseLastDailySeq = dailyInGroup.size() == pageSize ? dailyInGroup.get(dailyInGroup.size() - 1).getDailySeq() : null;
        GroupEntity groupEntity = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));

        Long dailyCount = groupEntity.getDailyEntities().stream()
                .filter(d -> !d.getIsDelete())
                .filter((blocks != null && blocks.size() > 0) ? d -> !blocks.contains(d.getMasterMid()) : d -> true)
                .filter(targetMid != null ? d -> d.getMasterMid().equals(targetMid) : d -> true)
                .count();

        return new DailyDto.ResponseDailyInGroup(dailyInGroup, responseLastDailySeq, dailyCount);
    }

    @Override
    @Transactional
    public GroupDto.GroupHome selectGroupHome(String groupId) {
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));
        group.checkExistMember(CryptUtils.getMid());

        return new GroupDto.GroupHome(group.getName(),
                group.getGroupMemberMappingEntity().stream().filter(m -> m.getStatus().equals(GroupMemberStatus.JOIN)).count(),
                group.getReviewEntities().stream().filter(d-> Objects.equals(d.getMasterMid(), CryptUtils.getMid()) && !d.getIsDelete()).count()
                );
    }

    @Override
    @Transactional
    public void follow(String groupId, String targetMid)  {
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));
        boolean alarmYn = group.follow(groupId, targetMid);

        if (alarmYn) {
            Map<String, Object> data = new HashMap<>();
            data.put("receiverMid", targetMid);
            data.put("senderMid", CryptUtils.getMid());
            data.put("groupId", groupId);

            CompletableFuture.supplyAsync(() -> msgService.publishMsg(MQExchange.KPS_EXCHANGE, MQRoutingKey.MY_FOLLOW, data));
        }
    }

    @Override
    @Transactional
    public void exileMember(String groupId, String memberId) {
        GroupEntity group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));
        group.checkGroupMaster();
        group.exileMember(memberId);

        Map<String, String> data = new HashMap<>();
        data.put("receiverMid", group.getMasterMid());
        data.put("groupId", groupId);
        msgService.publishMsg(MQExchange.KPS_EXCHANGE, MQRoutingKey.GROUP_JOIN, data);
    }

    @Override
    public List<GroupDto.GroupAlarmSetting> selectGroupReviewAlarm(String alarmType) throws CustomException {
        if (!alarmType.equals("REVIEW") && !alarmType.equals("DAILY")) {
            throw new CustomException(ApiExceptionCode.INVALID_ALARM_TYPE);
        }

        return groupRepository.selectGroupAlarm(alarmType);
    }

    @Override
    @Transactional
    public void updateGroupAlarm(GroupDto.GroupAlarmSetting groupAlarmSetting) throws CustomException {
        String alarmType = groupAlarmSetting.getAlarmType() == null ? "" : groupAlarmSetting.getAlarmType();
        if (!alarmType.equals("REVIEW") && !alarmType.equals("DAILY")) {
            throw new CustomException(ApiExceptionCode.INVALID_ALARM_TYPE);
        }

        String mId = CryptUtils.getMid();
        GroupMemberMappingEntity groupMemberMapping = groupRepository.selectGroupMemberMappingEntity(mId, groupAlarmSetting.getGroupId())
                .orElseThrow(() -> new CustomException(ApiExceptionCode.GROUP_MEMBER_NOT_FOUND));

        groupMemberMapping.updateGroupAlarm(alarmType);
    }

    @Override
    public List<GroupDto.MyFollow> selectMyFollow() {
        return groupRepository.selectMyFollow();
    }

    @Override
    @Transactional
    public void updateFollowAlarm(GroupDto.MyFollow myFollow) {
        GroupEntity group = groupRepository.findByGroupId(myFollow.getGroupId())
                .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND));

        group.updateFollowAlarm(CryptUtils.getMid(), myFollow.getMid());
    }

    public List<String> getBlockTarget() {
        return memberRepository.findByMid(CryptUtils.getMid())
                .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.MEMBER_NOT_FOUND))
                .getBlockTarget();
    }
}
