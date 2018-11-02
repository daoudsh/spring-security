package de.shiyar.springframework.security.model.repo;

import org.springframework.data.repository.CrudRepository;

import de.shiyar.springframework.security.model.entity.Scope;

public interface ScopeRepository  extends CrudRepository<Scope, Long>{

	Scope findByScopeName(String name);
}
