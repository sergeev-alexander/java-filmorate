# java-filmorate
Template repository for Filmorate project.

![filmorate_er_diagram](https://github.com/sergeev-alexander/java-filmorate/assets/131286043/bf1037cb-e730-4839-a883-6f735de7e089)

-- SOME QUERRY EXAMPLES --
```

-- get film by <film_id>

SELECT *
FROM films
WHERE film_id = <film_id>;


-- get film genres by <film_id>

SELECT *
FROM genres
INNER JOIN film_genres USING (genre_id)
INNER JOIN films USING (film_id)
WHERE films.film_id = <film_id>;


-- get mpa by <film_id>

SELECT *
FROM mpa_ratings
INNER JOIN films USING (mpa_id)
WHERE films.film_id = '<film_id>';


-- get rates by <film_id>

SELECT user_id
FROM rates
WHERE film_id = <film_id>;


--get users friends by <user_id>

SELECT *
FROM users
INNER JOIN frienships USING (user_id)
WHERE user_id = <user_id>;
FROM users
INNER JOIN friendships USING (user_id)
INNER JOIN friendship_statuses USING (friendship_status_id)
WHERE friendships.user_id = <user_id>
	AND frendship_statuses.name = '<frendship_status.name>';
```
