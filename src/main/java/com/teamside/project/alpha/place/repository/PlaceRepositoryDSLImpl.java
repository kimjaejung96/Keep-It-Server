package com.teamside.project.alpha.place.repository;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.domain.daily.model.entity.QDailyCommentEntity;
import com.teamside.project.alpha.group.domain.daily.model.entity.QDailyEntity;
import com.teamside.project.alpha.group.domain.daily.model.entity.QDailyKeepEntity;
import com.teamside.project.alpha.group.domain.review.model.entity.QReviewCommentEntity;
import com.teamside.project.alpha.group.domain.review.model.entity.QReviewEntity;
import com.teamside.project.alpha.group.domain.review.model.entity.QReviewKeepEntity;
import com.teamside.project.alpha.group.model.entity.QGroupEntity;
import com.teamside.project.alpha.group.model.entity.QGroupMemberMappingEntity;
import com.teamside.project.alpha.group.model.entity.QMemberFollowEntity;
import com.teamside.project.alpha.group.model.entity.QStatReferralGroupEntity;
import com.teamside.project.alpha.member.model.entity.QMemberBlockEntity;
import com.teamside.project.alpha.member.model.entity.QMemberEntity;
import com.teamside.project.alpha.place.model.dto.*;
import com.teamside.project.alpha.place.model.entity.QPlaceEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlaceRepositoryDSLImpl implements PlaceRepositoryDSL {
    public PlaceRepositoryDSLImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

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

    @Override
    public List<PlaceDto.PlacePinDto> getPlacePins(String groupId) {
        List<PlaceDto.PlacePinDto> result = new ArrayList<>();
        List<String> blocks = getBlocks();

        String queryString = "SELECT P.PLACE_ID "
                + ", P.PLACE_NAME "
                + ", P.ADDRESS "
                + ", P.ROAD_ADDRESS "
                + ", P.PHONE "
                + ", P.CATEGORY_GROUP_CODE "
                + ", TRIM(SUBSTRING_INDEX(P.CATEGORY_NAME, '& gt;', -1)) AS CATEGORY_NAME "
                + ", P.X "
                + ", P.Y "
                + ", COUNT(R.REVIEW_ID) "
                + ", SUBSTRING_INDEX(GROUP_CONCAT(R.IMAGES, ','), ',', 1) "
                + "FROM place P "
                + "INNER JOIN ( "
                + "SELECT * "
                + "FROM review A "
                + "ORDER BY A.SEQ "
                + ") R ON R.PLACE_ID = P.PLACE_ID "
                + "WHERE R.GROUP_ID = ? "
                + "AND R.IS_DELETE = 0 "
                + "AND R.MASTER NOT IN (?) "
                + "GROUP BY P.PLACE_ID";

        Query query =  entityManager.createNativeQuery(queryString);
        List<Object[]> resultSets = query
                .setParameter(1, groupId)
                .setParameter(2, blocks.stream().collect(Collectors.joining(",")))
                .getResultList();


        resultSets.forEach(d -> result.add(new PlaceDto.PlacePinDto(
                Long.parseLong(d[0].toString()),
                d[1].toString(),
                d[2].toString().isEmpty() ? null : d[2].toString(),
                d[3].toString().isEmpty() ? null : d[3].toString(),
                d[4].toString().isEmpty() ? null : d[4].toString(),
                d[5].toString().isEmpty() ? null : d[5].toString(),
                d[6].toString().isEmpty() ? null : d[6].toString(),
                new BigDecimal(d[7].toString()),
                new BigDecimal(d[8].toString()),
                d[9].toString().isEmpty() ? null : Long.parseLong(d[9].toString()),
                d[10].toString().isEmpty() ? null : d[10].toString()
                )));

        return result;
    }

    @Override
    public List<PlaceDto.ReviewInfo> getPlaceReviews(Long placeId, String groupId, Long pageSize, Long lastReviewSeq) {
        return jpaQueryFactory.select(new QPlaceDto_ReviewInfo(
                new QPlaceDto_ReviewInfo_Review(
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
                        reviewKeep.isNotNull()
                ),
                new QPlaceDto_ReviewInfo_Member(
                        member.mid,
                        member.name,
                        member.profileUrl
                ),
                new QPlaceDto_ReviewInfo_Place(
                        place.placeId,
                        place.placeName,
                        place.roadAddress,
                        place.address
                )))
                .from(review)
                .innerJoin(member).on(review.masterMid.eq(member.mid))
                .innerJoin(place).on(review.place.placeId.eq(place.placeId))
                .leftJoin(reviewKeep).on(review.reviewId.eq(reviewKeep.review.reviewId),
                        reviewKeep.memberMid.eq(CryptUtils.getMid()),
                        reviewKeep.keepYn.eq(true))
                .where(review.group.groupId.eq(groupId),
                        review.isDelete.eq(false),
                        place.placeId.eq(placeId),
                        ltReviewId(lastReviewSeq),
                        notInBlocks()
                )
                .orderBy(review.seq.desc())
                .limit(pageSize)
                .fetch();
    }

    private BooleanExpression ltReviewId(Long seq) {
        return seq != null ? review.seq.lt(seq) : null;
    }

    private BooleanExpression notInBlocks() {
        List<String> blocks = getBlocks();

        if (blocks != null && blocks.size() > 0) {
            return review.masterMid.notIn(blocks);
        } else {
            return null;
        }
    }

    public List<String> getBlocks() {
        return jpaQueryFactory
                .select(block.targetMid)
                .from(block)
                .where(block.mid.eq(CryptUtils.getMid()))
                .fetch();
    }

}
