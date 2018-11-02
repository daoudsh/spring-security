package de.shiyar.springframework.security.model.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import de.shiyar.springframework.security.model.entity.User;
import de.shiyar.springframework.security.model.repo.UserRepository;

@Component
public class AppUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;


	@Override
	public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(s);

		if (user == null) {
			throw new UsernameNotFoundException(String.format("The username %s doesn't exist", s));
		}

		List<GrantedAuthority> authorities = new ArrayList<>();
		user.getRoles().forEach(role -> {
			authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
		});

		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				authorities);

	}
}
