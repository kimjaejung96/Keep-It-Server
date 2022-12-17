package com.teamside.project.alpha.group.repository;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.common.dto.CommentDto;
import com.teamside.project.alpha.group.common.dto.QCommentDto;
import com.teamside.project.alpha.group.common.enumurate.CommentStatus;
import com.teamside.project.alpha.group.domain.daily.model.dto.DailyDto;
import com.teamside.project.alpha.group.domain.daily.model.dto.QDailyDto_DailyDetail;
import com.teamside.project.alpha.group.domain.daily.model.dto.QDailyDto_DailyInGroup;
import com.teamside.project.alpha.group.domain.daily.model.entity.QDailyCommentEntity;
import com.teamside.project.alpha.group.domain.daily.model.entity.QDailyEntity;
import com.teamside.project.alpha.group.domain.daily.model.entity.QDailyKeepEntity;
import com.teamside.project.alpha.group.domain.review.model.dto.*;
import com.teamside.project.alpha.group.domain.review.model.entity.QReviewCommentEntity;
import com.teamside.project.alpha.group.domain.review.model.entity.QReviewEntity;
import com.teamside.project.alpha.group.domain.review.model.entity.QReviewKeepEntity;
import com.teamside.project.alpha.group.model.dto.*;
import com.teamside.project.alpha.group.model.entity.*;
import com.teamside.project.alpha.group.model.enumurate.Category;
import com.teamside.project.alpha.group.model.enumurate.GroupMemberStatus;
import com.teamside.project.alpha.group.model.enumurate.MyGroupType;
import com.teamside.project.alpha.member.domain.mypage.model.dto.*;
import com.teamside.project.alpha.member.domain.mypage.model.enumurate.MyGroupManagementType;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import com.teamside.project.alpha.member.model.entity.QMemberBlockEntity;
import com.teamside.project.alpha.member.model.entity.QMemberEntity;
import com.teamside.project.alpha.place.model.entity.QPlaceEntity;
import org.apache.logging.log4j.util.Strings;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class GroupRepositoryDSLImpl implements GroupRepositoryDSL {
    @PersistenceContext
    private EntityManager entityManager;
    private final JPAQueryFactory jpaQueryFactory;


    QReviewKeepEntity reviewKeep = QReviewKeepEntity.reviewKeepEntity;
    QGroupEntity group = QGroupEntity.groupEntity;
    QReviewCommentEntity reviewComment = QReviewCommentEntity.reviewCommentEntity;
    QPlaceEntity place = QPlaceEntity.placeEntity;
    QReviewEntity review = QReviewEntity.reviewEntity;

    QDailyEntity daily = QDailyEntity.dailyEntity;

    QDailyKeepEntity dailyKeep = QDailyKeepEntity.dailyKeepEntity;
    QDailyCommentEntity dailyComment = QDailyCommentEntity.dailyCommentEntity;
    QMemberEntity member = QMemberEntity.memberEntity;
    QGroupMemberMappingEntity groupMemberMapping = QGroupMemberMappingEntity.groupMemberMappingEntity;
    QMemberFollowEntity memberFollow = QMemberFollowEntity.memberFollowEntity;
    QMemberBlockEntity block = QMemberBlockEntity.memberBlockEntity;

    QStatReferralGroupEntity statReferralGroup = QStatReferralGroupEntity.statReferralGroupEntity;

    public GroupRepositoryDSLImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<GroupDto.SearchGroupDto> searchGroup(Long lastGroupSeq, Long pageSize, String search) {
        return jpaQueryFactory
                .select(new QGroupDto_SearchGroupDto(
                        group.groupId,
                        group.seq,
                        group.name,
                        group.category,
                        group.profileUrl,
                        group.usePrivate,
                        groupMemberMapping.count().as("participantCount")))
                .from(group)
                .innerJoin(groupMemberMapping).on(group.groupId.eq(groupMemberMapping.groupId)
                        .and(groupMemberMapping.status.eq(GroupMemberStatus.JOIN)))
                .where(group.isDelete.eq(false),
                        gtGroupId(lastGroupSeq),
                        containSearch(search)
                )
                .limit(pageSize)
                .groupBy(group.groupId, group.name, group.category, group.profileUrl, group.usePrivate)
                .orderBy(group.seq.asc())
                .fetch();
    }

    public BooleanExpression gtGroupId(Long lastGroupSeq) {
        return lastGroupSeq != null ? group.seq.gt(lastGroupSeq) : null;
    }

    public BooleanExpression containSearch(String search) {
        return (search != null && !Strings.isBlank(search)) ? group.name.contains(search) : null;
    }

    @Override
    public List<GroupDto.MyGroupDto> selectMyGroups(String mId, MyGroupType type) {
        return jpaQueryFactory
                .select(new QGroupDto_MyGroupDto(
                        group.groupId,
                        group.name,
                        group.category,
                        group.profileUrl,
                        group.usePrivate,
                        group.isDelete,
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(groupMemberMapping.count())
                                        .from(groupMemberMapping)
                                        .where(groupMemberMapping.groupId.eq(group.groupId), groupMemberMapping.status.eq(GroupMemberStatus.JOIN)),
                        "participantCount"),
                        groupMemberMapping.favorite,
                        new CaseBuilder()
                                .when(group.masterMid.eq(groupMemberMapping.mid))
                                .then(true)
                                .otherwise(false)
                                .as("isMaster"),
                        groupMemberMapping.ord
                    ))
                .from(groupMemberMapping)
                .innerJoin(group).on(groupMemberMapping.groupId.eq(group.groupId))
                .where(
//                        group.isDelete.eq(false),
                        groupMemberMapping.member.mid.eq(mId),
                        groupMemberMapping.status.eq(GroupMemberStatus.JOIN),
                        isFavorite(type))
                .fetch();
    }

    public BooleanExpression isFavorite(MyGroupType type) {
        return type == MyGroupType.FAVORITE ? groupMemberMapping.favorite.eq(true) : null;
    }
    @Override
    public List<GroupDto.SearchGroupDto> random() {
        return jpaQueryFactory
                .select(new QGroupDto_SearchGroupDto(
                        group.groupId,
                        group.seq,
                        group.name,
                        group.category,
                        group.profileUrl,
                        group.usePrivate,
                        groupMemberMapping.count().as("participantCount")))
                .from(group)
                .innerJoin(groupMemberMapping).on(group.groupId.eq(groupMemberMapping.groupId)
                        .and(groupMemberMapping.status.eq(GroupMemberStatus.JOIN)))
                .where(group.isDelete.eq(false))
                .orderBy(Expressions.numberTemplate(Long.class,"function('rand')").asc())
                .limit(10)
                .groupBy(group.groupId, group.name, group.category, group.profileUrl, group.usePrivate)
                .fetch();
    }

    @Override
    public Optional<GroupMemberMappingEntity> selectGroupMemberMappingEntity(String mid, String groupId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(groupMemberMapping)
                .from(groupMemberMapping)
                .where(groupMemberMapping.mid.eq(mid),
                        groupMemberMapping.groupId.eq(groupId))
                .fetchOne());
    }

    @Override
    public Optional<Integer> selectLatestFavoriteOrd(String mid) {
        return Optional.ofNullable(jpaQueryFactory
                .select(groupMemberMapping.ord)
                .from(groupMemberMapping)
                .where(groupMemberMapping.mid.eq(mid)
                        .and(groupMemberMapping.favorite.eq(true)))
                .orderBy(groupMemberMapping.ord.desc())
                .fetchFirst());
    }

    @Override
    public List<GroupMemberMappingEntity> selectFavoriteMappingGroups(String mid) {
        return jpaQueryFactory
                .select(groupMemberMapping)
                .from(groupMemberMapping)
                .where(groupMemberMapping.mid.eq(mid),
                        groupMemberMapping.favorite.eq(true),
                        groupMemberMapping.status.eq(GroupMemberStatus.JOIN))
                .fetch();
    }

    @Override
    public GroupDto.GroupInfoDto selectGroup(String groupId) {
        BooleanExpression booleanExpression = new CaseBuilder()
                .when(groupMemberMapping.mid.eq(CryptUtils.getMid()).and(groupMemberMapping.favorite.eq(true)))
                .then(1)
                .otherwise(0)
                .sum().eq(1);

        GroupDto.GroupInfoDto groupInfoDto = jpaQueryFactory.select(
                        new QGroupDto_GroupInfoDto(
                                group,
                                group.masterMid,
                                new CaseBuilder()
                                        .when(booleanExpression)
                                        .then(true)
                                        .otherwise(false),
                                groupMemberMapping.count(),
                                ExpressionUtils.as(
                                        JPAExpressions
                                                .select(review.count())
                                                .from(review)
                                                .where(group.groupId.eq(review.group.groupId), review.isDelete.eq(false)),
                                "inReviews")
                        )
                )
                .from(group)
                .innerJoin(groupMemberMapping).on(group.groupId.eq(groupMemberMapping.groupId)
                        .and(groupMemberMapping.status.eq(GroupMemberStatus.JOIN)))
                .where(group.groupId.eq(groupId))
                .groupBy(group.groupId)
                .fetchOne();

        Objects.requireNonNull(groupInfoDto).addGroupInfoMembers(
                jpaQueryFactory.select(
                        new QGroupDto_GroupInfoDto_MembersDto(
                                member.name,
                                member.mid,
                                member.profileUrl,
                                new CaseBuilder()
                                        .when(memberFollow.mid.isNotNull())
                                        .then(true)
                                        .otherwise(false)
                                )
                        )
                        .from(groupMemberMapping)
                        .innerJoin(member).on(groupMemberMapping.mid.eq(member.mid))
                        .leftJoin(memberFollow)
                            .on(memberFollow.group.groupId.eq(groupId), memberFollow.mid.eq(CryptUtils.getMid()),
                                memberFollow.targetMid.eq(member.mid), memberFollow.followYn.eq(true))
                        .where(groupMemberMapping.groupId.eq(groupId), groupMemberMapping.status.eq(GroupMemberStatus.JOIN))
                        .fetch()
        );

        return groupInfoDto;
    }

    @Override
    public List<GroupDto.SearchGroupDto> statGroups(String referralType, String category) {
        // STAT_DT , RANK로 정렬
        return jpaQueryFactory
                .select(new QGroupDto_SearchGroupDto(
                        group.groupId,
                        group.seq,
                        group.name,
                        group.category,
                        group.profileUrl,
                        group.usePrivate,
                        groupMemberMapping.count().as("participantCount")))
                .from(statReferralGroup)
                .innerJoin(group).on(statReferralGroup.groupId.eq(group.groupId))
                .innerJoin(groupMemberMapping).on(group.groupId.eq(groupMemberMapping.groupId).and(groupMemberMapping.status.eq(GroupMemberStatus.JOIN)))
                .where(group.isDelete.eq(false),
                        statReferralGroup.referralType.eq(referralType), statReferralGroup.category.eq(category),
                        statReferralGroup.statDt.eq(
                                JPAExpressions
                                        .select(statReferralGroup.statDt.max())
                                        .from(statReferralGroup)
                            )
                        )
                .groupBy(group.groupId, group.seq, group.name, group.category, group.profileUrl, group.usePrivate, statReferralGroup.rankNum)
                .orderBy(statReferralGroup.rankNum.asc())
                .fetch();
    }

    @Override
    public List<GroupDto.SearchGroupDto> selectGroups(Long lastGroupSeq, Long pageSize) {
        return jpaQueryFactory
                .select(new QGroupDto_SearchGroupDto(
                        group.groupId,
                        group.seq,
                        group.name,
                        group.category,
                        group.profileUrl,
                        group.usePrivate,
                        groupMemberMapping.count().as("participantCount")))
                .from(group)
                .innerJoin(groupMemberMapping).on(group.groupId.eq(groupMemberMapping.groupId)
                        .and(groupMemberMapping.status.eq(GroupMemberStatus.JOIN)))
                .where(group.isDelete.eq(false), ltGroupId(lastGroupSeq))
                .limit(pageSize)
                .groupBy(group.groupId, group.name, group.category, group.profileUrl, group.usePrivate)
                .orderBy(group.seq.desc())
                .fetch();
    }

    public BooleanExpression ltGroupId(Long lastGroupSeq) {
        return lastGroupSeq != null ? group.seq.lt(lastGroupSeq) : null;
    }

    @Override
    public GroupDto.GroupMemberProfileDto groupMemberProfile(String groupId, String memberId) {
        // mid - targetMid
        return jpaQueryFactory
                .select(new QGroupDto_GroupMemberProfileDto(
                        member.mid,
                        member.name,
                        new CaseBuilder()
                                .when(memberFollow.mid.isNull())
                                .then(false)
                                .otherwise(true)
                ))
                .from(member)
                .leftJoin(memberFollow)
                    .on(memberFollow.group.groupId.eq(groupId), memberFollow.mid.eq(CryptUtils.getMid()),
                        memberFollow.targetMid.eq(member.mid), memberFollow.followYn.eq(true))
                .where(member.mid.eq(memberId))
                .fetchOne();
    }

    @Override
    public List<ReviewDto.SelectReviewsInGroup> selectReviewsInGroup(String groupId, String targetId, Long pageSize, Long lastReviewSeq, List<String> blocks) {
         return jpaQueryFactory.select(new QReviewDto_SelectReviewsInGroup(
                new QReviewDto_SelectReviewsInGroup_Review(
                        review.reviewId,
                        review.seq,
                        review.content,
                        review.reviewCommentEntities.size(),
                        review.createTime,
                        review.images,
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(reviewKeep.count())
                                        .from(reviewKeep)
                                        .where(reviewKeep.review.reviewId.eq(review.reviewId),
                                                reviewKeep.keepYn.eq(true)),
                                "keepCount"
                        ),
                        reviewKeep.isNotNull()),
                        new QReviewDto_SelectReviewsInGroup_Member(
                                member.mid,
                                member.name,
                                member.profileUrl
                        ),
                        new QReviewDto_SelectReviewsInGroup_Place(
                                place.placeId,
                                place.placeName,
                                place.roadAddress,
                                place.address
                        ))
                 )
                 .from(review)
                 .innerJoin(member).on(review.masterMid.eq(member.mid))
                 .innerJoin(place).on(review.place.placeId.eq(place.placeId))
                 .leftJoin(reviewKeep).on(review.reviewId.eq(reviewKeep.review.reviewId),
                         reviewKeep.memberMid.eq(CryptUtils.getMid()),
                         reviewKeep.keepYn.eq(true))
                 .where(review.group.groupId.eq(groupId),
                         review.isDelete.eq(false),
                         eqReviewMaster(targetId),
                         ltReviewId(lastReviewSeq),
                         notInBlocks(blocks, "REVIEW"))
                 .orderBy(review.seq.desc())
                 .limit(pageSize)
                 .fetch();
    }

    @Override
    public List<DailyDto.DailyInGroup> selectDailyInGroup(String groupId, String targetId, Long pageSize, Long lastDailySeq, List<String> blocks) {
        return jpaQueryFactory
                .select(new QDailyDto_DailyInGroup(
                        daily.dailyId,
                        daily.seq,
                        daily.title,
                        daily.image,
                        member.name,
                        daily.dailyCommentEntities.size(),
                        daily.createTime.stringValue()
                ))
                .from(daily)
                .innerJoin(member).on(member.mid.eq(daily.masterMid))
                .where(daily.group.groupId.eq(groupId),
                        daily.isDelete.eq(false),
                        eqDailyMaster(targetId),
                        ltDailyId(lastDailySeq),
                        notInBlocks(blocks, "DAILY"))
                .orderBy(daily.seq.desc())
                .limit(pageSize)
                .fetch();
    }

    private BooleanExpression ltReviewId(Long seq) {
        return seq != null ? review.seq.lt(seq) : null;
    }


    private BooleanExpression eqReviewMaster(String targetId) {
        return targetId != null ? review.masterMid.eq(targetId) : null;
    }

    private BooleanExpression ltDailyId(Long lastDailySeq) {
        return lastDailySeq != null ? daily.seq.lt(lastDailySeq) : null;
    }


    private BooleanExpression eqDailyMaster(String targetId) {
        return targetId != null ? daily.masterMid.eq(targetId) : null;
    }

    public BooleanExpression notInBlocks(List<String> blocks, String type) {
        if (blocks != null && blocks.size() > 0) {
            if (type.equals("REVIEW")) {
                return review.masterMid.notIn(blocks);
            } else {
                return daily.masterMid.notIn(blocks);
            }
        } else {
            return null;
        }
    }

    @Override
    public ReviewDto.ResponseReviewDetail selectReviewDetail(String groupId, String reviewId) {
        ReviewDto.ReviewDetail reviewDetail = jpaQueryFactory
                .select(new QReviewDto_ReviewDetail(
                        review, member, place
                ))
                .from(review)
                .innerJoin(member).on(member.mid.eq(review.masterMid))
                .innerJoin(place).on(review.place.placeId.eq(place.placeId))
                .where(review.reviewId.eq(reviewId))
                .fetchFirst();
        List<CommentDto> comments = getReviewComments(reviewId);
        MemberEntity loginMember = jpaQueryFactory.select(member).from(member).where(member.mid.eq(CryptUtils.getMid())).fetchOne();

        return new ReviewDto.ResponseReviewDetail(reviewDetail, comments, loginMember);
    }

    private List<CommentDto> getReviewComments(String reviewId) {
        QMemberEntity targetMember = new QMemberEntity("targetMember");
        List<CommentDto> result = jpaQueryFactory.select(new QCommentDto(reviewComment, member, targetMember))
                .from(reviewComment)
                .innerJoin(member).on(member.mid.eq(reviewComment.masterMid))
                .leftJoin(targetMember).on(targetMember.mid.eq(reviewComment.targetMemberMid))
                .where(reviewComment.review.reviewId.eq(reviewId))
                .orderBy(reviewComment.seq.asc())
                .fetch();

        // block
        List<String> blocks = getBlocks();
        result.forEach(comment -> comment.filterBlockComment(blocks));

        result.stream()
                .filter(commentDto -> commentDto.getParentCommentId() != null)
                .forEach(comment -> result.stream()
                        .filter(co -> Objects.equals(co.getCommentId(), comment.getParentCommentId()))
                        .forEach(dd -> dd.insertChildComments(comment)));
        result.removeIf(d -> d.getParentCommentId() != null);

        return result;
    }

    @Override
    public DailyDto.ResponseDailyDetail selectDaily(String groupId, String dailyId) {
        String mid = CryptUtils.getMid();

        DailyDto.DailyDetail dailyDetail = jpaQueryFactory
                .select(new QDailyDto_DailyDetail(
                        daily.title,
                        member.mid,
                        member.name,
                        member.profileUrl,
                        daily.createTime.stringValue(),
                        daily.content,
                        daily.image,
                        group.name,
                        new CaseBuilder()
                                .when(dailyKeep.seq.isNull())
                                .then(Boolean.FALSE)
                                .otherwise(Boolean.TRUE)
                        )
                )
                .from(daily)
                .innerJoin(daily.group, group)
                .innerJoin(member).on(member.mid.eq(daily.masterMid))
                .leftJoin(daily.dailyKeepEntities, dailyKeep).on(dailyKeep.memberMid.eq(mid), dailyKeep.keepYn.eq(true))
                .where(daily.dailyId.eq(dailyId), daily.group.groupId.eq(groupId))
                .fetchOne();

        List<CommentDto> comments = this.getDailyComments(dailyId);
        MemberEntity loginMember = jpaQueryFactory.select(member).from(member).where(member.mid.eq(mid)).fetchOne();

        return new DailyDto.ResponseDailyDetail(dailyDetail, comments, loginMember);
    }

    public List<CommentDto> getDailyComments(String dailyId) {
        QMemberEntity targetMember = new QMemberEntity("targetMember");

        List<CommentDto> result = jpaQueryFactory
                .select(new QCommentDto(dailyComment, member, targetMember))
                .from(dailyComment)
                .innerJoin(member).on(member.mid.eq(dailyComment.masterMid))
                .leftJoin(targetMember).on(targetMember.mid.eq(dailyComment.targetMemberMid))
                .where(dailyComment.daily.dailyId.eq(dailyId))
                .orderBy(dailyComment.seq.asc())
                .fetch();

        // block
        List<String> blocks = getBlocks();
        result.forEach(comment -> comment.filterBlockComment(blocks));

        result.stream()
                .filter(commentDto ->  commentDto.getParentCommentId() != null)
                .forEach(comment -> result.stream()
                        .filter(co -> Objects.equals(co.getCommentId(), comment.getParentCommentId()))
                        .forEach(dd -> dd.insertChildComments(comment)
                        ));
        result.removeIf(d -> d.getParentCommentId() != null);

        return result;
    }

    public List<String> getBlocks() {
        return jpaQueryFactory
                .select(block.targetMid)
                .from(block)
                .where(block.mid.eq(CryptUtils.getMid()))
                .fetch();
    }

    @Override
    public long countJoinGroup(String mid) {
        return jpaQueryFactory
                .select(groupMemberMapping.count())
                .from(groupMemberMapping)
                .where(groupMemberMapping.mid.eq(mid),
                        groupMemberMapping.status.eq(GroupMemberStatus.JOIN))
                .fetchOne();
    }

    @Override
    public List<GroupDto.GroupAlarmSetting> selectGroupAlarm(String alarmType) {
        return jpaQueryFactory
                .select(new QGroupDto_GroupAlarmSetting(
                        group.groupId,
                        group.name,
                        groupMemberMapping.reviewAlarm,
                        groupMemberMapping.dailyAlarm,
                        Expressions.constant(alarmType)
                ))
                .from(groupMemberMapping)
                .innerJoin(groupMemberMapping.group, group)
                .where(groupMemberMapping.status.eq(GroupMemberStatus.JOIN),
                        groupMemberMapping.mid.eq(CryptUtils.getMid()))
                .orderBy(group.name.asc())
                .fetch();
    }

    @Override
    public List<GroupDto.MyFollow> selectMyFollow() {
        return jpaQueryFactory
                .select(new QGroupDto_MyFollow(
                        group.groupId,
                        group.name,
                        member.mid,
                        member.profileUrl,
                        member.name,
                        memberFollow.alarmYn
                ))
                .from(memberFollow)
                .innerJoin(memberFollow.group, group)
                .innerJoin(member).on(memberFollow.targetMid.eq(member.mid))
                .where(memberFollow.mid.eq(CryptUtils.getMid()),
                        memberFollow.followYn.eq(true))
                .orderBy(memberFollow.createTime.asc())
                .fetch();
    }


    @Override
    public CommentDto.CommentDetail getReviewCommentDetail(String groupId, String reviewId, Integer nextOffset, int limit) {
        List<CommentDto> comments = new ArrayList<>();
        String queryString = "WITH RECURSIVE CMTCTE(" +
                " COMMENT_ID," +
                " COMMENT," +
                " IMAGE_URL," +
                " STATUS," +
                " CREATE_DT," +
                " PARENT_COMMENT_ID," +
                " DEPTH," +
                " MEMBER_ID," +
                " MEMBER_NAME," +
                " MEMBER_PROFILE_URL," +
                " TARGET_MID," +
                " TARGET_NAME" +
                ") AS (" +
                " SELECT A.COMMENT_ID" +
                "  , A.COMMENT" +
                "  , A.IMAGE_URL" +
                "  , A.STATUS" +
                "  , A.CREATE_DT" +
                "  , A.PARENT_COMMENT_ID" +
                "  , A.SEQ AS DEPTH" +
                "  , B.MID AS MEMBER_ID" +
                "  , B.NAME AS MEMBER_NAME" +
                "  , B.PROFILE_URL AS MEMBER_PROFILE_URL" +
                "  , C.MID AS TARGET_MID" +
                "  , C.NAME AS TARGET_NAME" +
                " FROM REVIEW_COMMENT A" +
                " INNER JOIN MEMBER B ON A.MASTER_MID = B.MID" +
                " LEFT JOIN MEMBER C ON A.TARGET_MID  = C.MID " +
                " WHERE A.REVIEW_ID = ?" +
                "  AND A.PARENT_COMMENT_ID IS NULL" +
                " UNION ALL" +
                " SELECT A.COMMENT_ID" +
                "  , A.COMMENT" +
                "  , A.IMAGE_URL " +
                "  , A.STATUS" +
                "  , A.CREATE_DT" +
                "  , A.PARENT_COMMENT_ID" +
                "  , CTE.DEPTH AS DEPTH" +
                "  , B.MID AS MEMBER_ID" +
                "  , B.NAME AS MEMBER_NAME" +
                "  , B.PROFILE_URL AS MEMBER_PROFILE_URL" +
                "  , C.MID AS TARGET_MID" +
                "  , C.NAME AS TARGET_NAME" +
                " FROM REVIEW_COMMENT A" +
                " INNER JOIN CMTCTE CTE" +
                "  ON A.PARENT_COMMENT_ID = CTE.COMMENT_ID" +
                " INNER JOIN MEMBER B ON A.MASTER_MID = B.MID" +
                " LEFT JOIN MEMBER C ON A.TARGET_MID  = C.MID " +
                ")" +
                "SELECT COMMENT_ID," +
                " COMMENT," +
                " IMAGE_URL," +
                " STATUS," +
                " CREATE_DT," +
                " IFNULL(PARENT_COMMENT_ID, '')," +
                " MEMBER_ID," +
                " MEMBER_NAME," +
                " MEMBER_PROFILE_URL," +
                " IFNULL(TARGET_MID, '')," +
                " IFNULL(TARGET_NAME, '')" +
                " FROM CMTCTE CTE" +
                " ORDER BY CTE.DEPTH DESC, CTE.CREATE_DT DESC" +
                " LIMIT ?, ?";
        Query query =  entityManager.createNativeQuery(queryString);
        List<Object[]> resultSets = query
                .setParameter(1, reviewId)
                .setParameter(2, nextOffset != null ? nextOffset : 0)
                .setParameter(3, limit)
                .getResultList();

        resultSets.forEach(rs -> comments.add(CommentDto.builder()
                        .commentId(rs[0].toString())
                        .comment(rs[1].toString())
                        .imageUrl(rs[2].toString())
                        .status(CommentStatus.valueOf(rs[3].toString()))
                        .createDt(rs[4].toString())
                        .parentCommentId(!rs[5].toString().isBlank() ? rs[5].toString():null)
                        .memberId(rs[6].toString())
                        .memberName(rs[7].toString())
                        .memberProfileUrl(rs[8].toString())
                        .targetMid(!rs[9].toString().isBlank() ? rs[9].toString() : null)
                        .targetName(!rs[10].toString().isBlank() ? rs[10].toString() : null)
                .build()
        ));

        Collections.reverse(comments);

        return new CommentDto.CommentDetail(nextOffset, limit, comments);
    }

    @Override
    public List<MyGroups> getMyGroups() {
        return jpaQueryFactory.select(Projections.fields(MyGroups.class
                        , group.groupId
                        , group.name.as("groupName")))
                .from(group)
                .innerJoin(groupMemberMapping).on(group.groupId.eq(groupMemberMapping.groupId))
                .where(group.isDelete.eq(false),
                        groupMemberMapping.member.mid.eq(CryptUtils.getMid())
                        , groupMemberMapping.status.eq(GroupMemberStatus.JOIN))
                .fetch();
    }

    @Override
    public List<MyReviews.Reviews> getMyReviews(String groupId, Long lastSeq, Long pageSize) {
        return jpaQueryFactory.select(Projections.fields(MyReviews.Reviews.class,
                        review.seq.as("seq"),
                        place.placeName.as("placeName"),
                        group.groupId.as("groupId"),
                        group.name.as("groupName"),
                        review.createTime.stringValue().as("createDt"),
                        review.reviewId.as("reviewId"),
                        ExpressionUtils.as(JPAExpressions
                                .select(reviewComment.commentId.count())
                                .from(reviewComment)
                                .where(reviewComment.review.reviewId.eq(review.reviewId)), "commentCount"),
                        ExpressionUtils.as(JPAExpressions
                                .select(reviewKeep.seq.count())
                                .from(reviewKeep)
                                .where(reviewKeep.review.reviewId.eq(review.reviewId)), "keepCount"),
                new CaseBuilder().when(review.images.isNotEmpty()).then(review.images)
                        .otherwise(Expressions.nullExpression()).as("imageUrl")
                ))
                .from(review)
                .innerJoin(group).on(group.groupId.eq(review.group.groupId))
                .innerJoin(place).on(place.placeId.eq(review.place.placeId))
                .where(review.isDelete.eq(false), review.masterMid.eq(CryptUtils.getMid()), existGroupId(groupId), existReviewLastSeq(lastSeq))
                .limit(pageSize)
                .orderBy(review.seq.desc())
                .fetch();
    }
    private BooleanExpression existGroupId(String groupId) {
        if (groupId == null) {
            return null;
        }
        return group.groupId.eq(groupId);
    }
    private BooleanExpression existReviewLastSeq(Long lastSeq) {
        if (lastSeq == null) {
            return null;
        }
        return review.seq.lt(lastSeq);
    }

    private BooleanExpression existDailyLastSeq(Long lastSeq) {
        if (lastSeq == null) {
            return null;
        }
        return daily.seq.lt(lastSeq);
    }

    @Override
    public List<MyDaily.Daily> getMyDaily(String groupId, Long lastSeq, Long pageSize) {
        return jpaQueryFactory.select(Projections.fields(MyDaily.Daily.class,
                        daily.seq.as("seq"),
                        daily.title.as("title"),
                        group.groupId.as("groupId"),
                        group.name.as("groupName"),
                        daily.createTime.stringValue().as("createDt"),
                        daily.dailyId.as("dailyId"),
                        ExpressionUtils.as(JPAExpressions
                                .select(dailyComment.commentId.count())
                                .from(dailyComment)
                                .where(dailyComment.daily.dailyId.eq(daily.dailyId)), "commentCount"),
                        new CaseBuilder().when(daily.image.isNotEmpty()).then(daily.image)
                                .otherwise(Expressions.nullExpression()).as("imageUrl")
                ))
                .from(daily)
                .innerJoin(group).on(group.groupId.eq(daily.group.groupId))
                .where(daily.isDelete.eq(false), daily.masterMid.eq(CryptUtils.getMid()), existGroupId(groupId), existDailyLastSeq(lastSeq))
                .limit(pageSize)
                .orderBy(daily.seq.desc())
                .fetch();
    }

    @Override
    public List<MyKeep.KeepReview> getKeepMyReviews(Long nextOffset, Long pageSize) {
        List<String> blocks = getBlocks();

        return jpaQueryFactory.select(Projections.fields(MyKeep.KeepReview.class,
                        reviewKeep.seq.as("seq"),
                        review.place.placeName.as("placeName"),
                        review.reviewId.as("reviewId"),
                        group.groupId,
                        group.name.as("groupName"),
                        member.name.as("memberName"),
                        review.createTime.stringValue().as("createDt"),
                        new CaseBuilder()
                                .when(review.images.isNotEmpty())
                                .then(review.images)
                                .otherwise(Expressions.nullExpression())
                                .as("imageUrl"),
                        review.isDelete.as("isDelete")
                ))
                .from(reviewKeep)
                .innerJoin(review).on(reviewKeep.review.reviewId.eq(review.reviewId)).fetchJoin()
                .innerJoin(group).on(review.group.groupId.eq(group.groupId))
                .innerJoin(member).on(review.masterMid.eq(member.mid))
                .where(reviewKeep.memberMid.eq(CryptUtils.getMid()),
                        reviewKeep.keepYn.eq(true),
                        notInBlocks(blocks, "REVIEW"))
                .limit(pageSize)
                .offset(nextOffset == null ? 0 : nextOffset)
                .orderBy(reviewKeep.updateTime.desc())
                .fetch();
    }

    @Override
    public List<MyKeep.KeepDaily> getKeepMyDaily(Long nextOffset, Long pageSize) {
        List<String> blocks = getBlocks();

        return jpaQueryFactory.select(Projections.fields(MyKeep.KeepDaily.class,
                        dailyKeep.seq,
                        daily.dailyId,
                        daily.title,
                        daily.image.as("imageUrl"),
                        daily.createTime.stringValue().as("createDt"),
                        daily.isDelete,
                        group.groupId,
                        group.name.as("groupName"),
                        member.name.as("memberName"),
                        ExpressionUtils.as(JPAExpressions
                                .select(dailyComment.count())
                                .from(dailyComment)
                                .where(dailyComment.daily.dailyId.eq(daily.dailyId))
                                , "commentCount")
                        ))
                .from(dailyKeep)
                .innerJoin(dailyKeep.daily, daily)
                .innerJoin(daily.group, group)
                .innerJoin(member).on(daily.masterMid.eq(member.mid))
                .where(dailyKeep.memberMid.eq(CryptUtils.getMid()),
                        dailyKeep.keepYn.eq(true),
                        notInBlocks(blocks, "DAILY"))
                .limit(pageSize)
                .offset(nextOffset == null ? 0 : nextOffset)
                .orderBy(dailyKeep.updateTime.desc())
                .fetch();
    }

    @Override
    public List<MyComments.comments> getMyComments(String groupId, Long offset, Long pageSize) {
        List<MyComments.comments> comments = new ArrayList<>();
        String mid = CryptUtils.getMid();
        String queryString = "SELECT A.VIEW_TYPE " +
                ", TRIM(REPLACE(A.COMMENT, CHAR(10),' ')) AS 'COMMENT' " +
                ", A.GROUP_NAME " +
                ", A.TITLE " +
                ", DATE_FORMAT(A.CREATE_DT, '%Y.%m.%d') AS 'CREATE_DT' " +
                ", A.STATUS " +
                ", A.VIEW_IS_DELETE " +
                ", A.GROUP_ID " +
                ", A.VIEW_ID " +
                "FROM ( " +
                "SELECT 'REVIEW' AS 'VIEW_TYPE' " +
                ", RC.COMMENT " +
                ", G.NAME AS 'GROUP_NAME' " +
                ", P.PLACE_NAME AS 'TITLE' " +
                ", RC.CREATE_DT " +
                ", RC.STATUS " +
                ", R.IS_DELETE AS 'VIEW_IS_DELETE' " +
                ", G.GROUP_ID " +
                ", R.REVIEW_ID AS 'VIEW_ID' " +
                "FROM REVIEW_COMMENT RC " +
                "INNER JOIN REVIEW R ON RC.REVIEW_ID = R.REVIEW_ID " +
                "INNER JOIN PLACE P ON R.PLACE_ID = P.PLACE_ID " +
                "INNER JOIN GROUP_LIST G ON R.GROUP_ID = G.GROUP_ID " +
                "WHERE RC.MASTER_MID = ? " +
                (groupId != null ? "AND G.GROUP_ID = ? " : "") +
                "UNION " +
                "SELECT 'DAILY' AS 'VIEW_TYPE' " +
                ", TRIM(REPLACE(DC.COMMENT, CHAR(10),' ')) AS 'COMMENT' " +
                ", G.NAME AS 'GROUP_NAME' " +
                ", D.TITLE AS 'TITLE' " +
                ", DC.CREATE_DT " +
                ", DC.STATUS " +
                ", D.IS_DELETE AS 'VIEW_IS_DELETE' " +
                ", G.GROUP_ID " +
                ", D.DAILY_ID AS 'VIEW_ID' " +
                "FROM DAILY_COMMENT DC " +
                "INNER JOIN DAILY D ON DC.DAILY_ID = D.DAILY_ID " +
                "INNER JOIN GROUP_LIST G ON D.GROUP_ID = G.GROUP_ID " +
                "WHERE DC.MASTER_MID = ? " +
                (groupId != null ? "AND G.GROUP_ID = ? " : "") +
                ") A " +
                "ORDER BY A.CREATE_DT DESC " +
                "LIMIT ?, ?";

        Query query =  entityManager.createNativeQuery(queryString);
        if (groupId != null) {
            query.setParameter(1, mid)
                    .setParameter(2, groupId)
                    .setParameter(3, mid)
                    .setParameter(4, groupId)
                    .setParameter(5, offset)
                    .setParameter(6, pageSize);
        } else {
            query.setParameter(1, mid)
                    .setParameter(2, mid)
                    .setParameter(3, offset)
                    .setParameter(4, pageSize);
        }

        List<Object[]> resultSets = query.getResultList();

        resultSets.forEach(rs -> comments.add(MyComments.comments.builder()
                .viewType(rs[0].toString())
                .comment(rs[1].toString())
                .groupName(rs[2].toString())
                .title(rs[3].toString())
                .createDt(rs[4].toString())
                .status(rs[5].toString())
                .viewIsDelete(rs[6].toString().equals("1"))
                .groupId(rs[7].toString())
                .viewId(rs[8].toString())
                .build()
        ));

        return comments;
    }

    @Override
    public MyFollowingDto getMyFollowingDto(Long nextOffset, Long pageSize) {
        List<MyFollowingDto.MyFollowing> myFollowings = jpaQueryFactory.select(Projections.fields(MyFollowingDto.MyFollowing.class,
                        member.name.as("memberName"),
                        group.groupId,
                        group.name.as("groupName"),
                        member.mid.as("memberMid"),
                        member.isDelete.as("isWithdrawal"),
                        new CaseBuilder()
                                .when(member.profileUrl.isNotEmpty())
                                .then(member.profileUrl)
                                .otherwise(Expressions.nullExpression())
                                .as("profileUrl")
                ))
                .from(memberFollow)
                .innerJoin(member).on(memberFollow.targetMid.eq(member.mid))
                .innerJoin(group).on(memberFollow.groupId.eq(group.groupId))
                .where(memberFollow.mid.eq(CryptUtils.getMid()), memberFollow.followYn.eq(true))
                .offset(nextOffset == null ? 0 : nextOffset)
                .limit(pageSize)
                .orderBy(memberFollow.updateTime.desc())
                .fetch();
        return new MyFollowingDto(myFollowings, nextOffset, pageSize);
    }

    @Override
    public List<MyBlock.Block> getMyBlocks(Long nextOffset, Long pageSize) {
        return jpaQueryFactory
                .select(new QMyBlock_Block(
                        block.targetMid,
                        member.name,
                        member.profileUrl,
                        group.name,
                        member.isDelete
                ))
                .from(block)
                .innerJoin(member).on(block.targetMid.eq(member.mid))
                .innerJoin(group).on(block.groupId.eq(group.groupId))
                .where(block.mid.eq(CryptUtils.getMid()))
                .orderBy(block.createTime.asc())
                .limit(pageSize)
                .offset(nextOffset == null ? 0 : nextOffset)
                .fetch();
    }

    @Override
    public void editReviewKeep(MyKeep.editKeep editKeep) {
        jpaQueryFactory
                .update(reviewKeep)
                .set(reviewKeep.keepYn, false)
                .set(reviewKeep.updateTime, LocalDateTime.now())
                .where(reviewKeep.memberMid.eq(CryptUtils.getMid()), editBooleanExpression(editKeep))
                .execute();
    }

    @Override
    public void editDailyKeep(MyKeep.editKeep editKeep) {
        jpaQueryFactory
                .update(dailyKeep)
                .set(dailyKeep.keepYn, false)
                .set(dailyKeep.updateTime, LocalDateTime.now())
                .where(dailyKeep.memberMid.eq(CryptUtils.getMid()), editBooleanExpression(editKeep))
                .execute();
    }

    private BooleanExpression editBooleanExpression(MyKeep.editKeep editKeep) {
        if (editKeep.getIsAll()) {
            return null;
        }

        return editKeep.getType().equals("REVIEW") ? reviewKeep.seq.in(editKeep.getKeepSeqList()) : dailyKeep.seq.in(editKeep.getKeepSeqList());
    }

    @Override
    public List<MyGroupManagement.Group> getMyGroupsManagements(MyGroupManagementType type) {
        String mid = CryptUtils.getMid();
        List<MyGroupManagement.Group> response;

        if (type.equals(MyGroupManagementType.WITHDRAWAL)) {
            response = this.getManagementWithdrawalGroups(mid);

            response = response.stream()
                    .filter(item -> (item.getExistReview() || item.getExistDaily() || item.getExistReviewComment() || item.getExistDailyComment()))
                    .collect(Collectors.toList());
        } else {
            response = this.getManagementGroups(type, mid);
        }

        return response;
    }

    public List<MyGroupManagement.Group> getManagementGroups(MyGroupManagementType type, String mid) {
        return jpaQueryFactory
                .select(Projections.fields(MyGroupManagement.Group.class,
                        group.groupId,
                        group.name.as("groupName"),
                        group.category
                        ))
                .from(groupMemberMapping)
                .innerJoin(groupMemberMapping.group, group)
                .where(groupMemberMapping.mid.eq(mid),
                        groupMemberMapping.status.eq(GroupMemberStatus.JOIN),
                        isMaster(type, mid))
                .orderBy(group.name.asc())
                .fetch();
    }

    public BooleanExpression isMaster(MyGroupManagementType type, String mid) {
        if (type.equals(MyGroupManagementType.ALL)) {
            return group.masterMid.ne(mid);
        } else {
            return group.masterMid.eq(mid);
        }
    }

    public List<MyGroupManagement.Group> getManagementWithdrawalGroups(String mid) {
        List<MyGroupManagement.Group> data = new ArrayList<>();

        String queryString = "SELECT A.GROUP_ID\n" +
                ", B.NAME AS GROUP_NAME\n" +
                ", B.CATEGORY\n" +
                ", CAST(A.EXIT_DT AS CHAR) AS EXIT_DT\n" +
                ", !ISNULL(C.GROUP_ID) AS EXIST_REVIEW\n" +
                ", !ISNULL(D.GROUP_ID) AS EXIST_DAILY\n" +
                ", !ISNULL(E.GROUP_ID) AS EXIST_REVIEW_COMMENT\n" +
                ", !ISNULL(F.GROUP_ID) AS EXIST_DAILY_COMMENT\n" +
                "FROM GROUP_MEMBER_MAPPING A\n" +
                "INNER JOIN GROUP_LIST B ON A.GROUP_ID = B.GROUP_ID\n" +
                "LEFT JOIN (\n" +
                "SELECT DISTINCT R.GROUP_ID\n" +
                "FROM REVIEW R\n" +
                "WHERE R.MASTER = ?\n" +
                "AND R.IS_DELETE = 0\n" +
                ") C ON C.GROUP_ID = B.GROUP_ID\n" +
                "LEFT JOIN (\n" +
                "SELECT DISTINCT D.GROUP_ID\n" +
                "FROM DAILY D\n" +
                "WHERE D.MASTER = ?\n" +
                "AND D.IS_DELETE = 0\n" +
                ") D ON D.GROUP_ID = B.GROUP_ID\n" +
                "LEFT JOIN (\n" +
                "SELECT DISTINCT R.GROUP_ID\n" +
                "FROM REVIEW_COMMENT RC\n" +
                "INNER JOIN REVIEW R ON RC.REVIEW_ID = R.REVIEW_ID\n" +
                "WHERE RC.MASTER_MID = ?\n" +
                "AND RC.STATUS = 'CREATED'\n" +
                ") E ON E.GROUP_ID = B.GROUP_ID \n" +
                "LEFT JOIN (\n" +
                "SELECT DISTINCT D.GROUP_ID\n" +
                "FROM DAILY_COMMENT DC\n" +
                "INNER JOIN DAILY D ON DC.DAILY_ID = D.DAILY_ID\n" +
                "WHERE DC.MASTER_MID = ?\n" +
                "AND DC.STATUS = 'CREATED'\n" +
                ") F ON F.GROUP_ID = B.GROUP_ID\n" +
                "WHERE A.MID = ?\n" +
                "AND A.STATUS != 'JOIN'\n" +
                "AND B.IS_DELETE = 0\n" +
                "ORDER BY B.NAME ASC";

        Query query =  entityManager.createNativeQuery(queryString);

        List<Object[]> resultSets = query
                .setParameter(1, mid)
                .setParameter(2, mid)
                .setParameter(3, mid)
                .setParameter(4, mid)
                .setParameter(5, mid)
                .getResultList();

        resultSets.forEach(item -> data.add(MyGroupManagement.Group.builder()
                        .groupId(item[0].toString())
                        .groupName(item[1].toString())
                        .category(Category.valueOf(item[2].toString()))
                        .exitDt(item[3].toString())
                        .existReview(item[4].toString().equals("1"))
                        .existDaily(item[5].toString().equals("1"))
                        .existReviewComment(item[6].toString().equals("1"))
                        .existDailyComment(item[7].toString().equals("1"))
                        .build()
        ));

        return data;
    }
}
