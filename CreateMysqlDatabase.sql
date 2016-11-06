
DROP DATABASE AccountServer;
CREATE DATABASE AccountServer;
USE AccountServer;

DROP USER AccServUsr;
FLUSH PRIVILEGES;
CREATE USER AccServUsr IDENTIFIED BY 'password'; 

GRANT USAGE ON *.* TO 'AccServUsr'@'%' IDENTIFIED BY 'password'; 
GRANT ALL PRIVILEGES ON AccountServer.* TO 'AccServUsr'@'%'; 
