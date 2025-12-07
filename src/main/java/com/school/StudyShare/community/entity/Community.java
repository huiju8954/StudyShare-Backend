package com.school.StudyShare.community.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "community")
@Getter
@Setter
@NoArgsConstructor
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "community_title", length = 200, nullable = false)
    private String title;

    @Column(name = "community_category", length = 50, nullable = false)
    private String category;

    @Column(name = "community_content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "community_likes_count")
    private Integer likesCount = 0;

    @Column(name = "community_comment_count")
    private Integer commentCount = 0;

    @Column(name = "community_comment_like_count")
    private Integer commentLikeCount = 0;

    // 💡 [추가] 북마크 개수
    @Column(name = "community_bookmarks_count")
    private Integer bookmarksCount = 0;

    @CreationTimestamp
    @Column(name = "community_create_date", nullable = false)
    private LocalDateTime createDate;
}