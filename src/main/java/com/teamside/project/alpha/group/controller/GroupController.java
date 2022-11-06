package com.teamside.project.alpha.group.controller;


import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.model.constant.KeepitConstant;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.group.model.dto.GroupDto;
import com.teamside.project.alpha.group.model.enumurate.MyGroupType;
import com.teamside.project.alpha.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/groups")
public class GroupController {
    private final GroupService groupService;

    @GetMapping("/{groupId}/reviews")
    public ResponseEntity<ResponseObject> selectReviewsInGroup(@PathVariable String groupId, @RequestParam(required = false) String targetMid, @RequestParam Long pageSize, @RequestParam(required = false) Long lastReviewId) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(groupService.selectReviewsInGroup(groupId, targetMid, pageSize, lastReviewId));

        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }
    @GetMapping("/{groupId}/daily")
    public ResponseEntity<ResponseObject> selectDailyInGroup(@PathVariable String groupId, @RequestParam(required = false) String targetMid, @RequestParam Long pageSize, @RequestParam(required = false) Long lastDailyId) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(groupService.selectDailyInGroup(groupId, targetMid, pageSize, lastDailyId));

        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<ResponseObject> createGroup(@RequestBody @Valid GroupDto group) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.CREATED);
        responseObject.setBody(groupService.createGroup(group));

        return new ResponseEntity<>(responseObject, HttpStatus.CREATED);
    }

    @PostMapping("/{groupId}/join")
    public ResponseEntity<ResponseObject> joinGroup(@PathVariable String groupId, @RequestParam(required = false) String password) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        groupService.joinGroup(groupId, password);

        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @DeleteMapping("/{groupId}/leave")
    public ResponseEntity<ResponseObject> leaveGroup(@PathVariable String groupId) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        groupService.leaveGroup(groupId);

        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<ResponseObject> deleteGroup(@PathVariable String groupId) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        groupService.deleteGroup(groupId);

        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    /**
     * 그룹 - 그룹 정보(그룹 조회)
     *
     * @param groupId
     * @return
     * @throws CustomException
     */
    @GetMapping("/{groupId}")
    public ResponseEntity<ResponseObject> selectGroup(@PathVariable String groupId) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(groupService.selectGroup(groupId));

        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }
    @PatchMapping("/{groupId}")
    public ResponseEntity<ResponseObject> updateGroup(@RequestBody @Valid GroupDto groupDto, @PathVariable String groupId) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        groupService.updateGroup(groupId, groupDto);

        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }
    @GetMapping("/{groupName}/exists")
    public ResponseEntity<ResponseObject> isExistGroupName(@Pattern(regexp = KeepitConstant.REGEXP_GROUP_NAME, message = "그룹 이름이 올바르지 않습니다.")
                                                               @Size(min = 4, max = 20, message = "그룹 제목은 4~20자 입니다.")
                                                               @PathVariable String groupName) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        groupService.isExistGroupName(groupName);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseObject> searchGroups(@RequestParam(required = false) Long lastGroupSeq,
                                                       @RequestParam Long pageSize,
                                                       @RequestParam String search) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(groupService.searchGroup(lastGroupSeq, pageSize, search));
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @GetMapping("/my-groups")
    public ResponseEntity<ResponseObject> selectMyGroups(@RequestParam MyGroupType type) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(groupService.selectMyGroups(type));
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @PostMapping("/{groupId}/favorite")
    public ResponseEntity<ResponseObject> editFavorite(@PathVariable String groupId) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        groupService.editFavorite(groupId);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }
    @PostMapping("/{groupId}/members/{memberId}")
    public ResponseEntity<ResponseObject> inviteMember(@PathVariable String groupId,
                                                       @PathVariable String memberId) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        groupService.inviteMember(groupId, memberId);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }


    /**
     * 그룹 내 멤버 내보내기
     * @param groupId
     * @param memberId
     * @return
     * @throws CustomException
     */
    @PostMapping("/{groupId}/members/{memberId}/exile")
    public ResponseEntity<ResponseObject> exileMember(@PathVariable String groupId,
                                                       @PathVariable String memberId) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        groupService.exileMember(groupId, memberId);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @PostMapping("/ords")
    public ResponseEntity<ResponseObject> updateOrd(@RequestBody GroupDto.RequestUpdateOrdDto request) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        groupService.updateOrd(request);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    // statistic - 추천, 인기, 리뷰
    // 1. POPULARITY
    // 2. MANYREVIEW
    // 3. RECOM >> RANDOM
    @GetMapping("/statistics")
    public ResponseEntity<ResponseObject> statGroups(@RequestParam String referralType,
                                                     @RequestParam String category) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(groupService.statGroups(referralType, category));
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    /**
     * 그룹 - 그룹 찾기
     * @param lastGroupId
     * @param pageSize
     * @return
     */
    @GetMapping
    public ResponseEntity<ResponseObject> selectGroups(@RequestParam(required = false) Long lastGroupId,
                                                       @RequestParam Long pageSize) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(groupService.selectGroups(lastGroupId, pageSize));
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @GetMapping("/{groupId}/members/{memberId}")
    public ResponseEntity<ResponseObject> groupMemberProfile(@PathVariable String groupId,
                                                             @PathVariable String memberId) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(groupService.groupMemberProfile(groupId, memberId));
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    /**
     * 그룹 홈 화면
     * @param groupId
     * @return
     */
    @GetMapping("/{groupId}/home")
    public ResponseEntity<ResponseObject> groupHomePage(@PathVariable String groupId) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(groupService.selectGroupHome(groupId));
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    /**
     * 그룹 멤버 팔로우
     *
     * @param groupId
     * @param targetMid
     * @return
     * @throws CustomException
     */
    @PostMapping("/{groupId}/members/{targetMid}/follow")
    public ResponseEntity<ResponseObject> follow(@PathVariable String groupId, @PathVariable String targetMid) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        groupService.follow(groupId, targetMid);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @GetMapping("/alarms")
    public ResponseEntity<ResponseObject> selectGroupAlarm(@RequestParam String alarmType) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(groupService.selectGroupReviewAlarm(alarmType));
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @PatchMapping("/alarms")
    public ResponseEntity<ResponseObject> updateGroupAlarm(@RequestBody GroupDto.GroupAlarmSetting groupAlarmSetting) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        groupService.updateGroupAlarm(groupAlarmSetting);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }



    @GetMapping("/random")
    public ResponseEntity<ResponseObject> random(){
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(groupService.random());
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }
}
