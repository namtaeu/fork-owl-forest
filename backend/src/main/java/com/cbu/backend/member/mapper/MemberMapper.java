package com.cbu.backend.member.mapper;

import com.cbu.backend.global.Mapper;
import com.cbu.backend.member.dto.request.MemberSignupRequestDTO;
import com.cbu.backend.member.dto.response.MemberResponseDTO;
import com.cbu.backend.member.entity.Member;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper
public class MemberMapper {
    public MemberResponseDTO toDto(Member member) {
        return MemberResponseDTO.builder()
                .id(member.getId())
                .accountId(member.getAccountId())
                .email(member.getEmail())
                .name(member.getName())
                .generation(member.getGeneration())
                .build();

    }

    public Member toEntity(MemberSignupRequestDTO dto, PasswordEncoder passwordEncoder) {
        return Member.builder()
                .name(dto.getAccountId())
                .password(passwordEncoder.encode(dto.getPassword())) //member객체 생성할때부터 바로 인코딩해서 비밀번호 저장
                .accountId(dto.getAccountId())
                .build();
    }
}
