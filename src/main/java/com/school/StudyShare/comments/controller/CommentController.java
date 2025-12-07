package com.school.StudyShare.comments.controller;

import com.school.StudyShare.comments.dto.CommentRequestDto;
import com.school.StudyShare.comments.dto.CommentResponseDto;
import com.school.StudyShare.comments.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    // 임시 유저 ID
    private Integer getCurrentUserId() {
        return 1;
    }

    // 1. 댓글 작성 [POST] /api/v1/comments
    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(@RequestBody CommentRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(dto, getCurrentUserId()));
    }

    // 2. 특정 노트의 댓글 조회 [GET] /api/v1/comments/note/{noteId}
    @GetMapping("/note/{noteId}")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByNoteId(@PathVariable Long noteId) {
        return ResponseEntity.ok(commentService.getCommentsByNoteId(noteId));
    }

    // 3. 특정 커뮤니티 글의 댓글 조회 [GET] /api/v1/comments/community/{communityId}
    @GetMapping("/community/{communityId}")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByCommunityId(@PathVariable Long communityId) {
        return ResponseEntity.ok(commentService.getCommentsByCommunityId(communityId));
    }
}