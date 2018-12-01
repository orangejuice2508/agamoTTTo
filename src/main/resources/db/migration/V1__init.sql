CREATE TABLE IF NOT EXISTS `role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(60) NOT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `name_UNIQUE` (`role_name`)
);

CREATE TABLE IF NOT EXISTS `agamottto_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(60) NOT NULL,
  `last_name` varchar(60) NOT NULL,
  `e_mail` varchar(60) NOT NULL,
  `encrypted_password` varchar(120) NOT NULL,
  `enabled` tinyint(4) NOT NULL,
  `role_id` bigint(20) NOT NULL DEFAULT '3' COMMENT 'Default is ''3'' which stands for ''ROLE_MITARBEITER''',
  PRIMARY KEY (`user_id`),
  KEY `agamottto_user_fk_idx` (`role_id`),
  CONSTRAINT `agamoTTTo_user_fk` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`)
);

CREATE TABLE IF NOT EXISTS `pool` (
  `pool_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `pool_name` varchar(100) NOT NULL,
  `owner` bigint(20) NOT NULL,
  PRIMARY KEY (`pool_id`),
  KEY `pool_fk_idx` (`owner`),
  CONSTRAINT `pool_fk` FOREIGN KEY (`owner`) REFERENCES `agamottto_user` (`user_id`)
);

CREATE TABLE IF NOT EXISTS `record` (
  `record_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date` date NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `duration` time NOT NULL,
  `description` varchar(250) NOT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `user_id` bigint(20) NOT NULL,
  `pool_id` bigint(20) NOT NULL,
  PRIMARY KEY (`record_id`),
  KEY `record_fk1_idx` (`user_id`),
  KEY `record_fk2_idx` (`pool_id`),
  CONSTRAINT `record_fk1` FOREIGN KEY (`user_id`) REFERENCES `agamottto_user` (`user_id`),
  CONSTRAINT `record_fk2` FOREIGN KEY (`pool_id`) REFERENCES `pool` (`pool_id`)
);

CREATE TABLE IF NOT EXISTS `record_log` (
  `record_log_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `record_id` bigint(20) NOT NULL,
  `date` date NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `duration` time NOT NULL,
  `description` varchar(250) NOT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `change_by` bigint(20) NOT NULL,
  `change_at` timestamp NULL DEFAULT NULL,
  `change_type` enum('created','modified','deleted') DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  `pool_id` bigint(20) NOT NULL,
  PRIMARY KEY (`record_log_id`),
  KEY `record_fk1_idx` (`user_id`),
  KEY `record_fk2_idx` (`pool_id`),
  KEY `record_fk3_idx` (`change_by`),
  CONSTRAINT `record_log_fk1` FOREIGN KEY (`user_id`) REFERENCES `agamottto_user` (`user_id`),
  CONSTRAINT `record_log_fk2` FOREIGN KEY (`pool_id`) REFERENCES `pool` (`pool_id`),
  CONSTRAINT `record_log_fk3` FOREIGN KEY (`change_by`) REFERENCES `agamottto_user` (`user_id`)
);

CREATE TABLE IF NOT EXISTS `user_pool` (
  `user_pool_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `pool_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_pool_id`),
  KEY `user_pool_fk1_idx` (`user_id`),
  KEY `user_pool_fk2_idx` (`pool_id`),
  CONSTRAINT `user_pool_fk1` FOREIGN KEY (`user_id`) REFERENCES `agamottto_user` (`user_id`),
  CONSTRAINT `user_pool_fk2` FOREIGN KEY (`pool_id`) REFERENCES `pool` (`pool_id`)
);
