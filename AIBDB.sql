DROP DATABASE IF EXISTS `ITP4511_DB`;
CREATE DATABASE `ITP4511_DB` /*!40100 DEFAULT CHARACTER SET utf8mb4 */ /*!80016 DEFAULT ENCRYPTION='N' */;

use ITP4511_DB;
CREATE TABLE `country` (
  `id` BIGINT PRIMARY KEY,
  `name` varchar(255) NOT NULL
);

CREATE TABLE `city` (
  `id` BIGINT PRIMARY KEY,
  `countryid` BIGINT NOT NULL,
  `name` varchar(255) NOT NULL
);

CREATE TABLE `warehouse` (
  `id` BIGINT PRIMARY KEY,
  `countryid` BIGINT NOT NULL,
  `name` varchar(255) NOT NULL,
  `type` ENUM ('S', 'C') NOT NULL COMMENT 'S = source warehouse, C = central warehouse'
);

CREATE TABLE `shop` (
  `id` BIGINT PRIMARY KEY,
  `cityid` BIGINT NOT NULL,
  `name` varchar(255) NOT NULL
);

CREATE TABLE `fruit` (
  `id` BIGINT PRIMARY KEY,
  `sourceCountryid` BIGINT NOT NULL,
  `name` varchar(255) NOT NULL,
  `unit` varchar(255) NOT NULL
);

CREATE TABLE `user` (
  `loginName` varchar(255) PRIMARY KEY,
  `name` varchar(255) NOT NULL,
  `password` char(64) NOT NULL COMMENT 'use sha256',
  `type` ENUM ('B', 'W', 'S', 'D') NOT NULL COMMENT 'B = Bakery shop staff / W = Warehouse Staff / S = Senior Management / D = deleted',
  `warehouseid` BIGINT,
  `shopid` BIGINT
);

CREATE TABLE `warehouseStock` (
  `warehouseid` BIGINT,
  `fruitid` BIGINT,
  `num` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`warehouseid`, `fruitid`)
);

CREATE TABLE `shopStock` (
  `shopid` BIGINT,
  `fruitid` BIGINT,
  `num` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`shopid`, `fruitid`)
);

CREATE TABLE `borrow` (
  `id` BIGINT PRIMARY KEY,
  `DT` datetime NOT NULL DEFAULT (now()),
  `sourceShopid` BIGINT,
  `destinationShopid` BIGINT,
  `state` ENUM ('C', 'A', 'R', 'F') NOT NULL DEFAULT 'C' COMMENT 'C = create, A = approve, R = Reject, F = finish'
);

CREATE TABLE `borrowDetail` (
  `borrowid` BIGINT,
  `fruitid` BIGINT,
  `num` int NOT NULL,
  PRIMARY KEY (`borrowid`, `fruitid`)
);

CREATE TABLE `reserve` (
  `id` BIGINT PRIMARY KEY,
  `DT` datetime NOT NULL DEFAULT (now()) COMMENT 'record create datetime',
  `Shopid` BIGINT,
  `reserveDT` datetime NOT NULL COMMENT 'arrive datetime (max now() + 14 days)',
  `state` ENUM ('C', 'A', 'R', 'F') NOT NULL DEFAULT 'C' COMMENT 'C = create, A = approve, R = Reject, F = finish'
);

CREATE TABLE `reserveDetail` (
  `reserveid` BIGINT,
  `fruitid` BIGINT,
  `num` int NOT NULL,
  PRIMARY KEY (`reserveid`, `fruitid`)
);

CREATE TABLE `consume` (
  `id` BIGINT PRIMARY KEY,
  `DT` datetime NOT NULL DEFAULT (now()),
  `shopid` BIGINT
);

CREATE TABLE `consumeDetail` (
  `consumeid` BIGINT,
  `fruitid` BIGINT,
  `num` int NOT NULL,
  PRIMARY KEY (`consumeid`, `fruitid`)
);

