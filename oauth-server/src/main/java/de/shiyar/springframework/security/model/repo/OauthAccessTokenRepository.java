package de.shiyar.springframework.security.model.repo;

import org.springframework.data.repository.CrudRepository;

import de.shiyar.springframework.security.model.entity.OauthAccessToken;

public interface OauthAccessTokenRepository extends CrudRepository<OauthAccessToken, String> {

	OauthAccessToken findByJsessionid(String sessionId);
	

}
