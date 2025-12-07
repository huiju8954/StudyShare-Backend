package com.school.StudyShare.community.service;

import com.school.StudyShare.community.dto.CommunityCreateRequestDto; // File 1 DTO
import com.school.StudyShare.community.dto.CommunityResponseDto;
import com.school.StudyShare.community.dto.CommunityUpdateRequestDto; // File 1 DTO
import com.school.StudyShare.community.entity.Community;
import com.school.StudyShare.community.entity.CommunityLike;
import com.school.StudyShare.community.entity.CommunityBookmark;
import com.school.StudyShare.community.repository.CommunityRepository;
import com.school.StudyShare.community.repository.CommunityLikeRepository;
import com.school.StudyShare.community.repository.CommunityBookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    // 💡 [통합] 좋아요/북마크 Repository 추가 (File 2)
    private final CommunityLikeRepository communityLikeRepository;
    private final CommunityBookmarkRepository communityBookmarkRepository;

    // -------------------------------------------------------------
    // 💡 CRUD & Read Endpoints
    // -------------------------------------------------------------

    /**
     * 게시글 생성
     * File 1의 CommunityCreateRequestDto를 사용하도록 통합합니다.
     */
    @Transactional
    public CommunityResponseDto createPost(CommunityCreateRequestDto dto, Integer userId) {
        Community community = new Community();
        community.setUserId(userId);
        community.setTitle(dto.getTitle());
        community.setCategory(dto.getCategory());
        community.setContent(dto.getContent());

        community.setLikesCount(0);
        community.setBookmarksCount(0);
        community.setCommentCount(0);
        community.setCommentLikeCount(0);

        Community savedPost = communityRepository.save(community);
        // 생성 시에는 좋아요/북마크 상태는 항상 false
        return new CommunityResponseDto(savedPost, false, false);
    }

    /**
     * 게시글 수정
     * 💡 [통합] File 1의 보안 검사 로직 추가
     */
    @Transactional
    public CommunityResponseDto updatePost(Long postId, CommunityUpdateRequestDto dto, Integer userId) {
        // 1. 게시글 조회
        Community post = communityRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다. id=" + postId));

        // 2. (보안) 작성자 ID와 현재 로그인한 사용자 ID가 같은지 확인
        if (!post.getUserId().equals(userId)) {
            throw new SecurityException("게시글을 수정할 권한이 없습니다.");
        }

        // 3. DTO의 정보로 엔티티 필드 업데이트
        post.setTitle(dto.getTitle());
        post.setCategory(dto.getCategory());
        post.setContent(dto.getContent());

        Community updatedPost = communityRepository.save(post);

        // 수정 시에는 좋아요/북마크 상태를 다시 체크할 필요 없이 기본값으로 응답
        return new CommunityResponseDto(updatedPost, false, false);
    }

    /**
     * 게시글 삭제
     * 💡 [통합] File 1의 보안 검사 로직 추가
     */
    @Transactional
    public void deletePost(Long postId, Integer userId) {
        // 1. 게시글 조회
        Community post = communityRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다. id=" + postId));

        // 2. (보안) 작성자와 로그인 유저가 같은지 확인
        if (!post.getUserId().equals(userId)) {
            throw new SecurityException("게시글을 삭제할 권한이 없습니다.");
        }

        // 3. 삭제
        communityRepository.delete(post);
    }

    /**
     * 전체 게시글 조회 (로그인한 유저 기준 좋아요 여부 포함)
     * File 2의 로직을 사용하여 좋아요/북마크 상태를 동적으로 포함합니다.
     */
    @Transactional(readOnly = true)
    public List<CommunityResponseDto> getAllPosts(Integer userId) {
        return communityRepository.findAllByOrderByCreateDateDesc().stream()
                .map(post -> {
                    // userId가 제공된 경우에만 좋아요/북마크 여부를 확인합니다.
                    boolean isLiked = (userId != null) && communityLikeRepository.existsByCommunityAndUserId(post, userId);
                    boolean isBookmarked = (userId != null) && communityBookmarkRepository.existsByCommunityAndUserId(post, userId);
                    return new CommunityResponseDto(post, isLiked, isBookmarked);
                })
                .collect(Collectors.toList());
    }

    // 오버로딩 (비로그인용) - Controller에서 userId를 null로 호출하는 데 사용
    public List<CommunityResponseDto> getAllPosts() {
        return getAllPosts(null);
    }

    /**
     * 특정 게시글 1개 상세 조회
     * File 2의 로직을 사용하여 좋아요/북마크 상태를 동적으로 포함합니다.
     */
    @Transactional(readOnly = true)
    public CommunityResponseDto getPostById(Long id, Integer userId) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));
        boolean isLiked = (userId != null) && communityLikeRepository.existsByCommunityAndUserId(community, userId);
        boolean isBookmarked = (userId != null) && communityBookmarkRepository.existsByCommunityAndUserId(community, userId);
        return new CommunityResponseDto(community, isLiked, isBookmarked);
    }

    /**
     * 카테고리별 조회
     * 💡 [통합] File 2의 카테고리별 조회 로직을 추가합니다.
     */
    @Transactional(readOnly = true)
    public List<CommunityResponseDto> getPostsByCategory(String category, Integer userId) {
        // Repository에 findByCategory 메서드가 있다고 가정
        return communityRepository.findByCategory(category).stream()
                .map(post -> {
                    boolean isLiked = (userId != null) && communityLikeRepository.existsByCommunityAndUserId(post, userId);
                    boolean isBookmarked = (userId != null) && communityBookmarkRepository.existsByCommunityAndUserId(post, userId);
                    return new CommunityResponseDto(post, isLiked, isBookmarked);
                })
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------
    // 💡 User Activity & Interaction Endpoints (File 2)
    // -------------------------------------------------------------

    /**
     * 특정 사용자가 작성한 게시글 조회
     * File 2의 로직을 사용하여 좋아요/북마크 상태를 포함합니다.
     */
    @Transactional(readOnly = true)
    public List<CommunityResponseDto> getPostsByUserId(Integer userId) {
        // Repository에 findByUserId 메서드가 있다고 가정
        return communityRepository.findByUserId(userId).stream()
                .map(post -> {
                    boolean isLiked = communityLikeRepository.existsByCommunityAndUserId(post, userId);
                    boolean isBookmarked = communityBookmarkRepository.existsByCommunityAndUserId(post, userId);
                    return new CommunityResponseDto(post, isLiked, isBookmarked);
                })
                .collect(Collectors.toList());
    }

    /**
     * 좋아요/좋아요 취소 토글
     */
    @Transactional
    public void toggleLike(Long communityId, Integer userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        Optional<CommunityLike> likeOptional = communityLikeRepository.findByCommunityAndUserId(community, userId);

        if (likeOptional.isPresent()) {
            communityLikeRepository.delete(likeOptional.get());
            if (community.getLikesCount() > 0) community.setLikesCount(community.getLikesCount() - 1);
        } else {
            communityLikeRepository.save(new CommunityLike(community, userId));
            community.setLikesCount(community.getLikesCount() + 1);
        }
    }

    /**
     * 북마크/북마크 취소 토글
     */
    @Transactional
    public void toggleBookmark(Long communityId, Integer userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        Optional<CommunityBookmark> bookmarkOptional = communityBookmarkRepository.findByCommunityAndUserId(community, userId);

        if (bookmarkOptional.isPresent()) {
            communityBookmarkRepository.delete(bookmarkOptional.get());
            if (community.getBookmarksCount() > 0) community.setBookmarksCount(community.getBookmarksCount() - 1);
        } else {
            communityBookmarkRepository.save(new CommunityBookmark(community, userId));
            if (community.getBookmarksCount() == null) community.setBookmarksCount(0); // null 방지
            community.setBookmarksCount(community.getBookmarksCount() + 1);
        }
    }

    /**
     * 내가 좋아요한 글 조회
     */
    @Transactional(readOnly = true)
    public List<CommunityResponseDto> getLikedPosts(Integer userId) {
        return communityLikeRepository.findByUserId(userId).stream()
                .map(like -> {
                    Community post = like.getCommunity();
                    // 북마크 상태 포함
                    boolean isBookmarked = communityBookmarkRepository.existsByCommunityAndUserId(post, userId);
                    return new CommunityResponseDto(post, true, isBookmarked); // isLiked는 항상 true
                })
                .collect(Collectors.toList());
    }

    /**
     * 내가 북마크한 글 조회
     */
    @Transactional(readOnly = true)
    public List<CommunityResponseDto> getBookmarkedPosts(Integer userId) {
        return communityBookmarkRepository.findByUserId(userId).stream()
                .map(bookmark -> {
                    Community post = bookmark.getCommunity();
                    // 좋아요 상태 포함
                    boolean isLiked = communityLikeRepository.existsByCommunityAndUserId(post, userId);
                    return new CommunityResponseDto(post, isLiked, true); // isBookmarked는 항상 true
                })
                .collect(Collectors.toList());
    }
}