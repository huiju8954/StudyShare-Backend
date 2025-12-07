package com.school.StudyShare.comments.service;

import com.school.StudyShare.comments.dto.CommentRequestDto;
import com.school.StudyShare.comments.dto.CommentResponseDto;
import com.school.StudyShare.comments.entity.Comment;
import com.school.StudyShare.comments.repository.CommentRepository;
import com.school.StudyShare.community.entity.Community;
import com.school.StudyShare.community.repository.CommunityRepository;
import com.school.StudyShare.notes.entity.Note;
import com.school.StudyShare.notes.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final NoteRepository noteRepository;
    private final CommunityRepository communityRepository;

    // 댓글 작성
    @Transactional
    public CommentResponseDto createComment(CommentRequestDto dto, Integer userId) {
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setContent(dto.getContent());
        comment.setParentCommentId(dto.getParentCommentId()); // 부모 댓글 ID 설정

        // 1. 노트 댓글인 경우
        if (dto.getNoteId() != null) {
            Note note = noteRepository.findById(dto.getNoteId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 노트가 없습니다."));

            comment.setNote(note);

            // ✅ [통합된 핵심 기능] 노트의 댓글 수 증가 및 저장
            int currentCount = note.getNoteCommentsCount() == null ? 0 : note.getNoteCommentsCount();
            note.setNoteCommentsCount(currentCount + 1);
            noteRepository.save(note);
        }
        // 2. 커뮤니티 댓글인 경우
        else if (dto.getCommunityId() != null) {
            Community community = communityRepository.findById(dto.getCommunityId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));

            comment.setCommunity(community);

            // ✅ [통합된 핵심 기능] 커뮤니티의 댓글 수 증가 및 저장
            // Note: Community 엔티티에 getCommentCount()와 setCommentCount()가 구현되어 있어야 합니다.
            int currentCount = community.getCommentCount() == null ? 0 : community.getCommentCount();
            community.setCommentCount(currentCount + 1);
            communityRepository.save(community);
        } else {
            throw new IllegalArgumentException("noteId 또는 communityId 중 하나는 필수입니다.");
        }

        Comment savedComment = commentRepository.save(comment);
        return new CommentResponseDto(savedComment);
    }

    // 노트별 댓글 조회
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getCommentsByNoteId(Long noteId) {
        return commentRepository.findByNoteId(noteId).stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }

    // 커뮤니티별 댓글 조회
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getCommentsByCommunityId(Long communityId) {
        return commentRepository.findByCommunityId(communityId).stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }
}