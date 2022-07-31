package com.teamside.project.common.model.entity.entitiy;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(value = {AuditingEntityListener.class})
@Getter
public class TimeEntity {
    @CreatedDate
    @Column(name = "CREATE_DT", nullable = false, updatable = false, columnDefinition = "DATETIME")
    private LocalDateTime createTime;

    @LastModifiedDate
    @Column(name = "UPDATE_DT",nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime updateTime;
}
