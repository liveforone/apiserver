package apiserver.apiserver.service.member;

import apiserver.apiserver.dto.member.MemberDto;
import apiserver.apiserver.entity.member.Member;
import apiserver.apiserver.exception.MemberNotFoundException;
import apiserver.apiserver.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    private boolean notExistsMember(Long id) {
        return !memberRepository.existsById(id);
    }

    public MemberDto read(Long id) {
        return MemberDto.toDto(memberRepository.findById(id).orElseThrow(MemberNotFoundException::new));
    }

    @Transactional
    public void delete(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
        memberRepository.delete(member);
    }
}
