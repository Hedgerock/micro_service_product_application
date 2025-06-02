package com.hedgerock.admin.web.client;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.web.client.HttpHeadersProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

@RequiredArgsConstructor
public class OauthHttpHeadersProvider implements HttpHeadersProvider {

    private final OAuth2AuthorizedClientManager auth2AuthorizedClientManager;

    @Override
    public HttpHeaders getHeaders(Instance instance) {

        var authorizedClient = this.auth2AuthorizedClientManager
                .authorize(OAuth2AuthorizeRequest.withClientRegistrationId("keycloak")
                        .principal("admin-service")
                        .build()
                );

        var httpHeaders = new HttpHeaders();

        if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
            httpHeaders.setBearerAuth(authorizedClient.getAccessToken().getTokenValue());
        }

        return httpHeaders;
    }
}
