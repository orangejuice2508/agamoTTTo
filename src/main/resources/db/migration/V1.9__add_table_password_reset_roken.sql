CREATE TABLE password_reset_token
(password_reset_token_id BIGINT AUTO_INCREMENT primary key,
  token  VARCHAR (45) NOT NULL,
  user_id                 BIGINT      NOT NULL,
  expiry_date             TIMESTAMP NOT NULL,
  CONSTRAINT password_reset_token_fk1
  FOREIGN KEY (user_id) REFERENCES agamottto_user (user_id));

