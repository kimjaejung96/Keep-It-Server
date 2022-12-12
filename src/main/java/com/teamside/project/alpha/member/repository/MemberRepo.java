package com.teamside.project.alpha.member.repository;

import com.teamside.project.alpha.member.model.entity.MemberEntity;
import com.teamside.project.alpha.member.model.enumurate.SignUpType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepo extends JpaRepository<MemberEntity, Long>, MemberRepoDSL {
    boolean existsByName(String name);
    boolean existsByPhone(String phone);
    boolean existsByMid(String mid);
    Optional<MemberEntity> findByPhoneAndType(String phone, SignUpType signUpType);
    Optional<MemberEntity> findByMid(String mid);
    Optional<MemberEntity> findByFcmToken(String token);
}
