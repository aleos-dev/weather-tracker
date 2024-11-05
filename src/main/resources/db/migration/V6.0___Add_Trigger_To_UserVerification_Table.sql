CREATE OR REPLACE FUNCTION delete_expired_tokens()
    RETURNS TRIGGER AS
$$
BEGIN
    DELETE
    FROM User_verification
    WHERE expiration_date <= NOW() - INTERVAL '24 hours';
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER delete_expired_tokens_trigger
    AFTER INSERT OR UPDATE
    ON User_verification
    FOR EACH STATEMENT
EXECUTE FUNCTION delete_expired_tokens();