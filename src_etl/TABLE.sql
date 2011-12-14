create table area ( -- type=TABLE
	cod int4(10) not null,
	area float8(17,17),
	nome text,
	constraint area_pk primary key (cod)
);

create table mesorregioes ( -- type=TABLE
	uf int2(5),
	mesorregiaogeografica int4(10) not null,
	mesorregiaogeografica_nome text,
	constraint mesorregiaogeografica_pk primary key (mesorregiaogeografica)
);

create table microrregioes ( -- type=TABLE
	uf int2(5),
	mesorregiaogeografica int4(10),
	microrregiaogeografica int4(10) not null,
	microrregiaogeografica_nome text,
	constraint microrregiaogeografica_pk primary key (microrregiaogeografica)
);

create table municipios ( -- type=TABLE
	uf int2(5),
	mesorregiaogeografica int4(10),
	microrregiaogeografica int4(10),
	municipio int4(10) not null,
	municipio_nome text,
	constraint municipios_pk primary key (municipio)
);

create table pib ( -- type=TABLE
	ano int2(5) not null,
	cod_munic int8(19) not null,
	munic text,
	agro float8(17,17),
	ind float8(17,17),
	serv float8(17,17),
	apu float8(17,17),
	impostos float8(17,17),
	pib float8(17,17),
	pop float8(17,17),
	pib_pcap float8(17,17),
	constraint pib_pk primary key (ano, cod_munic)
);

create table populacao ( -- type=TABLE
	cod_mun int8(19) not null,
	nome_mun text,
	pop_2000 float8(17,17),
	pop_homens float8(17,17),
	pop_mulheres float8(17,17),
	pop_urbana float8(17,17),
	pop_rural float8(17,17),
	pop_2010 float8(17,17),
	constraint pop_pk primary key (cod_mun)
);

create table ufs ( -- type=TABLE
	uf int2(5) not null,
	nome_uf text,
	sigla varchar(2),
	constraint ufs_pk primary key (uf)
);

create table violencia ( -- type=TABLE
	municipio varchar(100),
	uf text,
	populacao2008 int8(19),
	media int4(10),
	homic2006 int4(10),
	homic2007 int4(10),
	homic2008 int4(10),
	taxa int4(10),
	pos_nacional int4(10),
	pos_estadual int4(10),
	nr_uf int2(5),
	nr_municipio int4(10) not null,
	constraint violencia_pk primary key (nr_municipio)
);

