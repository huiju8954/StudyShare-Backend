package com.school.StudyShare.community.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class CommunityLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;

    // 💡 [수정] Long -> Integer
    private Integer userId;

    // 💡 [수정] 생성자도 Integer로 변경
    public CommunityLike(Community community, Integer userId) {
        this.community = community;
        this.userId = userId;
    }
}