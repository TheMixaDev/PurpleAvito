CREATE USER replicator WITH REPLICATION ENCRYPTED PASSWORD 'Qwerty123';
SELECT * FROM pg_create_physical_replication_slot('replication_slot1');
SELECT * FROM pg_create_physical_replication_slot('replication_slot2');
