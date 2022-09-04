package com.teamside.project.alpha.group.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.teamside.project.alpha.common.model.constant.KeepitConstant;
import com.teamside.project.alpha.group.domain.GroupMemberMappingEntity;
import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.group.model.enumurate.Category;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupDto {
    private Long groupId;
    @NotNull
    @Pattern(regexp = KeepitConstant.REGEXP_GROUP_NAME, message = "그룹 이름이 올바르지 않습니다.")
    private String name;
    @NotNull
    @Pattern(regexp = KeepitConstant.REGEXP_GROUP_DESCRIPTION, message = "그룹설명 벨리데이션이 올바르지 않습니다.")
    private String description;
    @Pattern(regexp = KeepitConstant.REGEXP_GROUP_PASSWORD, message = "password가 올바르지 않습니다.")
    private String password;
    @NotNull
    private Boolean usePrivate;
    @NotNull
    private Integer memberQuantity;
    private String profileUrl;
    @NotNull
    private Category category;
    public GroupDto(GroupEntity groupEntity) {
        this.groupId = groupEntity.getGroupId();
        this.name = groupEntity.getName();
        this.description = groupEntity.getDescription();
        this.password = groupEntity.getPassword();
        this.usePrivate = groupEntity.getUsePrivate();
        this.memberQuantity = groupEntity.getMemberQuantity();
        this.profileUrl = groupEntity.getProfileUrl();
        this.category = groupEntity.getCategory();
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SelectGroupDto extends GroupDto{
        public SelectGroupDto(GroupEntity entity) {
            super(entity);
            this.master = entity.getMaster().getMid();
        }
        private String master;
    }
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SearchGroupDto {
        private Long groupId;
        private String name;
        private Category category;
        private String profileUrl;
        private Boolean usePrivate;
        private Long participantCount;
        @QueryProjection
        public SearchGroupDto(Long groupId, String name, Category category, String profileUrl, Boolean usePrivate, Long participantCount) {
            this.groupId = groupId;
            this.name = name;
            this.category = category;
            this.profileUrl = profileUrl;
            this.usePrivate = usePrivate;
            this.participantCount = participantCount;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MyGroupDto {
        private Long groupId;
        private String name;
        private Category category;
        private String profileUrl;
        private Boolean usePrivate;
        private Long participantCount;
        private Boolean favorite;
        private Boolean isMaster;
        private Integer ord;
        @QueryProjection
        public MyGroupDto(Long groupId, String name, Category category, String profileUrl, Boolean usePrivate, Long participantCount, Boolean favorite, Boolean isMaster, Integer ord) {
            this.groupId = groupId;
            this.name = name;
            this.category = category;
            this.profileUrl = profileUrl;
            this.usePrivate = usePrivate;
            this.participantCount = participantCount;
            this.favorite = favorite;
            this.isMaster = isMaster;
            this.ord = ord;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseMyGroupDto {
        List<MyGroupDto> favoriteGroupList;
        List<MyGroupDto> groupList;

        public ResponseMyGroupDto(List<MyGroupDto> favoriteGroupList, List<MyGroupDto> groupList) {
            this.favoriteGroupList = favoriteGroupList;
            if (groupList != null) {
                this.groupList = groupList;
            }
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseSearchGroupDto {
        Long totalCount;
        List<SearchGroupDto> groupList;

        public ResponseSearchGroupDto(Long totalCount, List<SearchGroupDto> groupList) {
            this.totalCount = totalCount;
            this.groupList = groupList;
        }
    }

    @Getter
    public static class RequestUpdateOrdDto {
        List<MyGroupDto> groupList = new ArrayList<>();
    }
}
