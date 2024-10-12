CREATE INDEX user_verification_token_hash_idx
    ON user_verification USING HASH (token);
