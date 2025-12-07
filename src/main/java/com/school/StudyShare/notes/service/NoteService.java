package com.school.StudyShare.notes.service;

import com.school.StudyShare.notes.dto.NoteCreateRequestDto;
import com.school.StudyShare.notes.dto.NoteResponseDto;
import com.school.StudyShare.notes.dto.NoteUpdateRequestDto;
import com.school.StudyShare.notes.entity.Note;
import com.school.StudyShare.notes.entity.NoteBookmark;
import com.school.StudyShare.notes.entity.NoteLike;
import com.school.StudyShare.notes.repository.NoteBookmarkRepository;
import com.school.StudyShare.notes.repository.NoteLikeRepository;
import com.school.StudyShare.notes.repository.NoteRepository;
import com.school.StudyShare.member.entity.Member;
import com.school.StudyShare.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final NoteLikeRepository noteLikeRepository;
    private final NoteBookmarkRepository noteBookmarkRepository;
    private final MemberRepository memberRepository;

    /**
     * 노트 생성 (시그니처 일치)
     */
    @Transactional
    public NoteResponseDto createNote(NoteCreateRequestDto dto, Integer userId) {
        Note note = new Note();
        note.setNoteUserId(userId);
        note.setNoteTitle(dto.getTitle());
        note.setNoteSubjectId(dto.getNoteSubjectId());
        note.setNoteContent(dto.getNoteContent());
        note.setNoteFileUrl(dto.getNoteFileUrl());
        note.setNoteLikesCount(0);
        note.setNoteCommentsCount(0);
        note.setNoteCommentsLikesCount(0);

        String plainText = Jsoup.parse(dto.getNoteContent()).text();
        note.setNotePlainText(plainText);

        note.setNoteFileUrl(dto.getNoteFileUrl());

        Note savedNote = noteRepository.save(note);
        return new NoteResponseDto(savedNote, false, false);
    }

    /**
     * 노트 수정 (시그니처 일치)
     */
    @Transactional
    public NoteResponseDto updateNote(Long noteId, NoteUpdateRequestDto dto, Integer userId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("노트 없음 id=" + noteId));

        if (!note.getNoteUserId().equals(userId)) {
            throw new SecurityException("권한 없음");
        }

        note.setNoteTitle(dto.getTitle());
        note.setNoteSubjectId(dto.getNoteSubjectId());
        note.setNoteContent(dto.getNoteContent());
        note.setNoteFileUrl(dto.getNoteFileUrl());

        String plainText = Jsoup.parse(dto.getNoteContent()).text();
        note.setNotePlainText(plainText);

        note.setNoteFileUrl(dto.getNoteFileUrl());

        Note updatedNote = noteRepository.save(note);

        // JPA 관계 수정 반영
        Long memberId = userId.longValue();
        Member member = memberRepository.findById(memberId).orElse(null);

        boolean isLiked = (member != null) && noteLikeRepository.existsByNoteAndMember(updatedNote, member);
        boolean isBookmarked = (member != null) && noteBookmarkRepository.existsByNoteAndMember(updatedNote, member);

        return new NoteResponseDto(updatedNote, isLiked, isBookmarked);
    }

    /**
     * 노트 삭제 (시그니처 일치)
     */
    @Transactional
    public void deleteNote(Long noteId, Integer userId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("노트 없음 id=" + noteId));
        if (!note.getNoteUserId().equals(userId)) {
            throw new SecurityException("권한 없음");
        }
        noteRepository.delete(note);
    }

    // (toggleLike, toggleBookmark 및 나머지 조회 메서드는 이전 답변의 최종 코드를 그대로 사용합니다.)

    @Transactional(readOnly = true)
    public List<NoteResponseDto> searchNotes(String keyword, Integer userId) {
        List<Note> notes = noteRepository.findByNoteTitleContainingOrNotePlainTextContainingOrderByNoteCreateDateDesc(keyword, keyword);

        Long memberId = (userId != null) ? userId.longValue() : null;
        Member member = (memberId != null) ? memberRepository.findById(memberId).orElse(null) : null;

        return notes.stream()
                .map(note -> {
                    boolean isLiked = (member != null) && noteLikeRepository.existsByNoteAndMember(note, member);
                    boolean isBookmarked = (member != null) && noteBookmarkRepository.existsByNoteAndMember(note, member);
                    return new NoteResponseDto(note, isLiked, isBookmarked);
                })
                .collect(Collectors.toList());
    }

    // 💡 좋아요 토글 로직 (안정화 적용 + Member 객체 사용)
    @Transactional
    public void toggleLike(Long noteId, Integer userId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("노트 없음"));

        Long memberId = userId.longValue();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음 ID=" + userId));

        Optional<NoteLike> likeOptional = noteLikeRepository.findByNoteAndMember(note, member);

        if (likeOptional.isPresent()) {
            noteLikeRepository.delete(likeOptional.get());
            if (note.getNoteLikesCount() > 0) note.setNoteLikesCount(note.getNoteLikesCount() - 1);
            noteRepository.save(note);
        } else {
            noteRepository.save(note);
            noteLikeRepository.save(new NoteLike(note, member));
            note.setNoteLikesCount(note.getNoteLikesCount() + 1);
            noteRepository.save(note);
        }
    }

    // ✅ 북마크 토글 로직 (안정화 적용 + Member 객체 사용)
    @Transactional
    public void toggleBookmark(Long noteId, Integer userId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("노트 없음"));

        Long memberId = userId.longValue();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음 ID=" + userId));

        Optional<NoteBookmark> bookmarkOptional = noteBookmarkRepository.findByNoteAndMember(note, member);

        if (bookmarkOptional.isPresent()) {
            noteBookmarkRepository.delete(bookmarkOptional.get());

            if (note.getNoteBookmarksCount() != null && note.getNoteBookmarksCount() > 0) {
                note.setNoteBookmarksCount(note.getNoteBookmarksCount() - 1);
            }
            noteRepository.save(note);
        } else {
            noteRepository.save(note);
            noteBookmarkRepository.save(new NoteBookmark(note, member));

            if (note.getNoteBookmarksCount() == null) {
                note.setNoteBookmarksCount(1);
            } else {
                note.setNoteBookmarksCount(note.getNoteBookmarksCount() + 1);
            }
            noteRepository.save(note);
        }
    }

    // ⭐️ [이하 모든 조회 메서드 유지]

    @Transactional(readOnly = true)
    public List<NoteResponseDto> getBookmarkedNotes(Integer userId) {
        Long memberId = userId.longValue();
        List<NoteBookmark> bookmarks = noteBookmarkRepository.findByMember_Id(memberId);

        Member currentMember = memberRepository.findById(memberId).orElse(null);

        return bookmarks.stream()
                .map(bookmark -> {
                    Note note = bookmark.getNote();
                    boolean isLiked = (currentMember != null) && noteLikeRepository.existsByNoteAndMember(note, currentMember);
                    return new NoteResponseDto(note, isLiked, true);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NoteResponseDto> getLikedNotes(Integer userId) {
        Long memberId = userId.longValue();
        List<NoteLike> likes = noteLikeRepository.findByMember_Id(memberId);

        Member currentMember = memberRepository.findById(memberId).orElse(null);

        return likes.stream()
                .map(like -> {
                    Note note = like.getNote();
                    boolean isBookmarked = (currentMember != null) && noteBookmarkRepository.existsByNoteAndMember(note, currentMember);
                    return new NoteResponseDto(note, true, isBookmarked);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NoteResponseDto> getAllNotes(Integer userId) {
        Long memberId = (userId != null) ? userId.longValue() : null;
        Member member = (memberId != null) ? memberRepository.findById(memberId).orElse(null) : null;

        return noteRepository.findAllByOrderByNoteCreateDateDesc().stream()
                .map(note -> {
                    boolean isLiked = (member != null) && noteLikeRepository.existsByNoteAndMember(note, member);
                    boolean isBookmarked = (member != null) && noteBookmarkRepository.existsByNoteAndMember(note, member);
                    return new NoteResponseDto(note, isLiked, isBookmarked);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NoteResponseDto> getAllNotes() {
        return getAllNotes(null);
    }

    @Transactional(readOnly = true)
    public NoteResponseDto getNoteById(Long noteId, Integer userId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("노트 없음"));

        Long memberId = (userId != null) ? userId.longValue() : null;
        Member member = (memberId != null) ? memberRepository.findById(memberId).orElse(null) : null;

        boolean isLiked = (member != null) && noteLikeRepository.existsByNoteAndMember(note, member);
        boolean isBookmarked = (member != null) && noteBookmarkRepository.existsByNoteAndMember(note, member);
        return new NoteResponseDto(note, isLiked, isBookmarked);
    }

    @Transactional(readOnly = true)
    public List<NoteResponseDto> getNotesByUserId(Integer targetUserId, Integer currentUserId) {
        Long memberId = (currentUserId != null) ? currentUserId.longValue() : null;
        Member member = (memberId != null) ? memberRepository.findById(memberId).orElse(null) : null;

        return noteRepository.findByNoteUserId(targetUserId).stream()
                .map(note -> {
                    boolean isLiked = (member != null) && noteLikeRepository.existsByNoteAndMember(note, member);
                    boolean isBookmarked = (member != null) && noteBookmarkRepository.existsByNoteAndMember(note, member);
                    return new NoteResponseDto(note, isLiked, isBookmarked);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NoteResponseDto> getNotesByUserId(Integer userId) {
        return getNotesByUserId(userId, null);
    }
}