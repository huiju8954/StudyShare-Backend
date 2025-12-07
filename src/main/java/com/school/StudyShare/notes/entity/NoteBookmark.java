package com.school.StudyShare.notes.entity;

import com.school.StudyShare.member.entity.Member; // ⭐️ [추가] Member 엔티티 임포트
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; // Setter 추가 (권장)

@Entity
@Table(name = "note_bookmark") // ⭐️ 테이블명 명시 (권장)
@Getter
@Setter
@NoArgsConstructor
public class NoteBookmark {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    // ⭐️ [핵심 수정] 단순 Integer userId 대신 Member 엔티티 관계로 변경
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;

    // ⭐️ [수정] 생성자: Member 객체를 받도록 변경
    public NoteBookmark(Note note, Member member) {
        this.note = note;
        this.member = member;
    }
}