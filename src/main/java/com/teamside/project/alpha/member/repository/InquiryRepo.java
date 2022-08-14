package com.teamside.project.alpha.member.repository;

import com.teamside.project.alpha.member.model.entity.InquiryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InquiryRepo extends JpaRepository<InquiryEntity, Long> {
}
