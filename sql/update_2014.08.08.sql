--Add unique constraint on login field on user table
ALTER TABLE `fablab`.`t_user` ADD UNIQUE INDEX `login_UNIQUE` (`login` ASC);

