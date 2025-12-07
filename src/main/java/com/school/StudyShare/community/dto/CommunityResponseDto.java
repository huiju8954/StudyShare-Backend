package com.school.StudyShare.community.dto;

import com.school.StudyShare.community.entity.Community;
import com.fasterxml.jackson.annotation.JsonProperty; // 💡 필수 임포트
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class CommunityResponseDto {

    private Long id;
    private Integer userId;
    private String title;
    private String category;
    private String content;

    @JsonProperty("likesCount")
    private Integer likesCount;

    private Integer commentCount;
    private Integer commentLikeCount;

    @JsonProperty("bookmarksCount")
    private Integer bookmarksCount;

    private String createDate;

    // 🚨 [수정] 여기에 @JsonProperty를 꼭 붙여야 합니다!
    // 그래야 "liked"가 아니라 "isLiked"라는 이름 그대로 앱에 도착합니다.
    @JsonProperty("isLiked")
    private boolean isLiked;

    @JsonProperty("isBookmarked")
    private boolean isBookmarked;

    public CommunityResponseDto(Community community, boolean isLiked, boolean isBookmarked) {
        this.id = community.getId();
        this.userId = community.getUserId();
        this.title = community.getTitle();
        this.category = community.getCategory();
        this.content = community.getContent();
        this.likesCount = community.getLikesCount();
        this.commentCount = community.getCommentCount();
        this.commentLikeCount = community.getCommentLikeCount();

        this.bookmarksCount = community.getBookmarksCount();
        if (this.bookmarksCount == null) this.bookmarksCount = 0;

        if (community.getCreateDate() != null) {
            this.createDate = community.getCreateDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } else {
            this.createDate = "";
        }

        this.isLiked = isLiked;
        this.isBookmarked = isBookmarked;
    }

    // 단순 변환용 생성자
    public CommunityResponseDto(Community community) {
        this(community, false, false);
    }
}