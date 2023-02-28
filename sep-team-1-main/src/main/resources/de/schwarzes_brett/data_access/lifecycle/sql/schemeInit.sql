/* Author: Jonas Elsper */

/*
Creates schema if not exists for the webapplication.
*/
CREATE SCHEMA IF NOT EXISTS schwarzes_brett;

/*
All enum items of currency.
*/

/*
Creates an enum for the different user roles.
*/
CREATE TYPE schwarzes_brett.USER_ROLE AS ENUM ('USER', 'ADMIN');

/*
Creates an enum for all accepted currencies.
*/
CREATE TYPE schwarzes_brett.CURRENCY AS ENUM ('AED', 'AFN', 'ALL', 'AMD', 'ANG', 'AOA', 'ARS', 'AUD', 'AWG', 'AZN', 'BAM', 'BBD', 'BDT', 'BGN', 'BHD', 'BIF', 'BMD', 'BND', 'BOB', 'BRL', 'BSD', 'BTN', 'BWP', 'BYR', 'BZD', 'CAD', 'CDF', 'CHF', 'CLP', 'CNY', 'COP', 'CRC', 'CUC', 'CUP', 'CVE', 'CZK', 'DJF', 'DKK', 'DOP', 'DZD', 'EGP', 'ERN', 'ETB', 'EUR', 'FJD', 'FKP', 'GBP', 'GEL', 'GGP', 'GHS', 'GIP', 'GMD', 'GNF', 'GTQ', 'GYD', 'HKD', 'HNL', 'HRK', 'HTG', 'HUF', 'IDR', 'ILS', 'IMP', 'INR', 'IQD', 'IRR', 'ISK', 'JEP', 'JMD', 'JOD', 'JPY', 'KES', 'KGS', 'KHR', 'KMF', 'KPW', 'KRW', 'KWD', 'KYD', 'KZT', 'LAK', 'LBP', 'LKR', 'LRD', 'LSL', 'LYD', 'MAD', 'MDL', 'MGA', 'MKD', 'MMK', 'MNT', 'MOP', 'MRO', 'MUR', 'MVR', 'MWK', 'MXN', 'MYR', 'MZN', 'NAD', 'NGN', 'NIO', 'NOK', 'NPR', 'NZD', 'OMR', 'PAB', 'PEN', 'PGK', 'PHP', 'PKR', 'PLN', 'PYG', 'QAR', 'RON', 'RSD', 'RUB', 'RWF', 'SAR', 'SBD', 'SCR', 'SDG', 'SEK', 'SGD', 'SHP', 'SLL', 'SOS', 'SPL', 'SRD', 'STD', 'SVC', 'SYP', 'SZL', 'THB', 'TJS', 'TMT', 'TND', 'TOP', 'TRY', 'TTD', 'TVD', 'TWD', 'TZS', 'UAH', 'UGX', 'USD', 'UYU', 'UZS', 'VEF', 'VND', 'VUV', 'WST', 'XAF', 'XCD', 'XDR', 'XOF', 'XPF', 'YER', 'ZAR', 'ZMW', 'ZWD');

/*
Creates an enum for all verification status.
*/
CREATE TYPE schwarzes_brett.VERIFICATION_STATUS AS ENUM ('VERIFIED', 'NOT_REGISTERED', 'REGISTERED_NOT_VERIFIED');

/*
Table for contact data of a user.
*/
CREATE TABLE IF NOT EXISTS schwarzes_brett.contact_data
(
    id             SERIAL PRIMARY KEY,
    first_name     VARCHAR(30),
    last_name      VARCHAR(30),
    e_mail         VARCHAR(127) NOT NULL,
    phone_number   VARCHAR(20),
    country        VARCHAR(30)  NOT NULL,
    city           VARCHAR(168) NOT NULL,
    postcode       VARCHAR(20)  NOT NULL,
    street         VARCHAR(168),
    house_number   VARCHAR(20),
    address_suffix VARCHAR(30)
);

