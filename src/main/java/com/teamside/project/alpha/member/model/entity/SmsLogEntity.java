package com.teamside.project.alpha.member.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

}
