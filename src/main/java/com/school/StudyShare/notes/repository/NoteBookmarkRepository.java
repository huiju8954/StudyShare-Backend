package com.school.StudyShare.notes.repository;

import com.school.StudyShare.notes.entity.Note;
import com.school.StudyShare.notes.entity.NoteBookmark;
import com.school.StudyShare.member.entity.Member; // ⭐️ [추가] Member 임포트
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteBookmarkRepository extends JpaRepository<NoteBookmark, Long> {
    // ⭐️ [수정] userId 대신 Member 객체를 받도록 변경
    Optional<NoteBookmark> findByNoteAndMember(Note note, Member member);
    boolean existsByNoteAndMember(Note note, Member member);

    // ⭐️ [수정] findByUserId 대신 findByMember_Id를 사용해야 정확함 (Long 타입)
    List<NoteBookmark> findByMember_Id(Long memberId);
}