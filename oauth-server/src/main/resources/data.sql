INSERT INTO `oauth_client_details` (`client_id`, `access_token_validity`, `additional_information`, `authorities`, `authorized_grant_types`, `autoapprove`, `client_secret`, `refresh_token_validity`, `resource_ids`, `scope`, `web_server_redirect_uri`) VALUES
	('barClientIdPassword', 36000, NULL, NULL, 'password,authorization_code,refresh_token', '1', 'secret', 36000, NULL, 'bar,read,write', NULL),
	('fooClientIdPassword', 36000, NULL, NULL, 'password,authorization_code,refresh_token', '1', 'secret', 36000, NULL, 'foo,read,write', NULL),
	('sampleClientId', 36000, NULL, NULL, 'implicit', '0', 'secret', 36000, NULL, 'read,write,foo,bar', NULL);
	
INSERT INTO `app_scope` (`id`, `description`, `scope_name`) VALUES
	(1, 'Write Scope', 'write'),
	(2, 'Foo Scope', 'foo'),
	(3, 'Read Scope', 'read');
	
INSERT INTO `app_role` (`id`, `description`, `role_name`) VALUES
	(1, 'Standard User - Has no admin rights', 'STANDARD_USER'),
	(2, 'Admin User - Has permission to perform admin tasks', 'ADMIN_USER');