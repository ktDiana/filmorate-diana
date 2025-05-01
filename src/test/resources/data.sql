-- почистим все таблицы, потому что мало ли что
delete from film_likes;
delete from film_genres;
delete from films;
delete from users;
delete from mpa;
delete from genres;

-- сброс автоинкремента -> гарантирует чистую загрузку данных при повторных перезапусках
alter table mpa alter column id restart with 1;
alter table genres alter column id restart with 1;
alter table films alter column id restart with 1;
alter table users alter column id restart with 1;

insert into mpa (name)
values ('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');

insert into genres (name)
values ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик');

insert into films (name, description, release_date, duration, mpa_id)
values ('Побег из Шоушенка', 'История побега', '1994-09-22', 142, 4),
       ('Интерстеллар', 'Космос', '2014-11-07', 169, 3);

insert into film_genres (film_id, genre_id)
values (1, 2), --- Побег из Шоушенка – Драма
       (2, 2); --- Интерстеллар – Драма (из-за постман изменила жанры)

insert into users (email, login, name, birthday)
values ('diana31@java.com', 'diana', 'Diana', '2002-01-31'),
       ('islam@jaava.com', 'islam', 'Islam', '2004-03-08'),
       ('amina@jaaava.com', 'amina', 'Amina', '2009-09-21');

