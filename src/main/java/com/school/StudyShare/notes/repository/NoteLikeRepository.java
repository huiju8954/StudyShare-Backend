package com.school.StudyShare.notes.repository;

import com.school.StudyShare.notes.entity.Note;
import com.school.StudyShare.notes.entity.NoteLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteLikeRepository extends JpaRepository<NoteLike, Long> {
    Optional<NoteLike> findByNoteAndUserId(Note note, Integer userId);
    boolean existsByNoteAndUserId(Note note, Integer userId);

    // 👇 [추가]
    List<NoteLike> findByUserId(Integer userId);
}