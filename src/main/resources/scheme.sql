
CREATE TABLE IF NOT EXISTS `student`
(
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `created_at` DATETIME(6),
    `email`      VARCHAR(255) NOT NULL,
    `password`   VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `department`
(
    `id`   BIGINT       NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `enrollment`
(
    `id`            BIGINT NOT NULL AUTO_INCREMENT,
    `student_id`    BIGINT NOT NULL,
    `department_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
    FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
);

CREATE TABLE IF NOT EXISTS `student_roles`
(
    `student_id` BIGINT       NOT NULL,
    `roles`      VARCHAR(255) NOT NULL,
    FOREIGN KEY (`student_id`) REFERENCES `student` (`id`)
);

CREATE TABLE IF NOT EXISTS `board`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `post_at`       DATE         NOT NULL,
    `post_number`   INT          NOT NULL,
    `department_id` BIGINT       NOT NULL,
    `link`          VARCHAR(255),
    `title`         VARCHAR(255) NOT NULL,
    `writer`        VARCHAR(255) NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
);

CREATE TABLE IF NOT EXISTS `keywords`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `enrollment_id` BIGINT       NOT NULL,
    `content`       VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`enrollment_id`) REFERENCES `enrollment` (`id`)
);

CREATE TABLE IF NOT EXISTS `notification`
(
    `id`         BIGINT      NOT NULL AUTO_INCREMENT,
    `board_id`   BIGINT      NOT NULL,
    `sent_at`    DATETIME(6) NOT NULL,
    `student_id` BIGINT      NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`board_id`) REFERENCES `board` (`id`),
    FOREIGN KEY (`student_id`) REFERENCES `student` (`id`)
);
