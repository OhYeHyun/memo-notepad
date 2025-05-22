package com.yehyun.memo.notepad.security.service;

import com.yehyun.memo.notepad.domain.member.Member;
import com.yehyun.memo.notepad.repository.MemberRepository;
import com.yehyun.memo.notepad.security.dto.PrincipalMember;
import com.yehyun.memo.notepad.security.oauth.KakaoResponse;
import com.yehyun.memo.notepad.security.oauth.OAuth2Response;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2MemberService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = extractOAuth2Response(registrationId, oAuth2User);
        if (oAuth2Response == null) return null;

        String loginId = buildLoginId(oAuth2Response);
        Optional<Member> optionalMember = memberRepository.findByLoginId(loginId);

        Member member;
        if (optionalMember.isEmpty()) {
            member = createMemberFromOAuth2Response(oAuth2Response);
        } else {
            member = optionalMember.get();
            updateMember(member, oAuth2Response);
        }

        return new PrincipalMember(member, oAuth2User.getAttributes());
    }

    private OAuth2Response extractOAuth2Response(String registrationId, OAuth2User oAuth2User) {
        if (registrationId.equals("kakao")) {
            return new KakaoResponse(oAuth2User.getAttributes());
        }
        return null;
    }

    private Member createMemberFromOAuth2Response(OAuth2Response oAuth2Response) {
        Member member = Member.ofOAuth(
                oAuth2Response.getName(),
                buildLoginId(oAuth2Response),
                UUID.randomUUID().toString(),
                "ROLE_USER",
                oAuth2Response.getProvider(),
                oAuth2Response.getProviderId()
        );

        return memberRepository.save(member);
    }

    private String buildLoginId(OAuth2Response oAuth2Response) {
        return oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();
    }

    private void updateMember(Member member, OAuth2Response response) {
        member.setName(response.getName());
        member.setProvider(response.getProvider());
        member.setProviderId(response.getProviderId());
    }
}