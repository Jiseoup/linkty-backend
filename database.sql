CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email TEXT NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    join_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);


CREATE TABLE urls (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    alias VARCHAR(20),
    original_url TEXT NOT NULL,
    shorten_url CHAR(8) NOT NULL UNIQUE,
    click_count INTEGER NOT NULL DEFAULT 0,
    create_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    expire_date TIMESTAMP WITH TIME ZONE,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT "fk_urls.user_id" FOREIGN KEY (user_id) REFERENCES users(id)
);


CREATE TABLE logs (
    id BIGSERIAL PRIMARY KEY,
    url_id BIGINT NOT NULL,
    click_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    ip_address VARCHAR(45) NOT NULL,
    client_info TEXT NOT NULL,
    CONSTRAINT "fk_logs.url_id" FOREIGN KEY (url_id) REFERENCES urls(id)
);
