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
-- Table structure for table `designs`
--

DROP TABLE IF EXISTS `designs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `designs` (
  `isoid` varchar(5) NOT NULL,
  `name` varchar(100) NOT NULL,
  `regionid` int DEFAULT NULL,
  `typeid` int DEFAULT NULL,
  PRIMARY KEY (`isoid`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  KEY `typeid_idx` (`typeid`),
  KEY `regionid_idx` (`regionid`),
  CONSTRAINT `regionid` FOREIGN KEY (`regionid`) REFERENCES `regions` (`regionid`),
  CONSTRAINT `typeid` FOREIGN KEY (`typeid`) REFERENCES `designtypes` (`typeid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `designs`
--

LOCK TABLES `designs` WRITE;
/*!40000 ALTER TABLE `designs` DISABLE KEYS */;
INSERT INTO `designs` VALUES ('AC','Ascension Island',0,0),('AD','Andorra',2,1),('AE','United Arab Emirates',1,1),('AF','Afghanistan',1,1),('AG','Antigua and Barbuda',3,1),('AI','Anguilla',3,0),('AL','Albania',2,1),('AM','Armenia',1,1),('AN','Alderney',2,0),('AO','Angola',0,1),('AR','Argentina',5,1),('AS','American Samoa',4,1),('AT','Austria',2,1),('AU','Australia',4,1),('AW','Aruba',5,1),('AX','Aland Islands',2,1),('AZ','Azerbaijan',1,1),('BA','Bosnia and Herzegovina',2,1),('BB','Barbados',3,1),('BD','Bangladesh',1,1),('BE','Belgium',2,1),('BF','Burkina Faso',0,1),('BG','Bulgaria',2,1),('BH','Bahrain',1,1),('BI','Burundi',0,1),('BJ','Benin',0,1),('BL','Saint Barthelemy',3,1),('BM','Bermuda',3,0),('BN','Brunei',1,1),('BO','Bolivia',5,1),('BQ-BN','Bonaire',5,1),('BQ-SB','Saba',3,1),('BQ-SE','Sint Eustatius',3,1),('BR','Brazil',5,1),('BS','Bahamas',3,1),('BT','Bhutan',1,1),('BW','Botswana',0,1),('BY','Belarus',2,1),('BZ','Belize',3,1),('CA','Canada',3,1),('CC','Cocos (Keeling) Islands',1,0),('CD','Democratic Republic of the Congo',0,1),('CF','Central African Republic',0,1),('CG','Republic of the Congo',0,1),('CH','Switzerland',2,1),('CI','Cote d\'Ivoire',0,1),('CK','Cook Islands',4,1),('CL','Chile',5,1),('CM','Cameroon',0,1),('CN','China',1,1),('CO','Colombia',5,1),('CR','Costa Rica',3,1),('CU','Cuba',3,1),('CV','Cabo Verde',0,1),('CW','Curacao',5,1),('CX','Christmas Island',4,1),('CY','Cyprus',2,1),('CY-AN','Anglesey',2,0),('CY-BD','Bedfordshire',2,0),('CY-BK','Berkshire',2,0),('CY-BU','Buckinghamshire',2,0),('CY-CF','Caernarfonshire',2,0),('CY-CH','Cheshire',2,0),('CY-CM','Cambridgeshire',2,0),('CY-CO','Cornwall',2,0),('CY-CT','Caithness',2,0),('CY-CU','Cumberland',2,0),('CY-DE','Devon',2,0),('CY-DH','Durham',2,0),('CY-DO','Dorset',2,0),('CY-DY','Derbyshire',2,0),('CY-EL','East Lothian',2,0),('CY-EX','Essex',2,0),('CY-FS','Flintshire',2,0),('CY-GM','Glamorgan',2,0),('CY-GO','Gloucestershire',2,0),('CY-HE','Hertfordshire',2,0),('CY-HP','Hampshire',2,0),('CY-HU','Huntingdonshire',2,0),('CY-KD','Kirkcudbrightshire',2,0),('CY-KE','Kent',2,0),('CY-LA','Lancashire',2,0),('CY-LI','Lincolnshire',2,0),('CY-MO','Monmouthshire',2,0),('CY-MR','Merioneth',2,0),('CY-MX','Middlesex',2,0),('CY-NF','Norfolk',2,0),('CY-NG','Nottinghamshire',2,0),('CY-NH','Northamptonshire',2,0),('CY-NT','Norththumberland',2,0),('CY-OK','Orkney',2,0),('CY-OX','Oxfordshire',2,0),('CY-PM','Pembrokeshire',2,0),('CY-RT','Rutland',2,0),('CY-SF','Suffolk',2,0),('CY-SH','Shetland',2,0),('CY-SO','Somerset',2,0),('CY-SP','Shropshire',2,0),('CY-SR','Surrey',2,0),('CY-ST','Staffordshire',2,0),('CY-SU','Sutherland',2,0),('CY-SX','Sussex',2,0),('CY-WM','Westmorland',2,0),('CY-WO','Worchestershire',2,0),('CY-WT','Wiltshire',2,0),('CY-WW','Warwickshire',2,0),('CY-YK','Yorkshire',2,0),('CZ','Czech Republic',2,1),('DC','Tristan da Cunha',0,0),('DE','Germany',2,1),('DJ','Djibouti',0,1),('DK','Denmark',2,1),('DM','Dominica',3,1),('DO','Dominican Republic',3,1),('DZ','Algeria',0,1),('EC','Ecuador',5,1),('EE','Estonia',2,1),('EG','Egypt',0,1),('EH','Western Sahara',0,1),('ER','Eritrea',0,1),('ES','Spain',2,1),('ET','Ethiopia',0,1),('FI','Finland',2,1),('FJ','Fiji',4,1),('FK','Falkland Islands',5,0),('FM','Federated States of Micronesia',4,1),('FO','Faroes',2,1),('FR','France',2,1),('GA','Gabon',0,1),('GB','United Kingdom',2,0),('GB-EN','England',2,0),('GB-NI','Northern Ireland',2,0),('GB-SC','Scotland',2,0),('GB-WL','Wales',2,0),('GD','Grenada',3,1),('GE','Georgia',1,1),('GF','French Guiana',5,1),('GG','Guernsey',2,0),('GH','Ghana',0,1),('GI','Gibraltar',2,0),('GL','Greenland',3,1),('GM','Gambia',0,1),('GN','Guinea',0,1),('GP','Guadeloupe',3,1),('GQ','Equatorial Guinea',0,1),('GR','Greece',2,1),('GS','South Georgia and the South Sandwich Islands',5,0),('GT','Guatemala',5,1),('GU','Guam',4,1),('GW','Guinea-Bissau',0,1),('GY','Guyana',5,1),('HE','Herm',2,0),('HK','Hong Kong',1,1),('HN','Honduras',3,1),('HR','Croatia',2,1),('HT','Haiti',3,1),('HU','Hungary',2,1),('ID','Indonesia',1,1),('IE','Ireland',2,0),('IL','Israel',1,1),('IM','Isle of Man',2,0),('IN','India',1,1),('IO','British Indian Ocean Territory',1,0),('IQ','Iraq',1,1),('IR','Iran',1,1),('IS','Iceland',2,1),('IT','Italy',2,1),('JE','Jersey',2,0),('JK','Sark',2,0),('JM','Jamaica',3,1),('JO','Jordan',1,1),('JP','Japan',1,1),('KE','Kenya',0,1),('KG','Kyrgyzstan',1,1),('KH','Cambodia',1,1),('KI','Kiribati',4,1),('KM','Comoros',0,1),('KN','Saint Kitts and Nevis',3,1),('KP','North Korea',1,1),('KR','South Korea',1,1),('KW','Kuwait',1,1),('KY','Cayman Islands',3,0),('KZ','Kazakhstan',1,1),('LA','Laos',1,1),('LB','Lebanon',1,1),('LC','Saint Lucia',3,1),('LI','Liechtenstein',2,1),('LK','Sri Lanka',1,1),('LL','Sami',2,1),('LR','Liberia',0,1),('LS','Lesotho',0,1),('LT','Lithuania',2,1),('LU','Luxembourg',2,1),('LV','Latvia',2,1),('LY','Libya',0,1),('MA','Morocco',0,1),('MC','Monaco',2,1),('MD','Moldova',2,1),('ME','Montenegro',2,1),('MG','Madagascar',0,1),('MH','Marshall Islands',4,1),('MK','North Macedonia',2,1),('ML','Mali',0,1),('MM','Myanmar',1,1),('MN','Mongolia',1,1),('MO','Macao',1,1),('MP','Northern Mariana Islands',4,1),('MQ','Martinique',3,1),('MR','Mauritania',0,1),('MS','Montserrat',3,0),('MT','Malta',1,1),('MU','Mauritius',0,1),('MV','Maldives',1,1),('MW','Malawi',0,1),('MX','Mexico',3,1),('MY','Malaysia',1,1),('MZ','Mozambique',0,1),('NA','Namibia',0,1),('NC','New Caledonia',4,1),('NE','Niger',0,1),('NF','Norfolk Island',4,1),('NG','Nigeria',0,1),('NI','Nicaragua',3,1),('NL','Netherlands',2,1),('NO','Norway',2,1),('NP','Nepal',1,1),('NR','Nauru',4,1),('NU','Niue',4,1),('NZ','New Zealand',4,1),('OL','Somaliland',0,1),('OM','Oman',1,1),('OR-AF','African Union',0,1),('OR-AL','Arab League',1,1),('OR-CN','Commonwealth of Nations',NULL,0),('OR-EU','European Union',2,1),('OR-UN','United Nations',NULL,1),('OS','Sovereign Military Order of Malta',2,1),('PA','Panama',3,1),('PE','Peru',5,1),('PF','French Polynesia',4,1),('PG','Papua New Guinea',4,1),('PH','Philippines',1,1),('PK','Pakistan',1,1),('PL','Poland',2,1),('PM','Saint Pierre and Miquelon',3,1),('PN','Pitcairn Islands',4,0),('PR','Puerto Rico',3,1),('PR-AR','Aromantic',NULL,2),('PR-AX','Asexual',NULL,2),('PR-BI','Bisexual',NULL,2),('PR-GM','Gay Men',NULL,2),('PR-GQ','Gender Queer',NULL,2),('PR-IX','Intersex',NULL,2),('PR-LB','Lesbian',NULL,2),('PR-NB','Non-Binary',NULL,2),('PR-PG','Progress',NULL,2),('PR-PI','Intersex-Progress',NULL,2),('PR-PR','Pride',NULL,2),('PR-PX','Pansexual',NULL,2),('PR-TG','Transgender',NULL,2),('PS','Palestine',1,1),('PT','Portugal',2,1),('PW','Palau',4,1),('PY','Paraguay',5,1),('QA','Qatar',1,1),('RE','Reunion',0,1),('RO','Romania',2,1),('RS','Serbia',2,1),('RU','Russia',2,1),('RW','Rwanda',0,1),('SA','Saudi Arabia',1,1),('SB','Solomon Islands',4,1),('SC','Seychelles',0,1),('SD','Sudan',0,1),('SE','Sweden',2,1),('SG','Singapore',1,1),('SH','Saint Helena',0,0),('SI','Slovenia',2,1),('SK','Slovakia',2,1),('SL','Sierra Leone',0,1),('SM','San Marino',2,1),('SN','Senegal',0,1),('SO','Somalia',0,1),('SR','Suriname',5,1),('SS','South Sudan',0,1),('ST','Sao Tome and Principe',0,1),('SV','El Salvador',3,1),('SX','Sint Maarten',3,1),('SY','Syria',1,1),('SZ','Eswatini',0,1),('TC','Turks and Caicos Islands',3,0),('TD','Chad',0,1),('TF','French Southern Territories',0,1),('TG','Togo',0,1),('TH','Thailand',1,1),('TJ','Tajikistan',1,1),('TK','Tokelau',4,1),('TL','Timor-Leste',1,1),('TM','Turkmenistan',1,1),('TN','Tunisia',0,1),('TO','Tonga',4,1),('TR','Turkiye',2,1),('TS','Transnistria',2,1),('TT','Trinidad and Tobago',5,1),('TV','Tuvalu',4,1),('TW','Taiwan',1,1),('TZ','Tanzania',0,1),('UA','Ukraine',2,1),('UG','Uganda',0,1),('US','United States',3,1),('UY','Uruguay',5,1),('UZ','Uzbekistan',1,1),('VA','Vatican City',2,1),('VC','Saint Vincent and the Grenadines',3,1),('VE','Venezuela',5,1),('VG','British Virgin Islands',3,0),('VI','U.S. Virgin Islands.',3,1),('VN','Vietnam',1,1),('VU','Vanuatu',4,1),('WF','Wallis and Futuna Islands',4,1),('WS','Samoa',4,1),('XK','Kosovo',2,1),('YE','Yemen',1,1),('YN','North Cyprus',2,1),('YT','Mayotte',0,1),('ZA','South Africa',0,1),('ZM','Zambia',0,1),('ZW','Zimbabwe',0,1);
/*!40000 ALTER TABLE `designs` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-06-18 12:38:19
