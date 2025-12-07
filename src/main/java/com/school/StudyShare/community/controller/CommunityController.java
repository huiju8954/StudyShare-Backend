package com.school.StudyShare.community.controller;

import com.school.StudyShare.community.dto.CommunityCreateRequestDto; // File 1에서 사용된 DTO
import com.school.StudyShare.community.dto.CommunityResponseDto;
import com.school.StudyShare.community.dto.CommunityUpdateRequestDto; // File 1에서 사용된 DTO
import com.school.StudyShare.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/communities")
public class CommunityController {

    private final CommunityService communityService;

    // -------------------------------------------------------------
    // 💡 CRUD Endpoints
    // -------------------------------------------------------------

    /**
     * 게시글 생성
     * [POST] /communities
     * File 1의 DTO를 사용하고, File 2의 하드코딩된 user ID (1)를 사용합니다.
     */
    @PostMapping
    public ResponseEntity<CommunityResponseDto> createPost(@RequestBody CommunityCreateRequestDto requestDto) {
        // 임시 user ID 1을 전달합니다. (인증 구현 시 @AuthenticationPrincipal로 대체 필요)
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(communityService.createPost(requestDto, 1));
    }

    /**
     * 게시글 수정
     * [PUT] /communities/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<CommunityResponseDto> updatePost(@PathVariable Long id,
                                                           @RequestBody CommunityUpdateRequestDto requestDto) {
        // 임시 user ID 1을 전달합니다.
        return ResponseEntity.ok(communityService.updatePost(id, requestDto, 1));
    }

    /**
     * 게시글 삭제
     * [DELETE] /communities/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        // 임시 user ID 1을 전달합니다.
        communityService.deletePost(id, 1);
        return ResponseEntity.noContent().build();
    }

    /**
     * 전체 게시글 조회
     * [GET] /communities?userId={userId}
     * userId를 optional param으로 받아 좋아요/북마크 상태를 함께 조회합니다.
     */
    @GetMapping
    public ResponseEntity<List<CommunityResponseDto>> getAllPosts(@RequestParam(required = false) Integer userId) {
        return ResponseEntity.ok(communityService.getAllPosts(userId));
    }

    /**
     * 카테고리별 조회
     * [GET] /communities/category/{categoryName}?userId={userId}
     */
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<CommunityResponseDto>> getPostsByCategory(@PathVariable String categoryName,
                                                                         @RequestParam(required = false) Integer userId) {
        return ResponseEntity.ok(communityService.getPostsByCategory(categoryName, userId));
    }


    /**
     * 특정 게시글 1개 상세 조회
     * [GET] /communities/{id}?userId={userId}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommunityResponseDto> getPostById(@PathVariable Long id,
                                                            @RequestParam(required = false) Integer userId) {
        return ResponseEntity.ok(communityService.getPostById(id, userId));
    }

    // -------------------------------------------------------------
    // 💡 User Activity Endpoints (File 2 기반)
    // -------------------------------------------------------------

    /**
     * 특정 사용자(ID)의 작성 게시글 목록 조회
     * [GET] /communities/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommunityResponseDto>> getPostsByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(communityService.getPostsByUserId(userId));
    }

    /**
     * 내가 좋아요한 글 목록 조회
     * [GET] /communities/user/{userId}/likes
     */
    @GetMapping("/user/{userId}/likes")
    public ResponseEntity<List<CommunityResponseDto>> getLikedPosts(@PathVariable Integer userId) {
        return ResponseEntity.ok(communityService.getLikedPosts(userId));
    }

    /**
     * 내가 북마크한 글 목록 조회
     * [GET] /communities/user/{userId}/bookmarks
     */
    @GetMapping("/user/{userId}/bookmarks")
    public ResponseEntity<List<CommunityResponseDto>> getBookmarkedPosts(@PathVariable Integer userId) {
        return ResponseEntity.ok(communityService.getBookmarkedPosts(userId));
    }

    // -------------------------------------------------------------
    // 💡 Interaction Endpoints (File 2 기반)
    // -------------------------------------------------------------

    /**
     * 좋아요/좋아요 취소 토글
     * [POST] /communities/{id}/like?userId={userId}
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<String> toggleLike(@PathVariable Long id, @RequestParam Integer userId) {
        communityService.toggleLike(id, userId);
        return ResponseEntity.ok("좋아요 변경 완료");
    }

    /**
     * 북마크/북마크 취소 토글
     * [POST] /communities/{id}/bookmark?userId={userId}
     */
    @PostMapping("/{id}/bookmark")
    public ResponseEntity<String> toggleBookmark(@PathVariable Long id, @RequestParam Integer userId) {
        communityService.toggleBookmark(id, userId);
        return ResponseEntity.ok("북마크 변경 완료");
    }
}