CREATE TABLE `warehouseStockChange` (
  `id` BIGINT PRIMARY KEY,
  `deliveryStartTime` datetime NOT NULL DEFAULT (now()),
  `deliveryEndTime` datetime,
  `sourceWarehouseid` BIGINT NOT NULL,
  `destinationWarehouseid` BIGINT NOT NULL,
  `state` ENUM ('C', 'A', 'R', 'F') NOT NULL DEFAULT 'C' COMMENT 'C = create, A = approve, R = Reject, F = finish'
);

CREATE TABLE `warehouseStockChangeDetail` (
  `warehouseStockChangeid` BIGINT,
  `fruitid` BIGINT,
  `num` int NOT NULL,
  `state` ENUM ('C', 'A', 'R', 'F') NOT NULL DEFAULT 'C' COMMENT 'C = create, A = approve, R = Reject, F = finish',
  PRIMARY KEY (`warehouseStockChangeid`, `fruitid`)
);

ALTER TABLE `warehouse` COMMENT = 'one country only have one source warehouse and one central warehouse';

ALTER TABLE `fruit` COMMENT = 'record will not show in central warehouse if num = 0
record will show in source warehouse if num != 0 and fruit.sourceCityId != warehouse.cityId';

ALTER TABLE `city` ADD FOREIGN KEY (`countryid`) REFERENCES `country` (`id`);

ALTER TABLE `warehouse` ADD FOREIGN KEY (`countryid`) REFERENCES `country` (`id`);

ALTER TABLE `shop` ADD FOREIGN KEY (`cityid`) REFERENCES `city` (`id`);

ALTER TABLE `fruit` ADD FOREIGN KEY (`sourceCountryid`) REFERENCES `country` (`id`);

ALTER TABLE `user` ADD FOREIGN KEY (`warehouseid`) REFERENCES `warehouse` (`id`);

ALTER TABLE `user` ADD FOREIGN KEY (`shopid`) REFERENCES `shop` (`id`);

ALTER TABLE `warehouseStock` ADD FOREIGN KEY (`warehouseid`) REFERENCES `warehouse` (`id`);

ALTER TABLE `warehouseStock` ADD FOREIGN KEY (`fruitid`) REFERENCES `fruit` (`id`);

ALTER TABLE `shopStock` ADD FOREIGN KEY (`shopid`) REFERENCES `shop` (`id`);

ALTER TABLE `shopStock` ADD FOREIGN KEY (`fruitid`) REFERENCES `fruit` (`id`);

ALTER TABLE `borrow` ADD FOREIGN KEY (`sourceShopid`) REFERENCES `shop` (`id`);

ALTER TABLE `borrow` ADD FOREIGN KEY (`destinationShopid`) REFERENCES `shop` (`id`);

ALTER TABLE `borrowDetail` ADD FOREIGN KEY (`borrowid`) REFERENCES `borrow` (`id`);

ALTER TABLE `borrowDetail` ADD FOREIGN KEY (`fruitid`) REFERENCES `fruit` (`id`);

ALTER TABLE `reserve` ADD FOREIGN KEY (`Shopid`) REFERENCES `shop` (`id`);

ALTER TABLE `reserveDetail` ADD FOREIGN KEY (`reserveid`) REFERENCES `reserve` (`id`);

ALTER TABLE `reserveDetail` ADD FOREIGN KEY (`fruitid`) REFERENCES `fruit` (`id`);

ALTER TABLE `consume` ADD FOREIGN KEY (`shopid`) REFERENCES `shop` (`id`);

ALTER TABLE `consumeDetail` ADD FOREIGN KEY (`consumeid`) REFERENCES `consume` (`id`);

ALTER TABLE `consumeDetail` ADD FOREIGN KEY (`fruitid`) REFERENCES `fruit` (`id`);

ALTER TABLE `warehouseStockChange` ADD FOREIGN KEY (`sourceWarehouseid`) REFERENCES `warehouse` (`id`);

ALTER TABLE `warehouseStockChange` ADD FOREIGN KEY (`destinationWarehouseid`) REFERENCES `warehouse` (`id`);

ALTER TABLE `warehouseStockChangeDetail` ADD FOREIGN KEY (`warehouseStockChangeid`) REFERENCES `warehouseStockChange` (`id`);

ALTER TABLE `warehouseStockChangeDetail` ADD FOREIGN KEY (`fruitid`) REFERENCES `fruit` (`id`);
