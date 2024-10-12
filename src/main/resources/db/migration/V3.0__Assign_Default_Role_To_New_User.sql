-- Step 1: Create a function that sets a default role
CREATE OR REPLACE FUNCTION set_default_role()
    RETURNS TRIGGER AS $$
BEGIN
    -- If role_id is not provided, set it to the 'USER' role by default
    IF NEW.role_id IS NULL THEN
        SELECT id INTO NEW.role_id
        FROM authorization_role
        WHERE role = 'USER';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Step 2: Create a trigger that calls the function before inserting a new user
CREATE TRIGGER set_default_role_trigger
    BEFORE INSERT ON users
    FOR EACH ROW
EXECUTE FUNCTION set_default_role();
