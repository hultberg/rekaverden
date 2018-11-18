-- Adminer 4.6.3 MySQL dump

SET NAMES utf8;
SET time_zone = '+00:00';
SET foreign_key_checks = 0;
SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';

DROP TABLE IF EXISTS `r_blocklog`;
CREATE TABLE `r_blocklog` (
  `uid` smallint(6) NOT NULL,
  `x` mediumint(9) NOT NULL,
  `y` smallint(6) NOT NULL,
  `z` mediumint(9) NOT NULL,
  `world` varchar(60) NOT NULL,
  `block_id` smallint(6) NOT NULL,
  `new_block_id` int(11) NOT NULL,
  `block_data` smallint(6) NOT NULL,
  `timestamp` int(11) NOT NULL,
  `action` tinyint(1) NOT NULL DEFAULT 0,
  KEY `uid` (`uid`),
  KEY `location` (`x`,`y`,`z`,`world`),
  KEY `new_block_id` (`new_block_id`),
  CONSTRAINT `r_blocklog_ibfk_1` FOREIGN KEY (`new_block_id`) REFERENCES `r_blocklog_block_types` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `r_blocklog_block_types`;
CREATE TABLE `r_blocklog_block_types` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `material_name` varchar(150) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `material_name` (`material_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `r_blocks`;
CREATE TABLE `r_blocks` (
  `uid` smallint(11) NOT NULL,
  `x` mediumint(9) NOT NULL,
  `y` smallint(6) NOT NULL,
  `z` mediumint(9) NOT NULL,
  `world` varchar(60) NOT NULL,
  UNIQUE KEY `x` (`x`,`y`,`z`,`world`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `r_groups`;
CREATE TABLE `r_groups` (
  `group_ID` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(160) NOT NULL,
  `owner` int(11) NOT NULL,
  PRIMARY KEY (`group_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `r_group_invitations`;
CREATE TABLE `r_group_invitations` (
  `invite_ID` int(11) NOT NULL AUTO_INCREMENT,
  `invited` mediumint(9) NOT NULL,
  `invitee` mediumint(9) NOT NULL,
  `to_group` mediumint(9) NOT NULL,
  PRIMARY KEY (`invite_ID`),
  UNIQUE KEY `togroup` (`invited`,`to_group`),
  KEY `to_group` (`to_group`),
  KEY `invited` (`invited`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `r_group_membership`;
CREATE TABLE `r_group_membership` (
  `user_ID` mediumint(9) NOT NULL,
  `group_ID` mediumint(9) NOT NULL,
  KEY `data` (`user_ID`,`group_ID`),
  KEY `group_ID` (`group_ID`),
  KEY `user_ID` (`user_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `r_users`;
CREATE TABLE `r_users` (
  `uid` smallint(6) NOT NULL AUTO_INCREMENT,
  `nick` varchar(16) NOT NULL,
  `uuid` varchar(150) NOT NULL,
  `access` tinyint(1) NOT NULL DEFAULT 1,
  `restricted` tinyint(1) NOT NULL DEFAULT 0,
  `groups` varchar(50) NOT NULL DEFAULT '',
  `home` varchar(230) DEFAULT NULL,
  PRIMARY KEY (`uid`),
  KEY `uuid` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `r_waypoints`;
CREATE TABLE `r_waypoints` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(230) NOT NULL,
  `owner` int(11) NOT NULL DEFAULT 0,
  `created` timestamp NOT NULL DEFAULT current_timestamp(),
  `enabled` tinyint(1) NOT NULL DEFAULT 0,
  `type` tinyint(1) NOT NULL COMMENT '1=User waypoint,2=Home,3=Wrap',
  `data` mediumint(9) NOT NULL DEFAULT 0 COMMENT '1=Primary user home',
  `x` int(11) NOT NULL,
  `y` mediumint(9) NOT NULL,
  `z` int(11) NOT NULL,
  `f` smallint(6) NOT NULL,
  `world` varchar(230) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_2` (`name`,`owner`,`type`),
  KEY `owner_2` (`owner`,`type`,`data`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- 2018-11-18 11:54:29
