ALTER TABLE match_request RENAME match_proposal;
ALTER TABLE match_chat CHANGE match_request_id match_proposal_id BIGINT