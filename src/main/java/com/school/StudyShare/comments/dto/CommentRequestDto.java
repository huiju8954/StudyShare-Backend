package com.school.StudyShare.comments.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentRequestDto {
    private String content;      // 댓글 내용
    private Long noteId;         // (선택) 노트에 달 거면 이거 보냄
    private Long communityId;    // (선택) 커뮤니티에 달 거면 이거 보냄
    private Long parentCommentId;// (선택) 대댓글이면 부모 ID 보냄
}