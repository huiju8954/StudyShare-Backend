package com.school.StudyShare.community.entity;

import com.school.StudyShare.member.entity.Member; // ⭐️ Member 엔티티 임포트
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; // Setter 추가 (권장)

@Entity
@Table(name = "community_bookmark")
@Getter
@Setter
@NoArgsConstructor
public class CommunityBookmark {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false) // ⭐️ nullable 명시
    private Community community;

    // ⭐️ [핵심 수정] 단순 Integer userId 대신 Member 엔티티 관계로 변경
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;

    // ⭐️ [수정] 생성자: Member 객체를 받도록 변경
    public CommunityBookmark(Community community, Member member) {
        this.community = community;
        this.member = member;
    }
}