CREATE INDEX user_verification_token_hash_idx
    ON User_verification USING HASH (token);
