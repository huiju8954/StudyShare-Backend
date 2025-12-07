package com.school.StudyShare.comments.dto;

import com.school.StudyShare.comments.entity.Comment;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {
    private Long id;
    private Integer userId;
    private String content;
    private Long noteId;
    private Long communityId;
    private Long parentCommentId;
    private LocalDateTime createDate;

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.userId = comment.getUserId();
        this.content = comment.getContent();
        this.createDate = comment.getCreateDate();
        this.parentCommentId = comment.getParentCommentId();

        if (comment.getNote() != null) {
            this.noteId = comment.getNote().getId();
        }
        if (comment.getCommunity() != null) {
            this.communityId = comment.getCommunity().getId();
        }
    }
}