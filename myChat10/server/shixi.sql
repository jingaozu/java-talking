CREATE DATABASE shixi;

create user 'shixiuser'@'%' identified by 'shixipass';
grant all privileges on shixi.* to shixiuser@'%' identified by 'shixipass';
flush privileges; 

USE shixi;
DROP TABLE IF EXISTS roomUser;
DROP TABLE IF EXISTS room;
DROP TABLE IF EXISTS user;

CREATE TABLE user(
	name nvarchar(50) PRIMARY KEY ,
	password nvarchar(50) NOT NULL ,
	online int default 0
);


CREATE TABLE room(
	port int PRIMARY KEY,
	master nvarchar(50) NOT NULL,
	ip nvarchar(50) NOT NULL,
	date timestamp default CURRENT_TIMESTAMP
);

CREATE TABLE roomUser(
	roomPort int NOT NULL,
	roomUser nvarchar(50) NOT NULL
);

alter table room add foreign key(master) references user(name);
alter table roomUser add foreign key(roomPort) references room(port);
alter table roomUser add foreign key(roomUser) references user(name);

CREATE UNIQUE INDEX index_roomUser ON roomUser(roomPort,roomUser);


