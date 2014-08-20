SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema fablab
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `fablab` ;
CREATE SCHEMA IF NOT EXISTS `fablab` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `fablab` ;

-- -----------------------------------------------------
-- Table `fablab`.`t_membership_type`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fablab`.`t_membership_type` (
  `membership_type_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`membership_type_id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fablab`.`t_user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fablab`.`t_user` (
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `membership_type_id` INT NOT NULL,
  `auth_by_sql` TINYINT(1) NOT NULL,
  `login` VARCHAR(45) NOT NULL,
  `password` VARCHAR(64) NOT NULL,
  `firstname` VARCHAR(45) NOT NULL,
  `lastname` VARCHAR(45) NOT NULL,
  `email` VARCHAR(100) NULL,
  `date_inscr` DATETIME NOT NULL,
  `balance` FLOAT NOT NULL DEFAULT 0,
  `rfid` CHAR(16) NULL,
  `enabled` TINYINT(1) NOT NULL DEFAULT 1,
  `last_subscription_confirmation` DATETIME NULL,
  `phone` VARCHAR(45) NULL,
  `address` VARCHAR(200) NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `rfid_UNIQUE` (`rfid` ASC),
  INDEX `fk_t_users_t_membership_type1_idx` (`membership_type_id` ASC),
  UNIQUE INDEX `login_UNIQUE` (`login` ASC),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC),
  CONSTRAINT `fk_t_users_t_membership_type1`
    FOREIGN KEY (`membership_type_id`)
    REFERENCES `fablab`.`t_membership_type` (`membership_type_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fablab`.`t_machine_type`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fablab`.`t_machine_type` (
  `machine_type_id` INT NOT NULL AUTO_INCREMENT,
  `technicalname` VARCHAR(45) NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `restricted` TINYINT(1) NOT NULL,
  PRIMARY KEY (`machine_type_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fablab`.`t_machine`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fablab`.`t_machine` (
  `machine_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `machine_type_id` INT NOT NULL,
  PRIMARY KEY (`machine_id`),
  INDEX `fk_t_machines_t_machine_type1_idx` (`machine_type_id` ASC),
  CONSTRAINT `fk_t_machines_t_machine_type1`
    FOREIGN KEY (`machine_type_id`)
    REFERENCES `fablab`.`t_machine_type` (`machine_type_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fablab`.`t_reservation`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fablab`.`t_reservation` (
  `reservation_id` INT NOT NULL,
  `date_start` DATETIME NOT NULL,
  `date_end` DATETIME NOT NULL,
  `user_id` INT NOT NULL,
  `machine_id` INT NOT NULL,
  PRIMARY KEY (`reservation_id`),
  INDEX `fk_t_reservation_t_users_idx` (`user_id` ASC),
  INDEX `fk_t_reservation_t_machines1_idx` (`machine_id` ASC),
  CONSTRAINT `fk_t_reservation_t_users`
    FOREIGN KEY (`user_id`)
    REFERENCES `fablab`.`t_user` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_t_reservation_t_machines1`
    FOREIGN KEY (`machine_id`)
    REFERENCES `fablab`.`t_machine` (`machine_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fablab`.`t_price_revision`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fablab`.`t_price_revision` (
  `price_revision_id` INT NOT NULL AUTO_INCREMENT,
  `date_revision` DATETIME NOT NULL,
  `membership_duration` INT NOT NULL COMMENT 'in days',
  PRIMARY KEY (`price_revision_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fablab`.`r_price_machine`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fablab`.`r_price_machine` (
  `price_revision_id` INT NOT NULL,
  `machine_type_id` INT NOT NULL,
  `membership_type_id` INT NOT NULL,
  `price` FLOAT NOT NULL,
  PRIMARY KEY (`machine_type_id`, `membership_type_id`, `price_revision_id`),
  INDEX `fk_r_price_t_machine_type1_idx` (`machine_type_id` ASC),
  INDEX `fk_r_price_t_membership_type1_idx` (`membership_type_id` ASC),
  INDEX `fk_r_price_t_price_revisions1_idx` (`price_revision_id` ASC),
  CONSTRAINT `fk_r_price_t_machine_type1`
    FOREIGN KEY (`machine_type_id`)
    REFERENCES `fablab`.`t_machine_type` (`machine_type_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_r_price_t_membership_type1`
    FOREIGN KEY (`membership_type_id`)
    REFERENCES `fablab`.`t_membership_type` (`membership_type_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_r_price_t_price_revisions1`
    FOREIGN KEY (`price_revision_id`)
    REFERENCES `fablab`.`t_price_revision` (`price_revision_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fablab`.`t_payment`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fablab`.`t_payment` (
  `payment_id` INT NOT NULL AUTO_INCREMENT,
  `total` FLOAT NOT NULL,
  `date_payement` DATETIME NOT NULL,
  `user_id` INT NOT NULL,
  `cashier_id` INT NOT NULL,
  `comment` VARCHAR(255) NULL,
  PRIMARY KEY (`payment_id`),
  INDEX `fk_t_payment_t_users2_idx` (`user_id` ASC),
  INDEX `fk_t_payment_t_users1_idx` (`cashier_id` ASC),
  CONSTRAINT `fk_t_payment_t_users2`
    FOREIGN KEY (`user_id`)
    REFERENCES `fablab`.`t_user` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_t_payment_t_users1`
    FOREIGN KEY (`cashier_id`)
    REFERENCES `fablab`.`t_user` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fablab`.`t_usage`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fablab`.`t_usage` (
  `usage_id` INT NOT NULL AUTO_INCREMENT,
  `date_start` DATETIME NOT NULL,
  `minutes` INT(11) NOT NULL,
  `additional_cost` FLOAT NOT NULL,
  `user_id` INT NOT NULL,
  `machine_id` INT NOT NULL,
  `membership_type_id` INT NOT NULL,
  `price_revision_id` INT NOT NULL,
  `comment` VARCHAR(255) NULL,
  PRIMARY KEY (`usage_id`),
  INDEX `fk_t_utilisations_t_users1_idx` (`user_id` ASC),
  INDEX `fk_t_utilisations_t_machines1_idx` (`machine_id` ASC),
  INDEX `fk_t_usages_t_membership_type1_idx` (`membership_type_id` ASC),
  INDEX `fk_t_usages_t_price_revisions1_idx` (`price_revision_id` ASC),
  CONSTRAINT `fk_t_utilisations_t_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `fablab`.`t_user` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_t_utilisations_t_machines1`
    FOREIGN KEY (`machine_id`)
    REFERENCES `fablab`.`t_machine` (`machine_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_t_usages_t_membership_type1`
    FOREIGN KEY (`membership_type_id`)
    REFERENCES `fablab`.`t_membership_type` (`membership_type_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_t_usages_t_price_revisions1`
    FOREIGN KEY (`price_revision_id`)
    REFERENCES `fablab`.`t_price_revision` (`price_revision_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fablab`.`t_group`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fablab`.`t_group` (
  `group_id` INT NOT NULL AUTO_INCREMENT,
  `technicalname` VARCHAR(45) NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`group_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fablab`.`r_group_user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fablab`.`r_group_user` (
  `group_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  PRIMARY KEY (`group_id`, `user_id`),
  INDEX `fk_t_groups_has_t_users_t_groups1_idx` (`group_id` ASC),
  INDEX `fk_r_group_user_t_user1_idx` (`user_id` ASC),
  CONSTRAINT `fk_t_groups_has_t_users_t_groups1`
    FOREIGN KEY (`group_id`)
    REFERENCES `fablab`.`t_group` (`group_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_r_group_user_t_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `fablab`.`t_user` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fablab`.`t_audit`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fablab`.`t_audit` (
  `audit_id` INT NOT NULL AUTO_INCREMENT,
  `who` INT NULL,
  `action` INT NOT NULL,
  `object` INT NOT NULL,
  `object_id` INT NULL,
  `dateandtime` DATETIME NOT NULL,
  `success` TINYINT(1) NOT NULL,
  `content` VARCHAR(1000) NOT NULL,
  `detail` TEXT NULL,
  PRIMARY KEY (`audit_id`),
  INDEX `fk_t_audits_t_users1_idx` (`who` ASC),
  CONSTRAINT `fk_t_audits_t_users1`
    FOREIGN KEY (`who`)
    REFERENCES `fablab`.`t_user` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fablab`.`r_price_cotisation`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fablab`.`r_price_cotisation` (
  `price_revision_id` INT NOT NULL,
  `membership_type_id` INT NOT NULL,
  `price` FLOAT NOT NULL,
  PRIMARY KEY (`price_revision_id`, `membership_type_id`),
  INDEX `fk_r_price_cotisation_t_membership_type1_idx` (`membership_type_id` ASC),
  CONSTRAINT `fk_r_price_cotisation_t_price_revisions1`
    FOREIGN KEY (`price_revision_id`)
    REFERENCES `fablab`.`t_price_revision` (`price_revision_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_r_price_cotisation_t_membership_type1`
    FOREIGN KEY (`membership_type_id`)
    REFERENCES `fablab`.`t_membership_type` (`membership_type_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fablab`.`t_subscription`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fablab`.`t_subscription` (
  `subscription_id` INT NOT NULL AUTO_INCREMENT COMMENT 'Is like a usage. User has to pay for it',
  `user_id` INT NOT NULL,
  `price_revision_id` INT NOT NULL,
  `membership_type_id` INT NOT NULL,
  `date_subscription` DATETIME NOT NULL,
  `comment` VARCHAR(255) NULL,
  PRIMARY KEY (`subscription_id`),
  INDEX `fk_t_membership_t_users1_idx` (`user_id` ASC),
  INDEX `fk_t_membership_r_price_cotisation1_idx` (`price_revision_id` ASC, `membership_type_id` ASC),
  CONSTRAINT `fk_t_membership_t_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `fablab`.`t_user` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_t_membership_r_price_cotisation1`
    FOREIGN KEY (`price_revision_id` , `membership_type_id`)
    REFERENCES `fablab`.`r_price_cotisation` (`price_revision_id` , `membership_type_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fablab`.`EJB__TIMER__TBL`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fablab`.`EJB__TIMER__TBL` (
  `CREATIONTIMERAW` BIGINT NOT NULL,
  `BLOB` BLOB NULL,
  `TIMERID` VARCHAR(255) NOT NULL,
  `CONTAINERID` BIGINT NOT NULL,
  `OWNERID` VARCHAR(255) NULL,
  `STATE` INT(11) NOT NULL,
  `PKHASHCODE` INT(11) NOT NULL,
  `INTERVALDURATION` BIGINT NOT NULL,
  `INITIALEXPIRATIONRAW` BIGINT NOT NULL,
  `LASTEXPIRATIONRAW` BIGINT NOT NULL,
  `SCHEDULE` VARCHAR(255) NULL,
  `APPLICATIONID` BIGINT NOT NULL COMMENT 'Schedule table for glassfish',
  PRIMARY KEY (`TIMERID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fablab`.`t_system_status`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fablab`.`t_system_status` (
  `system_status_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `type` VARCHAR(255) NOT NULL,
  `content` TEXT NOT NULL,
  `last_check` DATETIME NOT NULL,
  `notify` TINYINT(1) NOT NULL,
  PRIMARY KEY (`system_status_id`),
  UNIQUE INDEX `name_UNIQUE` USING BTREE (`name` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fablab`.`r_user_authorized_machine_type`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fablab`.`r_user_authorized_machine_type` (
  `user_id` INT NOT NULL,
  `machine_type_id` INT NOT NULL,
  `formation_date` DATE NOT NULL,
  PRIMARY KEY (`user_id`, `machine_type_id`),
  INDEX `fk_t_user_has_t_machine_type_t_machine_type1_idx` (`machine_type_id` ASC),
  INDEX `fk_t_user_has_t_machine_type_t_user1_idx` (`user_id` ASC),
  CONSTRAINT `fk_t_user_has_t_machine_type_t_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `fablab`.`t_user` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_t_user_has_t_machine_type_t_machine_type1`
    FOREIGN KEY (`machine_type_id`)
    REFERENCES `fablab`.`t_machine_type` (`machine_type_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

USE `fablab` ;

-- -----------------------------------------------------
-- Placeholder table for view `fablab`.`v_group_user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fablab`.`v_group_user` (`login` INT, `group_id` INT, `user_id` INT, `technicalname` INT);

-- -----------------------------------------------------
-- Placeholder table for view `fablab`.`v_usage_detail`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fablab`.`v_usage_detail` (`usage_id` INT, `date_start` INT, `minutes` INT, `additional_cost` INT, `user_id` INT, `machine_id` INT, `membership_type_id` INT, `price_revision_id` INT, `comment` INT, `price` INT);

-- -----------------------------------------------------
-- View `fablab`.`v_group_user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `fablab`.`v_group_user`;
USE `fablab`;
CREATE  OR REPLACE VIEW `v_group_user` AS
	SELECT u.login, r.*, g.technicalname
	FROM r_group_user AS r
	INNER JOIN t_group AS g USING(group_id)
	INNER JOIN t_user AS u USING(user_id);

-- -----------------------------------------------------
-- View `fablab`.`v_usage_detail`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `fablab`.`v_usage_detail`;
USE `fablab`;
CREATE  OR REPLACE VIEW `v_usage_detail` AS
	SELECT u.*, p.price
	FROM t_usage AS u
	INNER JOIN t_user AS usr ON u.user_id = usr.user_id
	INNER JOIN t_machine AS m ON u.machine_id = m.machine_id
	INNER JOIN t_machine_type AS mt ON m.machine_type_id = mt.machine_type_id
	INNER JOIN r_price_machine AS p ON 
		p.machine_type_id = mt.machine_type_id AND
		p.price_revision_id=u.price_revision_id AND
		p.membership_type_id=u.membership_type_id
;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Data for table `fablab`.`t_membership_type`
-- -----------------------------------------------------
START TRANSACTION;
USE `fablab`;
INSERT INTO `fablab`.`t_membership_type` (`membership_type_id`, `name`) VALUES (1, 'extern');
INSERT INTO `fablab`.`t_membership_type` (`membership_type_id`, `name`) VALUES (2, 'normal');
INSERT INTO `fablab`.`t_membership_type` (`membership_type_id`, `name`) VALUES (3, 'student');
INSERT INTO `fablab`.`t_membership_type` (`membership_type_id`, `name`) VALUES (4, 'angel');

COMMIT;


-- -----------------------------------------------------
-- Data for table `fablab`.`t_user`
-- -----------------------------------------------------
START TRANSACTION;
USE `fablab`;
INSERT INTO `fablab`.`t_user` (`user_id`, `membership_type_id`, `auth_by_sql`, `login`, `password`, `firstname`, `lastname`, `email`, `date_inscr`, `balance`, `rfid`, `enabled`, `last_subscription_confirmation`, `phone`, `address`) VALUES (1, 3, 1, 'gaetan.collaud', '94ad0573ed0f4a7607f857e571a2d997f1208b868d7f580c646267b63fea25ec', 'Gaétan', 'Collaud', NULL, '2014-01-09', 0, '120050C9ED66', 1, NULL, NULL, NULL);
INSERT INTO `fablab`.`t_user` (`user_id`, `membership_type_id`, `auth_by_sql`, `login`, `password`, `firstname`, `lastname`, `email`, `date_inscr`, `balance`, `rfid`, `enabled`, `last_subscription_confirmation`, `phone`, `address`) VALUES (2, 2, 1, 'animator1', '94ad0573ed0f4a7607f857e571a2d997f1208b868d7f580c646267b63fea25ec', 'anim', 'ator', NULL, '2014-01-11', 0, '6F005CC09467', 1, NULL, NULL, NULL);
INSERT INTO `fablab`.`t_user` (`user_id`, `membership_type_id`, `auth_by_sql`, `login`, `password`, `firstname`, `lastname`, `email`, `date_inscr`, `balance`, `rfid`, `enabled`, `last_subscription_confirmation`, `phone`, `address`) VALUES (3, 2, 1, 'member1', '94ad0573ed0f4a7607f857e571a2d997f1208b868d7f580c646267b63fea25ec', 'mem', 'ber', NULL, '2014-01-11', 0, '02', 1, NULL, NULL, NULL);
INSERT INTO `fablab`.`t_user` (`user_id`, `membership_type_id`, `auth_by_sql`, `login`, `password`, `firstname`, `lastname`, `email`, `date_inscr`, `balance`, `rfid`, `enabled`, `last_subscription_confirmation`, `phone`, `address`) VALUES (4, 1, 1, 'extern1', '94ad0573ed0f4a7607f857e571a2d997f1208b868d7f580c646267b63fea25ec', 'extern', '1', NULL, '2014-01-11', 0, '654', 1, NULL, NULL, NULL);

COMMIT;


-- -----------------------------------------------------
-- Data for table `fablab`.`t_machine_type`
-- -----------------------------------------------------
START TRANSACTION;
USE `fablab`;
INSERT INTO `fablab`.`t_machine_type` (`machine_type_id`, `technicalname`, `name`, `restricted`) VALUES (1, '3dprinter', 'Imprimante 3D', 1);
INSERT INTO `fablab`.`t_machine_type` (`machine_type_id`, `technicalname`, `name`, `restricted`) VALUES (2, 'lasercutter', 'Decoupeuse Laser', 1);
INSERT INTO `fablab`.`t_machine_type` (`machine_type_id`, `technicalname`, `name`, `restricted`) VALUES (3, 'cnc', 'CNC', 1);
INSERT INTO `fablab`.`t_machine_type` (`machine_type_id`, `technicalname`, `name`, `restricted`) VALUES (4, 'other', 'Other', 0);

COMMIT;


-- -----------------------------------------------------
-- Data for table `fablab`.`t_machine`
-- -----------------------------------------------------
START TRANSACTION;
USE `fablab`;
INSERT INTO `fablab`.`t_machine` (`machine_id`, `name`, `machine_type_id`) VALUES (1, 'Ultimaker V1', 1);
INSERT INTO `fablab`.`t_machine` (`machine_id`, `name`, `machine_type_id`) VALUES (2, 'Ultimkaer V2', 1);
INSERT INTO `fablab`.`t_machine` (`machine_id`, `name`, `machine_type_id`) VALUES (3, 'MakerBot Replicatior 2', 1);
INSERT INTO `fablab`.`t_machine` (`machine_id`, `name`, `machine_type_id`) VALUES (4, 'Decoupeuse laser', 2);
INSERT INTO `fablab`.`t_machine` (`machine_id`, `name`, `machine_type_id`) VALUES (5, 'CNC chinoise 1', 3);
INSERT INTO `fablab`.`t_machine` (`machine_id`, `name`, `machine_type_id`) VALUES (6, 'CNC chinoise 2', 3);

COMMIT;


-- -----------------------------------------------------
-- Data for table `fablab`.`t_reservation`
-- -----------------------------------------------------
START TRANSACTION;
USE `fablab`;
INSERT INTO `fablab`.`t_reservation` (`reservation_id`, `date_start`, `date_end`, `user_id`, `machine_id`) VALUES (1, '2014-06-23 18:00', '2014-06-23 19:00', 3, 1);
INSERT INTO `fablab`.`t_reservation` (`reservation_id`, `date_start`, `date_end`, `user_id`, `machine_id`) VALUES (2, '2014-06-23 18:00', '2014-06-23 19:00', 3, 4);
INSERT INTO `fablab`.`t_reservation` (`reservation_id`, `date_start`, `date_end`, `user_id`, `machine_id`) VALUES (3, '2014-06-23 18:00', '2014-06-23 20:00', 1, 2);

COMMIT;


-- -----------------------------------------------------
-- Data for table `fablab`.`t_price_revision`
-- -----------------------------------------------------
START TRANSACTION;
USE `fablab`;
INSERT INTO `fablab`.`t_price_revision` (`price_revision_id`, `date_revision`, `membership_duration`) VALUES (1, '01.01.2014', 365);

COMMIT;


-- -----------------------------------------------------
-- Data for table `fablab`.`r_price_machine`
-- -----------------------------------------------------
START TRANSACTION;
USE `fablab`;
INSERT INTO `fablab`.`r_price_machine` (`price_revision_id`, `machine_type_id`, `membership_type_id`, `price`) VALUES (1, 1, 1, 10);
INSERT INTO `fablab`.`r_price_machine` (`price_revision_id`, `machine_type_id`, `membership_type_id`, `price`) VALUES (1, 1, 2, 7);
INSERT INTO `fablab`.`r_price_machine` (`price_revision_id`, `machine_type_id`, `membership_type_id`, `price`) VALUES (1, 1, 3, 5);
INSERT INTO `fablab`.`r_price_machine` (`price_revision_id`, `machine_type_id`, `membership_type_id`, `price`) VALUES (1, 1, 4, 5);
INSERT INTO `fablab`.`r_price_machine` (`price_revision_id`, `machine_type_id`, `membership_type_id`, `price`) VALUES (1, 2, 1, 40);
INSERT INTO `fablab`.`r_price_machine` (`price_revision_id`, `machine_type_id`, `membership_type_id`, `price`) VALUES (1, 2, 2, 20);
INSERT INTO `fablab`.`r_price_machine` (`price_revision_id`, `machine_type_id`, `membership_type_id`, `price`) VALUES (1, 2, 3, 10);
INSERT INTO `fablab`.`r_price_machine` (`price_revision_id`, `machine_type_id`, `membership_type_id`, `price`) VALUES (1, 2, 4, 10);
INSERT INTO `fablab`.`r_price_machine` (`price_revision_id`, `machine_type_id`, `membership_type_id`, `price`) VALUES (1, 3, 1, 40);
INSERT INTO `fablab`.`r_price_machine` (`price_revision_id`, `machine_type_id`, `membership_type_id`, `price`) VALUES (1, 3, 2, 20);
INSERT INTO `fablab`.`r_price_machine` (`price_revision_id`, `machine_type_id`, `membership_type_id`, `price`) VALUES (1, 3, 3, 10);
INSERT INTO `fablab`.`r_price_machine` (`price_revision_id`, `machine_type_id`, `membership_type_id`, `price`) VALUES (1, 3, 4, 10);
INSERT INTO `fablab`.`r_price_machine` (`price_revision_id`, `machine_type_id`, `membership_type_id`, `price`) VALUES (1, 4, 1, 0);
INSERT INTO `fablab`.`r_price_machine` (`price_revision_id`, `machine_type_id`, `membership_type_id`, `price`) VALUES (1, 4, 2, 0);
INSERT INTO `fablab`.`r_price_machine` (`price_revision_id`, `machine_type_id`, `membership_type_id`, `price`) VALUES (1, 4, 3, 0);
INSERT INTO `fablab`.`r_price_machine` (`price_revision_id`, `machine_type_id`, `membership_type_id`, `price`) VALUES (1, 4, 4, 0);

COMMIT;


-- -----------------------------------------------------
-- Data for table `fablab`.`t_group`
-- -----------------------------------------------------
START TRANSACTION;
USE `fablab`;
INSERT INTO `fablab`.`t_group` (`group_id`, `technicalname`, `name`) VALUES (1, 'comite', 'Comité');
INSERT INTO `fablab`.`t_group` (`group_id`, `technicalname`, `name`) VALUES (2, 'animator', 'Animateur');
INSERT INTO `fablab`.`t_group` (`group_id`, `technicalname`, `name`) VALUES (3, 'member', 'Membre');

COMMIT;


-- -----------------------------------------------------
-- Data for table `fablab`.`r_group_user`
-- -----------------------------------------------------
START TRANSACTION;
USE `fablab`;
INSERT INTO `fablab`.`r_group_user` (`group_id`, `user_id`) VALUES (1, 1);
INSERT INTO `fablab`.`r_group_user` (`group_id`, `user_id`) VALUES (2, 2);
INSERT INTO `fablab`.`r_group_user` (`group_id`, `user_id`) VALUES (3, 3);

COMMIT;


-- -----------------------------------------------------
-- Data for table `fablab`.`r_price_cotisation`
-- -----------------------------------------------------
START TRANSACTION;
USE `fablab`;
INSERT INTO `fablab`.`r_price_cotisation` (`price_revision_id`, `membership_type_id`, `price`) VALUES (1, 1, 0);
INSERT INTO `fablab`.`r_price_cotisation` (`price_revision_id`, `membership_type_id`, `price`) VALUES (1, 2, 100);
INSERT INTO `fablab`.`r_price_cotisation` (`price_revision_id`, `membership_type_id`, `price`) VALUES (1, 3, 50);
INSERT INTO `fablab`.`r_price_cotisation` (`price_revision_id`, `membership_type_id`, `price`) VALUES (1, 4, 500);

COMMIT;


-- -----------------------------------------------------
-- Data for table `fablab`.`r_user_authorized_machine_type`
-- -----------------------------------------------------
START TRANSACTION;
USE `fablab`;
INSERT INTO `fablab`.`r_user_authorized_machine_type` (`user_id`, `machine_type_id`, `formation_date`) VALUES (1, 1, '2014-01-01');

COMMIT;

