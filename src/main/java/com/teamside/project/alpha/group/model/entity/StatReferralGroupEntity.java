package com.teamside.project.alpha.group.model.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "STAT_REFERRAL_GROUP_HOUR")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StatReferralGroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ")
    private Long seq;

    @Column(name = "RANK_NUM")
    private Long rankNum;

    @Column(name = "STAT_DT")
    private LocalDateTime statDt;

    @Column(name = "REFERRAL_TYPE")
    private String referralType;

    @Column(name = "GROUP_ID")
    private Long groupId;

    @Column(name = "CATEGORY")
    private String category;

    public StatReferralGroupEntity(Long seq, Long rankNum, LocalDateTime statDt, String referralType, Long groupId, String category) {
        this.seq = seq;
        this.rankNum = rankNum;
        this.statDt = statDt;
        this.referralType = referralType;
        this.groupId = groupId;
        this.category = category;
    }
}
