
DROP TABLE IF EXISTS `t_session_user_role`;

-- ----------------------------
-- Table structure for t_role
-- ----------------------------
DROP TABLE IF EXISTS `t_session_role`;
CREATE TABLE `t_session_role` (
  `id` varchar(36) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_role
-- ----------------------------
INSERT INTO `t_session_role` VALUES ('1', 'ROLE_ADMIN');
INSERT INTO `t_session_role` VALUES ('2', 'ROLE_CLIENT');

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_session_user`;
CREATE TABLE `t_session_user` (
  `id` varchar(36) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `passwd` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `t_session_group`;
CREATE TABLE `t_session_group` (
  `id` varchar(36) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `t_session_group` VALUES ('1', 'GROUP_ADMIN');
INSERT INTO `t_session_group` VALUES ('2', 'GROUP_CLIENT');

-- ----------------------------
-- Records of t_user
-- ----------------------------

-- ----------------------------
-- Table structure for t_user_role
-- ----------------------------
CREATE TABLE `t_session_user_role` (
  `role_id` varchar(255) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  PRIMARY KEY (`role_id`,`user_id`),
  KEY `FKq5un6x7ecoef5w1n39cop66kl` (`user_id`),
  CONSTRAINT `FKa9c8iiy6ut0gnx491fqx4pxam` FOREIGN KEY (`role_id`) REFERENCES `t_session_role` (`id`),
  CONSTRAINT `FKq5un6x7ecoef5w1n39cop66kl` FOREIGN KEY (`user_id`) REFERENCES `t_session_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;