-- ADDRESSES
INSERT INTO addresses (street, number_of_building, city, country) VALUES
('Main Street', 1, 'New York', 'USA'),
('High Street', 25, 'London', 'UK'),
('Ocean Drive', 12, 'Miami', 'USA'),
('Hill Road', 45, 'Los Angeles', 'USA'),
('Maple Avenue', 8, 'Toronto', 'Canada'),
('Park Lane', 10, 'Sydney', 'Australia'),
('King Street', 33, 'Melbourne', 'Australia'),
('Queen Street', 14, 'Vancouver', 'Canada'),
('Elm Street', 50, 'Chicago', 'USA'),
('Bay Road', 20, 'San Francisco', 'USA');

-- ACCOMMODATIONS
INSERT INTO accommodations (name, address_id, min_guests, max_guests, price_type, request_approval, host_id, is_deleted) VALUES
('Seaside Villa', 1, 2, 6, 'BY_ACCOMMODATION', 'MANUALLY', 16, false),
('Mountain Retreat', 2, 1, 4, 'BY_PERSON', 'AUTOMATIC', 16, false),
('City Apartment', 3, 1, 2, 'BY_PERSON', 'MANUALLY', 17, false),
('Countryside Cottage', 4, 3, 8, 'BY_ACCOMMODATION', 'AUTOMATIC', 18, false),
('Beach House', 5, 4, 10, 'BY_PERSON', 'MANUALLY', 19, false),
('Luxury Penthouse', 6, 1, 2, 'BY_ACCOMMODATION', 'AUTOMATIC', 20, false),
('Family Bungalow', 7, 2, 6, 'BY_PERSON', 'MANUALLY', 20, false),
('Rustic Lodge', 8, 2, 5, 'BY_PERSON', 'AUTOMATIC', 21, false),
('Modern Studio', 9, 1, 2, 'BY_ACCOMMODATION', 'MANUALLY', 21, false),
('Historic Mansion', 10, 5, 15, 'BY_ACCOMMODATION', 'AUTOMATIC', 22, false);

INSERT INTO accommodation_benefits (accommodation_id, benefit) VALUES
(1, 'WIFI'), (1, 'FREE_PARKING'), (1, 'AIR_CONDITIONING'),
(2, 'WIFI'), (2, 'SWIMMING_POOL'), (2, 'GYM_ACCESS'),
(3, 'WIFI'), (3, 'SMART_TV'), (3, 'BREAKFAST_INCLUDED'),
(4, 'PET_FRIENDLY'), (4, 'BBQ_GRILL'), (4, 'WHEELCHAIR_ACCESSIBLE'),
(5, 'WIFI'), (5, 'FREE_PARKING'), (5, 'OCEAN_VIEW'),
(6, 'SMART_TV'), (6, 'HEATING'), (6, 'PRIVATE_ENTRANCE'),
(7, 'GARDEN_VIEW'), (7, 'FULLY_EQUIPPED_KITCHEN'), (7, 'WASHING_MACHINE'),
(8, 'WIFI'), (8, 'DRYER'), (8, 'SPA_SERVICES'),
(9, 'BALCONY_OR_TERRACE'), (9, 'BBQ_GRILL'), (9, 'AIR_CONDITIONING'),
(10, 'FIREPLACE'), (10, 'HEATING'), (10, 'WIFI');

INSERT INTO availabilities (start_date, end_date, is_available, is_deleted, price, accommodation_id) VALUES
('2025-05-01', '2025-05-07', TRUE, false, 150.00, 1),
('2025-05-10', '2025-05-20', TRUE, false, 200.00, 1),
('2025-05-26', '2025-05-28', TRUE, false, 350.00, 1),
('2025-06-01', '2025-06-10', TRUE, false, 180.00, 1),

('2025-05-05', '2025-05-15', TRUE, false, 250.00, 2),
('2025-05-16', '2025-05-20', TRUE, false, 250.00, 2),
('2025-06-01', '2025-06-15', TRUE, false, 300.00, 2),

('2025-05-01', '2025-05-07', TRUE, false, 190.00, 3),
('2025-05-10', '2025-05-20', TRUE, false, 230.00, 3),
('2025-05-26', '2025-05-28', TRUE, false, 280.00, 3),
('2025-06-01', '2025-06-10', TRUE, false, 230.00, 3),

('2025-05-05', '2025-05-15', TRUE, false, 260.00, 4),
('2025-05-16', '2025-05-20', TRUE, false, 120.00, 4),
('2025-06-01', '2025-06-15', TRUE, false, 300.00, 4),

('2025-05-01', '2025-05-10', TRUE, false, 400.00, 5),
('2025-05-15', '2025-05-25', TRUE, false, 350.00, 5),
('2025-06-01', '2025-06-10', TRUE, false, 500.00, 5),

('2025-05-01', '2025-05-10', TRUE, false, 400.00, 6),
('2025-05-15', '2025-05-25', TRUE, false, 350.00, 7),
('2025-06-01', '2025-06-10', TRUE, false, 500.00, 8),

('2025-05-08', '2025-05-09', FALSE, FALSE, 400.00, 1),
('2025-05-21', '2025-05-25', FALSE, FALSE, 430.00, 1),
('2025-06-11', '2025-06-17', TRUE, FALSE, 200.00, 1),

('2025-05-20', '2025-05-28', FALSE, FALSE, 120.00, 2),
('2025-06-16', '2025-06-20', FALSE, FALSE, 322.00, 2),

('2025-05-08', '2025-05-09', FALSE, FALSE, 433.00, 3),
('2025-05-21', '2025-05-25', FALSE, FALSE, 500.00, 3),

('2025-05-21', '2025-05-25', FALSE, FALSE, 405.00, 4),
('2025-05-26', '2025-05-28', FALSE, FALSE, 342.00, 4),

('2025-05-11', '2025-05-14', FALSE, FALSE, 500.00, 5),
('2025-05-26', '2025-05-28', FALSE, FALSE, 544.00, 5);


INSERT INTO reservations (start_date, end_date, guest_num, status, is_deleted, is_canceled, accommodation_id, guest_id) VALUES
('2025-05-08', '2025-05-09', 2, 'ACCEPTED', FALSE, FALSE, 1, 1),
('2025-05-21', '2025-05-25', 4, 'ACCEPTED', FALSE, FALSE, 1, 2),
('2025-06-11', '2025-06-17', 3, 'PENDING', FALSE, FALSE, 1, 3),

('2025-05-20', '2025-05-28', 2, 'ACCEPTED', FALSE, FALSE, 2, 4),
('2025-06-16', '2025-06-20', 2, 'ACCEPTED', FALSE, FALSE, 2, 5),

('2025-05-08', '2025-05-09', 1, 'ACCEPTED', FALSE, FALSE, 3, 6),
('2025-05-21', '2025-05-25', 2, 'ACCEPTED', FALSE, FALSE, 3, 7),

('2025-05-21', '2025-05-25', 2, 'ACCEPTED', FALSE, FALSE, 4, 8),
('2025-05-26', '2025-05-28', 1, 'ACCEPTED', FALSE, FALSE, 4, 9),

('2025-05-11', '2025-05-14', 6, 'ACCEPTED', FALSE, FALSE, 5, 10),
('2025-05-26', '2025-05-28', 3, 'ACCEPTED', FALSE, FALSE, 5, 11);
