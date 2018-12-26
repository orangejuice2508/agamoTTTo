ALTER TABLE agamoTTTo_db.user_pool
  ADD COLUMN is_active TINYINT(4) NULL DEFAULT 1
  AFTER pool_id;