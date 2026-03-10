-- ============================================
-- DATA.SQL - Testdata voor DeMaker Autogarage
-- ============================================

-- STAP 1: Rollen aanmaken
INSERT INTO roles (name) VALUES ('ROLE_ADMIN') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('ROLE_MONTEUR') ON CONFLICT (name) DO NOTHING;

-- STAP 2: Users aanmaken (wachtwoord: admin123 en monteur123)
-- BCrypt hash voor 'admin123': $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZagcFl7p1xMh8qw/Z1AOZvMUMCxYu
-- BCrypt hash voor 'monteur123': $2a$10$dPCJ7DmOvbFBL97v8Z5Oo.TVnDHaIpVqB8E4JGgPrHqZZzJ5pO5Uy
INSERT INTO users (username, email, password)
VALUES ('admin', 'admin@demaker.nl', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZagcFl7p1xMh8qw/Z1AOZvMUMCxYu')
ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, email, password)
VALUES ('monteur', 'monteur@demaker.nl', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZagcFl7p1xMh8qw/Z1AOZvMUMCxYu')
ON CONFLICT (username) DO NOTHING;

-- STAP 3: User-rollen koppelen
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'monteur' AND r.name = 'ROLE_MONTEUR'
ON CONFLICT DO NOTHING;

-- STAP 4: Klanten aanmaken (minimaal 2)
INSERT INTO customers (first_name, last_name, email, phone_number)
VALUES ('Jan', 'de Vries', 'jan.devries@email.nl', '0612345678')
ON CONFLICT (email) DO NOTHING;

INSERT INTO customers (first_name, last_name, email, phone_number)
VALUES ('Maria', 'Jansen', 'maria.jansen@email.nl', '0687654321')
ON CONFLICT (email) DO NOTHING;

INSERT INTO customers (first_name, last_name, email, phone_number)
VALUES ('Pieter', 'Bakker', 'pieter.bakker@email.nl', '0611223344')
ON CONFLICT (email) DO NOTHING;

-- STAP 5: Auto's aanmaken (minimaal 3, gekoppeld aan klanten)
INSERT INTO cars (license_plate, brand, model, year, customer_id)
SELECT 'AB-123-CD', 'Volkswagen', 'Golf', 2020, c.id
FROM customers c WHERE c.email = 'jan.devries@email.nl'
ON CONFLICT (license_plate) DO NOTHING;

INSERT INTO cars (license_plate, brand, model, year, customer_id)
SELECT 'EF-456-GH', 'Toyota', 'Corolla', 2019, c.id
FROM customers c WHERE c.email = 'maria.jansen@email.nl'
ON CONFLICT (license_plate) DO NOTHING;

INSERT INTO cars (license_plate, brand, model, year, customer_id)
SELECT 'IJ-789-KL', 'BMW', '3 Serie', 2021, c.id
FROM customers c WHERE c.email = 'pieter.bakker@email.nl'
ON CONFLICT (license_plate) DO NOTHING;

INSERT INTO cars (license_plate, brand, model, year, customer_id)
SELECT 'MN-012-OP', 'Audi', 'A4', 2018, c.id
FROM customers c WHERE c.email = 'jan.devries@email.nl'
ON CONFLICT (license_plate) DO NOTHING;

-- STAP 6: Keuringen aanmaken (minimaal 2)
INSERT INTO inspections (planned_date, status, car_id)
SELECT '2024-03-15', 'COMPLETED', c.id
FROM cars c WHERE c.license_plate = 'AB-123-CD'
AND NOT EXISTS (SELECT 1 FROM inspections i WHERE i.car_id = c.id AND i.planned_date = '2024-03-15');

INSERT INTO inspections (planned_date, status, car_id)
SELECT '2024-03-20', 'PLANNED', c.id
FROM cars c WHERE c.license_plate = 'EF-456-GH'
AND NOT EXISTS (SELECT 1 FROM inspections i WHERE i.car_id = c.id AND i.planned_date = '2024-03-20');

INSERT INTO inspections (planned_date, status, car_id)
SELECT '2024-04-01', 'IN_PROGRESS', c.id
FROM cars c WHERE c.license_plate = 'IJ-789-KL'
AND NOT EXISTS (SELECT 1 FROM inspections i WHERE i.car_id = c.id AND i.planned_date = '2024-04-01');

-- STAP 7: Tekortkomingen aanmaken (minimaal 3)
INSERT INTO deficiencies (description, estimated_cost, safety_risk, inspection_id)
SELECT 'Remblokken versleten', 150.00, true, i.id
FROM inspections i
JOIN cars c ON i.car_id = c.id
WHERE c.license_plate = 'AB-123-CD' AND i.planned_date = '2024-03-15'
AND NOT EXISTS (SELECT 1 FROM deficiencies d WHERE d.inspection_id = i.id AND d.description = 'Remblokken versleten');

INSERT INTO deficiencies (description, estimated_cost, safety_risk, inspection_id)
SELECT 'Olielekkage motor', 250.00, false, i.id
FROM inspections i
JOIN cars c ON i.car_id = c.id
WHERE c.license_plate = 'AB-123-CD' AND i.planned_date = '2024-03-15'
AND NOT EXISTS (SELECT 1 FROM deficiencies d WHERE d.inspection_id = i.id AND d.description = 'Olielekkage motor');

INSERT INTO deficiencies (description, estimated_cost, safety_risk, inspection_id)
SELECT 'Band profiel te laag', 400.00, true, i.id
FROM inspections i
JOIN cars c ON i.car_id = c.id
WHERE c.license_plate = 'AB-123-CD' AND i.planned_date = '2024-03-15'
AND NOT EXISTS (SELECT 1 FROM deficiencies d WHERE d.inspection_id = i.id AND d.description = 'Band profiel te laag');

INSERT INTO deficiencies (description, estimated_cost, safety_risk, inspection_id)
SELECT 'Uitlaatsysteem beschadigd', 300.00, false, i.id
FROM inspections i
JOIN cars c ON i.car_id = c.id
WHERE c.license_plate = 'IJ-789-KL' AND i.planned_date = '2024-04-01'
AND NOT EXISTS (SELECT 1 FROM deficiencies d WHERE d.inspection_id = i.id AND d.description = 'Uitlaatsysteem beschadigd');
