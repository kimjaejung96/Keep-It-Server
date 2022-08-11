package com.teamside.project.alpha.member.repository;

import com.teamside.project.alpha.member.model.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepo extends JpaRepository<MemberEntity, Long> {
    boolean existsByName(String name);
    boolean existsByPhone(String phone);
}
