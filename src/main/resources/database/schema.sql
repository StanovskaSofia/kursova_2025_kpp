DROP DATABASE IF EXISTS Readile;
CREATE DATABASE Readile;
USE Readile;

CREATE TABLE IF NOT EXISTS User_Profile
(
    id            INT PRIMARY KEY AUTO_INCREMENT,
    name          VARCHAR(64)        NOT NULL,
    email         VARCHAR(64) UNIQUE NOT NULL,
    theme         TINYINT DEFAULT 1,
    profile_image VARCHAR(1024),
    registration  VARCHAR(6)
);

CREATE TABLE IF NOT EXISTS Book_Catalog
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(128)  NOT NULL,
    cover_id    VARCHAR(1024) NOT NULL,
    length      INT           NOT NULL,
    authors     VARCHAR(1024) NOT NULL,
    description VARCHAR(1024) NOT NULL
    );

CREATE TABLE IF NOT EXISTS User_Book
(
    id           INT PRIMARY KEY AUTO_INCREMENT,
    user_id      INT           NOT NULL,
    book_id      INT           NOT NULL,
    current_page INT           NOT NULL DEFAULT 0,
    start_date   DATE                     DEFAULT NULL,
    end_date     DATE                     DEFAULT NULL,
    rating       ENUM ('ONE_STAR', 'TWO_STARS', 'THREE_STARS', 'FOUR_STARS', 'FIVE_STARS') DEFAULT 'ONE_STAR',
    status       ENUM ('TO_READ', 'CURRENTLY_READING', 'READ')                             DEFAULT 'TO_READ',
    UNIQUE (user_id, book_id),
    FOREIGN KEY (user_id) REFERENCES User_Profile (id),
    FOREIGN KEY (book_id) REFERENCES Book_Catalog (id)
    );


CREATE TABLE IF NOT EXISTS Highlight
(
    id            INT PRIMARY KEY AUTO_INCREMENT,
    user_book_id  INT,
    highlight     VARCHAR(512) NOT NULL,
    FOREIGN KEY (user_book_id) REFERENCES User_Book (id)
);

CREATE TABLE IF NOT EXISTS Category
(
    id             INT PRIMARY KEY AUTO_INCREMENT,
    name           VARCHAR(32) NOT NULL,
    category_image VARCHAR(1024),
    user_id        INT,
    FOREIGN KEY (user_id) REFERENCES User_Profile (id)
);

CREATE TABLE IF NOT EXISTS Book_Category
(
    category_id INT,
    book_id     INT,
    PRIMARY KEY (category_id, book_id),
    FOREIGN KEY (category_id) REFERENCES Category (id),
    FOREIGN KEY (book_id) REFERENCES Book_Catalog (id)
);

CREATE TABLE IF NOT EXISTS Login_Info
(
    id       INT PRIMARY KEY AUTO_INCREMENT,
    user_id  INT          NOT NULL UNIQUE,
    password VARCHAR(256) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User_Profile (id)
);

DELIMITER $$
CREATE TRIGGER encrypt_password_on_insert
    BEFORE INSERT
    ON Login_Info
    FOR EACH ROW
BEGIN
    SET NEW.password = MD5(NEW.password);
END $$

CREATE TRIGGER encrypt_password_on_update
    BEFORE UPDATE
    ON Login_Info
    FOR EACH ROW
BEGIN
    SET NEW.password = MD5(NEW.password);
END $$