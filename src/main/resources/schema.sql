CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    identifier_type VARCHAR(50) NOT NULL,
    role VARCHAR(50) NOT NULL,
    CONSTRAINT users_identifier_type_check CHECK (identifier_type IN ('EMAIL', 'PHONE')),
    CONSTRAINT users_role_check CHECK (role IN ('ADMIN', 'COMPANY', 'INDIVIDUAL'))
);

CREATE TABLE IF NOT EXISTS company_profiles (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    company_name VARCHAR(255) NOT NULL,
    registration_number VARCHAR(255) NOT NULL,
    contact_person VARCHAR(255) NOT NULL,
    contact_email VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    website VARCHAR(255),
    CONSTRAINT company_profiles_user_id_fk
        FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS individual_profiles (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    bio VARCHAR(1000),
    CONSTRAINT individual_profiles_user_id_fk
        FOREIGN KEY (user_id) REFERENCES users (id)
);
