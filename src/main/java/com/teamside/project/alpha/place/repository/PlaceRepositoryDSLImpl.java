package com.teamside.project.alpha.place.repository;

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
import com.teamside.project.alpha.place.model.dto.PlaceDto;
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
//        jpaQueryFactory.select(Projections.fields(PlaceDto.PlacePinDto.class,
//                        place.placeId.as("placeId"),
//                        place.placeName.as("placeName"),
//                        place.address.as("address"),
//                        place.roadAddress.as("roadAddress"),
//                        place.phone.as("phone"),
//                        place.x.as("x"),
//                        place.y.as("y"),
//                        review.count().as("reviewCount"),
//                        Expressions.stringTemplate("GROUP_CONCAT({} SEPARATOR {1})", review.images, ":").as("imageUrl")
//                ))
//                .from(place)
//                .innerJoin(review).on(review.place.placeId.eq(place.placeId))
//                .where(review.group.groupId.eq(groupId),
//                        review.isDelete.eq(false))
//                .groupBy(place.placeId)
//                .fetch();
        List<PlaceDto.PlacePinDto> result = new ArrayList<>();
        List<String> blocks = getBlocks();
        String queryString = "SELECT P.PLACE_ID "
                + ", P.PLACE_NAME "
                + ", P.ADDRESS "
                + ", P.ROAD_ADDRESS "
                + ", P.PHONE "
                + ", P.X "
                + ", P.Y "
                + ", COUNT(R.REVIEW_ID) "
                + ", SUBSTRING_INDEX(GROUP_CONCAT(R.IMAGES, ','), ',', 1) "
                + "FROM PLACE P "
                + "INNER JOIN ( "
                + "SELECT * "
                + "FROM REVIEW A "
                + "ORDER BY A.SEQ "
                + ") R ON R.PLACE_ID = P.PLACE_ID "
                + "WHERE R.GROUP_ID = ? "
                + "AND R.IS_DELETE = 0 "
                + "AND R.MASTER NOT IN (?) "
                + "GROUP BY P.PLACE_ID;";
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
                new BigDecimal(d[5].toString()),
                new BigDecimal(d[6].toString()),
                d[7].toString().isEmpty() ? null : Long.parseLong(d[7].toString()),
                d[8].toString().isEmpty() ? null : d[8].toString()
                )));

        return result;
    }
    public List<String> getBlocks() {
        return jpaQueryFactory
                .select(block.targetMid)
                .from(block)
                .where(block.mid.eq(CryptUtils.getMid()))
                .fetch();
    }

}
