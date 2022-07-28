ALTER TABLE match_review MODIFY user_id BIGINT NULL;

ALTER TABLE match_review ADD team_id BIGINT NULL;
ALTER TABLE match_review ADD target_user_id BIGINT NULL;
ALTER TABLE match_review ADD target_team_id BIGINT NULL;

ALTER TABLE match_review ADD FOREIGN KEY (team_id) REFERENCES team(id);
ALTER TABLE match_review ADD FOREIGN KEY (target_user_id) REFERENCES users(id);
ALTER TABLE match_review ADD FOREIGN KEY (target_team_id) REFERENCES team(id);
