/* Author: Kilian Lichtenauer */

INSERT INTO schwarzes_brett.category
VALUES (1, 'Autos', 'Alles rund ums auto', 0, 2),
       (2, 'Spielzeuge', '', 0, 2),
       (3, 'Dienstleistungen', '', 0, 2),
       (4, 'Autoreifen', '', 1, 2),
       (5, 'Autowaschmittel', '', 1, 0),
       (6, 'Große Autoreifen', '', 4, 0),
       (7, 'Kleine Autoreifen', '', 4, 0),
       (8, 'Legos', '', 2, 0),
       (9, 'Playmobil', '', 2, 0),
       (10, 'Outdoor', '', 2, 0),
       (11, 'Babysitter', '', 3, 0),
       (12, 'Hundesitter', '', 3, 0),
       (13, 'Kunst', '', 0, 1);

INSERT INTO schwarzes_brett.contact_data(id, first_name, last_name, e_mail, phone_number, country, city, postcode, street, house_number,
                                         address_suffix)
VALUES (100, NULL, NULL, 'testemail', NULL, 'germany', 'passau', '94032', NULL, NULL, NULL),
       (200, NULL, NULL, 'stefanmaier1@email.com', NULL, 'germany', 'passau', '94032', NULL, NULL, NULL),
       (300, NULL, NULL, 'testemail', NULL, 'germany', 'passau', '94032', NULL, NULL, NULL),
       (400, NULL, NULL, 'testemail', NULL, 'germany', 'passau', '94032', NULL, NULL, NULL),
       (500, NULL, NULL, 'testemail', NULL, 'germany', 'passau', '94032', NULL, NULL, NULL),
       (22, 'Hans', 'Stefan', 'stefanhans@email.com', NULL, 'Germany', 'Passau', '94032', 'Italien', '1', NULL);

INSERT INTO schwarzes_brett.user(id, nickname, user_role, password_hash, password_salt, language, verification_status, contact_data)
VALUES (200, 'Stefan1', 'USER',
        'a7a6385d3220560dea70d6053f6ce15bd014f649723b30b2e1f98adbb4add30be90a2839953f6b955bd941599706b213cd86abdbcb78891ac85c500336abc473',
        '58aeb285', 'DE', 'VERIFIED', 200),
       (300, 'Lisa4', 'USER', 'passwordhash', 'passwordsalt', 'DE', 'VERIFIED', 300),
       (32, 'Kilian2', 'ADMIN', 'passwordhash', 'passwordsalt', 'DE', 'VERIFIED', 400),
       (583, 'something', 'USER',
        'a7a6385d3220560dea70d6053f6ce15bd014f649723b30b2e1f98adbb4add30be90a2839953f6b955bd941599706b213cd86abdbcb78891ac85c500336abc473',
        '58aeb285', 'EN', 'REGISTERED_NOT_VERIFIED', 500);

INSERT INTO schwarzes_brett.user(id, nickname, user_role, password_hash, password_salt, language, verification_status, contact_data)
VALUES (592, 'Hans', 'USER',
        'a7a6385d3220560dea70d6053f6ce15bd014f649723b30b2e1f98adbb4add30be90a2839953f6b955bd941599706b213cd86abdbcb78891ac85c500336abc473',
        '58aeb285', 'de', 'VERIFIED', 22);


INSERT INTO schwarzes_brett.ad(id, title, value, currency, is_basis_of_negotiation, has_price, publishing_time, termination_time, category, creator,
                               contact, description)
VALUES (100, 'schöne kleine Autoreifen', 10, 'EUR', TRUE, TRUE, '2020-10-05 14:01:10-08', '2021-10-05 14:01:10-08', 7, 1, 100, 'Description'),
       (300, 'Gummi Hammer', 20, 'EUR', TRUE, TRUE, '2020-10-05 14:01:10-08', '2023-10-05 14:01:10-08', 2, 1, 100, 'Description'),
       (400, 'Riesen Gummi HAMMER', 20, 'EUR', TRUE, TRUE, '2020-10-05 14:01:10-08', '2023-10-05 14:01:10-08', 2, 1, 100, 'Description'),
       (500, 'Hüpfburg', 420, 'EUR', FALSE, TRUE, '2021-10-05 14:01:10-08', '2034-10-05 14:01:10-08', 2, 592, 100, 'Description'),
       (600, 'Mittelgroßer Autoreifen', 207, 'EUR', TRUE, TRUE, '2020-10-05 14:01:10-08', '2023-10-05 14:01:10-08', 4, 1, 100, 'Description'),
       (700, 'großer benutzer Schrank mit Türen', 10, 'EUR', TRUE, TRUE, '2020-10-05 14:01:10-08', '2023-10-05 14:01:10-08', 10, 200, 200, 'Holz'),
       (800, 'kleiner schöner Schrank mit Fächer', 20, 'EUR', TRUE, TRUE, '2020-10-05 14:01:10-08', '2021-10-05 14:01:10-08', 10, 200,
        200, 'Kunststoff'),
       (900, 'Skulptur', 30, 'EUR', TRUE, TRUE, '2020-10-05 14:01:10-08', '2023-10-05 14:01:10-08', 13, 300, 300, 'Schöner Marmor');
