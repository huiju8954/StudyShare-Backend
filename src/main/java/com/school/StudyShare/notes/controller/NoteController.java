package com.school.StudyShare.notes.controller;

import com.school.StudyShare.notes.dto.NoteCreateRequestDto;
import com.school.StudyShare.notes.dto.NoteResponseDto;
import com.school.StudyShare.notes.dto.NoteUpdateRequestDto;
import com.school.StudyShare.notes.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    // 💡 임시 사용자 ID (모든 CRUD 및 좋아요 요청에 사용됨)
    private Integer getCurrentUserId() {
        return 1;
    }

    /**
     * 노트 생성
     * [POST] /notes
     */
    @PostMapping
    public ResponseEntity<NoteResponseDto> createNote(@RequestBody NoteCreateRequestDto requestDto) {
        Integer userId = getCurrentUserId();
        // ⭐️ Service 시그니처와 일치함: noteService.createNote(DTO, Integer)
        NoteResponseDto responseDto = noteService.createNote(requestDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 노트 수정
     * [PUT] /notes/{noteId}
     */
    @PutMapping("/{noteId}")
    public ResponseEntity<NoteResponseDto> updateNote(@PathVariable Long noteId,
                                                      @RequestBody NoteUpdateRequestDto requestDto) {
        Integer userId = getCurrentUserId();
        // ⭐️ Service 시그니처와 일치함: noteService.updateNote(Long, DTO, Integer)
        NoteResponseDto responseDto = noteService.updateNote(noteId, requestDto, userId);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 노트 삭제
     * [DELETE] /notes/{noteId}
     */
    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long noteId) {
        Integer userId = getCurrentUserId();
        // ⭐️ Service 시그니처와 일치함: noteService.deleteNote(Long, Integer)
        noteService.deleteNote(noteId, userId);
        return ResponseEntity.noContent().build();
    }

    // ✅ [통합] 좋아요 토글 API
    @PostMapping("/{id}/like")
    public ResponseEntity<String> toggleLike(@PathVariable Long id, @RequestParam Integer userId) {
        noteService.toggleLike(id, userId);
        return ResponseEntity.ok("좋아요 변경 완료");
    }

    // ✅ [통합] 북마크 토글 API
    @PostMapping("/{id}/bookmark")
    public ResponseEntity<String> toggleBookmark(@PathVariable Long id, @RequestParam Integer userId) {
        noteService.toggleBookmark(id, userId);
        return ResponseEntity.ok("북마크 변경 완료");
    }

    /**
     * 모든 노트 조회
     * [GET] /notes
     */
    @GetMapping
    public ResponseEntity<List<NoteResponseDto>> getAllNotes(@RequestParam(required = false) Integer userId) {
        List<NoteResponseDto> notes = noteService.getAllNotes(userId);
        return ResponseEntity.ok(notes);
    }

    /**
     * 특정 노트 1개 조회
     * [GET] /notes/{noteId}
     */
    @GetMapping("/{noteId}")
    public ResponseEntity<NoteResponseDto> getNoteById(@PathVariable Long noteId,
                                                       @RequestParam(required = false) Integer userId) {
        NoteResponseDto note = noteService.getNoteById(noteId, userId);
        return ResponseEntity.ok(note);
    }

    /**
     * 특정 사용자(ID)의 모든 노트 조회
     * [GET] /notes/user/{targetUserId}
     */
    @GetMapping("/user/{targetUserId}")
    public ResponseEntity<List<NoteResponseDto>> getNotesByUserId(@PathVariable Integer targetUserId,
                                                                  @RequestParam(required = false) Integer currentUserId) {
        List<NoteResponseDto> notes = noteService.getNotesByUserId(targetUserId, currentUserId);
        return ResponseEntity.ok(notes);
    }

    // ✅ [추가] 내가 북마크한 노트 목록 조회 API
    @GetMapping("/user/{userId}/bookmarks")
    public ResponseEntity<List<NoteResponseDto>> getBookmarkedNotes(@PathVariable Integer userId) {
        List<NoteResponseDto> notes = noteService.getBookmarkedNotes(userId);
        return ResponseEntity.ok(notes);
    }

    // ✅ [추가] 내가 좋아요한 노트 조회 API
    @GetMapping("/user/{userId}/likes")
    public ResponseEntity<List<NoteResponseDto>> getLikedNotes(@PathVariable Integer userId) {
        List<NoteResponseDto> notes = noteService.getLikedNotes(userId);
        return ResponseEntity.ok(notes);
    }

    // ✅ [추가] 검색 API
    @GetMapping("/search")
    public ResponseEntity<List<NoteResponseDto>> searchNotes(@RequestParam String keyword,
                                                             @RequestParam(required = false) Integer userId) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(noteService.searchNotes(keyword, userId));
    }
}