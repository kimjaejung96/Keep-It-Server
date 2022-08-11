package com.teamside.project.alpha.member.repository;

import com.teamside.project.alpha.member.model.entity.MemberEntity;
import com.teamside.project.alpha.member.model.enumurate.SignUpType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepo extends JpaRepository<MemberEntity, Long> {
    boolean existsByName(String name);
    boolean existsByPhone(String phone);
    Optional<MemberEntity> findByPhoneAndType(String phone, SignUpType signUpType);
}
