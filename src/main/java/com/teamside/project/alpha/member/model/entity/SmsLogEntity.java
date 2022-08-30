package com.teamside.project.alpha.member.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "SMS_LOG")
public class SmsLogEntity extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq", columnDefinition = "bigint")
    private Long seq;

    @Column(name = "phone", columnDefinition = "char(64)")
    private String phone;

    @Column(name = "authNum", columnDefinition = "char(6)")
    private String authNum;

    public SmsLogEntity(String phone, String authNum) {
        this.phone = phone;
        this.authNum = authNum;
    }
}
