package de.shiyar.springframework.security.model.repo;

import org.springframework.data.repository.CrudRepository;

import de.shiyar.springframework.security.model.entity.User;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
}
