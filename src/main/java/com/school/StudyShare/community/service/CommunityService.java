package com.school.StudyShare.community.service;

import com.school.StudyShare.community.dto.CommunityCreateRequestDto;
import com.school.StudyShare.community.dto.CommunityResponseDto;
import com.school.StudyShare.community.dto.CommunityUpdateRequestDto;
import com.school.StudyShare.community.entity.Community;
import com.school.StudyShare.community.entity.CommunityLike;
import com.school.StudyShare.community.entity.CommunityBookmark;
import com.school.StudyShare.community.repository.CommunityRepository;
import com.school.StudyShare.community.repository.CommunityLikeRepository;
import com.school.StudyShare.community.repository.CommunityBookmarkRepository;

// ⭐️ [추가] Member 엔티티 관련 임포트
import com.school.StudyShare.member.entity.Member;
import com.school.StudyShare.member.repository.MemberRepository;

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
    private final CommunityLikeRepository communityLikeRepository;
    private final CommunityBookmarkRepository communityBookmarkRepository;
    private final MemberRepository memberRepository; // ⭐️ [추가] MemberRepository 주입

    // -------------------------------------------------------------
    // 💡 Helper Method for Member Conversion
    // -------------------------------------------------------------
    // Integer userId를 Long memberId로 변환 후 Member 객체 조회
    private Member getMember(Integer userId) {
        if (userId == null) return null;
        Long memberId = userId.longValue();
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID=" + userId));
    }

    // Integer userId를 Long memberId로 변환 후 Optional Member 객체 조회 (null 허용)
    private Optional<Member> getOptionalMember(Integer userId) {
        if (userId == null) return Optional.empty();
        Long memberId = userId.longValue();
        return memberRepository.findById(memberId);
    }

    // -------------------------------------------------------------
    // 💡 CRUD & Read Endpoints
    // -------------------------------------------------------------

    /**
     * 게시글 생성
     */
    @Transactional
    public CommunityResponseDto createPost(CommunityCreateRequestDto dto, Integer userId) {
        // ⭐️ userId를 사용 (변경 없음)
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
        return new CommunityResponseDto(savedPost, false, false);
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public CommunityResponseDto updatePost(Long postId, CommunityUpdateRequestDto dto, Integer userId) {
        Community post = communityRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다. id=" + postId));

        if (!post.getUserId().equals(userId)) {
            throw new SecurityException("게시글을 수정할 권한이 없습니다.");
        }

        post.setTitle(dto.getTitle());
        post.setCategory(dto.getCategory());
        post.setContent(dto.getContent());

        Community updatedPost = communityRepository.save(post);

        // ⭐️ [JPA 관계 수정] 수정 후에도 현재 유저의 상태를 체크해야 함
        Member member = getMember(userId);
        boolean isLiked = communityLikeRepository.existsByCommunityAndMember(updatedPost, member);
        boolean isBookmarked = communityBookmarkRepository.existsByCommunityAndMember(updatedPost, member);

        return new CommunityResponseDto(updatedPost, isLiked, isBookmarked);
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public void deletePost(Long postId, Integer userId) {
        Community post = communityRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다. id=" + postId));

        if (!post.getUserId().equals(userId)) {
            throw new SecurityException("게시글을 삭제할 권한이 없습니다.");
        }

        communityRepository.delete(post);
    }

    /**
     * 전체 게시글 조회 (로그인한 유저 기준 좋아요 여부 포함)
     */
    @Transactional(readOnly = true)
    public List<CommunityResponseDto> getAllPosts(Integer userId) {
        Optional<Member> memberOptional = getOptionalMember(userId);
        Member member = memberOptional.orElse(null);

        return communityRepository.findAllByOrderByCreateDateDesc().stream()
                .map(post -> {
                    // ⭐️ [JPA 관계 수정] existsByCommunityAndUserId -> existsByCommunityAndMember 사용
                    boolean isLiked = (member != null) && communityLikeRepository.existsByCommunityAndMember(post, member);
                    boolean isBookmarked = (member != null) && communityBookmarkRepository.existsByCommunityAndMember(post, member);
                    return new CommunityResponseDto(post, isLiked, isBookmarked);
                })
                .collect(Collectors.toList());
    }

    public List<CommunityResponseDto> getAllPosts() {
        return getAllPosts(null);
    }

    /**
     * 특정 게시글 1개 상세 조회
     */
    @Transactional(readOnly = true)
    public CommunityResponseDto getPostById(Long id, Integer userId) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        Optional<Member> memberOptional = getOptionalMember(userId);
        Member member = memberOptional.orElse(null);

        // ⭐️ [JPA 관계 수정] existsByCommunityAndUserId -> existsByCommunityAndMember 사용
        boolean isLiked = (member != null) && communityLikeRepository.existsByCommunityAndMember(community, member);
        boolean isBookmarked = (member != null) && communityBookmarkRepository.existsByCommunityAndMember(community, member);
        return new CommunityResponseDto(community, isLiked, isBookmarked);
    }

    /**
     * 카테고리별 조회
     */
    @Transactional(readOnly = true)
    public List<CommunityResponseDto> getPostsByCategory(String category, Integer userId) {
        Optional<Member> memberOptional = getOptionalMember(userId);
        Member member = memberOptional.orElse(null);

        return communityRepository.findByCategory(category).stream()
                .map(post -> {
                    // ⭐️ [JPA 관계 수정] existsByCommunityAndUserId -> existsByCommunityAndMember 사용
                    boolean isLiked = (member != null) && communityLikeRepository.existsByCommunityAndMember(post, member);
                    boolean isBookmarked = (member != null) && communityBookmarkRepository.existsByCommunityAndMember(post, member);
                    return new CommunityResponseDto(post, isLiked, isBookmarked);
                })
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------
    // 💡 User Activity & Interaction Endpoints (File 2)
    // -------------------------------------------------------------

    /**
     * 특정 사용자가 작성한 게시글 조회
     */
    @Transactional(readOnly = true)
    public List<CommunityResponseDto> getPostsByUserId(Integer targetUserId) {
        // targetUserId로 조회하지만, 상태는 targetUserId 기준으로 표시 (자기 글 조회라 가정)
        Member member = getMember(targetUserId);

        return communityRepository.findByUserId(targetUserId).stream()
                .map(post -> {
                    // ⭐️ [JPA 관계 수정] existsByCommunityAndUserId -> existsByCommunityAndMember 사용
                    boolean isLiked = communityLikeRepository.existsByCommunityAndMember(post, member);
                    boolean isBookmarked = communityBookmarkRepository.existsByCommunityAndMember(post, member);
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

        Member member = getMember(userId); // ⭐️ [수정] Member 객체 조회

        // ⭐️ [JPA 관계 수정] findByCommunityAndUserId -> findByCommunityAndMember 사용
        Optional<CommunityLike> likeOptional = communityLikeRepository.findByCommunityAndMember(community, member);

        if (likeOptional.isPresent()) {
            communityLikeRepository.delete(likeOptional.get());
            if (community.getLikesCount() > 0) community.setLikesCount(community.getLikesCount() - 1);
        } else {
            // ⭐️ [수정] CommunityLike 생성 시 Member 객체를 전달
            communityLikeRepository.save(new CommunityLike(community, member));
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

        Member member = getMember(userId); // ⭐️ [수정] Member 객체 조회

        // ⭐️ [JPA 관계 수정] findByCommunityAndUserId -> findByCommunityAndMember 사용
        Optional<CommunityBookmark> bookmarkOptional = communityBookmarkRepository.findByCommunityAndMember(community, member);

        if (bookmarkOptional.isPresent()) {
            communityBookmarkRepository.delete(bookmarkOptional.get());
            if (community.getBookmarksCount() > 0) community.setBookmarksCount(community.getBookmarksCount() - 1);
        } else {
            // ⭐️ [수정] CommunityBookmark 생성 시 Member 객체를 전달
            communityBookmarkRepository.save(new CommunityBookmark(community, member));
            if (community.getBookmarksCount() == null) community.setBookmarksCount(0);
            community.setBookmarksCount(community.getBookmarksCount() + 1);
        }
    }

    /**
     * 내가 좋아요한 글 조회
     */
    @Transactional(readOnly = true)
    public List<CommunityResponseDto> getLikedPosts(Integer userId) {
        // ⭐️ [수정] Member 객체 조회 (Repository 메소드 시그니처와 일치시키기 위함)
        Member member = getMember(userId);

        // ⭐️ [JPA 관계 수정] findByUserId -> findByMember_Id를 사용해야 함 (Long 타입)
        // CommunityLikeRepository는 findByMember_Id(Long memberId)를 사용해야 함.
        Long memberId = userId.longValue();

        return communityLikeRepository.findByMember_Id(memberId).stream()
                .map(like -> {
                    Community post = like.getCommunity();
                    // ⭐️ [JPA 관계 수정] existsByCommunityAndUserId -> existsByCommunityAndMember 사용
                    boolean isBookmarked = communityBookmarkRepository.existsByCommunityAndMember(post, member);
                    return new CommunityResponseDto(post, true, isBookmarked);
                })
                .collect(Collectors.toList());
    }

    /**
     * 내가 북마크한 글 조회
     */
    @Transactional(readOnly = true)
    public List<CommunityResponseDto> getBookmarkedPosts(Integer userId) {
        // ⭐️ [수정] Member 객체 조회
        Member member = getMember(userId);

        // ⭐️ [JPA 관계 수정] findByUserId -> findByMember_Id를 사용해야 함 (Long 타입)
        Long memberId = userId.longValue();

        return communityBookmarkRepository.findByMember_Id(memberId).stream()
                .map(bookmark -> {
                    Community post = bookmark.getCommunity();
                    // ⭐️ [JPA 관계 수정] existsByCommunityAndUserId -> existsByCommunityAndMember 사용
                    boolean isLiked = communityLikeRepository.existsByCommunityAndMember(post, member);
                    return new CommunityResponseDto(post, isLiked, true);
                })
                .collect(Collectors.toList());
    }
}