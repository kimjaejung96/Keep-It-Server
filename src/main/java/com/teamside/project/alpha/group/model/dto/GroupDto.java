package com.teamside.project.alpha.group.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.teamside.project.alpha.common.model.constant.KeepitConstant;
import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.group.model.enumurate.Category;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupDto {
    private Long groupId;
    @NotNull
    @Pattern(regexp = KeepitConstant.REGEXP_GROUP_NAME, message = "그룹 이름이 올바르지 않습니다.")
    @Size(min = 4, max = 20, message = "그룹 제목은 4~20자 입니다.")
    private String name;
    @NotNull
    @Size(min = 10, max = 200, message = "그룹설명 벨리데이션이 올바르지 않습니다.")
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
    public static class GroupInfoDto extends GroupDto{
        public GroupInfoDto(GroupEntity entity) {
            super(entity);
            this.master = entity.getMaster().getMid();
        }
        private String master;
        private boolean isFavorite;
        private long inMembers;
        private long inReviews;
        private List<MembersDto> members;

        @Getter
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        public static class MembersDto{
            String name;
            String mid;
            String profileUrl;
            boolean isFollow;

            @QueryProjection
            public MembersDto(String name, String mid, String profileUrl, boolean isFollow) {
                this.name = name;
                this.mid = mid;
                this.profileUrl = profileUrl;
                this.isFollow = isFollow;
            }
        }

        @QueryProjection
        public GroupInfoDto(GroupEntity groupEntity, String master, boolean isFavorite, long inMembers, long inReviews) {
            super(groupEntity);
            this.master = master;
            this.isFavorite = isFavorite;
            this.inMembers = inMembers;
            this.members = new ArrayList<>();
            this.inReviews = inReviews;
        }
        public void addGroupInfoMembers(List<MembersDto> members) {
            this.members = members;
        }
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
        Long lastGroupId;
        List<SearchGroupDto> groupList;

        public ResponseSearchGroupDto(Long totalCount, Long lastGroupId, List<SearchGroupDto> groupList) {
            this.totalCount = totalCount;
            this.lastGroupId = lastGroupId;
            this.groupList = groupList;
        }
    }

    @Getter
    public static class RequestUpdateOrdDto {
        List<MyGroupDto> groupList = new ArrayList<>();
    }
}
