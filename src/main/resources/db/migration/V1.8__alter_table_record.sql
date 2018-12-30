ALTER TABLE agamoTTTo_db.record
  ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT 0
  AFTER pool_id;

ALTER TABLE agamottto_db.record
  ADD CONSTRAINT `record_log_fk4` FOREIGN KEY (record_id) REFERENCES agamoTTTo_user (user_id);