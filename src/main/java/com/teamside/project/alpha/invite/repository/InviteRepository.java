package com.teamside.project.alpha.invite.repository;

import com.teamside.project.alpha.group.model.entity.InvitationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InviteRepository extends JpaRepository<InvitationEntity, String> {
}
