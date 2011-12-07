alter table area
	add constraint area_munic_fk foreign key (cod)
	references municipios (municipio);

alter table mesorregioes
	add constraint meso_uf_fk foreign key (uf)
	references ufs (uf);

alter table microrregioes
	add constraint meso_micro_fk foreign key (mesorregiaogeografica)
	references mesorregioes (mesorregiaogeografica);

alter table municipios
	add constraint micro_munic_fk foreign key (microrregiaogeografica)
	references microrregioes (microrregiaogeografica);

alter table pib
	add constraint pib_munic_fk foreign key (cod_munic)
	references municipios (municipio);

alter table populacao
	add constraint pop_munic_fk foreign key (cod_mun)
	references municipios (municipio);

