package com.nelani.blog_land_backend;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import com.nelani.blog_land_backend.service.ModerationClient;

@TestConfiguration
public class TestConfig {
    @Bean
    public ModerationClient moderationClient() {
        return Mockito.mock(ModerationClient.class);
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return Mockito.mock(ClientRegistrationRepository.class);
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService() {
        return Mockito.mock(OAuth2AuthorizedClientService.class);
    }
}
