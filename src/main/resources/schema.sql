DROP TABLE IF EXISTS company_legal_docs CASCADE;
DROP TABLE IF EXISTS job_applications CASCADE;
DROP TABLE IF EXISTS job_posts CASCADE;
DROP TABLE IF EXISTS individual_profiles CASCADE;
DROP TABLE IF EXISTS company_profiles CASCADE;
DROP TABLE IF EXISTS job_contract_types CASCADE;
DROP TABLE IF EXISTS company_sectors CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    identifier_type VARCHAR(50) NOT NULL,
    role VARCHAR(50) NOT NULL,
    deleted INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT users_identifier_type_check CHECK (identifier_type IN ('EMAIL', 'PHONE')),
    CONSTRAINT users_role_check CHECK (role IN ('ADMIN', 'COMPANY', 'INDIVIDUAL')),
    CONSTRAINT users_deleted_check CHECK (deleted IN (0, 1))
);

CREATE TABLE company_profiles (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    profile_picture TEXT,
    company_name VARCHAR(255) NOT NULL,
    registration_number VARCHAR(255) NOT NULL,
    contact_person VARCHAR(255) NOT NULL,
    contact_email VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    website VARCHAR(255),
    bio VARCHAR(1000),
    sector VARCHAR(255),
    deleted INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT company_profiles_user_id_fk
        FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT company_profiles_deleted_check CHECK (deleted IN (0, 1))
);

CREATE TABLE company_sectors (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    display_order INTEGER NOT NULL DEFAULT 0,
    deleted INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT company_sectors_deleted_check CHECK (deleted IN (0, 1))
);

CREATE TABLE job_contract_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    display_order INTEGER NOT NULL DEFAULT 0,
    deleted INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT job_contract_types_deleted_check CHECK (deleted IN (0, 1))
);

CREATE TABLE company_legal_docs (
    company_profile_id UUID NOT NULL,
    document TEXT,
    deleted INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT company_legal_docs_company_profile_id_fk
        FOREIGN KEY (company_profile_id) REFERENCES company_profiles (id),
    CONSTRAINT company_legal_docs_deleted_check CHECK (deleted IN (0, 1))
);

CREATE TABLE individual_profiles (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    profile_picture TEXT,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    bio VARCHAR(1000),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    dob DATE,
    gender VARCHAR(255),
    email VARCHAR(255),
    address VARCHAR(255),
    nic_front TEXT,
    nic_back TEXT,
    has_drivers_license BOOLEAN NOT NULL,
    drivers_license_type VARCHAR(255),
    profession VARCHAR(255),
    preferred_categories VARCHAR(255),
    preferred_sectors VARCHAR(255),
    skills VARCHAR(1000),
    deleted INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT individual_profiles_user_id_fk
        FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT individual_profiles_deleted_check CHECK (deleted IN (0, 1))
);

CREATE TABLE job_posts (
    id UUID PRIMARY KEY,
    company_user_id UUID NOT NULL,
    company_name VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(255),
    sector VARCHAR(255),
    location VARCHAR(255),
    hours_per_week INTEGER,
    hourly_rate NUMERIC(38, 2),
    contract_type VARCHAR(255),
    contract_duration VARCHAR(255),
    cv_requirement VARCHAR(50) NOT NULL DEFAULT 'NOT_REQUIRED',
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    deleted INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT job_posts_company_user_id_fk
        FOREIGN KEY (company_user_id) REFERENCES users (id),
    CONSTRAINT job_posts_cv_requirement_check CHECK (cv_requirement IN ('REQUIRED', 'OPTIONAL', 'NOT_REQUIRED')),
    CONSTRAINT job_posts_status_check CHECK (status IN ('OPEN', 'CLOSED')),
    CONSTRAINT job_posts_deleted_check CHECK (deleted IN (0, 1))
);

CREATE TABLE job_applications (
    id UUID PRIMARY KEY,
    job_post_id UUID NOT NULL,
    individual_user_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    cv_document TEXT,
    deleted INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT job_applications_job_post_id_fk
        FOREIGN KEY (job_post_id) REFERENCES job_posts (id),
    CONSTRAINT job_applications_individual_user_id_fk
        FOREIGN KEY (individual_user_id) REFERENCES users (id),
    CONSTRAINT job_applications_job_user_unique
        UNIQUE (job_post_id, individual_user_id),
    CONSTRAINT job_applications_status_check CHECK (status IN ('SUBMITTED')),
    CONSTRAINT job_applications_deleted_check CHECK (deleted IN (0, 1))
);
