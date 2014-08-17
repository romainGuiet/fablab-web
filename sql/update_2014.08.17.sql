

--Add unique constraint on email field on user tableALTER TABLE `fablab`.`t_user` 
ALTER TABLE `fablab`.`t_user` ADD UNIQUE INDEX `email_UNIQUE` (`email` ASC);