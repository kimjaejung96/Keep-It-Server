package com.teamside.project.alpha.notification.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.model.enumurate.GroupMemberStatus;
import com.teamside.project.alpha.notification.model.dto.NotificationDto;
import com.teamside.project.alpha.notification.model.enumurate.NotificationType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepositoryDSLImpl implements NotificationRepositoryDSL{
    private final JPAQueryFactory jpaQueryFactory;
    public NotificationRepositoryDSLImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<NotificationDto> getNotifications(Long pageSize, Long nextOffset) {
        List<NotificationDto> data = new ArrayList<>();
        String mid = CryptUtils.getMid();
        Long offset = (nextOffset == null ? 0 : nextOffset);

        String queryString = "SELECT\n" +
                "NOTI.*\n" +
                "FROM\n" +
                "(\n" +
                "(SELECT\n" +
                "NL.SEQ AS SEQ ,\n" +
                "CAST(NL.NOTI_DATE AS CHAR) AS NOTI_DATE ,\n" +
                "NL.NOTI_TYPE AS NOTI_TYPE ,\n" +
                "RECEIVER.MID AS RECEIVER_MID,\n" +
                "RECEIVER.NAME AS RECEIVER_NAME,\n" +
                "IFNULL(SENDER.MID, '') AS SENDER_MID,\n" +
                "IFNULL(SENDER.NAME, '') AS SENDER_NAME,\n" +
                "IFNULL(SENDER.IS_DELETE, 0) AS SENDER_IS_WITHDRAWAL,\n" +
                "IFNULL(GL.GROUP_ID, '') AS GROUP_ID,\n" +
                "IFNULL(GL.NAME, '') AS NAME,\n" +
                "IFNULL(GMM.STATUS, '') AS JOIN_STATUS,\n" +
                "IFNULL(R.REVIEW_ID, '') AS REVIEW_ID,\n" +
                "IFNULL(P.PLACE_NAME, '') AS PLACE_NAME,\n" +
                "IFNULL(D.DAILY_ID, '') AS DAILY_ID,\n" +
                "IFNULL(D.TITLE, '') AS TITLE,\n" +
                "IFNULL(\n" +
                "CASE\n" +
                "WHEN NL.NOTI_TYPE IN ('KPS_GD' , 'KPS_GJ') THEN GL.PROFILE_URL\n" +
                "WHEN NL.NOTI_TYPE IN ('KPS_GE' , 'KPS_MKT' , 'KPS_UDT') THEN ''\n" +
                "WHEN (SENDER.IS_DELETE = 1 or SGMM.STATUS != 'JOIN') THEN 'dev/profile/default.jpg'\n" +
                "ELSE SENDER.PROFILE_URL\n" +
                "END, '') AS IMAGE_URL,\n" +
                "IFNULL(\n" +
                "CASE\n" +
                "WHEN NL.REVIEW_ID IS NOT NULL THEN RC.COMMENT_ID\n" +
                "WHEN NL.DAILY_ID IS NOT NULL THEN DC.COMMENT_ID\n" +
                "ELSE ''\n" +
                "END, '') AS COMMENT_ID,\n" +
                "IFNULL(\n" +
                "CASE\n" +
                "WHEN NL.REVIEW_ID IS NOT NULL THEN RC.COMMENT\n" +
                "WHEN NL.DAILY_ID IS NOT NULL THEN DC.COMMENT\n" +
                "ELSE ''\n" +
                "END, '') AS COMMENT_CONTENT,\n" +
                "CASE\n" +
                "WHEN NL.REVIEW_ID IS NOT NULL THEN !ISNULL(R.IMAGES)\n" +
                "WHEN NL.DAILY_ID IS NOT NULL THEN !ISNULL(D.IMAGE)\n" +
                "ELSE 0\n" +
                "END AS EXISTS_IMAGE,\n" +
                "CASE\n" +
                "WHEN NL.NOTI_TYPE = 'KPS_MRK' THEN 1\n" +
                "ELSE 0\n" +
                "END AS KEEP_CNT\n" +
                "FROM\n" +
                "noti_list NL\n" +
                "INNER JOIN member RECEIVER ON\n" +
                "(NL.RECEIVER_MID = RECEIVER.MID)\n" +
                "LEFT JOIN member SENDER ON\n" +
                "(NL.SENDER_MID = SENDER.MID)\n" +
                "LEFT JOIN group_list GL ON\n" +
                "(NL.GROUP_ID = GL.GROUP_ID)\n" +
                "LEFT JOIN group_member_mapping GMM ON\n" +
                "(GL.GROUP_ID = GMM.GROUP_ID\n" +
                "AND GMM.MID = ?)\n" +
                "LEFT JOIN group_member_mapping SGMM ON\n" +
                "(GL.GROUP_ID = SGMM.GROUP_ID\n" +
                "AND SGMM.MID = SENDER.MID)\n" +
                "LEFT JOIN review R ON\n" +
                "(NL.REVIEW_ID = R.REVIEW_ID)\n" +
                "LEFT JOIN place P ON\n" +
                "(R.PLACE_ID = P.PLACE_ID)\n" +
                "LEFT JOIN daily D ON\n" +
                "(NL.DAILY_ID = D.DAILY_ID)\n" +
                "LEFT JOIN review_comment RC ON\n" +
                "(NL.COMMENT_ID = RC.COMMENT_ID)\n" +
                "LEFT JOIN daily_comment DC ON\n" +
                "(NL.COMMENT_ID = DC.COMMENT_ID)\n" +
                "WHERE\n" +
                "NL.RECEIVER_MID = ?\n" +
                "AND NL.DELETE_YN != 1\n" +
                "AND NL.NOTI_DATE between DATE_ADD(NOW(), INTERVAL -14 DAY) and now()\n" +
                "AND (GMM.STATUS = 'JOIN' or NL.NOTI_TYPE = 'KPS_GE')\n" +
                "order by NL.NOTI_DATE desc \n" +
                "limit ?\n" +
                ")\n" +
                "UNION ALL\n" +
                "(SELECT\n" +
                "KNL.SEQ AS SEQ ,\n" +
                "CAST(KNL.NOTI_DATE AS CHAR) AS NOTI_DATE ,\n" +
                "'KPS_MRK' AS NOTI_TYPE ,\n" +
                "RECEIVER.MID AS RECEIVER_MID,\n" +
                "RECEIVER.NAME AS RECEIVER_NAME,\n" +
                "SENDER.MID AS SENDER_MID,\n" +
                "SENDER.NAME AS SENDER_NAME,\n" +
                "SENDER.IS_DELETE AS SENDER_IS_WITHDRAWAL,\n" +
                "GL.GROUP_ID AS GROUP_ID,\n" +
                "GL.NAME AS NAME,\n" +
                "GMM.STATUS AS JOIN_STATUS,\n" +
                "R.REVIEW_ID AS REVIEW_ID,\n" +
                "P.PLACE_NAME AS PLACE_NAME,\n" +
                "'' AS DAILY_ID,\n" +
                "'' AS TITLE,\n" +
                "IFNULL(\n" +
                "CASE\n" +
                "WHEN (SENDER.IS_DELETE = 1 or SGMM.STATUS != 'JOIN') THEN 'dev/profile/default.jpg'\n" +
                "ELSE SENDER.PROFILE_URL\n" +
                "END, '') AS IMAGE_URL,\n" +
                "'' AS COMMENT_ID,\n" +
                "'' AS COMMENT_CONTENT,\n" +
                "!ISNULL(R.IMAGES) AS EXISTS_IMAGE,\n" +
                "KNL.KEEP_CNT AS KEEP_CNT\n" +
                "FROM\n" +
                "review_keep_noti_list KNL\n" +
                "INNER JOIN member RECEIVER ON\n" +
                "(KNL.RECEIVER_MID = RECEIVER.MID)\n" +
                "INNER JOIN member SENDER ON\n" +
                "(KNL.LAST_KEEP_MID = SENDER.MID)\n" +
                "INNER JOIN group_list GL ON\n" +
                "(KNL.GROUP_ID = GL.GROUP_ID)\n" +
                "INNER JOIN group_member_mapping GMM ON\n" +
                "(GL.GROUP_ID = GMM.GROUP_ID\n" +
                "AND GMM.MID = ?)\n" +
                "INNER JOIN group_member_mapping SGMM ON\n" +
                "(GL.GROUP_ID = SGMM.GROUP_ID\n" +
                "AND SGMM.MID = SENDER.MID)\n" +
                "INNER JOIN review R ON\n" +
                "(KNL.REVIEW_ID = R.REVIEW_ID)\n" +
                "INNER JOIN place P ON\n" +
                "(R.PLACE_ID = P.PLACE_ID)\n" +
                "WHERE\n" +
                "KNL.RECEIVER_MID = ?\n" +
                "AND KNL.NOTI_DATE BETWEEN DATE_ADD(NOW(), INTERVAL -14 DAY) AND NOW()\n" +
                "AND GMM.STATUS = 'JOIN'\n" +
                "AND KNL.KEEP_CNT != 1\n" +
                "ORDER BY KNL.NOTI_DATE DESC\n" +
                "limit ?\n" +
                ")\n" +
                ") NOTI\n" +
                "ORDER BY\n" +
                "NOTI.NOTI_DATE DESC\n" +
                "LIMIT ?, ?";

        Query query =  entityManager.createNativeQuery(queryString);

        List<Object[]> resultSets = query
                .setParameter(1, mid)
                .setParameter(2, mid)
                .setParameter(3, offset + pageSize)
                .setParameter(4, mid)
                .setParameter(5, mid)
                .setParameter(6, offset + pageSize)
                .setParameter(7, offset)
                .setParameter(8, pageSize)
                .getResultList();

        resultSets.forEach(item -> data.add(NotificationDto.builder()
                    .seq(Long.valueOf(item[0].toString()))
                    .notiDate(item[1].toString())
                    .notificationType(NotificationType.valueOf(item[2].toString()))
                    .receiverMid(item[3].toString())
                    .receiverName(item[4].toString())
                    .senderMid(!item[5].toString().isBlank() ? item[5].toString() : null)
                    .senderName(!item[6].toString().isBlank() ? item[6].toString() : null)
                    .senderIsWithdrawal(item[7].toString().equals("1"))
                    .groupId(!item[8].toString().isBlank() ? item[8].toString() : null)
                    .groupName(!item[9].toString().isBlank() ? item[9].toString() : null)
                    .joinStatus(!item[10].toString().isBlank() ? GroupMemberStatus.valueOf(item[10].toString()) : null)
                    .reviewId(!item[11].toString().isBlank() ? item[11].toString() : null)
                    .reviewTitle(!item[12].toString().isBlank() ? item[12].toString() : null)
                    .dailyId(!item[13].toString().isBlank() ? item[13].toString() : null)
                    .dailyTitle(!item[14].toString().isBlank() ? item[14].toString() : null)
                    .imageUrl(!item[15].toString().isBlank() ? item[15].toString() : null)
                    .commentId(!item[16].toString().isBlank() ? item[16].toString() : null)
                    .commentContent(!item[17].toString().isBlank() ? item[17].toString() : null)
                    .existsImage(item[18].toString().equals("1"))
                    .keepCnt(Integer.valueOf(item[19].toString()))
                .build())
        );

        return data;
    }
}
