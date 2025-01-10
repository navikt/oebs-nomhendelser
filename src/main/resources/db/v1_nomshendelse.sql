-------------------------------------------------------------------------------
-- Skjema            : XXRTV
-- Tabell            : XXRTV_NOMSHENDELSE
-- Kildefil          : v1_nomsshendelse.sql
-- Beskrivelse       : Tabell med nomshendelser som er konsumert fra Kafka.
-------------------------------------------------------------------------------
CREATE TABLE xxrtv.xxrtv_nomshendelse( nomshendelse_id NUMBER  NOT NULL
    , korrelasjon_id                   VARCHAR2(50)   NOT NULL
    , status                           VARCHAR2(20)   NOT NULL
    , retry_teller                     NUMBER
    , retry_tidspunkt                  TIMESTAMP(9)
    , hendelse_id                      VARCHAR2(100)
    , hendelse_fodselsnr               VARCHAR2(11)
    , hendelse_opprettet               TIMESTAMP(9)
    , hendelse                         CLOB
    , hendelse_oebs                    CLOB
    , feilinformasjon                  CLOB
    , CONSTRAINT nom_hendelse_pk PRIMARY KEY(nomshendelse_id) );

-- Legg til sekvens
CREATE SEQUENCE xxrtv_nom_seq
    START WITH 1
    INCREMENT BY 1;

-- Legg til trigger for å sette primærnøkkel
CREATE OR REPLACE TRIGGER nom_hen_pk_trg
  BEFORE INSERT ON xxrtv.xxrtv_nomshendelse
  FOR EACH ROW
BEGIN
  IF :new.nomshendelse_id IS NULL THEN
SELECT xxrtv_nom_seq.NEXTVAL
INTO   :new.nomshendelse_id
FROM   DUAL;
END IF;
END nom_hen_pk_trg;
/

CREATE INDEX nom_hendelse_idx_u1 ON xxrtv.xxrtv_nomshendelse (korrelasjon_id);
CREATE INDEX nom_hendelse_idx_u2 ON xxrtv.xxrtv_nomshendelse (status);
CREATE INDEX nom_hendelse_idx_u3 ON xxrtv.xxrtv_nomshendelse (hendelse_id);
CREATE INDEX nom_hendelse_idx_u4 ON xxrtv.xxrtv_nomshendelse (hendelse_fodselsnr);
CREATE INDEX nom_hendelse_idx_u5 ON xxrtv.xxrtv_nomshendelse (hendelse_opprettet);

drop TRIGGER nom_hendelse_trg;

alter table xxrtv.xxrtv_nomshendelse drop column hendelse_oebs;
alter table xxrtv.xxrtv_nomshendelse drop column reg_dato;
alter table xxrtv.xxrtv_nomshendelse drop column reg_user;
alter table xxrtv.xxrtv_nomshendelse drop column mod_dato;
alter table xxrtv.xxrtv_nomshendelse drop column mod_user;