package com.school.StudyShare.notes.repository;

import com.school.StudyShare.notes.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    // JpaRepository가 기본 CRUD (save, findById, findAll, deleteById)를 제공합니다.

    // 💡 최신순으로 모든 노트 조회 (ORDER BY createDate DESC)
    List<Note> findAllByOrderByNoteCreateDateDesc();

    // 💡 유저 ID로 모든 노트 찾기
    List<Note> findByNoteUserId(Integer userId);

    // 💡 과목 ID로 모든 노트 찾기
    List<Note> findByNoteSubjectId(Integer noteSubjectId);

    // ✅ [추가] 제목 또는 '순수 텍스트' 내용에서 검색 (최신순 정렬)
    // 이 메서드는 NoteService의 searchNotes 기능을 지원합니다.
    List<Note> findByNoteTitleContainingOrNotePlainTextContainingOrderByNoteCreateDateDesc(String title, String plainText);
}