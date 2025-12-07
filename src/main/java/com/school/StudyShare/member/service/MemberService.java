// MemberService.java
package com.school.StudyShare.member.service;

import com.school.StudyShare.member.dto.MemberDto;
import com.school.StudyShare.member.entity.Member;
import com.school.StudyShare.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void create(@Valid MemberDto memberDto) {

        Member member = Member.builder()
                .username(memberDto.getUsername())
                .nickname(memberDto.getNickname()) // ⭐️ 닉네임 저장 로직 추가
                .password(passwordEncoder.encode(memberDto.getPassword1()))
                .email(memberDto.getEmail())
                .registration(memberDto.getRegistration())
                .build();

        memberRepository.save(member);
    }

    public Member getMember(String name) {
        Member member = memberRepository.findByUsername(name)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자가 존재하지 않습니다." + name));
        return member;
    }
}