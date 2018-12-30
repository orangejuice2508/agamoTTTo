ALTER TABLE agamoTTTo_db.record
  ADD COLUMN is_deleted BOOLEAN DEFAULT 0
  AFTER pool_id;