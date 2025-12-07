package com.school.StudyShare.comments.entity;

import com.school.StudyShare.community.entity.Community;
import com.school.StudyShare.notes.entity.Note;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 작성자 ID
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    // 댓글 내용
    @Column(name = "comment_content", columnDefinition = "TEXT", nullable = false)
    private String content;

    // [관계 1] 노트 댓글일 경우 (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_note_id") // 수정하신 이름 반영됨!
    private Note note;

    // [관계 2] 커뮤니티 댓글일 경우 (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_community_id") // 수정하신 이름 반영됨!
    private Community community;

    // [관계 3] 대댓글 (부모 댓글 ID) - 없으면 null
    @Column(name = "comment_parent_comment_id")
    private Long parentCommentId;

    // 작성일 (자동 생성)
    @CreationTimestamp
    @Column(name = "comment_create_date")
    private LocalDateTime createDate;
}