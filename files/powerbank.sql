-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: powerbank
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin` (
  `admin_id` int NOT NULL AUTO_INCREMENT,
  `admin_account` varchar(16) NOT NULL,
  `admin_password` varchar(32) NOT NULL,
  PRIMARY KEY (`admin_id`),
  UNIQUE KEY `adminAccount` (`admin_account`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin`
--

LOCK TABLES `admin` WRITE;
/*!40000 ALTER TABLE `admin` DISABLE KEYS */;
INSERT INTO `admin` VALUES (1,'admin','123456'),(2,'admin2','123456'),(3,'admin3','123456'),(4,'admin4','123654'),(5,'asdfawe','asdfawefasdf'),(6,'awefasdf6','546456'),(7,'asdfawefwa','asdfawefsdf'),(8,'adfsdafawe','asdfawefasef'),(9,'awefasdf','we23rwet'),(10,'daf2345d','q432asdf'),(11,'f2q35weddfasd','254wefsdf'),(12,'q25qw45','456wrddfg'),(13,'ikghj564d','dsf45'),(14,'dchw457dfg','q34dsxdgr'),(15,'456dzfg3465','dxcv3456e'),(16,'xa23','dsh67'),(17,'4523fd','fcgv47'),(18,'x76','sa346r6yt'),(19,'xcv746','a2ewa45dg5'),(20,'as54q2345','346cs5sc34'),(25,'admin6','12345678');
/*!40000 ALTER TABLE `admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `location`
--

DROP TABLE IF EXISTS `location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `location` (
  `location_id` int NOT NULL AUTO_INCREMENT,
  `location_city` varchar(16) NOT NULL,
  `location_district` varchar(16) NOT NULL,
  `location_address` varchar(64) NOT NULL,
  `location_alias` varchar(32) NOT NULL,
  `location_yun_id` int DEFAULT NULL,
  `location_amount` int(11) unsigned zerofill DEFAULT NULL,
  `location_available` int(11) unsigned zerofill DEFAULT NULL,
  `location_longitude` decimal(10,7) DEFAULT NULL,
  `location_latitude` decimal(10,7) DEFAULT NULL,
  PRIMARY KEY (`location_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `location`
--

LOCK TABLES `location` WRITE;
/*!40000 ALTER TABLE `location` DISABLE KEYS */;
INSERT INTO `location` VALUES (14,'广州','越秀区','一层大厅','酒店大堂',-1,00000000015,00000000007,113.2644000,23.1291000),(15,'北京','朝阳区','北门','便民服务站',-1,00000000013,00000000008,116.4074000,39.9042000),(16,'上海','浦东区','东门','便民服务站',-1,00000000014,00000000011,121.4737000,31.2304000),(20,'深圳','西城区','地下一层','酒店大堂',-1,00000000020,00000000010,114.0579000,22.5431000),(22,'云浮','云安区','广东药科大学','教学楼',-1,00000000020,00000000018,112.1800000,23.0300000);
/*!40000 ALTER TABLE `location` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order`
--

DROP TABLE IF EXISTS `order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order` (
  `order_id` int NOT NULL AUTO_INCREMENT,
  `order_user_id` int NOT NULL,
  `order_lent_location_id` int NOT NULL,
  `order_pobk_id` int NOT NULL,
  `order_create_time` datetime NOT NULL,
  `order_has_finished` int(2) unsigned zerofill DEFAULT NULL,
  `order_revert_location_id` int DEFAULT NULL,
  `order_finish_time` datetime DEFAULT NULL,
  `order_cost` float DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  KEY `order_user_id` (`order_user_id`) USING BTREE,
  KEY `order_lent_location_id` (`order_lent_location_id`) USING BTREE,
  KEY `order_pobk_id` (`order_pobk_id`) USING BTREE,
  KEY `order_revert_location_id` (`order_revert_location_id`) USING BTREE,
  CONSTRAINT `order_lent_location_id` FOREIGN KEY (`order_lent_location_id`) REFERENCES `location` (`location_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `order_pobk_id` FOREIGN KEY (`order_pobk_id`) REFERENCES `pobk` (`pobk_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `order_revert_location_id` FOREIGN KEY (`order_revert_location_id`) REFERENCES `location` (`location_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `order_user_id` FOREIGN KEY (`order_user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
/*!40000 ALTER TABLE `order` DISABLE KEYS */;
INSERT INTO `order` VALUES (1,11,22,62,'2026-05-20 13:56:01',01,22,'2026-05-20 13:56:11',2),(2,11,22,62,'2026-05-21 14:01:37',01,22,'2026-05-21 14:01:45',2),(3,11,22,62,'2026-05-22 14:01:54',01,22,'2026-05-22 14:01:59',2),(4,11,14,80,'2026-05-23 14:02:09',01,14,'2026-05-23 14:02:14',2),(5,11,14,80,'2026-05-19 14:19:03',01,14,'2026-05-19 14:19:22',2);
/*!40000 ALTER TABLE `order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pobk`
--

DROP TABLE IF EXISTS `pobk`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pobk` (
  `pobk_id` int NOT NULL AUTO_INCREMENT,
  `pobk_location_id` int NOT NULL,
  `pobk_status` enum('lent','available') NOT NULL DEFAULT 'available',
  PRIMARY KEY (`pobk_id`),
  KEY `pobk_location_key` (`pobk_location_id`),
  CONSTRAINT `pobk_location_key` FOREIGN KEY (`pobk_location_id`) REFERENCES `location` (`location_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pobk`
--

LOCK TABLES `pobk` WRITE;
/*!40000 ALTER TABLE `pobk` DISABLE KEYS */;
INSERT INTO `pobk` VALUES (23,10,'available'),(24,10,'available'),(25,10,'available'),(26,10,'available'),(27,10,'available'),(28,10,'available'),(29,11,'available'),(30,11,'available'),(31,11,'available'),(32,11,'available'),(33,11,'available'),(34,11,'available'),(35,11,'available'),(36,11,'available'),(37,11,'available'),(38,11,'available'),(39,11,'available'),(40,11,'available'),(41,11,'available'),(42,11,'available'),(43,11,'available'),(44,13,'available'),(45,13,'available'),(46,13,'available'),(47,13,'available'),(48,13,'available'),(49,13,'available'),(50,13,'available'),(51,13,'available'),(52,20,'available'),(53,20,'available'),(54,20,'available'),(55,20,'available'),(56,20,'available'),(57,20,'available'),(58,20,'available'),(59,20,'available'),(60,20,'available'),(61,20,'available'),(62,22,'available'),(63,22,'available'),(64,22,'available'),(65,22,'available'),(66,22,'available'),(67,22,'available'),(68,22,'available'),(69,22,'available'),(70,22,'available'),(71,22,'available'),(72,22,'available'),(73,22,'available'),(74,22,'available'),(75,22,'available'),(76,22,'available'),(77,22,'available'),(78,22,'available'),(79,22,'available'),(80,14,'available'),(81,14,'available'),(82,14,'available'),(83,14,'available'),(84,14,'available'),(85,14,'available'),(86,14,'available'),(87,15,'available'),(88,15,'available'),(89,15,'available'),(90,15,'available'),(91,15,'available'),(92,15,'available'),(93,15,'available'),(94,15,'available'),(95,16,'available'),(96,16,'available'),(97,16,'available'),(98,16,'available'),(99,16,'available'),(100,16,'available'),(101,16,'available'),(102,16,'available'),(103,16,'available'),(104,16,'available'),(105,16,'available');
/*!40000 ALTER TABLE `pobk` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `user_phone` varchar(12) NOT NULL,
  `user_alias` varchar(20) DEFAULT NULL,
  `user_password` varchar(32) NOT NULL,
  `user_balance` float unsigned zerofill DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `userPhone` (`user_phone`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (3,'15602209156','阿周咯','123456',0000000121.8),(10,'13533388335','测试昵称','123456',000000000057),(11,'18027688206','龟龟','123456',0000000053.6);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-24 14:38:46
