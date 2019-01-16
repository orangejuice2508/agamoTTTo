CREATE TABLE agamoTTTo_db.password_reset_token (
  password_reset_token_id BIGINT      NOT NULL AUTO_INCREMENT,
  token                   VARCHAR(45) NOT NULL,
  user_id                 BIGINT      NOT NULL,
  expiry_date             TIMESTAMP   NOT NULL,
  PRIMARY KEY (password_reset_token_id),
  KEY `password_reset_token_fk_idx` (user_id),
  CONSTRAINT `password_reset_token_fk` FOREIGN KEY (user_id) REFERENCES agamoTTTo_user (user_id)
);