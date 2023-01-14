package com.jinjjaseoul.auth.oauth2;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
@ConfigurationProperties(prefix = "jinjja-seoul")
public class AppProperties {

    private final Jwt jwt = new Jwt();
    private final OAuth2 oAuth2 = new OAuth2();

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Jwt {
        private String secret;
        private String grantType;
        private long accessTime;
        private long refreshTime;
    }

    public static final class OAuth2 {
        private List<String> authorizedRedirectUris = new ArrayList<>();

        public List<String> getAuthorizedRedirectUris() {
            return authorizedRedirectUris;
        }

        public OAuth2 authorizedRedirectUris(List<String> authorizedRedirectUris) {
            this.authorizedRedirectUris = authorizedRedirectUris;
            return this;
        }
    }
}