set foreign_key_checks = 0;

CREATE TABLE `r_blocklog_block_types` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `material_name` varchar(150) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `material_name` (`material_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `r_blocklog`
ADD `new_block_id` int(11) NOT NULL AFTER `block_id`,
ADD FOREIGN KEY (`new_block_id`) REFERENCES `r_blocklog_block_types` (`id`) ON DELETE RESTRICT;

ALTER TABLE r_blocklog MODIFY block_id SMALLINT(6) NOT NULL DEFAULT 0;

set foreign_key_checks = 1;
