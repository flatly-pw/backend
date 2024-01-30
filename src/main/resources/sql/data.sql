insert into address_entity (id, city, country, latitude, longitude, postal_code, street)
values (1, 'Warsaw', 'Poland', 52.242196, 21.015816, '00-325', 'Krakowskie Przedmieście 42/44'),
       (2, 'Warsaw', 'Poland', 52.227837, 21.004423, '00-697', 'al. Jerozolimskie 65/79');

insert into flat_owner_entity (id, email, last_name, name, phone_number, registered_at)
values (1, 'jan.kowalski@mail.com', 'Kowalski', 'Jan', '+48 123 456 789', '2020-01-01'),
       (2, 'jan.nowak@mail.com', 'Nowak', 'Jan', '+48 987 654 321', '2018-01-01');

insert into flat_entity (id, area, beds, bathrooms, bedrooms, capacity, description, title, type, address_entity_id,
                         flat_owner_entity_id)
values ('1', 30, 1, 1, 1, 2, 'Luxury Marriott hotel in center of Warsaw', 'Marriott', 'hotel', 2, 2),
       ('2', 38, 2, 2, 2, 3, 'Luxury Bristol hotel next to presidential palace', 'Bristol', 'hotel', 1, 1);

insert into flat_image_entity (id, ordinal, file_name, file_type, flat_entity_id)
values ('1', 0, 'bristol', 'image/png', '2'),
       ('2', 1, 'bristol_interior', 'image/png', '2'),
       ('3', 0, 'marriott', 'image/png', '1');

insert into flat_facility_entity (name)
values ('wi-fi'),
       ('paid breakfast'),
       ('private bathroom');

insert into flat_facility_entity_flats (facilities_name, flats_id)
values ('wi-fi', '1'),
       ('paid breakfast', '1'),
       ('wi-fi', '2'),
       ('private bathroom', '2');

insert into price_entity (price_dollars, flat_entity_id)
values (900.0, '1'),
       (700.0, '2');

insert into user_entity (id, email, last_name, name, password)
values (1000, 'janusz@mail.com', 'Nowak', 'Janusz', 'janusznowak123'),
       (1001, 'marcin@mail.com', 'Nowak', 'Marcin', 'marcin123'),
       (1002, 'lambert@mail.com', 'Wilk', 'Lambert', 'lambert123'),
       (1003, 'john.smith@mail.com', 'John', 'Smith', 'password123');

insert into flat_review_entity (flat_entity_id, user_entity_id, rating, review, date)
values ('1', 1000, 5, 'Very nice place to stay. I had a great time there.', '2020-06-03'),
       ('2', 1000, 4, 'It was a pleasure to stay there although the service was not so good.', '2020-05-15'),
       ('2', 1001, 2, 'I did not like it there.', '2023-01-01'),
       ('2', 1002, 1, 'Awful.', '2023-01-08');

insert into reservation_entity (id, start_date, end_date, adults, children, pets, special_requests, cancelled,
                                flat_entity_id,
                                user_entity_id)
values (1, '2024-01-01', '2024-01-07', 1, 0, 0, 'Comfy pillow', false, '2', 1002),
       (2, '2025-01-01', '2025-01-03', 2, 0, 1, 'Water for my cat pls', false, '2', 1000),
       (3, '2025-01-10', '2025-01-13', 2, 0, 1, 'Moar water!', false, '2', 1000),
       (4, '2025-01-14', '2025-01-18', 2, 0, 0, null, false, '1', 1000),
       (5, '2025-01-21', '2025-01-27', 2, 0, 0, 'Roses', false, '1', 1000),
       (6, '2025-01-27', '2025-02-05', 2, 0, 0, null, false, '1', 1003),
       (7, '2025-02-16', '2025-03-02', 2, 0, 0, null, false, '1', 1003),
       (8, '2025-12-20', '2026-02-20', 2, 0, 0, null, false, '1', 1003),
       (9, '2025-11-20', '2026-02-20', 2, 0, 0, null, false, '2', 1003),
       (10, '2026-03-20', '2026-05-20', 2, 0, 0, null, false, '2', 1003),
       (11, '2027-01-01', '2029-01-01', 2, 0, 0, null, false, '2', 1003),
       (12, '2026-03-20', '2026-03-21', 2, 0, 0, null, true, '2', 1003),
       (13, '2026-03-25', '2026-05-27', 2, 0, 0, null, true, '2', 1003);

