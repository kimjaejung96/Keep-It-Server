package com.teamside.project.alpha.group.model.entity;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.model.converter.CategoryConverter;
import com.teamside.project.alpha.group.model.domain.DailyEntity;
import com.teamside.project.alpha.group.model.domain.GroupMemberMappingEntity;
import com.teamside.project.alpha.group.model.domain.ReviewEntity;
import com.teamside.project.alpha.group.model.dto.GroupDto;
import com.teamside.project.alpha.group.model.enumurate.Category;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "GROUP_LIST")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupEntity extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GROUP_ID", columnDefinition = "bigint")
    private Long groupId;

    @Column(name = "NAME", columnDefinition = "varchar(20)")
    private String name;

    @Column(name = "DESCRIPTION", columnDefinition = "varchar(200)")
    private String description;

    @Column(name = "PASSWORD", columnDefinition = "varchar(8)")
    private String password;

    @Column(name = "USE_PRIVATE", columnDefinition = "boolean")
    private Boolean usePrivate;

    @Column(name = "MEMBER_QUANTITY", columnDefinition = "int")
    private Integer memberQuantity;

    @Column(name = "PROFILE_URL", columnDefinition = "varchar(255)")
    private String profileUrl;

    @Convert(converter = CategoryConverter.class)
    @Column(name = "CATEGORY", columnDefinition = "varchar(50)")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MASTER_MID",  referencedColumnName = "MID")
    private MemberEntity master;

    @OneToMany(mappedBy = "group", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GroupMemberMappingEntity> groupMemberMappingEntity;

    @OneToMany(mappedBy = "group", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReviewEntity> reviewEntities;

    @OneToMany(mappedBy = "group", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DailyEntity> dailyEntities;

    public GroupEntity(GroupDto group) {
        this.name = group.getName();
        this.description = group.getDescription();
        this.password = group.getUsePrivate() ?  group.getPassword() : "";
        this.usePrivate = group.getUsePrivate();
        this.memberQuantity = group.getMemberQuantity();
        this.profileUrl = group.getProfileUrl();
        this.category = group.getCategory();

        this.groupMemberMappingEntity = new ArrayList<>();
        setMasterMember();

        this.reviewEntities = new ArrayList<>();
        this.dailyEntities = new ArrayList<>();
    }

    private void setMasterMember(){
        this.master = new MemberEntity(CryptUtils.getMid());
    }
    public void addMember(String mid) {
        this.groupMemberMappingEntity.add(new GroupMemberMappingEntity(mid, this.groupId));
    }
    public void removeMember(String mid) {
        GroupMemberMappingEntity findEntity = this.groupMemberMappingEntity.stream()
                .filter(entity -> entity.getMid().equals(mid))
                .filter(entity -> entity.getGroupId().equals(this.groupId))
                .findFirst()
                .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.MEMBER_NOT_FOUND));
        this.groupMemberMappingEntity.remove(findEntity);
    }

    public void updateGroup(GroupDto group) {
        this.name = group.getName();
        this.description = group.getDescription();
        this.password = group.getUsePrivate() ? group.getPassword() : "";
        this.memberQuantity = group.getMemberQuantity();
        this.profileUrl = group.getProfileUrl();
    }

    public void checkJoinPossible(GroupEntity group, String password) throws CustomException {
        if (Boolean.TRUE.equals(group.getUsePrivate())) {
            if (!password.equals(group.getPassword())) {
                throw new CustomException(ApiExceptionCode.PASSWORD_IS_INCORRECT);
            }
        }
        if (group.getGroupMemberMappingEntity().stream().anyMatch(g -> g.getMid().equals(CryptUtils.getMid()))) {
            throw new CustomException(ApiExceptionCode.ALREADY_JOINED_GROUP);
        }
        if (group.getGroupMemberMappingEntity().size() >= this.memberQuantity) {
            throw new CustomException(ApiExceptionCode.MEMBER_QUANTITY_IS_FULL);
        }
    }

    public void createReview(ReviewEntity review) {
        this.reviewEntities.add(review);
    }

    public void createDaily(DailyEntity dailyEntity) {
        this.dailyEntities.add(dailyEntity);
    }

    public void checkExistReview(long placeId) {
        if (this.reviewEntities
                .stream()
                .filter(place -> placeId == place.getPlace().getPlaceId())
                .anyMatch(reviewEntity -> reviewEntity.getMaster().getMid().equals(CryptUtils.getMid()))) {
            throw new CustomRuntimeException(ApiExceptionCode.REVIEW_ALREADY_EXIST);
        }
    }

    public void checkExistMember(String mid) {
        this.groupMemberMappingEntity.stream()
                .filter(member -> member.getMember().getMid().equals(mid))
                .findAny().orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.NOT_PARTICIPANT_IN_GROUP));
    }

    public GroupEntity(Long groupId) {
        this.groupId = groupId;
    }

    public void checkGroupMaster() {
        if (!this.master.getMid().equals(CryptUtils.getMid())) {
            throw new CustomRuntimeException(ApiExceptionCode.FORBIDDEN);
        }
    }

    public void checkReviewMaster(Long reviewId) throws CustomException {
        if (this.getReviewEntities().stream()
                .filter(r -> r.getReviewId().equals(reviewId))
                .noneMatch(r -> r.getMaster().getMid().equals(CryptUtils.getMid()))) {
            throw new CustomException(ApiExceptionCode.FORBIDDEN);
        }
    }

    public void deleteReview(Long reviewId) throws CustomException {
        this.checkReviewMaster(reviewId);
        ReviewEntity review = this.getReviewEntities().stream().filter(r-> r.getReviewId().equals(reviewId)).findFirst().orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.REVIEW_NOT_EXIST));
        this.getReviewEntities().remove(review);
    }

}
