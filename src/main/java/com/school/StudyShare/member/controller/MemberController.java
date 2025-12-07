
// MemberController.java (최종 수정: DTO 반환)
package com.school.StudyShare.member.controller;

import com.school.StudyShare.member.dto.MemberDto;
import com.school.StudyShare.member.dto.MemberProfileDto; // ⭐️ 사용
import com.school.StudyShare.member.service.MemberService;
import com.school.StudyShare.member.service.MemberSecurityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityNotFoundException;
import com.school.StudyShare.member.entity.Member;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final MemberSecurityService memberSecurityService;

    // ⭐️ [로그인 로직 수정] String 대신 MemberProfileDto를 반환하도록 수정
    @GetMapping("/login")
    public ResponseEntity<MemberProfileDto> login( // ⭐️ ResponseEntity<String> -> ResponseEntity<MemberProfileDto>
                                                   @RequestParam String username,
                                                   @RequestParam String password,
                                                   HttpServletRequest request
    ) {
        try {
// 1. UserDetailsService를 통해 사용자 정보(암호화된 PW 포함) 로드
            UserDetails userDetails = memberSecurityService.loadUserByUsername(username);

// 2. 입력된 PW와 DB의 암호화된 PW 비교 (인증)
            if (passwordEncoder.matches(password, userDetails.getPassword())) {

                // 3. Spring Security Context에 인증 정보 저장 (로그인 처리)
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 4. Member 엔티티를 가져와 DTO로 변환하여 응답 본문에 포함
                Member member = memberService.getMember(username);
                MemberProfileDto profileDto = MemberProfileDto.builder()
                        .id(member.getId())
                        .nickname(member.getNickname())
                        .username(member.getUsername())
                        .build();

                log.info("========== 로그인 성공 (GET API): {}", username);
                return ResponseEntity.ok(profileDto); // ⭐️ DTO와 200 OK 반환

            } else {
                log.warn("========== 로그인 실패 (PW 불일치): {}", username);
                // 실패 시 401 Unauthorized 상태로 빈 DTO 또는 null 반환 (클라이언트가 DTO를 기대하므로)
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (UsernameNotFoundException e) {
// 사용자 없음
            log.error("========== 로그인 실패 (사용자 없음): {}", username);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("========== 로그인 처리 중 알 수 없는 오류: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ⭐️ [기존 프로필 로직 유지] /profile 엔드포인트는 로그인 유지 검증용으로 유지
    @GetMapping("/profile")
    public ResponseEntity<MemberProfileDto> getProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 401
        }

        try {
            String username = authentication.getName();
            Member member = memberService.getMember(username);

            MemberProfileDto profileDto = MemberProfileDto.builder()
                    .id(member.getId())
                    .nickname(member.getNickname())
                    .username(member.getUsername())
                    .build();

            log.info("========== 프로필 조회 성공: {}", username);
            return ResponseEntity.ok(profileDto);

        } catch (EntityNotFoundException e) {
            log.error("========== 프로필 조회 실패: 사용자 없음");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("========== 프로필 조회 중 오류: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("========== 로그아웃");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return ResponseEntity.ok("로그아웃 성공");
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody MemberDto memberDto, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            log.warn("회원가입 유효성 검사 실패: {}", errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }

        if (!memberDto.getPassword1().equals(memberDto.getPassword2())) {
            return new ResponseEntity<>("2개의 패스워드가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        try {
            memberService.create(memberDto);
            log.info("========== 회원가입 성공 : {}", memberDto.getUsername());
            return new ResponseEntity<>("회원가입 성공", HttpStatus.CREATED);

        } catch (DataIntegrityViolationException e) {
            log.warn("========== 회원가입 실패 : 이미 등록된 사용자입니다.");
            return new ResponseEntity<>("이미 등록된 사용자입니다.", HttpStatus.CONFLICT);

        } catch(Exception e){
            log.error("========== 회원가입 알 수 없는 오류: {}", e.getMessage());
            return new ResponseEntity<>("서버 오류: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}