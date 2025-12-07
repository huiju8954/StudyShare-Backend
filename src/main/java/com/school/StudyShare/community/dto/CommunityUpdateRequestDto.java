// package com.school.StudyShare.community.dto;

package com.school.StudyShare.community.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommunityUpdateRequestDto {
    private String title;
    private String category; // 💡 [수정] category 사용
    private String content;
}