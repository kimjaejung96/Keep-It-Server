package com.teamside.project.alpha.group.repository;

import com.querydsl.core.types.ExpressionUtils;
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
import com.teamside.project.alpha.group.model.enumurate.GroupMemberStatus;
import com.teamside.project.alpha.group.model.enumurate.MyGroupType;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import com.teamside.project.alpha.member.model.entity.QMemberEntity;
import com.teamside.project.alpha.place.model.entity.QPlaceEntity;
import org.apache.logging.log4j.util.Strings;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
                        group.isDelete.eq(false),
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
                .where(group.isDelete.eq(false)
                        .and(group.groupId.eq(groupId)))
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
//                        .where(groupMemberMapping.groupId.eq(groupId).and(member.mid.ne(CryptUtils.getMid())))
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
                        statReferralGroup.referralType.eq(referralType), statReferralGroup.category.eq(category))
                .limit(10)
                .groupBy(group.groupId, group.name, group.category, group.profileUrl, group.usePrivate, statReferralGroup.statDt, statReferralGroup.rankNum)
                .orderBy(statReferralGroup.statDt.desc(), statReferralGroup.rankNum.asc())
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
                                .otherwise(true),
                        review.count(),
                        daily.count()
                ))
                .from(member)
                .leftJoin(review)
                    .on(review.masterMid.eq(member.mid), review.group.groupId.eq(groupId), review.isDelete.eq(false))
                .leftJoin(daily)
                    .on(daily.masterMid.eq(member.mid), daily.group.groupId.eq(groupId), daily.isDelete.eq(false))
                .leftJoin(memberFollow)
                    .on(memberFollow.group.groupId.eq(groupId), memberFollow.mid.eq(CryptUtils.getMid()),
                        memberFollow.targetMid.eq(member.mid), memberFollow.followYn.eq(true))
                .where(member.mid.eq(memberId))
                .fetchOne();
    }

    @Override
    public List<ReviewDto.SelectReviewsInGroup> selectReviewsInGroup(String groupId, String targetId, Long pageSize, Long seq) {
         return jpaQueryFactory.select(new QReviewDto_SelectReviewsInGroup(
                new QReviewDto_SelectReviewsInGroup_Review(
                        review.reviewId,
                        review.seq,
                        review.content,
                        review.reviewCommentEntities.size(),
                        review.createTime,
                        review.images,
                        review.reviewKeepEntities.size(),
                        reviewKeep.isNotNull()),
                        new QReviewDto_SelectReviewsInGroup_Member(
                                member.mid,
                                member.name,
                                member.profileUrl
                        ),
                        new QReviewDto_SelectReviewsInGroup_Place(
                                place.placeId,
                                place.placeName,
                                place.roadAddress
                        ))
                 )
                 .from(review)
                 .innerJoin(member).on(review.masterMid.eq(member.mid))
                 .innerJoin(place).on(review.place.placeId.eq(place.placeId))
                 .leftJoin(reviewKeep).on(review.reviewId.eq(reviewKeep.review.reviewId).and(reviewKeep.memberMid.eq(CryptUtils.getMid())))
                 .where(review.group.groupId.eq(groupId), review.isDelete.eq(false), eqReviewMaster(targetId), ltReviewId(seq))
                 .orderBy(review.seq.desc())
                 .limit(pageSize)
                 .fetch();
    }

    @Override
    public List<DailyDto.DailyInGroup> selectDailyInGroup(String groupId, String targetId, Long pageSize, Long lastDailyId) {
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
                .where(daily.group.groupId.eq(groupId), daily.isDelete.eq(false), eqDailyMaster(targetId), ltDailyId(lastDailyId))
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
        return this.getReviewCommentDetail("", reviewId, 0, 5).getComments();
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
                        new CaseBuilder()
                                .when(dailyKeep.keepId.isNull())
                                .then(Boolean.FALSE)
                                .otherwise(Boolean.TRUE)
                        )
                )
                .from(daily)
                .innerJoin(member).on(member.mid.eq(daily.masterMid))
                .leftJoin(daily.dailyKeepEntities, dailyKeep).on(dailyKeep.memberMid.eq(mid))
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

        result.stream()
                .filter(commentDto ->  commentDto.getParentCommentId() != null)
                .forEach(comment -> result.stream()
                        .filter(co -> Objects.equals(co.getCommentId(), comment.getParentCommentId()))
                        .forEach(dd -> dd.insertChildComments(comment)
                        ));
        result.removeIf(d -> d.getParentCommentId() != null);

        return result;
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
                .orderBy(groupMemberMapping.createTime.asc())
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

        return new CommentDto.CommentDetail(nextOffset, limit, comments);
    }
}
