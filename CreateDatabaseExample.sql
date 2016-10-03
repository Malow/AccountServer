
DROP DATABASE AccountServer;
CREATE DATABASE AccountServer;
USE AccountServer;

DROP USER AccServUsr;
FLUSH PRIVILEGES;
CREATE USER AccServUsr IDENTIFIED BY 'password'; 

GRANT USAGE ON *.* TO 'AccServUsr'@'%' IDENTIFIED BY 'password'; 
GRANT ALL PRIVILEGES ON AccountServer.* TO 'AccServUsr'@'%'; 

CREATE TABLE Accounts (id INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(40) NOT NULL UNIQUE,
    PASSWORD VARCHAR(100) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    pw_reset_token VARCHAR(50),
    failed_login_attempts INT NOT NULL,
    PRIMARY KEY (ID));