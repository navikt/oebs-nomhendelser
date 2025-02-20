-------------------------------------------------------------------------------
-- Skjema            : XXRTV
-- Tabell            : XXRTV_NOM_LOGG
-- Kildefil          : v1_xxrv_nom_logg.sql
-- Beskrivelse       : Loggtabell for API-kall.
-------------------------------------------------------------------------------
CREATE TABLE xxrtv.xxrtv_nom_logg( kall_logg_id NUMBER    NOT NULL
    , korrelasjon_id      VARCHAR2(50)  NOT NULL
    , tidspunkt           TIMESTAMP(9)  NOT NULL
    , type                VARCHAR2(10)  NOT NULL
    , kall_retning        VARCHAR2(10)  NOT NULL
    , method              VARCHAR2(10)
    , operation           VARCHAR2(100) NOT NULL
    , status              NUMBER
    , kalltid             NUMBER        NOT NULL
    , kafka_partition     NUMBER
    , kafka_offset        NUMBER
    , kafka_timestamp     TIMESTAMP(9)
    , kafka_timestamptype VARCHAR2(50)
    , kafka_key           VARCHAR2(100)
    , request             CLOB
    , response            CLOB
    , logginfo            CLOB
    , CONSTRAINT nom_logg_pk PRIMARY KEY(kall_logg_id))
    PARTITION BY RANGE(tidspunkt)
    INTERVAL(NUMTOYMINTERVAL(1, 'MONTH'))
( PARTITION kall_logg_data_p1 VALUES LESS THAN( DATE '2025-01-01'));

create synonym xxrtv_nom_logg for xxrtv.xxrtv_nom_logg;

CREATE INDEX nom_logg_idx_u1 ON xxrtv.xxrtv_nom_logg (status) LOCAL;
CREATE INDEX nom_logg_idx_u2 ON xxrtv.xxrtv_nom_logg (operation, kall_retning);
CREATE INDEX nom_logg_idx_u3 ON xxrtv.xxrtv_nom_logg (korrelasjon_id);

CREATE  SEQUENCE xxrtv_nom_logg_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER xxrtv_nom_logg_trg
    BEFORE INSERT ON xxrtv.xxrtv_nom_logg
    FOR EACH ROW
BEGIN
    SELECT xxrtv_nom_logg_seq.nextval
    INTO   :new.kall_logg_id
    FROM   DUAL;
END;