package com.school.StudyShare.comments.repository;

import com.school.StudyShare.comments.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // ^^^^ 여기가 제일 중요합니다! (extends JpaRepository...)
    // 이게 있어야 .save() 기능이 생깁니다.

    // 1. 특정 노트의 댓글들 찾기
    List<Comment> findByNoteId(Long noteId);

    // 2. 특정 커뮤니티 글의 댓글들 찾기
    List<Comment> findByCommunityId(Long communityId);
}