/*
Creates a Table for all system users
*/
CREATE TABLE IF NOT EXISTS schwarzes_brett.user
(
    id                                SERIAL PRIMARY KEY,
    nickname                          VARCHAR(255) UNIQUE                                    NOT NULL,
    avatar_image_oid                  OID,
    user_role                         schwarzes_brett.USER_ROLE                              NOT NULL,
    password_hash                     VARCHAR(512)                                           NOT NULL,
    password_salt                     VARCHAR(512)                                           NOT NULL,
    language                          VARCHAR(128)                                           NOT NULL,
    verification_status               schwarzes_brett.VERIFICATION_STATUS                    NOT NULL,
    verification_secret               VARCHAR(512),
    verification_secret_creation_time TIMESTAMP WITHOUT TIME ZONE,
    contact_data                      INTEGER REFERENCES schwarzes_brett.contact_data UNIQUE NOT NULL
);

/*
Trigger with procedure to delete a contact_data if user gets deleted
*/ CREATE OR REPLACE FUNCTION schwarzes_brett.delete_contact_data() RETURNS TRIGGER
    LANGUAGE plpgsql AS
$$
BEGIN
    IF new.contact_data IS NULL THEN DELETE FROM schwarzes_brett.contact_data WHERE id = old.contact_data; END IF;

    RETURN new;
END;
$$;

CREATE TRIGGER contact_data_changed
    AFTER DELETE
    ON schwarzes_brett.user
    FOR EACH ROW
EXECUTE PROCEDURE schwarzes_brett.delete_contact_data();

/*
Inserting admin user.
Default password: start123
*/
INSERT INTO schwarzes_brett.contact_data(e_mail, country, city, postcode)
VALUES ('email@example.com', 'Germany', 'Passau', '94032');
INSERT INTO schwarzes_brett.user(nickname, user_role, password_hash, password_salt, language, verification_status, contact_data)
VALUES ('admin', 'ADMIN',
        '6b5697e3278b6ea2ce354cd09199e4d27067bfb60ee787580c55f5f52daa0b23208115f02c7a2e90de8306fa7994514b6ee78a09402dff5da609ed26a67b41c7',
        '34c6a7f07077c515c5229aad81f81f5e', 'DE', 'VERIFIED', 1);

/*
Trigger that deletes the LOB representing the avatar image when the image gets updated or the user gets deleted.
*/

CREATE OR REPLACE FUNCTION schwarzes_brett.delete_avatar_lob() RETURNS TRIGGER
    LANGUAGE plpgsql AS
$$
BEGIN
    IF new.avatar_image_oid <> old.avatar_image_oid OR new.avatar_image_oid IS NULL THEN
        DELETE FROM schwarzes_brett.temp_img WHERE oid = old.avatar_image_oid;
    END IF;

    RETURN new;
END;
$$;

CREATE TRIGGER avatar_changed
    AFTER UPDATE OF avatar_image_oid OR DELETE
    ON schwarzes_brett.user
    FOR EACH ROW
EXECUTE PROCEDURE schwarzes_brett.delete_avatar_lob();

/*
Creates Table with application settings of the system.
*/
CREATE TABLE IF NOT EXISTS schwarzes_brett.application_settings
(
    id                   SERIAL PRIMARY KEY,
    operator_img         OID,
    operator_name        VARCHAR(255),
    operator_contact     TEXT,
    operator_description TEXT,
    imprint              TEXT,
    privacy_policy       TEXT,
    css_name             TEXT
);

CREATE OR REPLACE FUNCTION schwarzes_brett.delete_operator_img_lob() RETURNS TRIGGER
    LANGUAGE plpgsql AS
$$
BEGIN
    IF new.operator_img <> old.operator_img OR new.operator_img IS NULL THEN
        DELETE FROM schwarzes_brett.temp_img WHERE oid = old.operator_img;
    END IF;

    RETURN new;
END;
$$;

CREATE TRIGGER operator_img_changed
    AFTER UPDATE OF operator_img OR DELETE
    ON schwarzes_brett.application_settings
    FOR EACH ROW
EXECUTE PROCEDURE schwarzes_brett.delete_operator_img_lob();

/*
Insert placeholder settings.
*/
INSERT INTO schwarzes_brett.application_settings
VALUES (1, NULL, 'Schwarzes-Brett.de', 'Kontaktinformationen Text', 'Beschreibungstext', 'Impressumstext', 'Privacy Policy Text', 'style.css');


/*
Creates table for categories of ads.
*/
CREATE TABLE IF NOT EXISTS schwarzes_brett.category
(
    id            SERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    description   VARCHAR(511),
    parent_id     INT REFERENCES schwarzes_brett.category ON DELETE CASCADE,
    child_counter INTEGER DEFAULT 0
);

