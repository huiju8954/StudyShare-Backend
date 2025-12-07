package com.school.StudyShare.community.repository;

import com.school.StudyShare.community.entity.Community;
import com.school.StudyShare.community.entity.CommunityBookmark;
import com.school.StudyShare.member.entity.Member; // ⭐️ [필수 추가] Member 엔티티 임포트
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommunityBookmarkRepository extends JpaRepository<CommunityBookmark, Long> {

    // ⭐️ [수정] findByCommunityAndUserId -> findByCommunityAndMember로 변경 (Service와 일치)
    // Member 객체를 받아 관계를 통해 조회합니다.
    Optional<CommunityBookmark> findByCommunityAndMember(Community community, Member member);

    // ⭐️ [수정] existsByCommunityAndUserId -> existsByCommunityAndMember로 변경 (Service와 일치)
    boolean existsByCommunityAndMember(Community community, Member member);

    // ⭐️ [수정] findByUserId -> findByMember_Id로 변경 (JPA 관계 및 Service와 일치)
    // CommunityBookmark 엔티티 내부의 Member 객체(member)의 ID(id)를 기준으로 조회합니다.
    List<CommunityBookmark> findByMember_Id(Long memberId);
}