-- 国家数据
INSERT INTO country (id, name) VALUES
(1, 'Japan'),
(2, 'USA'),
(3, 'Hong Kong');

-- 城市数据
INSERT INTO city (id, countryid, name) VALUES
(1, 1, 'Tokyo'),   -- 日本
(2, 1, 'Osaka'),    -- 日本
(3, 2, 'New York'), -- 美国
(4, 3, 'Central');  -- 香港

-- 仓库数据（每个国家一个源仓库(S)和一个中央仓库(C)）
INSERT INTO warehouse (id, countryid, name, type) VALUES
-- Japan
(101, 1, 'Japan Source Warehouse', 'S'),
(102, 1, 'Japan Central Warehouse', 'C'),
-- USA
(201, 2, 'USA Source Warehouse', 'S'),
(202, 2, 'USA Central Warehouse', 'C'),
-- Hong Kong
(301, 3, 'HK Source Warehouse', 'S'),
(302, 3, 'HK Central Warehouse', 'C');



-- 店铺数据（每个城市至少1个店铺）
INSERT INTO shop (id, cityid, name) VALUES
-- Tokyo
(1001, 1, 'Tokyo Shop A'),
(1002, 1, 'Tokyo Shop B'),
-- Osaka
(2001, 2, 'Osaka Shop A'),
-- New York
(3001, 3, 'NY Shop A'),
-- Hong Kong
(4001, 4, 'HK Shop A');

-- 水果数据（每个国家至少两种）
INSERT INTO fruit (id, sourceCountryid, name, unit) VALUES
-- 日本水果
(1, 1, 'Japanese Strawberry', 'kg'),
(2, 1, 'Satsuma Orange', 'box'),
-- 美国水果
(3, 2, 'American Blueberry', 'box'),
(4, 2, 'California Avocado', 'kg'),
-- 香港水果
(5, 3, 'Hong Kong Mango', 'box'),
(6, 3, 'Dragon Fruit', 'piece');

-- 用户数据（确保每个店铺和仓库有至少一名员工）
INSERT INTO user (loginName, name, password, type, shopid, warehouseid) VALUES
-- 店铺员工（每个店铺至少1人）
('tokyo_shop1', 'Taro', SHA2('123', 256), 'B', 1001, NULL),
('tokyo_shop2', 'Hanako', SHA2('123', 256), 'B', 1002, NULL),
('osaka_shop1', 'Kenji', SHA2('123', 256), 'B', 2001, NULL),
('ny_shop1', 'John', SHA2('123', 256), 'B', 3001, NULL),
('hk_shop1', 'Amy', SHA2('123', 256), 'B', 4001, NULL),
-- 仓库员工（每个仓库至少1人）
('jp_wh1', 'Yamada',SHA2('123', 256), 'W', NULL, 101),
('jp_wh2', 'Suzuki', SHA2('123', 256), 'W', NULL, 102),
('us_wh1', 'Smith',SHA2('123', 256), 'W', NULL, 201),
('us_wh2', 'Smith',SHA2('123', 256), 'W', NULL, 202),
('hk_wh1', 'Wong',SHA2('123', 256), 'W', NULL, 301),
('hk_wh2', 'Wong',SHA2('123', 256), 'W', NULL, 302),
-- 管理层
('admin', 'Admin', SHA2('123', 256), 'S', NULL, NULL);

-- 源仓库初始库存
INSERT INTO warehouseStock (warehouseid, fruitid, num) VALUES
-- 日本源仓库
(101, 1, 1000),  -- 草莓
(101, 2, 500),   -- 橙子
-- 美国源仓库
(201, 3, 800),   -- 蓝莓
(201, 4, 300),   -- 牛油果
-- 香港源仓库
(301, 5, 200),   -- 芒果
(301, 6, 100);   -- 火龙果

-- 店铺初始库存
INSERT INTO shopStock (shopid, fruitid, num) VALUES
-- 东京Shop A
(1001, 1, 50),   -- 草莓
(1001, 2, 20),   -- 橙子
-- 纽约Shop A
(3001, 3, 30),   -- 蓝莓
(3001, 4, 10);   -- 牛油果

