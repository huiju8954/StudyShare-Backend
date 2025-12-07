package com.school.StudyShare.community.repository;

import com.school.StudyShare.community.entity.Community;
import com.school.StudyShare.community.entity.CommunityLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {

    /**
     * 특정 게시글에 대해 특정 유저가 좋아요를 눌렀는지 확인하고, 해당 엔티티를 반환합니다.
     * @param community 좋아요 대상 게시글 엔티티
     * @param userId 좋아요를 누른 사용자 ID
     * @return CommunityLike 엔티티 (존재하지 않으면 Optional.empty())
     */
    Optional<CommunityLike> findByCommunityAndUserId(Community community, Integer userId);

    /**
     * 특정 게시글에 대해 특정 유저가 좋아요를 눌렀는지의 존재 여부를 확인합니다.
     * @param community 좋아요 대상 게시글 엔티티
     * @param userId 좋아요를 누른 사용자 ID
     * @return 존재 여부 (true/false)
     */
    boolean existsByCommunityAndUserId(Community community, Integer userId);

    /**
     * 💡 [필수 추가] 특정 유저가 좋아요를 누른 모든 목록을 조회합니다.
     * @param userId 좋아요를 누른 사용자 ID
     * @return 해당 사용자가 좋아요 누른 CommunityLike 엔티티 목록
     */
    List<CommunityLike> findByUserId(Integer userId);
}