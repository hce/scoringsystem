--
-- PostgreSQL database dump
--

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: advisories; Type: TABLE; Schema: public; Owner: ctf; Tablespace: 
--

CREATE TABLE advisories (
    uid bigint NOT NULL,
    advisory_team character varying(255),
    advisory_description text DEFAULT ''::text,
    advisory_status character varying(255),
    advisory_time bigint,
    advisory_comment text,
    advisory_from character varying(255),
    advisory_generated boolean DEFAULT false
);


ALTER TABLE public.advisories OWNER TO ctf;

--
-- Name: flags; Type: TABLE; Schema: public; Owner: ctf; Tablespace: 
--

CREATE TABLE flags (
    uid bigint NOT NULL,
    flag_name character varying(255),
    flag_collected boolean,
    flag_collectingteam character varying(255),
    flag_team character varying(255),
    flag_service character varying(255),
    flag_teamhost character varying(255),
    flag_disttime bigint,
    flag_captured boolean DEFAULT false
);


ALTER TABLE public.flags OWNER TO ctf;

--
-- Name: flagstats; Type: TABLE; Schema: public; Owner: ctf; Tablespace: 
--

CREATE TABLE flagstats (
    uid bigint NOT NULL,
    flag_name character varying(255),
    flag_fromteam character varying(255),
    flag_collectingteam character varying(255),
    flag_service character varying(255)
);


ALTER TABLE public.flagstats OWNER TO ctf;

--
-- Name: services; Type: TABLE; Schema: public; Owner: ctf; Tablespace: 
--

CREATE TABLE services (
    uid bigint NOT NULL,
    service_name character varying(255),
    service_script character varying(255),
    service_script_type character varying(10) DEFAULT 'adela'::character varying,
    service_check_interval integer DEFAULT 300,
    service_flags_per_check integer DEFAULT 5
);


ALTER TABLE public.services OWNER TO ctf;

--
-- Name: states; Type: TABLE; Schema: public; Owner: ctf; Tablespace: 
--

CREATE TABLE states (
    uid bigint NOT NULL,
    status_team character varying(255),
    status_service character varying(255),
    status_text character varying(255),
    status_updated bigint,
    status_verboseerror character varying(255),
    status_color character varying(32)
);


ALTER TABLE public.states OWNER TO ctf;

--
-- Name: stats_points; Type: TABLE; Schema: public; Owner: ctf; Tablespace: 
--

CREATE TABLE stats_points (
    uid bigint NOT NULL,
    stats_time bigint,
    stats_team character varying(255),
    stats_points_offensive integer,
    stats_points_defensive integer,
    stats_points_advisory integer,
    stats_points_rulecompliance integer
);


ALTER TABLE public.stats_points OWNER TO ctf;

--
-- Name: stats_services; Type: TABLE; Schema: public; Owner: ctf; Tablespace: 
--

CREATE TABLE stats_services (
    uid bigint NOT NULL,
    stats_status character varying(255),
    stats_statusmsg character varying(255),
    stats_time bigint,
    stats_team character varying(255),
    stats_service character varying(255)
);


ALTER TABLE public.stats_services OWNER TO ctf;

--
-- Name: stats_times; Type: TABLE; Schema: public; Owner: ctf; Tablespace: 
--

CREATE TABLE stats_times (
    stats_time bigint NOT NULL
);


ALTER TABLE public.stats_times OWNER TO ctf;

--
-- Name: teams; Type: TABLE; Schema: public; Owner: ctf; Tablespace: 
--

CREATE TABLE teams (
    uid bigint NOT NULL,
    team_name character varying(255),
    team_points_offensive integer DEFAULT 0,
    team_points_defensive integer DEFAULT 0,
    team_points_advisories integer DEFAULT 0,
    team_points_total integer DEFAULT 0,
    team_host character varying(255),
    team_points_hacking integer DEFAULT 0
);


ALTER TABLE public.teams OWNER TO ctf;

--
-- Name: advisories_uid_seq; Type: SEQUENCE; Schema: public; Owner: ctf
--

