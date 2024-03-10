CREATE USER replicator WITH REPLICATION ENCRYPTED PASSWORD 'Qwerty123';
SELECT pg_create_physical_replication_slot('replication_slot');
