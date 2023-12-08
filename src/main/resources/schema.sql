CREATE TABLE IF NOT EXISTS users
(
    	user_id integer GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
    	name varchar,
    	email varchar UNIQUE NOT NULL,
    	login varchar UNIQUE NOT NULL,
    	birthday date
);

CREATE TABLE IF NOT EXISTS mpa_ratings
(
	mpa_id integer GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
	name varchar
);

CREATE TABLE IF NOT EXISTS genres
(
	genre_id integer GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
	name varchar
);

CREATE TABLE IF NOT EXISTS films
(
	film_id integer GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
	mpa_id integer REFERENCES mpa_ratings(mpa_id),
	name varchar,
	description varchar,
	release_date date,
	duration integer
);

CREATE TABLE IF NOT EXISTS film_genres
(
	film_id integer REFERENCES films(film_id) ON DELETE CASCADE,
	genre_id integer REFERENCES genres(genre_id) ON DELETE CASCADE,

	PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS rates
(
	film_id integer REFERENCES films(film_id) ON DELETE CASCADE,
	user_id integer REFERENCES users(user_id) ON DELETE CASCADE,

	PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS friendships
(
	user_id integer REFERENCES users(user_id) ON DELETE CASCADE,
	friend_id integer REFERENCES users(user_id) ON DELETE CASCADE,

	PRIMARY KEY (user_id, friend_id)
);
