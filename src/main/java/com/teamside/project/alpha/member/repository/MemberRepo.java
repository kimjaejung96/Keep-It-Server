package com.teamside.project.alpha.member.repository;

import com.teamside.project.alpha.member.model.entity.MemberEntity;
import com.teamside.project.alpha.member.model.enumurate.SignUpType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepo extends JpaRepository<MemberEntity, Long>, MemberRepoDSL {
    boolean existsByName(String name);
    boolean existsByNameAndIsDelete(String name, boolean isDelete);
    boolean existsByPhone(String phone);
    boolean existsByMid(String mid);
    Optional<MemberEntity> findByPhoneAndType(String phone, SignUpType signUpType);
    Optional<MemberEntity> findByMid(String mid);
    Optional<MemberEntity> findByMidAndIsDelete(String mid, boolean isDelete);
    List<MemberEntity> findAllByFcmToken(String token);
}
