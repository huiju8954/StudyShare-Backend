// src/main/java/com/school/StudyShare/notes/entity/NoteLike.java (최종 수정)
package com.school.StudyShare.notes.entity;

import com.school.StudyShare.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "note_like")
@Getter
@Setter
@NoArgsConstructor
public class NoteLike {

    // ⭐️ [필수] 복합 키를 사용하지 않는 단일 ID 테이블에서 외래 키를 참조하는 경우, 
    // ID 필드는 Note나 Member의 ID와 같은 Long 타입이어야 합니다.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ⭐️ [핵심 수정] Note 관계: 외래 키가 Long 타입임을 명확히 하고, JoinColumn으로 연결합니다.
    // Note 엔티티의 ID가 Long 타입인 것이 확인되었으므로, 매핑을 안정화합니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    // ⭐️ [JPA 관계 수정] Member 관계: user_id가 Member.member_id (Long)를 참조함을 명확히 합니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;

    public NoteLike(Note note, Member member) {
        this.note = note;
        this.member = member;
    }
}