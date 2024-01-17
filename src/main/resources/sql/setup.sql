use backend;

# drop table if exists flat_image_entity;
# drop table if exists flat_facility_entity_flats;
# drop table if exists flat_facility_entity;
# drop table if exists flat_review_entity;
# drop table if exists price_entity;
# drop table if exists price_entity_seq;
# drop table if exists flat_entity;
# drop table if exists flat_owner_entity;
# drop table if exists address_entity;

delete from flat_facility_entity_flats where true;
delete from flat_facility_entity where true;
delete from flat_image_entity where true;
delete from price_entity where true;
delete from flat_review_entity where true;
delete from flat_entity where true;
delete from flat_owner_entity where true;
delete from address_entity where true;

replace into address_entity (id, city, country, latitude, longitude, postal_code, street)
values (1, 'Warsaw', 'Poland', 52.242196, 21.015816, '00-325', 'Krakowskie Przedmieście 42/44'),
       (2, 'Warsaw', 'Poland', 52.227837, 21.004423, '00-697', 'al. Jerozolimskie 65/79');

replace into flat_owner_entity (id, email, last_name, name, phone_number, registered_at)
values (1, 'jan.kowalski@mail.com', 'Kowalski', 'Jan', '+48 123 456 789', '2020-01-01'),
       (2, 'jan.nowak@mail.com', 'Nowak', 'Jan', '+48 987 654 321', '2018-01-01');

replace into flat_entity (id, area, bathrooms, bedrooms, capacity, description, title, type, address_entity_id,
                          flat_owner_entity_id)
values ('1', 30, 1, 1, 2, 'Luxury Marriott hotel in center of Warsaw', 'Marriott', 'hotel', 2, 2),
       ('2', 38, 2, 2, 3, 'Luxury Bristol hotel next to presidential palace', 'Bristol', 'hotel', 1, 1);

replace into flat_image_entity (id, file_name, file_type, flat_entity_id)
values ('1', 'bristol', 'png', '2'),
       ('2', 'marriott', 'png', '1');

replace into flat_facility_entity (name)
values ('wi-fi'),
       ('paid breakfast'),
       ('private bathroom');

replace into flat_facility_entity_flats (facilities_name, flats_id)
values ('wi-fi', '1'),
       ('paid breakfast', '1'),
       ('wi-fi', '2'),
       ('private bathroom', '2');

replace into price_entity (price_dollars, flat_entity_id)
values (900.0, '1'),
       (700.0, '2');

replace into user_entity (id, email, last_name, name, password)
values (1000, 'janusz@mail.com', 'Nowak', 'Janusz', 'janusznowak123');

replace into flat_review_entity (flat_entity_id, user_entity_id, rating, review)
values ('1', 1000, 5, 'Very nice place to stay. I had a great time there.'),
       ('2', 1000, 4, 'It was a pleasure to stay there although the service was not so good.');