CREATE SEQUENCE advisories_uid_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.advisories_uid_seq OWNER TO ctf;

--
-- Name: advisories_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ctf
--

ALTER SEQUENCE advisories_uid_seq OWNED BY advisories.uid;


--
-- Name: advisories_uid_seq; Type: SEQUENCE SET; Schema: public; Owner: ctf
--

SELECT pg_catalog.setval('advisories_uid_seq', 1, false);


--
-- Name: flags_uid_seq; Type: SEQUENCE; Schema: public; Owner: ctf
--

CREATE SEQUENCE flags_uid_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.flags_uid_seq OWNER TO ctf;

--
-- Name: flags_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ctf
--

ALTER SEQUENCE flags_uid_seq OWNED BY flags.uid;


--
-- Name: flags_uid_seq; Type: SEQUENCE SET; Schema: public; Owner: ctf
--

SELECT pg_catalog.setval('flags_uid_seq', 96, true);


--
-- Name: flagstats_uid_seq; Type: SEQUENCE; Schema: public; Owner: ctf
--

CREATE SEQUENCE flagstats_uid_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.flagstats_uid_seq OWNER TO ctf;

--
-- Name: flagstats_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ctf
--

ALTER SEQUENCE flagstats_uid_seq OWNED BY flagstats.uid;


--
-- Name: flagstats_uid_seq; Type: SEQUENCE SET; Schema: public; Owner: ctf
--

SELECT pg_catalog.setval('flagstats_uid_seq', 1, true);


--
-- Name: services_uid_seq; Type: SEQUENCE; Schema: public; Owner: ctf
--

CREATE SEQUENCE services_uid_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.services_uid_seq OWNER TO ctf;

--
-- Name: services_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ctf
--

ALTER SEQUENCE services_uid_seq OWNED BY services.uid;


--
-- Name: services_uid_seq; Type: SEQUENCE SET; Schema: public; Owner: ctf
--

SELECT pg_catalog.setval('services_uid_seq', 1, false);


--
-- Name: states_uid_seq; Type: SEQUENCE; Schema: public; Owner: ctf
--

CREATE SEQUENCE states_uid_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.states_uid_seq OWNER TO ctf;

--
-- Name: states_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ctf
--

ALTER SEQUENCE states_uid_seq OWNED BY states.uid;


--
-- Name: states_uid_seq; Type: SEQUENCE SET; Schema: public; Owner: ctf
--

SELECT pg_catalog.setval('states_uid_seq', 1097, true);


--
-- Name: stats_points_uid_seq; Type: SEQUENCE; Schema: public; Owner: ctf
--

CREATE SEQUENCE stats_points_uid_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.stats_points_uid_seq OWNER TO ctf;

--
-- Name: stats_points_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ctf
--

ALTER SEQUENCE stats_points_uid_seq OWNED BY stats_points.uid;


--
-- Name: stats_points_uid_seq; Type: SEQUENCE SET; Schema: public; Owner: ctf
--

SELECT pg_catalog.setval('stats_points_uid_seq', 330, true);


--
-- Name: stats_services_uid_seq; Type: SEQUENCE; Schema: public; Owner: ctf
--

CREATE SEQUENCE stats_services_uid_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.stats_services_uid_seq OWNER TO ctf;

--
-- Name: stats_services_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ctf
--

ALTER SEQUENCE stats_services_uid_seq OWNED BY stats_services.uid;


--
-- Name: stats_services_uid_seq; Type: SEQUENCE SET; Schema: public; Owner: ctf
--

SELECT pg_catalog.setval('stats_services_uid_seq', 856, true);


--
-- Name: teams_uid_seq; Type: SEQUENCE; Schema: public; Owner: ctf
--

CREATE SEQUENCE teams_uid_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.teams_uid_seq OWNER TO ctf;

--
-- Name: teams_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ctf
--

ALTER SEQUENCE teams_uid_seq OWNED BY teams.uid;


