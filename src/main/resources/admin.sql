


---------
---------
---------


-- $2a$12$uUVAII/akLAynDZaOjCOrezITF8yvaeHFIEBCnUzUvT1amlIZraOO = BCryptPasswordEncoder da senha "jadson.santos@ufrn.br"
INSERT INTO HOLTER.USER_ (email, password) VALUES ('jadson.santos@ufrn.br', '$2a$12$uUVAII/akLAynDZaOjCOrezITF8yvaeHFIEBCnUzUvT1amlIZraOO');

INSERT INTO  HOLTER.PERMISSION  (role, user_id) VALUES ('ADMIN', (SELECT id FROM HOLTER.USER_ WHERE email = 'jadson.santos@ufrn.br'));

UPDATE  HOLTER.USER_ SET alert = true WHERE email = 'jadson.santos@ufrn.br';

