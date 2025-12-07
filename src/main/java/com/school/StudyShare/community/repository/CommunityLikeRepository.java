package com.school.StudyShare.community.repository;

import com.school.StudyShare.community.entity.Community;
import com.school.StudyShare.community.entity.CommunityLike;
import com.school.StudyShare.member.entity.Member; // ⭐️ [추가] Member 엔티티 임포트
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {

    // ⭐️ [수정] findByCommunityAndUserId -> findByCommunityAndMember로 변경 (Service와 일치)
    Optional<CommunityLike> findByCommunityAndMember(Community community, Member member);

    // ⭐️ [수정] existsByCommunityAndUserId -> existsByCommunityAndMember로 변경 (Service와 일치)
    boolean existsByCommunityAndMember(Community community, Member member);

    // ⭐️ [수정] findByUserId -> findByMember_Id로 변경 (JPA 관계 및 Service와 일치)
    // CommunityLike 엔티티 내부의 Member 객체(member)의 ID(id)를 기준으로 조회합니다.
    List<CommunityLike> findByMember_Id(Long memberId);
}