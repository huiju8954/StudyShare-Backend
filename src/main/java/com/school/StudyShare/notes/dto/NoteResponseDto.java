package com.school.StudyShare.notes.dto;

import com.school.StudyShare.notes.entity.Note;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter; // 💡 날짜 포맷팅을 위해 추가

@Getter
@NoArgsConstructor
public class NoteResponseDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("note_title")
    private String title;

    @JsonProperty("note_subject_id")
    private Integer noteSubjectId;

    @JsonProperty("note_content")
    private String noteContent;

    @JsonProperty("note_file_url")
    private String noteFileUrl;

    // 💡 [수정] 프론트엔드 키 이름(snake_case)과 맞춤
    @JsonProperty("note_likes_count")
    private Integer likesCount;

    @JsonProperty("note_comments_count")
    private Integer commentsCount;

    @JsonProperty("note_comments_likes_count")
    private Integer commentsLikesCount;

    // 🚨 [핵심 수정] LocalDateTime -> String으로 변경!
    // 이렇게 해야 프론트에서 오류 없이 받습니다.
    @JsonProperty("note_create_date")
    private String createDate;

    @JsonProperty("note_bookmarks_count")
    private Integer bookmarksCount;

    // 💡 좋아요/북마크 상태
    @JsonProperty("isLiked")
    private boolean isLiked;

    @JsonProperty("isBookmarked")
    private boolean isBookmarked;

    // 생성자
    public NoteResponseDto(Note note, boolean isLiked, boolean isBookmarked) {
        this.id = note.getId();
        this.userId = note.getNoteUserId();
        this.title = note.getNoteTitle();
        this.noteSubjectId = note.getNoteSubjectId();
        this.noteContent = note.getNoteContent();
        this.noteFileUrl = note.getNoteFileUrl();
        this.likesCount = note.getNoteLikesCount();
        this.commentsCount = note.getNoteCommentsCount();
        this.commentsLikesCount = note.getNoteCommentsLikesCount();
        this.bookmarksCount = note.getNoteBookmarksCount();
        if (this.bookmarksCount == null) {
            this.bookmarksCount = 0;
        }

        // 🚨 [핵심 로직] 날짜를 "2024-12-04 10:30:00" 형식의 문자열로 변환
        if (note.getNoteCreateDate() != null) {
            this.createDate = note.getNoteCreateDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } else {
            this.createDate = "";
        }

        this.isLiked = isLiked;
        this.isBookmarked = isBookmarked;
    }
}