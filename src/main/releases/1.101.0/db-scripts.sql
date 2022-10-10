create table IF NOT EXISTS cicd_sample (
   ID INT PRIMARY KEY     NOT NULL,
   NAME           TEXT    NOT NULL,
   Description        CHAR(50));



INSERT INTO cicd_sample (ID, NAME, Description) VALUES (3, 'ramees', 'javaDeveloper')ON CONFLICT DO NOTHING;
