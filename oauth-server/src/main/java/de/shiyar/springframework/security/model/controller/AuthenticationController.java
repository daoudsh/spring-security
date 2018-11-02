package de.shiyar.springframework.security.model.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;

import de.shiyar.springframework.security.model.entity.OauthClients;
import de.shiyar.springframework.security.model.entity.Role;
import de.shiyar.springframework.security.model.entity.Scope;
import de.shiyar.springframework.security.model.entity.User;
import de.shiyar.springframework.security.model.repo.OauthClientsRepository;
import de.shiyar.springframework.security.model.repo.RoleRepository;
import de.shiyar.springframework.security.model.repo.ScopeRepository;
import de.shiyar.springframework.security.model.repo.UserRepository;
import de.shiyar.springframework.security.model.service.OuathAccessService;

@RestController
public class AuthenticationController {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	final String userRoleName = "STANDARD_USER";
	final String adminRoleName = "ADMIN_USER";

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private ScopeRepository scopeRepository;
	@Autowired
	private OauthClientsRepository oauthClientsRepository;

	@Autowired
	PasswordEncoder encoder;
	@Autowired
	private ResourceServerTokenServices resourceServerTokenServices;

	private AccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();

	private WebResponseExceptionTranslator exceptionTranslator = new DefaultWebResponseExceptionTranslator();

	@Autowired
	private OuathAccessService ouathAccessService;

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> registerUser(String username, String password, String firstName, String lastName) {

		User user = new User();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setPassword(password);
		user.setUsername(username);

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		logger.info(user.getPassword());
		String hashedPassword = passwordEncoder.encode(user.getPassword());

		user.setPassword(hashedPassword);

		Role roleAdmin = roleRepository.findByRoleName(adminRoleName);
		Role roleUser = roleRepository.findByRoleName(userRoleName);
		List<Role> roles = new ArrayList<>();
		roles.add(roleAdmin);
		roles.add(roleUser);

		Scope foo = scopeRepository.findByScopeName("foo");
		Scope read = scopeRepository.findByScopeName("read");
		Scope write = scopeRepository.findByScopeName("write");
		Set<Scope> scopes = new HashSet<>();
		scopes.add(foo);
		scopes.add(read);
		scopes.add(write);

		user.setScopes(scopes);
		user.setRoles(roles);

		OauthClients client = oauthClientsRepository.findById("fooClientIdPassword");
		user.setClient(client);

		userRepository.save(user);
		return null;
	}

	@RequestMapping(value = "/create-token-custom", method = RequestMethod.POST)
	public ResponseEntity<?> login(String username, String password) {

		User user = userRepository.findByUsername(username);
		if (user == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		HttpHeaders headers = getBasicHeader(user.getClient().getId(), user.getClient().getClientSecret());
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("username", username);
		map.add("password", password);
		map.add("grant_type", "password");
		map.add("client_id", user.getClient().getId());

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

		String url = "http://localhost:8081/oauth-server/oauth/token";

		ResponseEntity<DefaultOAuth2AccessToken> responseEntity = null;
		try {
			RestTemplate restTemplate = new RestTemplate();
			responseEntity = restTemplate.postForEntity(url, request, DefaultOAuth2AccessToken.class);

			if (responseEntity.getStatusCode() == HttpStatus.OK) {
				String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
				ouathAccessService.insert(responseEntity.getBody(), sessionId);
				// return responseEntity;
				return new ResponseEntity<>(sessionId, responseEntity.getStatusCode());
			}
		} catch (final HttpClientErrorException httpClientErrorException) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		return responseEntity;

	}

	@RequestMapping(value = "/oauth/check_token")
	@ResponseBody
	public Map<String, ?> checkToken(@RequestParam("token") String value) {

		String accessToken = ouathAccessService.getAccessTokenBySession(value).getAccessToken();

		// Read token hier from DB and pass it
		OAuth2AccessToken token = resourceServerTokenServices.readAccessToken(accessToken);
		if (token == null) {
			throw new InvalidTokenException("Token was not recognised");
		}

		if (token.isExpired()) {
			throw new InvalidTokenException("Token has expired");
		}

		OAuth2Authentication authentication = resourceServerTokenServices.loadAuthentication(token.getValue());

		Map<String, ?> response = accessTokenConverter.convertAccessToken(token, authentication);

		return response;
	}

	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<OAuth2Exception> handleException(Exception e) throws Exception {
		logger.info("Handling error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
		// This isn't an oauth resource, so we don't want to send an
		// unauthorized code here. The client has already authenticated
		// successfully with basic auth and should just
		// get back the invalid token error.
		@SuppressWarnings("serial")
		InvalidTokenException e400 = new InvalidTokenException(e.getMessage()) {
			@Override
			public int getHttpErrorCode() {
				return 400;
			}
		};
		return exceptionTranslator.translate(e400);
	}

	private HttpHeaders getBasicHeader(String username, String password) {
		String plainCreds = username + ":" + password;
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);
		return headers;

	}
}
