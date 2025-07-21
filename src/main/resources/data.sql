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
('2025-07-23', '2025-07-29', TRUE, false, 150.00, 1),
('2025-08-01', '2025-08-11', TRUE, false, 200.00, 1),
('2025-08-17', '2025-08-19', TRUE, false, 350.00, 1),
('2025-08-23', '2025-09-01', TRUE, false, 180.00, 1),

('2025-07-27', '2025-08-06', TRUE, false, 250.00, 2),
('2025-08-07', '2025-08-11', TRUE, false, 250.00, 2),
('2025-08-23', '2025-09-06', TRUE, false, 300.00, 2),

('2025-07-23', '2025-07-29', TRUE, false, 190.00, 3),
('2025-08-01', '2025-08-11', TRUE, false, 230.00, 3),
('2025-08-17', '2025-08-19', TRUE, false, 280.00, 3),
('2025-08-23', '2025-09-01', TRUE, false, 230.00, 3),

('2025-07-27', '2025-08-06', TRUE, false, 260.00, 4),
('2025-08-07', '2025-08-11', TRUE, false, 120.00, 4),
('2025-08-23', '2025-09-06', TRUE, false, 300.00, 4),

('2025-07-23', '2025-08-01', TRUE, false, 400.00, 5),
('2025-08-06', '2025-08-16', TRUE, false, 350.00, 5),
('2025-08-23', '2025-09-01', TRUE, false, 500.00, 5),

('2025-07-23', '2025-08-01', TRUE, false, 400.00, 6),
('2025-08-06', '2025-08-16', TRUE, false, 350.00, 7),
('2025-08-23', '2025-09-01', TRUE, false, 500.00, 8),

('2025-07-30', '2025-07-31', FALSE, FALSE, 400.00, 1),
('2025-08-12', '2025-08-16', FALSE, FALSE, 430.00, 1),
('2025-09-02', '2025-09-08', TRUE, FALSE, 200.00, 1),

('2025-08-11', '2025-08-19', FALSE, FALSE, 120.00, 2),
('2025-09-07', '2025-09-11', FALSE, FALSE, 322.00, 2),

('2025-07-30', '2025-07-31', FALSE, FALSE, 433.00, 3),
('2025-08-12', '2025-08-16', FALSE, FALSE, 500.00, 3),

('2025-08-12', '2025-08-16', FALSE, FALSE, 405.00, 4),
('2025-08-17', '2025-08-19', FALSE, FALSE, 342.00, 4),

('2025-08-02', '2025-08-05', FALSE, FALSE, 500.00, 5),
('2025-08-17', '2025-08-19', FALSE, FALSE, 544.00, 5),

('2025-09-22', '2025-09-25', TRUE, FALSE, 500.00, 1),
('2025-09-26', '2025-10-01', TRUE, FALSE, 544.00, 1);

INSERT INTO reservations (start_date, end_date, guest_num, status, is_deleted, is_canceled, accommodation_id, guest_id) VALUES
('2025-07-30', '2025-07-31', 2, 'ACCEPTED', FALSE, FALSE, 1, 1),
('2025-08-12', '2025-08-16', 4, 'ACCEPTED', FALSE, FALSE, 1, 2),
('2025-09-02', '2025-09-08', 3, 'PENDING', FALSE, FALSE, 1, 3),

('2025-08-11', '2025-08-19', 2, 'ACCEPTED', FALSE, FALSE, 2, 4),
('2025-09-07', '2025-09-11', 2, 'ACCEPTED', FALSE, FALSE, 2, 5),

('2025-07-30', '2025-07-31', 1, 'ACCEPTED', FALSE, FALSE, 3, 6),
('2025-08-12', '2025-08-16', 2, 'ACCEPTED', FALSE, FALSE, 3, 7),

('2025-08-12', '2025-08-16', 2, 'ACCEPTED', FALSE, FALSE, 4, 8),
('2025-08-17', '2025-08-19', 1, 'ACCEPTED', FALSE, FALSE, 4, 9),

('2025-08-02', '2025-08-05', 6, 'ACCEPTED', FALSE, FALSE, 5, 10),
('2025-08-17', '2025-08-19', 3, 'ACCEPTED', FALSE, FALSE, 5, 11),

('2025-09-22', '2025-09-25', 3, 'PENDING', FALSE, FALSE, 1, 2),
('2025-09-26', '2025-10-01', 4, 'PENDING', FALSE, FALSE, 1, 5);

insert into accommodation_photos(accommodation_id,photo_url) VALUES
(1,'../../assets/images/accommodation1.jpg'),
(1,'../../assets/images/accommodation2.jpg'),
(1,'../../assets/images/accommodation3.jpg'),
(2,'../../assets/images/accommodation1.jpg'),
(2,'../../assets/images/accommodation2.jpg'),
(2,'../../assets/images/accommodation3.jpg'),
(3,'../../assets/images/accommodation1.jpg'),
(3,'../../assets/images/accommodation2.jpg'),
(3,'../../assets/images/accommodation3.jpg'),
(4,'../../assets/images/accommodation1.jpg'),
(4,'../../assets/images/accommodation2.jpg'),
(4,'../../assets/images/accommodation3.jpg'),
(5,'../../assets/images/accommodation1.jpg'),
(5,'../../assets/images/accommodation2.jpg'),
(5,'../../assets/images/accommodation3.jpg'),
(6,'../../assets/images/accommodation1.jpg'),
(6,'../../assets/images/accommodation2.jpg'),
(6,'../../assets/images/accommodation3.jpg'),
(7,'../../assets/images/accommodation1.jpg'),
(7,'../../assets/images/accommodation2.jpg'),
(7,'../../assets/images/accommodation3.jpg'),
(8,'../../assets/images/accommodation1.jpg'),
(8,'../../assets/images/accommodation2.jpg'),
(8,'../../assets/images/accommodation3.jpg'),
(9,'../../assets/images/accommodation1.jpg'),
(9,'../../assets/images/accommodation2.jpg'),
(9,'../../assets/images/accommodation3.jpg'),
(10,'../../assets/images/accommodation1.jpg'),
(10,'../../assets/images/accommodation2.jpg'),
(10,'../../assets/images/accommodation3.jpg');
