-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: vexillumschema
-- ------------------------------------------------------
-- Server version	8.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cushions`
--

DROP TABLE IF EXISTS `cushions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cushions` (
  `cushionid` int NOT NULL,
  `isoid` varchar(5) NOT NULL,
  `stockid` int NOT NULL,
  PRIMARY KEY (`cushionid`),
  KEY `stockid_idx` (`stockid`),
  KEY `cushionisoid_idx` (`isoid`),
  CONSTRAINT `cushionisoid` FOREIGN KEY (`isoid`) REFERENCES `designs` (`isoid`),
  CONSTRAINT `cushionstockid` FOREIGN KEY (`stockid`) REFERENCES `stockmanager` (`stockid`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cushions`
--

LOCK TABLES `cushions` WRITE;
/*!40000 ALTER TABLE `cushions` DISABLE KEYS */;
INSERT INTO `cushions` VALUES (0,'PR',328),(1,'PS',329),(2,'PT',330),(3,'PW',331),(4,'PY',332),(5,'CY-YK',333),(6,'QA',334),(7,'PR-GQ',335),(8,'PR-GM',336),(9,'CY-HU',337),(10,'AC',338),(11,'AD',339),(12,'AE',340),(13,'AF',341),(14,'AG',342),(15,'AI',343),(16,'OR-AL',344),(17,'AL',345),(18,'AM',346),(19,'AN',347),(20,'AO',348),(21,'OR-AF',349),(22,'AR',350),(23,'AS',351),(24,'AT',352),(25,'RE',353),(26,'AU',354),(27,'AW',355),(28,'AX',356),(29,'AZ',357),(30,'RO',358),(31,'BA',359),(32,'BB',360),(33,'RS',361),(34,'BD',362),(35,'BE',363),(36,'RU',364),(37,'BF',365),(38,'CY-GM',366),(39,'BG',367),(40,'RW',368),(41,'BH',369),(42,'CY-GO',370),(43,'BI',371),(44,'BJ',372),(45,'BL',373),(46,'BM',374),(47,'BN',375),(48,'BO',376),(49,'CY-WT',377),(50,'CY-WW',378),(51,'SA',379),(52,'SB',380),(53,'BR',381),(54,'SC',382),(55,'BS',383),(56,'SD',384),(57,'BT',385),(58,'SE',386),(59,'CY-WM',387),(60,'SG',388),(61,'BW',389),(62,'SH',390),(63,'CY-WO',391),(64,'SI',392),(65,'BY',393),(66,'PR-IX',394),(67,'BZ',395),(68,'SK',396),(69,'SL',397),(70,'SM',398),(71,'SN',399),(72,'SO',400),(73,'CY-FS',401),(74,'CA',402),(75,'SR',403),(76,'CC',404),(77,'SS',405),(78,'CD',406),(79,'ST',407),(80,'CF',408),(81,'SV',409),(82,'CG',410),(83,'CH',411),(84,'SX',412),(85,'CI',413),(86,'CY-HP',414),(87,'SY',415),(88,'SZ',416),(89,'CK',417),(90,'CL',418),(91,'CM',419),(92,'CN',420),(93,'CO',421),(94,'CY-HE',422),(95,'CR',423),(96,'TC',424),(97,'TD',425),(98,'CU',426),(99,'TF',427),(100,'CV',428),(101,'TG',429),(102,'CW',430),(103,'TH',431),(104,'CX',432),(105,'CY',433),(106,'TJ',434),(107,'CZ',435),(108,'TK',436),(109,'TL',437),(110,'TM',438),(111,'TN',439),(112,'TO',440),(113,'TR',441),(114,'DC',442),(115,'TS',443),(116,'TT',444),(117,'DE',445),(118,'TV',446),(119,'TW',447),(120,'GB-SC',448),(121,'DJ',449),(122,'TZ',450),(123,'CY-EL',451),(124,'DK',452),(125,'DM',453),(126,'DO',454),(127,'UA',455),(128,'UG',456),(129,'PR-TG',457),(130,'CY-DY',458),(131,'DZ',459),(132,'CY-DO',460),(133,'EC',461),(134,'US',462),(135,'EE',463),(136,'EG',464),(137,'EH',465),(138,'UY',466),(139,'OR-EU',467),(140,'UZ',468),(141,'VA',469),(142,'ER',470),(143,'VC',471),(144,'ES',472),(145,'ET',473),(146,'VE',474),(147,'VG',475),(148,'CY-EX',476),(149,'VI',477),(150,'VN',478),(151,'OR-UN',479),(152,'VU',480),(153,'CY-SU',481),(154,'CY-ST',482),(155,'CY-CF',483),(156,'FI',484),(157,'FJ',485),(158,'CY-CH',486),(159,'CY-SX',487),(160,'FK',488),(161,'FM',489),(162,'FO',490),(163,'CY-SO',491),(164,'FR',492),(165,'CY-SP',493),(166,'CY-SR',494),(167,'WF',495),(168,'CY-BU',496),(169,'CY-SF',497),(170,'CY-SH',498),(171,'CY-BK',499),(172,'GA',500),(173,'GB',501),(174,'GB-EN',502),(175,'WS',503),(176,'GD',504),(177,'GE',505),(178,'GF',506),(179,'GG',507),(180,'CY-DE',508),(181,'GH',509),(182,'CY-DH',510),(183,'GI',511),(184,'GL',512),(185,'GM',513),(186,'GN',514),(187,'GP',515),(188,'GQ',516),(189,'OR-CN',517),(190,'GR',518),(191,'GS',519),(192,'GT',520),(193,'GU',521),(194,'CY-CU',522),(195,'GW',523),(196,'CY-CT',524),(197,'GY',525),(198,'XK',526),(199,'CY-CM',527),(200,'CY-CO',528),(201,'HE',529),(202,'GB-WL',530),(203,'HK',531),(204,'HN',532),(205,'HR',533),(206,'HT',534),(207,'YE',535),(208,'HU',536),(209,'YN',537),(210,'ID',538),(211,'YT',539),(212,'IE',540),(213,'CY-BD',541),(214,'CY-RT',542),(215,'PR-PR',543),(216,'IL',544),(217,'IM',545),(218,'IN',546),(219,'IO',547),(220,'ZA',548),(221,'IQ',549),(222,'IR',550),(223,'PR-PI',551),(224,'IS',552),(225,'IT',553),(226,'PR-PG',554),(227,'ZM',555),(228,'JE',556),(229,'CY-AN',557),(230,'ZW',558),(231,'PR-PX',559),(232,'JK',560),(233,'JM',561),(234,'JO',562),(235,'JP',563),(236,'BQ-SB',564),(237,'CY-OK',565),(238,'BQ-BN',566),(239,'PR-AR',567),(240,'BQ-SE',568),(241,'KE',569),(242,'KG',570),(243,'KH',571),(244,'KI',572),(245,'KM',573),(246,'KN',574),(247,'KP',575),(248,'KR',576),(249,'KW',577),(250,'CY-PM',578),(251,'KY',579),(252,'KZ',580),(253,'PR-AX',581),(254,'LA',582),(255,'LB',583),(256,'LC',584),(257,'CY-OX',585),(258,'LI',586),(259,'LK',587),(260,'PR-BI',588),(261,'CY-MO',589),(262,'LL',590),(263,'CY-MR',591),(264,'LR',592),(265,'LS',593),(266,'LT',594),(267,'LU',595),(268,'LV',596),(269,'LY',597),(270,'MA',598),(271,'MC',599),(272,'MD',600),(273,'ME',601),(274,'MG',602),(275,'MH',603),(276,'MK',604),(277,'ML',605),(278,'MM',606),(279,'MN',607),(280,'MO',608),(281,'MP',609),(282,'CY-NT',610),(283,'MQ',611),(284,'MR',612),(285,'MS',613),(286,'MT',614),(287,'CY-NH',615),(288,'MU',616),(289,'CY-NG',617),(290,'MV',618),(291,'MW',619),(292,'MX',620),(293,'MY',621),(294,'PR-LB',622),(295,'MZ',623),(296,'NA',624),(297,'NC',625),(298,'CY-NF',626),(299,'NE',627),(300,'NF',628),(301,'NG',629),(302,'CY-MX',630),(303,'NI',631),(304,'NL',632),(305,'NO',633),(306,'NP',634),(307,'NR',635),(308,'NU',636),(309,'CY-KE',637),(310,'CY-KD',638),(311,'NZ',639),(312,'PR-NB',640),(313,'OL',641),(314,'OM',642),(315,'OS',643),(316,'GB-NI',644),(317,'CY-LI',645),(318,'PA',646),(319,'CY-LA',647),(320,'PE',648),(321,'PF',649),(322,'PG',650),(323,'PH',651),(324,'PK',652),(325,'PL',653),(326,'PM',654),(327,'PN',655);
/*!40000 ALTER TABLE `cushions` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-06-17 14:16:13