-- 业务记录（借货、预订、运输）
-- 借货记录（同城店铺）
INSERT INTO borrow (id, sourceShopid, destinationShopid, state) VALUES
(1, 1001, 1002, 'F');  -- 东京Shop A → 东京Shop B

INSERT INTO borrowDetail (borrowid, fruitid, num) VALUES
(1, 1, 10);  -- 借出10kg草莓

-- 预订记录（跨城预订）
INSERT INTO reserve (id, Shopid, reserveDT, state) VALUES
(1, 3001, DATE_ADD(NOW(), INTERVAL 7 DAY), 'A');  -- 纽约Shop A预订

INSERT INTO reserveDetail (reserveid, fruitid, num) VALUES
(1, 6, 100),
(1, 5, 100),
(1, 4, 100),
(1, 3, 100),
(1, 2, 100),
(1, 1, 100);

-- 跨国运输记录（日本→美国）
INSERT INTO warehouseStockChange (id, sourceWarehouseid, destinationWarehouseid, deliveryStartTime, deliveryEndTime, state) VALUES
(1, 101, 202, '2024-01-01 08:00:00', '2024-01-05 12:00:00', 'F');


INSERT INTO warehouseStockChangeDetail (warehouseStockChangeid, fruitid, num) VALUES
(1, 1, 500);  -- 运输500kg草莓

-- 跨国运输记录（香港→美国）
INSERT INTO warehouseStockChange (id, sourceWarehouseid, destinationWarehouseid, deliveryStartTime, deliveryEndTime, state) VALUES
(2, 301, 202, '2024-01-03 08:00:00', '2024-01-05 12:00:00', 'F'),
(4, 301, 202, '2024-01-04 12:00:00', '2024-01-08 7:00:00', 'F');
INSERT INTO warehouseStockChangeDetail (warehouseStockChangeid, fruitid, num) VALUES
(2, 5, 100),   -- 运输100箱芒果
(2, 6, 50),
(4,5,20);    -- 运输50个火龙果


-- 同國运输记录（美国）
INSERT INTO warehouseStockChange (id, sourceWarehouseid, destinationWarehouseid, deliveryStartTime, deliveryEndTime, state) VALUES
(3, 201, 202, '2024-01-05 08:00:00', '2024-01-05 18:00:00', 'F');
INSERT INTO warehouseStockChangeDetail (warehouseStockChangeid, fruitid, num) VALUES
(3, 3, 200),   -- 运输200箱蓝莓
(3, 4, 100);   -- 运输100kg牛油果


-- 消費記錄 (日本店鋪 2023-2024)
INSERT INTO consume (id, DT, shopid) VALUES
-- Tokyo Shop A (id=1001)
(10001, '2023-04-15 09:30:00', 1001),
(10002, '2023-07-22 14:15:00', 1001),
(10003, '2023-12-05 16:45:00', 1001),
(10004, '2024-02-18 11:20:00', 1001),

-- Tokyo Shop B (id=1002)
(20001, '2023-05-10 10:00:00', 1002),
(20002, '2023-09-28 15:30:00', 1002),
(20003, '2024-01-12 12:45:00', 1002),

-- Osaka Shop A (id=2001)
(30001, '2023-06-20 13:10:00', 2001),
(30002, '2023-10-15 17:20:00', 2001),
(30003, '2024-03-05 09:00:00', 2001);

-- 消費細節 (日本水果 strawberry=1, orange=2)
INSERT INTO consumeDetail (consumeid, fruitid, num) VALUES
-- Tokyo Shop A 消費
(10001, 1, 8),  -- 草莓 8kg
(10001, 2, 3),  -- 橙子 3箱
(10002, 1, 12),
(10003, 2, 5),
(10004, 1, 10),

-- Tokyo Shop B 消費
(20001, 1, 6),
(20002, 2, 4),
(20003, 1, 9),

-- Osaka Shop A 消費
(30001, 2, 3),
(30002, 1, 7),
(30003, 2, 6);

-- 櫻花季特別消費（3-4月）
INSERT INTO consume (id, DT, shopid) VALUES
(10005, '2024-03-25 10:30:00', 1001);
INSERT INTO consumeDetail (consumeid, fruitid, num) VALUES
(10005, 1, 15);  -- 草莓消費增加