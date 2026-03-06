
DROP TABLE IF EXISTS `t_session_user_role`;

-- ----------------------------
-- Table structure for t_role
-- ----------------------------
DROP TABLE IF EXISTS `t_session_role`;
CREATE TABLE `t_session_role` (
  `id` varchar(36) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `role_type` int(1) NOT NULL DEFAULT 1 COMMENT '0:预置; 1:手动新增',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_role
-- ----------------------------
-- ROLE_ADMIN拥有所有模块权限以及模块下的操作(add,delete,update,get,deploy......)权限, 当资源模块及其操作权限有增加/删除时,必须同步增加关联/删除关联ROLE_ADMIN
INSERT INTO `t_session_role` VALUES ('1', 'ROLE_ADMIN', 0);
INSERT INTO `t_session_role` VALUES ('2', 'ROLE_CLIENT', 0);

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

-- 资源列表, 和角色关联, 用于控制登录用户前端页面菜单展示, 资源细分到button级别
CREATE TABLE `t_session_resource` (
    `id` varchar(64) DEFAULT NULL,
    `resource_name` varchar(64) DEFAULT NULL,
    `resource_key` varchar(128) DEFAULT NULL,
    `resource_type` varchar(128) DEFAULT NULL COMMENT 'MENU/BUTTON',
    `resource_path` varchar(128) DEFAULT NULL COMMENT '模块资源的路径(前端页面可以用可不用根据前端来决定), BUTTON类型该字段为空',
    `resource_code` varchar(128) DEFAULT NULL COMMENT '操作权限CODE: User:add,User:delete...MENU类型该字段为空',
    `parent_id` varchar(64) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `t_session_role_resource` (
    `role_id` varchar(64) DEFAULT NULL,
    `resource_id` varchar(128) DEFAULT NULL,
    PRIMARY KEY (`role_id`,`module_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;