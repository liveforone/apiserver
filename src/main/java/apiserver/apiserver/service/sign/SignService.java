package apiserver.apiserver.service.sign;

import apiserver.apiserver.config.token.TokenHelper;
import apiserver.apiserver.dto.sign.RefreshTokenResponse;
import apiserver.apiserver.dto.sign.SignInRequest;
import apiserver.apiserver.dto.sign.SignInResponse;
import apiserver.apiserver.dto.sign.SignUpRequest;
import apiserver.apiserver.entity.member.Member;
import apiserver.apiserver.entity.member.RoleType;
import apiserver.apiserver.exception.*;
import apiserver.apiserver.repository.member.MemberRepository;
import apiserver.apiserver.repository.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
public class SignService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenHelper accessTokenHelper;
    private final TokenHelper refreshTokenHelper;


    private void validateRefreshToken(String rToken) {
        if(!refreshTokenHelper.validate(rToken)) {
            throw new AuthenticationEntryPointException();
        }
    }

    private void validateSignUpInfo(SignUpRequest req) {
        if(memberRepository.existsByEmail(req.getEmail()))
            throw new MemberEmailAlreadyExistsException(req.getEmail());
        if(memberRepository.existsByNickname(req.getNickname()))
            throw new MemberNicknameAlreadyExistsException(req.getNickname());
    }

    private void validatePassword(SignInRequest req, Member member) {
        if(!passwordEncoder.matches(req.getPassword(), member.getPassword())) {
            throw new LoginFailureException();
        }
    }

    private String createSubject(Member member) {
        return String.valueOf(member.getId());
    }

    //==method==//

    /*
    파라미터로 전달 받은, 회원가입 정보를 검증합니다.
    여기에서는 이메일과 닉네임의 중복성을 검사하고, 주어진 SingUpRequest를 Entity로 변환해줍니다.
     */
    @Transactional
    public void signUp(SignUpRequest req) {
        validateSignUpInfo(req);
        memberRepository.save(SignUpRequest.toEntity(req,
                roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new),
                passwordEncoder));
    }

    /*
    SignInRequest로 전달받은 email로 Member를 조회한 뒤, 비밀번호 검증이 통과되었다면 액세스 토큰과 리프레시 토큰을 발급해줍니다.
     */
    @Transactional(readOnly = true)
    public SignInResponse signIn(SignInRequest req) {
        Member member = memberRepository.findByEmail(req.getEmail()).orElseThrow(LoginFailureException::new);
        validatePassword(req, member);
        String subject = createSubject(member);
        String accessToken = accessTokenHelper.createToken(subject);
        String refreshToken = refreshTokenHelper.createToken(subject);
        return new SignInResponse(accessToken, refreshToken);
    }

    public RefreshTokenResponse refreshToken(String rToken) {
        validateRefreshToken(rToken);
        String subject = refreshTokenHelper.extractSubject(rToken);
        String accessToken = accessTokenHelper.createToken(subject);
        return new RefreshTokenResponse(accessToken);
    }
}
