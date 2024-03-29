insert into source_baseline (name, ready) values ('baseline_matrix_1', true), ('baseline_matrix_2', true), ('baseline_matrix_3', true);
insert into discount_baseline (name, ready) values ('discount_matrix_1', true), ('discount_matrix_2', true), ('discount_matrix_3', true);

/* insert into current_baseline_matrix (id, name) values (1, 'baseline_matrix_1'); */

ALTER TABLE baseline_matrix_1 ADD CONSTRAINT baseline_matrix_1_pkey PRIMARY KEY (location_id, microcategory_id);
ALTER TABLE baseline_matrix_2 ADD CONSTRAINT baseline_matrix_2_pkey PRIMARY KEY (location_id, microcategory_id);
ALTER TABLE baseline_matrix_3 ADD CONSTRAINT baseline_matrix_3_pkey PRIMARY KEY (location_id, microcategory_id);
ALTER TABLE discount_matrix_1 ADD CONSTRAINT discount_matrix_1_pkey PRIMARY KEY (location_id, microcategory_id);
ALTER TABLE discount_matrix_2 ADD CONSTRAINT discount_matrix_2_pkey PRIMARY KEY (location_id, microcategory_id);
ALTER TABLE discount_matrix_3 ADD CONSTRAINT discount_matrix_3_pkey PRIMARY KEY (location_id, microcategory_id);

/*
insert into discount_segments (id, name)
values (156, 'discount_matrix_1'),
       (278, 'discount_matrix_2'),
       (168, 'discount_matrix_3'),
       (290, null),
       (412, null),
       (180, null),
       (192, null),
       (314, null),
       (436, null),
       (158, null),
       (204, null),
       (326, null),
       (148, null),
       (370, null),
       (592, null),
       (216, null),
       (228, null),
       (350, null),
       (472, null),
       (194, null),
       (240, null),
       (362, null),
       (484, null),
       (206, null),
       (428, null),
       (252, null),
       (374, null),
       (264, null),
       (386, null),
       (508, null),
       (230, null),
       (276, null),
       (398, null),
       (288, null),
       (410, null),
       (532, null),
       (254, null),
       (300, null),
       (422, null),
       (544, null),
       (166, null),
       (312, null),
       (434, null),
       (324, null),
       (446, null),
       (568, null),
       (190, null),
       (336, null),
       (458, null),
       (348, null),
       (470, null),
       (214, null),
       (360, null),
       (482, null),
       (604, null),
       (226, null),
       (372, null),
       (494, null),
       (616, null),
       (238, null),
       (384, null),
       (506, null),
       (628, null),
       (250, null),
       (396, null),
       (518, null),
       (640, null),
       (262, null);
*/