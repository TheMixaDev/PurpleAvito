drop table if exists discount_matrix_3;

create table discount_matrix_3(
    id bigint,
    microcategory_id int,
    location_id int,
    price int,
    found_price int,
    found_microcategory_id int,
    found_location_id int
);
CREATE INDEX idx_discount_matrix_3_hash ON discount_matrix_3 USING hash(id);
CREATE TRIGGER before_insert_discount_matrix_3 BEFORE INSERT ON discount_matrix_3 FOR EACH ROW EXECUTE FUNCTION set_matrix_id();

insert into discount_matrix_3 (microcategory_id, location_id, price)
values  (1, 1, 1);