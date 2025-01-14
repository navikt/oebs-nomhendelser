-------------------------------------------------------------------------------
-- Skjema            : XXRTV
-- Tabell            : XXRTV_NOMSHENDELSE
-- Kildefil          : v1_nomsshendelse.sql
-- Beskrivelse       : Tabell med nomshendelser som er konsumert fra Kafka.
-------------------------------------------------------------------------------
CREATE TABLE xxrtv.xxrtv_nom_hendelse( nom_hendelse_id NUMBER  NOT NULL
    , korrelasjon_id                   VARCHAR2(50)   NOT NULL
    , status                           VARCHAR2(20)   NOT NULL
    , status_beskrivelse               VARCHAR2(20)
    , retry_teller                     NUMBER
    , retry_tidspunkt                  TIMESTAMP(9)
    , hendelse_id                      VARCHAR2(100)
    , hendelse_fodselsnr               VARCHAR2(11)
    , hendelse_opprettet               TIMESTAMP(9)
    , hendelse                         CLOB
    , hendelse_oebs                    CLOB
    , feilinformasjon                  CLOB
    , meldingstype                     VARCHAR2(20)
    , CONSTRAINT nom_hendelse_pk PRIMARY KEY(nom_hendelse_id) );

-- Legg til sekvens
CREATE SEQUENCE xxrtv_nom_seq
    START WITH 1
    INCREMENT BY 1;

-- Legg til trigger for å sette primærnøkkel
CREATE OR REPLACE TRIGGER nom_hen_pk_trg
  BEFORE INSERT ON xxrtv.xxrtv_nom_hendelse
  FOR EACH ROW
BEGIN
  IF :new.nom_hendelse_id IS NULL THEN
SELECT xxrtv_nom_seq.NEXTVAL
INTO   :new.nom_hendelse_id
FROM   DUAL;
END IF;
END nom_hen_pk_trg;
/

CREATE INDEX nom_hendelse_idx_u1 ON xxrtv.xxrtv_nom_hendelse (korrelasjon_id);
CREATE INDEX nom_hendelse_idx_u2 ON xxrtv.xxrtv_nom_hendelse (status);
CREATE INDEX nom_hendelse_idx_u3 ON xxrtv.xxrtv_nom_hendelse (hendelse_id);
CREATE INDEX nom_hendelse_idx_u4 ON xxrtv.xxrtv_nom_hendelse (hendelse_fodselsnr);
CREATE INDEX nom_hendelse_idx_u5 ON xxrtv.xxrtv_nom_hendelse (hendelse_opprettet);

create synonym xxrtv_nom_hendelse for xxrtv.xxrtv_nom_hendelse;

drop TRIGGER nom_hendelse_trg;

alter table xxrtv.xxrtv_nomshendelse drop column hendelse_oebs;
alter table xxrtv.xxrtv_nomshendelse drop column reg_dato;
alter table xxrtv.xxrtv_nomshendelse drop column reg_user;
alter table xxrtv.xxrtv_nomshendelse drop column mod_dato;
alter table xxrtv.xxrtv_nomshendelse drop column mod_user;