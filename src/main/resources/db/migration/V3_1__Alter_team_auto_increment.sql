ALTER TABLE match_record DROP FOREIGN KEY match_record_ibfk_3;
ALTER TABLE match_request DROP FOREIGN KEY match_request_ibfk_3;
ALTER TABLE matches DROP FOREIGN KEY matches_ibfk_2;
ALTER TABLE team_invitation DROP FOREIGN KEY team_invitation_ibfk_1;
ALTER TABLE team_member DROP FOREIGN KEY team_member_ibfk_1;

ALTER TABLE team MODIFY id BIGINT NOT NULL AUTO_INCREMENT;

ALTER TABLE match_record ADD FOREIGN KEY(team_id) REFERENCES team(id);
ALTER TABLE match_request ADD FOREIGN KEY(team_id) REFERENCES team(id);
ALTER TABLE matches ADD FOREIGN KEY(team_id) REFERENCES team(id);
ALTER TABLE team_invitation ADD FOREIGN KEY(team_id) REFERENCES team(id);
ALTER TABLE team_member ADD FOREIGN KEY(team_id) REFERENCES team(id);
