package com.school.StudyShare.notes.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "notes")
@Getter
@Setter
@NoArgsConstructor
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 💡 [유지] 필드명 및 JSON 매핑
    @JsonProperty("user_id")
    @Column(name = "user_id", nullable = false)
    private Integer noteUserId;

    @JsonProperty("note_title")
    @Column(name = "note_title", length = 200, nullable = false)
    private String noteTitle;

    @JsonProperty("note_subject_id")
    @Column(name = "note_subject_id", nullable = false)
    private Integer noteSubjectId;

    // ✅ [통합] TEXT에서 LONGTEXT로 변경하여 긴 노트 내용 저장 허용
    @JsonProperty("note_content")
    @Column(name = "note_content", columnDefinition = "LONGTEXT", nullable = false)
    private String noteContent;

    // ✅ [추가] 노트 검색을 위한 순수 텍스트 필드 추가
    @Column(name = "note_plain_text", columnDefinition = "LONGTEXT")
    private String notePlainText;

    @JsonProperty("note_file_url")
    @Column(name = "note_file_url")
    private String noteFileUrl;

    @JsonProperty("note_likes_count")
    @Column(name = "note_likes_count")
    private Integer noteLikesCount = 0;

    @JsonProperty("note_comments_count")
    @Column(name = "note_comments_count")
    private Integer noteCommentsCount = 0;

    @JsonProperty("note_comments_likes_count")
    @Column(name = "note_comments_likes_count")
    private Integer noteCommentsLikesCount = 0;

    @JsonProperty("note_bookmarks_count")
    @Column(name = "note_bookmarks_count")
    private Integer noteBookmarksCount = 0;

    // 💡 [유지] DB 저장 시 현재 시각 자동 삽입
    @CreationTimestamp
    @JsonProperty("note_create_date")
    @Column(name = "note_create_date", nullable = false)
    private LocalDateTime noteCreateDate;
}