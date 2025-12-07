package com.school.StudyShare.notes.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class NoteBookmark {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id")
    private Note note;

    private Integer userId; // 💡 Note와 통일 (Integer)

    public NoteBookmark(Note note, Integer userId) {
        this.note = note;
        this.userId = userId;
    }
}