--
-- Name: teams_uid_seq; Type: SEQUENCE SET; Schema: public; Owner: ctf
--

SELECT pg_catalog.setval('teams_uid_seq', 1, false);


--
-- Name: uid; Type: DEFAULT; Schema: public; Owner: ctf
--

ALTER TABLE advisories ALTER COLUMN uid SET DEFAULT nextval('advisories_uid_seq'::regclass);


--
-- Name: uid; Type: DEFAULT; Schema: public; Owner: ctf
--

ALTER TABLE flags ALTER COLUMN uid SET DEFAULT nextval('flags_uid_seq'::regclass);


--
-- Name: uid; Type: DEFAULT; Schema: public; Owner: ctf
--

ALTER TABLE flagstats ALTER COLUMN uid SET DEFAULT nextval('flagstats_uid_seq'::regclass);


--
-- Name: uid; Type: DEFAULT; Schema: public; Owner: ctf
--

ALTER TABLE services ALTER COLUMN uid SET DEFAULT nextval('services_uid_seq'::regclass);


--
-- Name: uid; Type: DEFAULT; Schema: public; Owner: ctf
--

ALTER TABLE states ALTER COLUMN uid SET DEFAULT nextval('states_uid_seq'::regclass);


--
-- Name: uid; Type: DEFAULT; Schema: public; Owner: ctf
--

ALTER TABLE stats_points ALTER COLUMN uid SET DEFAULT nextval('stats_points_uid_seq'::regclass);


--
-- Name: uid; Type: DEFAULT; Schema: public; Owner: ctf
--

ALTER TABLE stats_services ALTER COLUMN uid SET DEFAULT nextval('stats_services_uid_seq'::regclass);


--
-- Name: uid; Type: DEFAULT; Schema: public; Owner: ctf
--

ALTER TABLE teams ALTER COLUMN uid SET DEFAULT nextval('teams_uid_seq'::regclass);


--
-- Data for Name: advisories; Type: TABLE DATA; Schema: public; Owner: ctf
--

COPY advisories (uid, advisory_team, advisory_description, advisory_status, advisory_time, advisory_comment, advisory_from, advisory_generated) FROM stdin;
\.


--
-- Data for Name: flags; Type: TABLE DATA; Schema: public; Owner: ctf
--

COPY flags (uid, flag_name, flag_collected, flag_collectingteam, flag_team, flag_service, flag_teamhost, flag_disttime, flag_captured) FROM stdin;
\.


--
-- Data for Name: flagstats; Type: TABLE DATA; Schema: public; Owner: ctf
--

COPY flagstats (uid, flag_name, flag_fromteam, flag_collectingteam, flag_service) FROM stdin;
\.


--
-- Data for Name: services; Type: TABLE DATA; Schema: public; Owner: ctf
--

COPY services (uid, service_name, service_script, service_script_type, service_check_interval, service_flags_per_check) FROM stdin;
\.


--
-- Data for Name: states; Type: TABLE DATA; Schema: public; Owner: ctf
--

COPY states (uid, status_team, status_service, status_text, status_updated, status_verboseerror, status_color) FROM stdin;
\.


--
-- Data for Name: stats_points; Type: TABLE DATA; Schema: public; Owner: ctf
--

COPY stats_points (uid, stats_time, stats_team, stats_points_offensive, stats_points_defensive, stats_points_advisory, stats_points_rulecompliance) FROM stdin;
\.


--
-- Data for Name: stats_services; Type: TABLE DATA; Schema: public; Owner: ctf
--

COPY stats_services (uid, stats_status, stats_statusmsg, stats_time, stats_team, stats_service) FROM stdin;
\.


--
-- Data for Name: stats_times; Type: TABLE DATA; Schema: public; Owner: ctf
--

COPY stats_times (stats_time) FROM stdin;
\.


--
-- Data for Name: teams; Type: TABLE DATA; Schema: public; Owner: ctf
--

COPY teams (uid, team_name, team_points_offensive, team_points_defensive, team_points_advisories, team_points_total, team_host, team_points_hacking) FROM stdin;
\.


