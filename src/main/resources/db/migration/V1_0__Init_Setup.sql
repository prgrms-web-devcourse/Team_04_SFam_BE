DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    id         BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    username   VARCHAR(24)  NOT NULL UNIQUE KEY,
    password   VARCHAR(255) NOT NULL,
    nickname   VARCHAR(16)  NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    created_by VARCHAR(255),
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_by VARCHAR(255)
);

CREATE TABLE token
(
    token      VARCHAR(255) NOT NULL PRIMARY KEY,
    user_id    BIGINT,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    created_by VARCHAR(255),
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_by VARCHAR(255)
);

CREATE TABLE team
(
    id              BIGINT       NOT NULL PRIMARY KEY,
    team_name        VARCHAR(10) NOT NULL,
    description     VARCHAR(100) NOT NULL,
    sports_category VARCHAR(50) NOT NULL,
    leader_id       BIGINT       NOT NULL,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    created_by      VARCHAR(255),
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_by      VARCHAR(255),
    FOREIGN KEY (leader_id) REFERENCES users (id)
);

CREATE TABLE team_member
(
    id         BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    team_id    BIGINT       NOT NULL,
    user_id    BIGINT       NOT NULL,
    role       VARCHAR(20) NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    created_by VARCHAR(255),
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_by VARCHAR(255),
    FOREIGN KEY (team_id) REFERENCES team (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE team_invitation
(
    id         BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    team_id    BIGINT       NOT NULL,
    user_id    BIGINT       NOT NULL,
    target_id  BIGINT       NOT NULL,
    status     VARCHAR(20) NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    created_by VARCHAR(255),
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_by VARCHAR(255),
    FOREIGN KEY (team_id) REFERENCES team (id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (target_id) REFERENCES users (id)
);

CREATE TABLE matches
(
    id              BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    title           VARCHAR(50) NOT NULL,
    sports_category VARCHAR(50) NOT NULL,
    match_type      VARCHAR(20) NOT NULL,
    match_date      DATE         NOT NULL,
    content         VARCHAR(255) NOT NULL,
    status          VARCHAR(20) NOT NULL,
    user_id         BIGINT       NOT NULL,
    team_id         BIGINT       NOT NULL,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    created_by      VARCHAR(255),
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_by      VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (team_id) REFERENCES team (id)
);

CREATE TABLE match_request
(
    id         BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    match_id   BIGINT       NOT NULL,
    content    VARCHAR(255) NOT NULL,
    user_id    BIGINT       NOT NULL,
    team_id    BIGINT       NOT NULL,
    status     VARCHAR(20) NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    created_by VARCHAR(255),
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_by VARCHAR(255),
    FOREIGN KEY (match_id) REFERENCES matches (id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (team_id) REFERENCES team (id)
);

CREATE TABLE match_chat
(
    id               BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    match_request_id BIGINT       NOT NULL,
    user_id          BIGINT       NOT NULL,
    target_id        BIGINT       NOT NULL,
    content          VARCHAR(255) NOT NULL,
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    created_by       VARCHAR(255),
    updated_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_by       VARCHAR(255),
    FOREIGN KEY (match_request_id) REFERENCES match_request (id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (target_id) REFERENCES users (id)
);

CREATE TABLE match_review
(
    id         BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    match_id   BIGINT       NOT NULL,
    review     VARCHAR(20) NOT NULL,
    user_id    BIGINT       NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    created_by VARCHAR(255),
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_by VARCHAR(255),
    FOREIGN KEY (match_id) REFERENCES matches (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE match_result
(
    id         BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    match_id   BIGINT       NOT NULL,
    user_id    BIGINT       NOT NULL,
    team_id    BIGINT       NOT NULL,
    result     VARCHAR(20) NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    created_by VARCHAR(255),
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_by VARCHAR(255),
    FOREIGN KEY (match_id) REFERENCES matches (id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (team_id) REFERENCES team (id)
);