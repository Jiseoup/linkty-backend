-- Users table.
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email TEXT NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    join_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    last_login TIMESTAMP WITH TIME ZONE
);


-- Urls table.
CREATE TABLE urls (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    alias VARCHAR(20),
    original_url TEXT NOT NULL,
    shorten_url CHAR(6) NOT NULL UNIQUE,
    active_date TIMESTAMP WITH TIME ZONE,
    expire_date TIMESTAMP WITH TIME ZONE,
    click_count INTEGER NOT NULL DEFAULT 0,
    starred BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    create_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT "fk_urls.user_id" FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);


-- Logs table.
CREATE TABLE logs (
    id BIGSERIAL PRIMARY KEY,
    url_id BIGINT NOT NULL,
    click_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    ip_address VARCHAR(45) NOT NULL,
    client_info TEXT NOT NULL,
    CONSTRAINT "fk_logs.url_id" FOREIGN KEY (url_id) REFERENCES urls(id) ON DELETE CASCADE
);