--
-- Name: advisories_pkey; Type: CONSTRAINT; Schema: public; Owner: ctf; Tablespace: 
--

ALTER TABLE ONLY advisories
    ADD CONSTRAINT advisories_pkey PRIMARY KEY (uid);


--
-- Name: flags_flag_name_key; Type: CONSTRAINT; Schema: public; Owner: ctf; Tablespace: 
--

ALTER TABLE ONLY flags
    ADD CONSTRAINT flags_flag_name_key UNIQUE (flag_name);


--
-- Name: flags_pkey; Type: CONSTRAINT; Schema: public; Owner: ctf; Tablespace: 
--

ALTER TABLE ONLY flags
    ADD CONSTRAINT flags_pkey PRIMARY KEY (uid);


--
-- Name: flagstats_flag_name_key; Type: CONSTRAINT; Schema: public; Owner: ctf; Tablespace: 
--

ALTER TABLE ONLY flagstats
    ADD CONSTRAINT flagstats_flag_name_key UNIQUE (flag_name, flag_collectingteam);


--
-- Name: flagstats_pkey; Type: CONSTRAINT; Schema: public; Owner: ctf; Tablespace: 
--

ALTER TABLE ONLY flagstats
    ADD CONSTRAINT flagstats_pkey PRIMARY KEY (uid);


--
-- Name: services_pkey; Type: CONSTRAINT; Schema: public; Owner: ctf; Tablespace: 
--

ALTER TABLE ONLY services
    ADD CONSTRAINT services_pkey PRIMARY KEY (uid);


--
-- Name: services_service_name_key; Type: CONSTRAINT; Schema: public; Owner: ctf; Tablespace: 
--

ALTER TABLE ONLY services
    ADD CONSTRAINT services_service_name_key UNIQUE (service_name);


--
-- Name: states_pkey; Type: CONSTRAINT; Schema: public; Owner: ctf; Tablespace: 
--

ALTER TABLE ONLY states
    ADD CONSTRAINT states_pkey PRIMARY KEY (uid);


--
-- Name: states_status_team_key; Type: CONSTRAINT; Schema: public; Owner: ctf; Tablespace: 
--

ALTER TABLE ONLY states
    ADD CONSTRAINT states_status_team_key UNIQUE (status_team, status_service);


--
-- Name: stats_points_pkey; Type: CONSTRAINT; Schema: public; Owner: ctf; Tablespace: 
--

ALTER TABLE ONLY stats_points
    ADD CONSTRAINT stats_points_pkey PRIMARY KEY (uid);


--
-- Name: stats_services_pkey; Type: CONSTRAINT; Schema: public; Owner: ctf; Tablespace: 
--

ALTER TABLE ONLY stats_services
    ADD CONSTRAINT stats_services_pkey PRIMARY KEY (uid);


--
-- Name: stats_times_pkey; Type: CONSTRAINT; Schema: public; Owner: ctf; Tablespace: 
--

ALTER TABLE ONLY stats_times
    ADD CONSTRAINT stats_times_pkey PRIMARY KEY (stats_time);


--
-- Name: teams_pkey; Type: CONSTRAINT; Schema: public; Owner: ctf; Tablespace: 
--

ALTER TABLE ONLY teams
    ADD CONSTRAINT teams_pkey PRIMARY KEY (uid);


--
-- Name: teams_team_name_key; Type: CONSTRAINT; Schema: public; Owner: ctf; Tablespace: 
--

ALTER TABLE ONLY teams
    ADD CONSTRAINT teams_team_name_key UNIQUE (team_name);


--
-- Name: fki_; Type: INDEX; Schema: public; Owner: ctf; Tablespace: 
--

CREATE INDEX fki_ ON stats_services USING btree (stats_time);


--
-- Name: fki_servicename; Type: INDEX; Schema: public; Owner: ctf; Tablespace: 
--

CREATE INDEX fki_servicename ON stats_services USING btree (stats_service);


--
-- Name: fki_statsteamname; Type: INDEX; Schema: public; Owner: ctf; Tablespace: 
--

