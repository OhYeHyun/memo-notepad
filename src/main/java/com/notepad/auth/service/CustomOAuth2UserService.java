package com.notepad.auth.service;

import com.notepad.entity.User;
import com.notepad.user.repository.UserRepository;
import com.notepad.auth.dto.PrincipalUser;
import com.notepad.global.enums.Role;
import com.notepad.auth.oauth.KakaoResponse;
import com.notepad.auth.oauth.OAuth2Response;
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
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = extractOAuth2Response(registrationId, oAuth2User);
        if (oAuth2Response == null) return null;

        String loginId = buildLoginId(oAuth2Response);
        Optional<User> optionalUser = userRepository.findByLoginId(loginId);

        User member;
        if (optionalUser.isEmpty()) {
            member = createMemberFromOAuth2Response(oAuth2Response);
        } else {
            member = optionalUser.get();
            updateUser(member, oAuth2Response);
        }

        return new PrincipalUser(member, oAuth2User.getAttributes());
    }

    private OAuth2Response extractOAuth2Response(String registrationId, OAuth2User oAuth2User) {
        if (registrationId.equals("kakao")) {
            return new KakaoResponse(oAuth2User.getAttributes());
        }
        return null;
    }

    private User createMemberFromOAuth2Response(OAuth2Response oAuth2Response) {
        User user = User.ofOAuth(
                oAuth2Response.getName(),
                buildLoginId(oAuth2Response),
                UUID.randomUUID().toString(),
                Role.ROLE_USER,
                oAuth2Response.getProvider(),
                oAuth2Response.getProviderId()
        );

        return userRepository.save(user);
    }

    private String buildLoginId(OAuth2Response oAuth2Response) {
        return oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();
    }

    private void updateUser(User user, OAuth2Response response) {
        user.updateProvider(response);
    }
}