/*
Constraint that category-path-tree root node is unique.
Explanation:
The root node is characterised by the fact that it is the only node with
parent_category_name = null.
For that reason we need a constraint that parent_category_name = null is
unique and distinct.
But in PostgreSQL a unique null is not distinct, for this case we have to
create a index to ensure null is distinct.
*/
CREATE UNIQUE INDEX category_unique_null ON schwarzes_brett.category ((parent_id IS NULL)) WHERE parent_id IS NULL;

/*
Trigger for inserting a category
*/ CREATE OR REPLACE FUNCTION schwarzes_brett.insert_category() RETURNS TRIGGER
    LANGUAGE plpgsql AS
$$
BEGIN
    IF new.parent_id IS NOT NULL THEN
        UPDATE schwarzes_brett.category
        SET child_counter = (SELECT COUNT(*) FROM schwarzes_brett.category WHERE parent_id = new.parent_id)
        WHERE id = new.parent_id;
    END IF;

    RETURN new;
END;
$$;


CREATE TRIGGER category_inserted
    AFTER INSERT
    ON schwarzes_brett.category
    FOR EACH ROW
EXECUTE PROCEDURE schwarzes_brett.insert_category();

/*
Trigger for updating a category
*/ CREATE OR REPLACE FUNCTION schwarzes_brett.update_category() RETURNS TRIGGER
    LANGUAGE plpgsql AS
$$
BEGIN
    IF new.parent_id IS NOT NULL THEN
        UPDATE schwarzes_brett.category
        SET child_counter = (SELECT COUNT(*) FROM schwarzes_brett.category WHERE parent_id = old.parent_id)
        WHERE id = old.parent_id;
        UPDATE schwarzes_brett.category
        SET child_counter = (SELECT COUNT(*) FROM schwarzes_brett.category WHERE parent_id = new.parent_id)
        WHERE id = new.parent_id;
    END IF;

    RETURN new;
END;
$$;

CREATE TRIGGER category_updated
    AFTER UPDATE OF parent_id
    ON schwarzes_brett.category
    FOR EACH ROW
EXECUTE PROCEDURE schwarzes_brett.update_category();

/*
Trigger for deleting a category
*/ CREATE OR REPLACE FUNCTION schwarzes_brett.delete_category() RETURNS TRIGGER
    LANGUAGE plpgsql AS
$$
BEGIN
    IF old.parent_id IS NOT NULL THEN
        UPDATE schwarzes_brett.category
        SET child_counter = (SELECT COUNT(*) FROM schwarzes_brett.category WHERE parent_id = old.parent_id)
        WHERE id = old.parent_id;
    END IF;

    RETURN new;
END;
$$;

CREATE TRIGGER category_deleted
    AFTER DELETE
    ON schwarzes_brett.category
    FOR EACH ROW
EXECUTE PROCEDURE schwarzes_brett.delete_category();

/*
Inserting the category-path-tree root node.
*/
INSERT INTO schwarzes_brett.category(id, name)
VALUES (0, 'Root');

/*
Creates Table for ads.
*/
CREATE TABLE IF NOT EXISTS schwarzes_brett.ad
(
    id                      SERIAL PRIMARY KEY,
    title                   VARCHAR(255)                NOT NULL,
    description             VARCHAR(2047),
    value                   NUMERIC(20, 2)              NOT NULL,
    currency                schwarzes_brett.CURRENCY    NOT NULL,
    is_basis_of_negotiation BOOLEAN                     NOT NULL,
    has_price               BOOLEAN                     NOT NULL,
    publishing_time         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    termination_time        TIMESTAMP WITHOUT TIME ZONE,
    category                INT                         NOT NULL REFERENCES schwarzes_brett.category ON DELETE CASCADE,
    creator                 INT                         NOT NULL REFERENCES schwarzes_brett.user ON DELETE CASCADE,
    contact                 INT                         NOT NULL REFERENCES schwarzes_brett.contact_data ON DELETE CASCADE
);

/*
Trigger with procedure to delete a contact_data if ad gets deleted
*/ CREATE OR REPLACE FUNCTION schwarzes_brett.delete_ad_contact_data() RETURNS TRIGGER
    LANGUAGE plpgsql AS
