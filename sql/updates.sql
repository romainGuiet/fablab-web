---------------------------------
--        08.08.2014           --
---------------------------------

--Add unique constraint on login field on user table
ALTER TABLE `fablab`.`t_user` ADD UNIQUE INDEX `login_UNIQUE` (`login` ASC);

---------------------------------
--        17.08.2014           --
---------------------------------

--Add unique constraint on email field on user tableALTER TABLE `fablab`.`t_user` 
ALTER TABLE `fablab`.`t_user` ADD UNIQUE INDEX `email_UNIQUE` (`email` ASC);