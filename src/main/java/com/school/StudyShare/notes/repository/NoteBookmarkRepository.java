package com.school.StudyShare.notes.repository;

import com.school.StudyShare.notes.entity.Note;
import com.school.StudyShare.notes.entity.NoteBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteBookmarkRepository extends JpaRepository<NoteBookmark, Long> {
    Optional<NoteBookmark> findByNoteAndUserId(Note note, Integer userId);
    boolean existsByNoteAndUserId(Note note, Integer userId);

    // 👇 [추가] 특정 유저가 북마크한 모든 데이터 찾기
    // (NoteBookmark 엔티티 안에 userId 필드명이 'userId'라고 가정.
    // 만약 엔티티에 User 객체가 없고 userId 숫자만 있다면 필드명을 확인해야 합니다.
    // 보통은 List<NoteBookmark> findByUserId(Integer userId); 입니다.)
    List<NoteBookmark> findByUserId(Integer userId);
}