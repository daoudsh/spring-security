package de.shiyar.springframework.security.model.repo;

import org.springframework.data.repository.CrudRepository;

import de.shiyar.springframework.security.model.entity.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {
	
	Role findByRoleName(String name);
}
