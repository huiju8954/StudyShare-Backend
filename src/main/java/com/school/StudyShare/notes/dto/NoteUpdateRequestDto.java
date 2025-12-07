package com.school.StudyShare.notes.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoteUpdateRequestDto {
    private String title;
    private Integer noteSubjectId;
    private String noteContent;
    private String noteFileUrl;
}