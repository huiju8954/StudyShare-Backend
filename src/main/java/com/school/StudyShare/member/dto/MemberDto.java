// MemberDto.java
package com.school.StudyShare.member.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {

    @NotEmpty(message = "사용자 ID는 필수 항목입니다.")
    @Size(min = 3, max = 30, message = "사용자 ID는 3~30자로 입력하세요.")
    private String username; // 사용자 ID

    @NotEmpty(message = "닉네임은 필수 항목입니다.") // ⭐️ 닉네임 필드 추가
    @Size(min = 2, max = 20, message = "닉네임은 2~20자로 입력하세요.")
    private String nickname;

    @NotEmpty(message = "패스워드는 필수 항목입니다.")
    @Size(min = 4, message = "패스워드는 4자 이상이어야 합니다.")
    private String password1; // 패스워드

    @NotEmpty(message = "패스워드 확인은 필수 항목입니다.")
    private String password2; // 패스워드

    @NotEmpty(message = "이메일은 필수 항목입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email; // 이메일

    @AssertTrue(message = "등록(이용) 확인에 동의해야 가입할 수 있습니다.")
    private Boolean registration; // 등록 여부
}