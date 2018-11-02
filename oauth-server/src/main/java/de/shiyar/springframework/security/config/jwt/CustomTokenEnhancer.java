package de.shiyar.springframework.security.config.jwt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import de.shiyar.springframework.security.model.entity.Scope;
import de.shiyar.springframework.security.model.repo.UserRepository;

public class CustomTokenEnhancer implements TokenEnhancer {
	
	@Autowired
    private UserRepository userRepository;
	
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        final Map<String, Object> additionalInfo = new HashMap<>();
        
        User user = (User) authentication.getPrincipal();
        de.shiyar.springframework.security.model.entity.User usr = userRepository.findByUsername(user.getUsername());
        additionalInfo.put("firstName", usr.getFirstName());
        additionalInfo.put("lastname", usr.getLastName());
        additionalInfo.put("grantType", authentication.getOAuth2Request().getGrantType());
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        Set<String> scops = new HashSet<>();
        for (Scope scope : usr.getScopes()) {
			scops.add(scope.getScopeName());
		}
        ((DefaultOAuth2AccessToken) accessToken).setScope(scops);
        return accessToken;
    }

}
