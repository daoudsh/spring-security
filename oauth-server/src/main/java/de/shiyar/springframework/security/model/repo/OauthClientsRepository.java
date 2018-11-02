package de.shiyar.springframework.security.model.repo;

import org.springframework.data.repository.CrudRepository;

import de.shiyar.springframework.security.model.entity.OauthClients;

public interface OauthClientsRepository  extends CrudRepository<OauthClients, String>  {

	OauthClients findById(String string);

}
