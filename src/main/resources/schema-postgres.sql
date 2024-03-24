

CREATE ROLE "holter-devops" WITH
  LOGIN
  NOSUPERUSER
  INHERIT
  NOCREATEDB
  NOCREATEROLE
  NOREPLICATION
  ENCRYPTED PASSWORD 'md5ca1d66c4b07a7baf45c799a4f47bfee3';

CREATE DATABASE "holter-devops"
    WITH
    OWNER = "holter-devops"
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

CREATE SCHEMA holter_ci AUTHORIZATION "holter-devops";



--------------------------------------------------------------
------------------------ tables ------------------------------
--------------------------------------------------------------


CREATE SEQUENCE holter_ci.metric_reference_values_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE holter_ci.metric_reference_values_id_seq OWNER TO "holter-devops";



CREATE TABLE IF NOT EXISTS holter_ci.metric_reference_values
(
    id bigint NOT NULL DEFAULT nextval('holter_ci.metric_reference_values_id_seq'::regclass),
    metric character varying(255) COLLATE pg_catalog."default" NOT NULL,
    value numeric(38,2) NOT NULL,
    CONSTRAINT measure_reference_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE holter_ci.metric_reference_values OWNER to "holter-devops";





CREATE SEQUENCE holter_ci.control_metric_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE holter_ci.control_metric_id_seq
    OWNER TO "holter-devops";



CREATE TABLE IF NOT EXISTS holter_ci.control_metric
(
    id bigint NOT NULL DEFAULT nextval('holter_ci.control_metric_id_seq'::regclass),
    n_developers numeric(38,2) NOT NULL DEFAULT 0,
    project_size numeric(38,2) NOT NULL DEFAULT 0,
    project_complexity numeric(38,2) NOT NULL DEFAULT 0,
    period_id bigint NOT NULL,
    technical_debt numeric(38,2) NOT NULL DEFAULT 0,
    CONSTRAINT general_metric_pkey PRIMARY KEY (id),
    CONSTRAINT fk_gm_period FOREIGN KEY (period_id)
        REFERENCES holter_ci.period (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)

TABLESPACE pg_default;

ALTER TABLE holter_ci.control_metric OWNER to "holter-devops";





CREATE SEQUENCE holter_ci.metric_history_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE holter_ci.metric_history_id_seq OWNER TO "holter-devops";


CREATE TABLE IF NOT EXISTS holter_ci.metric_history
(
    id bigint NOT NULL DEFAULT nextval('holter_ci.metric_history_id_seq'::regclass),
    period_id bigint NOT NULL,
    metric character varying(255) COLLATE pg_catalog."default" NOT NULL,
    value numeric(19,2) NOT NULL,
    CONSTRAINT measure_history_pkey PRIMARY KEY (id),
    CONSTRAINT fk9h3w0kdf1773f4docbx73ojov FOREIGN KEY (period_id)
        REFERENCES holter_ci.period (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE holter_ci.metric_history OWNER to "holter-devops";








CREATE SEQUENCE holter_ci.period_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE holter_ci.period_id_seq OWNER TO "holter-devops";


CREATE TABLE IF NOT EXISTS holter_ci.period
(
    id bigint NOT NULL DEFAULT nextval('holter_ci.period_id_seq'::regclass),
    init_p timestamp without time zone NOT NULL,
    end_p timestamp without time zone NOT NULL,
    project_id bigint NOT NULL,
    index integer NOT NULL,
    CONSTRAINT period_pkey PRIMARY KEY (id),
    CONSTRAINT fkej48148fgihow4ixqueehf9yv FOREIGN KEY (project_id)
        REFERENCES holter_ci.project (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE holter_ci.period OWNER to "holter-devops";





CREATE SEQUENCE holter_ci.project_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE holter_ci.project_id_seq OWNER TO "holter-devops";


CREATE TABLE IF NOT EXISTS holter_ci.project
(
    id bigint NOT NULL DEFAULT nextval('holter_ci.project_id_seq'::regclass),
    active boolean NOT NULL,
    name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    organization character varying(255) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT project_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE holter_ci.project OWNER to "holter-devops";









CREATE SEQUENCE holter_ci.project_configuration_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE holter_ci.project_configuration_id_seq OWNER TO "holter-devops";


CREATE TABLE IF NOT EXISTS holter_ci.project_configuration
(
    id bigint NOT NULL DEFAULT nextval('holter_ci.project_configuration_id_seq'::regclass),
    prodution_branch character varying(255) COLLATE pg_catalog."default" NOT NULL,
    main_repository_domain character varying(255) COLLATE pg_catalog."default" NOT NULL,
    main_repository_token character varying(255) COLLATE pg_catalog."default" NOT NULL,
    project_id bigint NOT NULL,
    secondary_repository_domain character varying(255) COLLATE pg_catalog."default",
    secondary_repository_token character varying(255) COLLATE pg_catalog."default",
    issues_erros_labels text COLLATE pg_catalog."default",
    secondary_repository_organization character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT project_configuration_pkey PRIMARY KEY (id),
    CONSTRAINT uk_fserys4sc7k295w86erainwlv UNIQUE (project_id),
    CONSTRAINT fk7vv9kt8iuqio1af1ebmjgrg1t FOREIGN KEY (project_id)
        REFERENCES holter_ci.project (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE holter_ci.project_configuration OWNER to "holter-devops";







CREATE SEQUENCE holter_ci.scheduler_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE holter_ci.scheduler_id_seq OWNER TO "holter-devops";



CREATE TABLE IF NOT EXISTS holter_ci.scheduler
(
    id bigint NOT NULL DEFAULT nextval('holter_ci.scheduler_id_seq'::regclass),
    automatic boolean NOT NULL,
    frequency smallint NOT NULL,
    last_execution timestamp without time zone,
    start_execution timestamp without time zone NOT NULL,
    project_id bigint,
    CONSTRAINT scheduler_pkey PRIMARY KEY (id),
    CONSTRAINT fkdlds16n9v1h05f63q9r9r0evq FOREIGN KEY (project_id)
        REFERENCES holter_ci.project (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE holter_ci.scheduler OWNER to "holter-devops";




--- MxN between scheduler and collection

CREATE TABLE IF NOT EXISTS holter_ci.scheduler_collectors
(
    scheduler_id bigint NOT NULL,
    collector_id uuid,
    CONSTRAINT fk5bgl4y4gpvhla4e4ram2vp25w FOREIGN KEY (scheduler_id)
        REFERENCES holter_ci.scheduler (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE holter_ci.scheduler_collectors OWNER to "holter-devops";










