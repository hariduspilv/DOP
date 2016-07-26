SET foreign_key_checks = 0;

CREATE TABLE UploadedFile(
  id                BIGINT  AUTO_INCREMENT PRIMARY KEY,
  path              VARCHAR(255),
  name VARCHAR(255) NOT NULL
);

ALTER TABLE dop.Material ADD uploadedFile BIGINT;
ALTER TABLE dop.Material
ADD CONSTRAINT
FOREIGN KEY (uploadedFile) REFERENCES UploadedFile (id);

SET foreign_key_checks = 1;

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'ADD_FILE','Lisa fail');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'ADD_FILE','Add file');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'ADD_FILE','Добавить файл');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'ADD_FILE_SUCCESS','Fail edukalt üleslaetud');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'ADD_FILE_SUCCESS','File has been successfully uploaded');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'ADD_FILE_SUCCESS','Файл был успешно загружен');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'ADD_FILE_UPLOADING','Laen faili üles ...');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'ADD_FILE_UPLOADING','Uploading file ...');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'ADD_FILE_UPLOADING','при загрузке файла');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'ADD_FILE_PLACEHOLDER','Palun laadige üles materjali fail');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'ADD_FILE_PLACEHOLDER','Please upload a material file');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'ADD_FILE_PLACEHOLDER','Пожалуйста, загрузите файл материалa');

INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (1,'MATERIAL_URL_OR_FILE_INVALID','Palun lisage viide materjalile või laadige üles fail');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (3,'MATERIAL_URL_OR_FILE_INVALID','Please provide a link to the material or upload a file');
INSERT INTO Translation(translationGroup, translationKey, translation) VALUES (2,'MATERIAL_URL_OR_FILE_INVALID','Пожалуйста, включите справочные материалы , или загрузить файл');
