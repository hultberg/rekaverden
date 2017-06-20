-- MySQL dump 10.15  Distrib 10.0.30-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: 127.0.0.1    Database: rekaverden
-- ------------------------------------------------------
-- Server version	10.0.30-MariaDB-0+deb8u2

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `dm_Faces`
--

DROP TABLE IF EXISTS `dm_Faces`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dm_Faces` (
  `PlayerName` varchar(64) COLLATE utf8_danish_ci NOT NULL,
  `TypeID` int(11) NOT NULL,
  `Image` blob,
  PRIMARY KEY (`PlayerName`,`TypeID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_danish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dm_Maps`
--

DROP TABLE IF EXISTS `dm_Maps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dm_Maps` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `WorldID` varchar(64) COLLATE utf8_danish_ci NOT NULL,
  `MapID` varchar(64) COLLATE utf8_danish_ci NOT NULL,
  `Variant` varchar(16) COLLATE utf8_danish_ci NOT NULL,
  `ServerID` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COLLATE=utf8_danish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dm_MarkerFiles`
--

DROP TABLE IF EXISTS `dm_MarkerFiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dm_MarkerFiles` (
  `FileName` varchar(128) COLLATE utf8_danish_ci NOT NULL,
  `Content` mediumtext COLLATE utf8_danish_ci,
  PRIMARY KEY (`FileName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_danish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dm_MarkerIcons`
--

DROP TABLE IF EXISTS `dm_MarkerIcons`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dm_MarkerIcons` (
  `IconName` varchar(128) COLLATE utf8_danish_ci NOT NULL,
  `Image` blob,
  PRIMARY KEY (`IconName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_danish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dm_SchemaVersion`
--

DROP TABLE IF EXISTS `dm_SchemaVersion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dm_SchemaVersion` (
  `level` int(11) NOT NULL,
  PRIMARY KEY (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_danish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dm_StandaloneFiles`
--

DROP TABLE IF EXISTS `dm_StandaloneFiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dm_StandaloneFiles` (
  `FileName` varchar(128) COLLATE utf8_danish_ci NOT NULL,
  `ServerID` bigint(20) NOT NULL DEFAULT '0',
  `Content` mediumtext COLLATE utf8_danish_ci,
  PRIMARY KEY (`FileName`,`ServerID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_danish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dm_Tiles`
--

DROP TABLE IF EXISTS `dm_Tiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dm_Tiles` (
  `MapID` int(11) NOT NULL,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `zoom` int(11) NOT NULL,
  `HashCode` bigint(20) NOT NULL,
  `LastUpdate` bigint(20) NOT NULL,
  `Format` int(11) NOT NULL,
  `Image` blob,
  PRIMARY KEY (`MapID`,`x`,`y`,`zoom`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_danish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dm_faces`
--

DROP TABLE IF EXISTS `dm_faces`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dm_faces` (
  `PlayerName` varchar(64) NOT NULL,
  `TypeID` int(11) NOT NULL,
  `Image` blob,
  PRIMARY KEY (`PlayerName`,`TypeID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dm_maps`
--

DROP TABLE IF EXISTS `dm_maps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dm_maps` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `WorldID` varchar(64) NOT NULL,
  `MapID` varchar(64) NOT NULL,
  `Variant` varchar(16) NOT NULL,
  `ServerID` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dm_markerfiles`
--

DROP TABLE IF EXISTS `dm_markerfiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dm_markerfiles` (
  `FileName` varchar(128) NOT NULL,
  `Content` mediumtext,
  PRIMARY KEY (`FileName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dm_markericons`
--

DROP TABLE IF EXISTS `dm_markericons`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dm_markericons` (
  `IconName` varchar(128) NOT NULL,
  `Image` blob,
  PRIMARY KEY (`IconName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dm_schemaversion`
--

DROP TABLE IF EXISTS `dm_schemaversion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dm_schemaversion` (
  `level` int(11) NOT NULL,
  PRIMARY KEY (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dm_standalonefiles`
--

DROP TABLE IF EXISTS `dm_standalonefiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dm_standalonefiles` (
  `FileName` varchar(128) NOT NULL,
  `ServerID` bigint(20) NOT NULL DEFAULT '0',
  `Content` mediumtext,
  PRIMARY KEY (`FileName`,`ServerID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dm_tiles`
--

DROP TABLE IF EXISTS `dm_tiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dm_tiles` (
  `MapID` int(11) NOT NULL,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `zoom` int(11) NOT NULL,
  `HashCode` bigint(20) NOT NULL,
  `LastUpdate` bigint(20) NOT NULL,
  `Format` int(11) NOT NULL,
  `Image` blob,
  PRIMARY KEY (`MapID`,`x`,`y`,`zoom`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `r_blocklog`
--

DROP TABLE IF EXISTS `r_blocklog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `r_blocklog` (
  `uid` smallint(6) NOT NULL,
  `x` mediumint(9) NOT NULL,
  `y` smallint(6) NOT NULL,
  `z` mediumint(9) NOT NULL,
  `world` varchar(60) NOT NULL,
  `block_id` smallint(6) NOT NULL,
  `block_data` smallint(6) NOT NULL,
  `timestamp` int(11) NOT NULL,
  `action` tinyint(1) NOT NULL DEFAULT '0',
  KEY `uid` (`uid`),
  KEY `location` (`x`,`y`,`z`,`world`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `r_blocks`
--

DROP TABLE IF EXISTS `r_blocks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `r_blocks` (
  `uid` smallint(11) NOT NULL,
  `x` mediumint(9) NOT NULL,
  `y` smallint(6) NOT NULL,
  `z` mediumint(9) NOT NULL,
  `world` varchar(60) NOT NULL,
  UNIQUE KEY `x` (`x`,`y`,`z`,`world`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `r_group_invitations`
--

DROP TABLE IF EXISTS `r_group_invitations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `r_group_invitations` (
  `invite_ID` int(11) NOT NULL AUTO_INCREMENT,
  `invited` mediumint(9) NOT NULL,
  `invitee` mediumint(9) NOT NULL,
  `to_group` mediumint(9) NOT NULL,
  PRIMARY KEY (`invite_ID`),
  UNIQUE KEY `togroup` (`invited`,`to_group`),
  KEY `to_group` (`to_group`),
  KEY `invited` (`invited`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `r_group_membership`
--

DROP TABLE IF EXISTS `r_group_membership`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `r_group_membership` (
  `user_ID` mediumint(9) NOT NULL,
  `group_ID` mediumint(9) NOT NULL,
  KEY `data` (`user_ID`,`group_ID`),
  KEY `group_ID` (`group_ID`),
  KEY `user_ID` (`user_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `r_groups`
--

DROP TABLE IF EXISTS `r_groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `r_groups` (
  `group_ID` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(160) NOT NULL,
  `owner` int(11) NOT NULL,
  PRIMARY KEY (`group_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `r_users`
--

DROP TABLE IF EXISTS `r_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `r_users` (
  `uid` smallint(6) NOT NULL AUTO_INCREMENT,
  `nick` varchar(16) NOT NULL,
  `uuid` varchar(150) NOT NULL,
  `access` tinyint(1) NOT NULL DEFAULT '1',
  `groups` varchar(50) NOT NULL DEFAULT '',
  `home` varchar(230) DEFAULT NULL,
  PRIMARY KEY (`uid`),
  KEY `uuid` (`uuid`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `r_waypoints`
--

DROP TABLE IF EXISTS `r_waypoints`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `r_waypoints` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(230) NOT NULL,
  `owner` int(11) NOT NULL DEFAULT '0',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `enabled` tinyint(1) NOT NULL DEFAULT '0',
  `type` tinyint(1) NOT NULL COMMENT '1=User waypoint,2=Home,3=Wrap',
  `data` mediumint(9) NOT NULL DEFAULT '0' COMMENT '1=Primary user home',
  `x` int(11) NOT NULL,
  `y` mediumint(9) NOT NULL,
  `z` int(11) NOT NULL,
  `f` smallint(6) NOT NULL,
  `world` varchar(230) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_2` (`name`,`owner`,`type`),
  KEY `owner_2` (`owner`,`type`,`data`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-06-19 19:12:44
