-- Roles (deze komen altijd eerst)
INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN') ON CONFLICT DO NOTHING;
INSERT INTO roles (id, name) VALUES (2, 'ROLE_MONTEUR') ON CONFLICT DO NOTHING;

-- Test users
-- Wachtwoord voor beide: password123
-- BCrypt encoded: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

INSERT INTO users (id, username, email, password) VALUES
    (1, 'admin', 'admin@demaker.nl', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy')
ON CONFLICT DO NOTHING;

INSERT INTO users (id, username, email, password) VALUES
    (2, 'monteur1', 'monteur@demaker.nl', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy')
ON CONFLICT DO NOTHING;

-- User-Role mappings
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1) ON CONFLICT DO NOTHING;
INSERT INTO user_roles (user_id, role_id) VALUES (1, 2) ON CONFLICT DO NOTHING;
INSERT INTO user_roles (user_id, role_id) VALUES (2, 2) ON CONFLICT DO NOTHING;