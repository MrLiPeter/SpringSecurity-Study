DROP TABLE IF EXISTS lxh_roles;
CREATE TABLE lxh_roles
(
    id           BIGINT      NOT NULL AUTO_INCREMENT,
    role_name    VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_mooc_roles_role_name UNIQUE (role_name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

DROP TABLE IF EXISTS lxh_users;
CREATE TABLE lxh_users
(
    id                      BIGINT       NOT NULL AUTO_INCREMENT,
    account_non_expired     BIT          NOT NULL,
    account_non_locked      BIT          NOT NULL,
    credentials_non_expired BIT          NOT NULL,
    email                   VARCHAR(255) NOT NULL,
    enabled                 BIT          NOT NULL,
    name                    VARCHAR(50)  NOT NULL,
    password_hash           VARCHAR(255)  NOT NULL,
    username                VARCHAR(50)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_mooc_users_username UNIQUE (username),
    CONSTRAINT uk_mooc_users_email UNIQUE (email)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

DROP TABLE IF EXISTS lxh_users_roles;
CREATE TABLE lxh_users_roles
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_users_roles_user_id_mooc_users_id FOREIGN KEY (user_id) REFERENCES lxh_users (id),
    CONSTRAINT fk_users_roles_role_id_mooc_roles_id FOREIGN KEY (role_id) REFERENCES lxh_roles (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
