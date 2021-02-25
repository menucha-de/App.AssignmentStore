CREATE SEQUENCE IF NOT EXISTS tag_sequence;
CREATE SEQUENCE IF NOT EXISTS location_sequence;

CREATE TABLE IF NOT EXISTS tag
(
    id INTEGER NOT NULL default tag_sequence.nextval,
    epc VARCHAR(255) NOT NULL,
    tid VARCHAR(255),
    label VARCHAR(255) NOT NULL,
    CONSTRAINT pk_tag_id PRIMARY KEY (id),
    CONSTRAINT uq_tag_epc UNIQUE (epc),
    CONSTRAINT uq_tag_label UNIQUE (label)
);

CREATE TABLE IF NOT EXISTS location
(
    id INTEGER NOT NULL default location_sequence.nextval,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_location_id PRIMARY KEY (id),
    CONSTRAINT uq_location_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS assignment
(
    location_id integer NOT NULL,
    tag_id integer NOT NULL,
    CONSTRAINT uq_assignment_lid_tid UNIQUE (location_id, tag_id),
    CONSTRAINT fk_assignment_lid FOREIGN KEY (location_id) REFERENCES public.location (id) 
        ON UPDATE NO ACTION 
        ON DELETE NO ACTION,
    CONSTRAINT fk_assignment_tid FOREIGN KEY (tag_id) REFERENCES public.tag (id) 
        ON UPDATE NO ACTION 
        ON DELETE CASCADE
);