SET foreign_key_checks = 0;

ALTER TABLE ImproperContent
  CHANGE COLUMN creator createdBy BIGINT NULL,
  ADD CONSTRAINT FK_IC_CreatedBy_User_id FOREIGN KEY (createdBy) REFERENCES User (id),
  ADD CONSTRAINT FK_IC_ReviewedBy_User_id FOREIGN KEY (reviewedBy) REFERENCES User (id);

SET foreign_key_checks = 1;