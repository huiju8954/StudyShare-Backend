// Member.entity.java
package com.school.StudyShare.member.entity;

import com.school.StudyShare.audit.BaseTimeEntity;
import com.school.StudyShare.member.constant.Department;
import com.school.StudyShare.member.constant.Gender;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // 사용자 ID

    @Column(unique = true, nullable = false) // ⭐️ 닉네임 추가 및 unique 설정
    private String nickname;

    @Column(nullable = false)
    private String password; // 패스워드

    @Column(unique = true, nullable = false)
    private String email; // 이메일

    @Enumerated(EnumType.STRING)
    @Column(nullable = true, length = 10)
    private Gender gender; // 성별 (현재 사용 안 함)

    @Enumerated(EnumType.STRING)
    @Column(nullable = true, length = 30)
    private Department department; // 학과 (현재 사용 안 함)

    @Column(nullable = false)
    private Boolean registration; // 등록 여부
}