CREATE INDEX fki_statsteamname ON stats_points USING btree (stats_team);


--
-- Name: fki_teamname; Type: INDEX; Schema: public; Owner: ctf; Tablespace: 
--

CREATE INDEX fki_teamname ON stats_services USING btree (stats_team);


--
-- Name: idx_advisory_generated; Type: INDEX; Schema: public; Owner: ctf; Tablespace: 
--

CREATE INDEX idx_advisory_generated ON advisories USING btree (advisory_generated);


--
-- Name: idx_advisory_time; Type: INDEX; Schema: public; Owner: ctf; Tablespace: 
--

CREATE INDEX idx_advisory_time ON advisories USING btree (advisory_time);


--
-- Name: idx_flag_collected; Type: INDEX; Schema: public; Owner: ctf; Tablespace: 
--

CREATE INDEX idx_flag_collected ON flags USING btree (flag_collected);


--
-- Name: idx_flag_collectingteam; Type: INDEX; Schema: public; Owner: ctf; Tablespace: 
--

CREATE INDEX idx_flag_collectingteam ON flags USING btree (flag_collectingteam);


--
-- Name: idx_flag_service; Type: INDEX; Schema: public; Owner: ctf; Tablespace: 
--

CREATE INDEX idx_flag_service ON flags USING btree (flag_service);


--
-- Name: idx_flag_team; Type: INDEX; Schema: public; Owner: ctf; Tablespace: 
--

CREATE INDEX idx_flag_team ON flags USING btree (flag_team);


--
-- Name: idx_stats_time; Type: INDEX; Schema: public; Owner: ctf; Tablespace: 
--

CREATE INDEX idx_stats_time ON stats_points USING btree (stats_time);


--
-- Name: idx_time; Type: INDEX; Schema: public; Owner: ctf; Tablespace: 
--

CREATE INDEX idx_time ON flags USING btree (flag_disttime);


--
-- Name: idx_tpd; Type: INDEX; Schema: public; Owner: ctf; Tablespace: 
--

CREATE INDEX idx_tpd ON teams USING btree (team_points_defensive);


--
-- Name: idx_tpe; Type: INDEX; Schema: public; Owner: ctf; Tablespace: 
--

CREATE INDEX idx_tpe ON teams USING btree (team_points_advisories);


--
-- Name: idx_tpo; Type: INDEX; Schema: public; Owner: ctf; Tablespace: 
--

CREATE INDEX idx_tpo ON teams USING btree (team_points_offensive);


--
-- Name: idx_tpt; Type: INDEX; Schema: public; Owner: ctf; Tablespace: 
--

CREATE INDEX idx_tpt ON teams USING btree (team_points_total);


--
-- Name: servicename; Type: FK CONSTRAINT; Schema: public; Owner: ctf
--

ALTER TABLE ONLY stats_services
    ADD CONSTRAINT servicename FOREIGN KEY (stats_service) REFERENCES services(service_name);


--
-- Name: stats_services_stats_team_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ctf
--

ALTER TABLE ONLY stats_services
    ADD CONSTRAINT stats_services_stats_team_fkey FOREIGN KEY (stats_team) REFERENCES teams(team_name);


--
-- Name: stats_services_stats_time_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ctf
--

ALTER TABLE ONLY stats_services
    ADD CONSTRAINT stats_services_stats_time_fkey FOREIGN KEY (stats_time) REFERENCES stats_times(stats_time);


--
-- Name: statsteamname; Type: FK CONSTRAINT; Schema: public; Owner: ctf
--

ALTER TABLE ONLY stats_points
    ADD CONSTRAINT statsteamname FOREIGN KEY (stats_team) REFERENCES teams(team_name);


--
-- Name: statstimes; Type: FK CONSTRAINT; Schema: public; Owner: ctf
--

ALTER TABLE ONLY stats_points
    ADD CONSTRAINT statstimes FOREIGN KEY (stats_time) REFERENCES stats_times(stats_time);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

