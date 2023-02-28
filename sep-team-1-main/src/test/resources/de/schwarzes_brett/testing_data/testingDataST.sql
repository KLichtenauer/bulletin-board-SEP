/* Author: Kilian Lichtenauer */

INSERT INTO schwarzes_brett.category
VALUES (100{ID}, 'Kunst{ID}', '', 0, 0);

INSERT INTO schwarzes_brett.contact_data(id, first_name, last_name, e_mail, phone_number, country, city, postcode, street, house_number,
                                         address_suffix)
VALUES (200{ID}, 'Stefan', 'Maier', 'stefanmaier1@email.com{ID}', NULL, 'germany', 'passau', '94032', 'Stefansweg', '1', NULL),
       (300{ID}, 'Lisa', 'Intriga', 'intrigalisa@email.com{ID}', NULL, 'germany', 'passau', '94032', 'Italien', '1', NULL),
       (400{ID}, 'Kilian', 'Lichtenauer', 'kilian22lichtenauer@gmail.com{ID}', NULL, 'germany', 'passau', '94032', 'Frankldorfer', '22', NULL),
       (1200{ID}, 'Stefan', 'Maier', 'stefanmaier1@email.com{ID}', NULL, 'germany', 'passau', '94032', 'Stefansweg', '1', NULL),
       (1300{ID}, 'Lisa', 'Intriga', 'intrigalisa@email.com{ID}', NULL, 'germany', 'passau', '94032', 'Italien', '1', NULL),
       (1400{ID}, 'Kilian', 'Lichtenauer', 'kilian22lichtenauer@gmail.com{ID}', NULL, 'germany', 'passau', '94032', 'Frankldorfer', '22', NULL);

/*
Inserting users.
password Stefan1: 123456
password Lisa4: abcdefg
password Kilian2: 654321
*/
INSERT INTO schwarzes_brett.user(id, nickname, user_role, password_hash, password_salt, language, verification_status, contact_data)
VALUES (200{ID}, 'Stefan1{ID}', 'USER',
        'a7a6385d3220560dea70d6053f6ce15bd014f649723b30b2e1f98adbb4add30be90a2839953f6b955bd941599706b213cd86abdbcb78891ac85c500336abc473',
        '58aeb285', 'DE', 'VERIFIED', 200{ID}),
       (300{ID}, 'Lisa4{ID}', 'USER',
        '789c7299a8a09d26c2b7f6d08ad2a43219caa49ae19ef3c0123e279aea2720bcfae9bd2cc9fa6cc875108154fb8d88cbf8d4ca62362d63104bfe977321271332',
        'a4ff3bb9488b44c9631ef79b5faf92d6', 'DE', 'VERIFIED', 300{ID}),
       (400{ID}, 'Kilian2{ID}', 'ADMIN',
        'c2baec8b1beeec6b640547f3edf31826625a7581153f52e339cc0021f6f0d1962388ec697d8691b503e9875aad0b1551ef690c7b7b3b592225800f756533e417',
        'c253cae45a84f6330a10dd68e5a6e5dc', 'DE', 'VERIFIED', 400{ID});


INSERT INTO schwarzes_brett.ad(id, title, description, value, currency, is_basis_of_negotiation, has_price, publishing_time, termination_time,
                               category, creator,
                               contact)
VALUES (100{ID}, 'großer benutzter Schrank{ID} mit Türen', 'Holz{ID}!', 10, 'EUR', TRUE, TRUE, '2020-10-05 14:01:10-08', '2026-10-05 14:01:10-08', 0,
        200{ID}, 1200{ID}),
       (300{ID}, 'kleiner schöner Schrank{ID} mit Fächer', 'Kunststoff', 20, 'EUR', TRUE, TRUE, '2020-10-05 14:01:10-08', '2020-10-05 14:01:10-08', 0,
        200{ID}, 1200{ID}),
       (400{ID}, 'Skulptur', 'Schöner Marmor', 30, 'EUR', TRUE, TRUE, '2020-10-05 14:01:10-08', '2023-10-05 14:01:10-08', 100{ID}, 300{ID}, 1300{ID});
