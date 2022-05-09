DROP TABLE IF EXISTS lxh_users;
CREATE TABLE lxh_users(
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    enabled TINYINT NOT NULL DEFAULT 1,
    name  VARCHAR(50) null,
    PRIMARY KEY (username)
)ENGINE=INNODB;

DROP TABLE IF EXISTS lxh_authorities;
CREATE TABLE lxh_authorities(
    username VARCHAR(50) NOT NULL,
    authority VARCHAR(50) NOT NULL,
    CONSTRAINT fk_authorities_users FOREIGN KEY (username) REFERENCES lxh_users(username)
)ENGINE=INNODB;

CREATE UNIQUE INDEX ix_auth_username on lxh_authorities(username,authority)