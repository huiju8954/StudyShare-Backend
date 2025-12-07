// MemberProfileDto.java (New File)

package com.school.StudyShare.member.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberProfileDto {
    private Long id;
    private String nickname;
    private String username;
}