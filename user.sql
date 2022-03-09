DROP DATABASE IF EXISTS `animal`;
CREATE DATABASE `user`;

USE `user`;

/*Table structure for table `tb_user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
                        `id` int(11) NOT NULL AUTO_INCREMENT,
                        `super` int(11) DEFAULT NULL,
                        `password` varchar(255) DEFAULT NULL,
                        `sex` int(11) DEFAULT NULL,
                        `username` varchar(255) DEFAULT NULL,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Data for the table `tb_user` */

insert  into `user`(`id`,`super`,`password`,`sex`,`username`) values (1,1,'123456',0,'admin');
insert  into `user`(`id`,`super`,`password`,`sex`,`username`) values (2,0,'456789',1,'brown');
