drop table if exists discount_matrix_2;

create table discount_matrix_2(
    id bigint,
    microcategory_id int,
    location_id int,
    price int,
    found_price int,
    found_microcategory_id int,
    found_location_id int
);
CREATE INDEX idx_discount_matrix_2_hash ON discount_matrix_2 USING hash(id);
CREATE TRIGGER before_insert_discount_matrix_2 BEFORE INSERT ON discount_matrix_2 FOR EACH ROW EXECUTE FUNCTION set_matrix_id();

insert into discount_matrix_2 (microcategory_id, location_id, price)
values  (18, 20, 216);