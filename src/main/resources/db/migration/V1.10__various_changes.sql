ALTER TABLE agamoTTTo_db.record_log
  DROP FOREIGN KEY record_log_fk4;
ALTER TABLE agamoTTTo_db.record_log
  CHANGE COLUMN record_id record_id BIGINT(20) NOT NULL,
  CHANGE COLUMN version version BIGINT(20) NOT NULL;
ALTER TABLE agamoTTTo_db.record_log
  ADD CONSTRAINT record_log_fk4
FOREIGN KEY (record_id)
REFERENCES agamoTTTo_db.record (record_id);
