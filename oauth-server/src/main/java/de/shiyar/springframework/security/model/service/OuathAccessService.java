package de.shiyar.springframework.security.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.stereotype.Service;

import de.shiyar.springframework.security.model.entity.OauthAccessToken;
import de.shiyar.springframework.security.model.repo.OauthAccessTokenRepository;

@Service
public class OuathAccessService {

	@Autowired
	OauthAccessTokenRepository accessRepo;

	public OauthAccessToken getAccessTokenBySession(String sessionId) {
		return accessRepo.findByJsessionid(sessionId);
	}

	public OauthAccessToken insert(DefaultOAuth2AccessToken oAuth2AccessToken, String sessionId) {

		OauthAccessToken token = new OauthAccessToken();
		token.setJsessionid(sessionId);

		token.setAccessToken(oAuth2AccessToken.getValue());
		token.setTokenType(oAuth2AccessToken.getTokenType());

		token.setRefreshToken(oAuth2AccessToken.getRefreshToken().getValue());
		token.setExpiresIn(oAuth2AccessToken.getExpiresIn());

		token.setScope(oAuth2AccessToken.getScope().toString());
		token.setJti((String) oAuth2AccessToken.getAdditionalInformation().get("jti"));

		token.setFirstName((String) oAuth2AccessToken.getAdditionalInformation().get("firstName"));
		token.setLastName((String) oAuth2AccessToken.getAdditionalInformation().get("lastname"));
		token.setGrantType((String) oAuth2AccessToken.getAdditionalInformation().get("grantType"));

		return accessRepo.save(token);
	}
}
