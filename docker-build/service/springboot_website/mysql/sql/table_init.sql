DROP TABLE IF EXISTS `t_test`;
CREATE TABLE `t_test` (
  `id` varchar(36) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO `t_test` VALUES ('1', 'ADMIN');
INSERT INTO `t_test` VALUES ('2', 'CLIENT');