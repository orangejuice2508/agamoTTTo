ALTER TABLE `agamottto_db`.`record`
  CHANGE COLUMN `duration` `duration_in_minutes` BIGINT NOT NULL ;
ALTER TABLE `agamottto_db`.`record_log`
  CHANGE COLUMN `duration` `duration_in_minutes` BIGINT NOT NULL ;