$$
BEGIN
    IF new.contact IS NULL THEN DELETE FROM schwarzes_brett.contact_data WHERE id = old.contact; END IF;

    RETURN new;
END;
$$;

CREATE TRIGGER contact_data_changed
    AFTER DELETE
    ON schwarzes_brett.ad
    FOR EACH ROW
EXECUTE PROCEDURE schwarzes_brett.delete_ad_contact_data();

/*
Creates table for product images of ads.
*/
CREATE TABLE IF NOT EXISTS schwarzes_brett.image
(
    id           SERIAL PRIMARY KEY,
    image_oid    OID UNIQUE NOT NULL,
    is_thumbnail BOOLEAN    NOT NULL,
    ad_id        INT        NOT NULL REFERENCES schwarzes_brett.ad ON DELETE CASCADE
);

/*
Trigger with procedure to delete a LOB if the OID of a image gets updated
or deleted
*/ CREATE OR REPLACE FUNCTION schwarzes_brett.delete_image_lob() RETURNS TRIGGER
    LANGUAGE plpgsql AS
$$
BEGIN
    IF new.image_oid <> old.image_oid OR new.image_oid IS NULL THEN DELETE FROM schwarzes_brett.temp_img WHERE oid = old.image_oid; END IF;

    RETURN new;
END;
$$;

CREATE TRIGGER image_changed
    AFTER UPDATE OF image_oid OR DELETE
    ON schwarzes_brett.image
    FOR EACH ROW
EXECUTE PROCEDURE schwarzes_brett.delete_image_lob();

/*
Creates table for messages.
*/
CREATE TABLE IF NOT EXISTS schwarzes_brett.message
(
    id                  SERIAL PRIMARY KEY,
    time_of_publication TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    content             VARCHAR(2047)               NOT NULL,
    is_public           BOOLEAN                     NOT NULL,
    is_anonymous        BOOLEAN                     NOT NULL,
    author              INT                         NOT NULL REFERENCES schwarzes_brett.user ON DELETE CASCADE,
    addressee           INT                         NOT NULL REFERENCES schwarzes_brett.user ON DELETE CASCADE,
    ad_id               INT                         NOT NULL REFERENCES schwarzes_brett.ad ON DELETE CASCADE
);

/*
Creates table for users who follow other users.
*/
CREATE TABLE IF NOT EXISTS schwarzes_brett.follow
(
    following_user INT REFERENCES schwarzes_brett.user ON DELETE CASCADE,
    followed_user  INT REFERENCES schwarzes_brett.user ON DELETE CASCADE,
    PRIMARY KEY (following_user, followed_user)
);

/*
Creates table for all abonnements.
*/
CREATE TABLE IF NOT EXISTS schwarzes_brett.abonnement
(
    "user" INT REFERENCES schwarzes_brett.user ON DELETE CASCADE,
    ad     INT REFERENCES schwarzes_brett.ad ON DELETE CASCADE,
    PRIMARY KEY ("user", ad)
);

/*
Creates Table for user ratings.
*/
CREATE TABLE IF NOT EXISTS schwarzes_brett.rating
(
    id          SERIAL PRIMARY KEY,
    rated_user  INT     NOT NULL REFERENCES schwarzes_brett.user ON DELETE CASCADE,
    rating_user INT     NOT NULL REFERENCES schwarzes_brett.user ON DELETE CASCADE,
    valuation   INTEGER NOT NULL,
    UNIQUE (rated_user, rating_user)
);

/*
Creates Table for temporary saved images.
*/
CREATE TABLE IF NOT EXISTS schwarzes_brett.temp_img
(
    oid      OID PRIMARY KEY,
    creation TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

/*
Delete LO link to deleted oid from temp_img
*/ CREATE OR REPLACE FUNCTION schwarzes_brett.delete_temp_img_lob() RETURNS TRIGGER
    LANGUAGE plpgsql AS
$$
BEGIN
    IF new.oid <> old.oid OR new.oid IS NULL THEN PERFORM lo_unlink(old.oid); END IF;

    RETURN new;
END;
$$;

CREATE TRIGGER image_changed
    AFTER UPDATE OF oid OR DELETE
    ON schwarzes_brett.temp_img
    FOR EACH ROW
EXECUTE PROCEDURE schwarzes_brett.delete_temp_img_lob();
