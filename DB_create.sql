create database DP;
use DP;

CREATE TABLE `DP_USERS` (
	`USER_ID` INT NOT NULL AUTO_INCREMENT,
	`FIRST_NAME` varchar(255) NOT NULL,
	`LAST_NAME` varchar(255) NOT NULL,
	`EMAIL` varchar(255) NOT NULL UNIQUE,
	`LOGIN` varchar(31) NOT NULL UNIQUE,
	`PASSWORD` varchar(31) NOT NULL,
	PRIMARY KEY (`USER_ID`)
);

CREATE TABLE `DP_EXERCISES` (
	`EXERCISE_ID` INT NOT NULL AUTO_INCREMENT,
	`TEXT` varchar(1023) NOT NULL,
	PRIMARY KEY (`EXERCISE_ID`)
);

CREATE TABLE `DP_EXERCISE_FILES` (
	`EXERCISE_ID` INT NOT NULL,
	`FILE` blob NOT NULL,
	`TEST` BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE `DP_USER_FILES` (
	`USER_ID` INT NOT NULL,
	`EXERCISE_ID` INT NOT NULL,
	`VERSION` INT NOT NULL
);

CREATE TABLE `DP_FILES` (
	`FILE` blob NOT NULL,
	`EXERCISE_ID` INT NOT NULL,
	`VERSION` INT NOT NULL
);

ALTER TABLE `DP_EXERCISE_FILES` ADD CONSTRAINT `DP_EXERCISE_FILES_fk0` FOREIGN KEY (`EXERCISE_ID`) REFERENCES `DP_EXERCISES`(`EXERCISE_ID`);

ALTER TABLE `DP_USER_FILES` ADD CONSTRAINT `DP_USER_FILES_fk0` FOREIGN KEY (`USER_ID`) REFERENCES `DP_USERS`(`USER_ID`);

ALTER TABLE `DP_USER_FILES` ADD CONSTRAINT `DP_USER_FILES_fk1` FOREIGN KEY (`EXERCISE_ID`) REFERENCES `DP_EXERCISES`(`EXERCISE_ID`);

