USE dop;

-- live taxon domains
insert into Taxon(id, name, level) values (128, 'Arhitektuur_ja_ehitus', 'DOMAIN');
insert into Domain(id, educationalContext) values (128, 4);
insert into Taxon(id, name, level) values (130, 'Isikuteenindus', 'DOMAIN');
insert into Domain(id, educationalContext) values (130, 4);
insert into Taxon(id, name, level) values (131, 'Kunstid', 'DOMAIN');
insert into Domain(id, educationalContext) values (131, 4);
insert into Taxon(id, name, level) values (132, 'Põllumajandus,_metsandus_ja_kalandus', 'DOMAIN');
insert into Domain(id, educationalContext) values (132, 4);
insert into Taxon(id, name, level) values (133, 'Riigikaitse', 'DOMAIN');
insert into Domain(id, educationalContext) values (133, 4);
insert into Taxon(id, name, level) values (134, 'Sisekaitse', 'DOMAIN');
insert into Domain(id, educationalContext) values (134, 4);
insert into Taxon(id, name, level) values (135, 'Sotsiaalteenused', 'DOMAIN');
insert into Domain(id, educationalContext) values (135, 4);
insert into Taxon(id, name, level) values (136, 'Tehnika,_tootmine_ja_tehnoloogia', 'DOMAIN');
insert into Domain(id, educationalContext) values (136, 4);
insert into Taxon(id, name, level) values (137, 'Tervishoid', 'DOMAIN');
insert into Domain(id, educationalContext) values (137, 4);
insert into Taxon(id, name, level) values (138, 'Transporditeenused', 'DOMAIN');
insert into Domain(id, educationalContext) values (138, 4);
insert into Taxon(id, name, level) values (139, 'Ärindus_ja_haldus', 'DOMAIN');
insert into Domain(id, educationalContext) values (139, 4);

-- live taxons
INSERT INTO EstCoreTaxonMapping (id, taxon, name) VALUES (128, 128, 'Arhitektuur_ja_ehitus');
INSERT INTO EstCoreTaxonMapping (id, taxon, name) VALUES (130, 130, 'Isikuteenindus');
INSERT INTO EstCoreTaxonMapping (id, taxon, name) VALUES (131, 131, 'Kunstid');
INSERT INTO EstCoreTaxonMapping (id, taxon, name) VALUES (132, 132, 'Põllumajandus,_metsandus_ja_kalandus');
INSERT INTO EstCoreTaxonMapping (id, taxon, name) VALUES (133, 133, 'Riigikaitse');
INSERT INTO EstCoreTaxonMapping (id, taxon, name) VALUES (134, 134, 'Sisekaitse');
INSERT INTO EstCoreTaxonMapping (id, taxon, name) VALUES (135, 135, 'Sotsiaalteenused');
INSERT INTO EstCoreTaxonMapping (id, taxon, name) VALUES (136, 136, 'Tehnika,_tootmine_ja_tehnoloogia');
INSERT INTO EstCoreTaxonMapping (id, taxon, name) VALUES (137, 137, 'Tervishoid');
INSERT INTO EstCoreTaxonMapping (id, taxon, name) VALUES (138, 138, 'Transporditeenused');
INSERT INTO EstCoreTaxonMapping (id, taxon, name) VALUES (139, 139, 'Ärindus_ja_haldus');
