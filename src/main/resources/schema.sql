drop table if exists mpa cascade;
create table if not exists mpa
(
    id   INT PRIMARY KEY auto_increment,
    name VARCHAR(250) NOT NULL
);

drop table if exists genres cascade;
create table if not exists genres
(
    id   INT PRIMARY KEY auto_increment,
    name VARCHAR(250) NOT NULL
);

drop table if exists films cascade;
create table if not exists films
(
    id           INT PRIMARY KEY auto_increment,
    name         VARCHAR(250) NOT NULL,
    description  VARCHAR(200),
    release_date DATE         NOT NULL check (release_date >= DATE '1895-12-28'),
    duration     INT          NOT NULL check (duration > 0),
    mpa_id       INT          NOT NULL,
    foreign key (mpa_id) references mpa (id)
);

drop table if exists films_genres cascade;
create table if not exists film_genres
(
    film_id  INT NOT NULL,
    genre_id INT NOT NULL,
    PRIMARY KEY (film_id, genre_id),
    foreign key (film_id) references films (id) on delete cascade,
    foreign key (genre_id) references genres (id) on delete cascade
);

drop table if exists users cascade;
create table if not exists users
(
    id       INT PRIMARY KEY auto_increment,
    email    VARCHAR(255) NOT NULL UNIQUE,
    login    VARCHAR(255) NOT NULL UNIQUE check (login not like '% %'),
    name     VARCHAR(255),
    birthday DATE         NOT NULL
);

drop table if exists films_likes cascade;
create table if not exists film_likes
(
    film_id INT NOT NULL,
    user_id INT NOT NULL,
    PRIMARY KEY (film_id, user_id),
    foreign key (film_id) references films (id) on delete cascade,
    foreign key (user_id) references users (id) on delete cascade
);

drop table if exists user_friends cascade;
create table if not exists user_friends
(
    user_id   INT          NOT NULL,
    friend_id INT          NOT NULL,
    --- confirmed — дружба подтверждена.
    --- unconfirmed — запрос в друзья ещё не принят.
    status    VARCHAR(255) NOT NULL check (status in ('confirmed', 'unconfirmed')),
    PRIMARY KEY (user_id, friend_id),
    foreign key (user_id) references users (id) on delete cascade,
    foreign key (friend_id) references users (id) on delete cascade
);

-- create table if not exists mpa
-- (
--     id   serial primary key,
--     name varchar(250) not null
-- );
--
-- create table if not exists genres
-- (
--     id   serial primary key,
--     name varchar(250) not null
-- );
--
-- create table if not exists films
-- (
--     id           serial primary key,
--     -- @NotBlank
--     name         varchar(250) not null,
--     -- @Size(max = 200)
--     description  varchar(200),
--     -- с минимальной датой
--     release_date date         not null check (release_date >= '1895-12-28'),
--     -- @Positive
--     duration     int          not null check (duration > 0),
--     mpa_id       int          not null,
--     foreign key (mpa_id) references mpa (id)
-- );
--
-- --- on delete cascade = удаление связей, т.е.
-- --- если фильм или жанр удаляются, соответствующие записи в film_genres тоже удаляются
--
-- create table if not exists film_genres
-- (
--     film_id  int not null,
--     genre_id int not null,
--     primary key (film_id, genre_id),
--     foreign key (film_id) references films (id) on delete cascade,
--     foreign key (genre_id) references genres (id) on delete cascade
-- );
--
-- create table if not exists users
-- (
--     id       serial primary key,
--     -- @Email + уникальность
--     email    varchar(255) not null unique,
--     -- @Pattern(regexp = "\\S+")
--     login    varchar(255) not null unique check (login not like '% %'),
--     -- если имя пустое, автоматически подставляем login
--     name     varchar(255),
--     -- @PastOrPresent
--     birthday date         not null
-- );
--
-- create table if not exists film_likes
-- (
--     film_id int not null,
--     user_id int not null,
--     primary key (film_id, user_id),
--     foreign key (film_id) references films (id) on delete cascade,
--     foreign key (user_id) references users (id) on delete cascade
-- );
--
-- create table if not exists user_friends
-- (
--     user_id   int          not null,
--     friend_id int          not null,

--     status    varchar(255) not null check (status in ('confirmed', 'unconfirmed')),
--     primary key (user_id, friend_id),
--     foreign key (user_id) references users (id) on delete cascade,
--     foreign key (friend_id) references users (id) on delete cascade
-- );