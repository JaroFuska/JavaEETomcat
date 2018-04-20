alter table dp_users change teacher TEACHER boolean;
insert into dp_exercises (text) values('This is first exercise');
SELECT count(*) as total from dp_exercises;
insert into dp_exercises (exercise_id, text) values(2, 'This is first exercise');

drop table dp_user_files;


drop table dp_exercise_files;




CREATE TABLE `DP_USER_FILES` (
	`USER_ID` INT NOT NULL,
	`EXERCISE_ID` INT NOT NULL,
	`FILE` blob NOT NULL,
	`VERSION` INT NOT NULL,
	`PATH` varchar(511) NOT NULL
);



CREATE TABLE `DP_EXERCISE_FILES` (
	`EXERCISE_ID` INT NOT NULL,
	`FILE` blob NOT NULL,
	`PATH` varchar(511) NOT NULL,
	`TEXT` varchar(1023) NOT NULL
);