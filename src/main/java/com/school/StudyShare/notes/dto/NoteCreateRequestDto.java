package com.school.StudyShare.notes.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoteCreateRequestDto {
    private String title;
    private Integer noteSubjectId;
    private String noteContent;
    private String noteFileUrl; // 파일 URL은 선택적이므로 null일 수 있음
}