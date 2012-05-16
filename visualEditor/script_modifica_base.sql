create table Condicion_Atributo (id numeric(19) PRIMARY KEY identity(1,1),condicion_id numeric(19), atributo_id numeric(19), atributo2_id numeric(19), operadorLogico varchar(2));

insert into Condicion_Atributo (condicion_id, atributo_id) select id, atributo_id from Condicion;

ALTER TABLE Condicion DROP CONSTRAINT FK4413D2AC129EDBA2;

ALTER table Condicion drop column atributo_id;

ALTER TABLE Condicion_Atributo ADD FOREIGN KEY (condicion_id) REFERENCES condicion(id);

ALTER TABLE Condicion_Atributo ADD FOREIGN KEY (atributo_id) REFERENCES atributo(id); 
