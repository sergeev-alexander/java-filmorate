# java-filmorate
Template repository for Filmorate project.

![filmorate ER diagram](https://github.com/sergeev-alexander/java-filmorate/assets/131286043/143683b3-4936-420f-9083-0368b108f0f6)


-- SOME QUERRY EXAMPLES --
```
-- get film by <film_id>

SELECT *
FROM films
WHERE films.film_id = <film_id>;


-- get film genre names by <film_id>

SELECT genres.name
FROM films
INNER JOIN film_genres USING (film_id)
INNER JOIN genres USING (genre_id)
WHERE films.film_id = <film_id>;


-- get films by <genre_name>

SELECT *
FROM films
INNER JOIN film_genre USING (film_id)
INNER JOIN genres USING (genre_id)
WHERE genres.name = '<genre_name>';


-- get films by <mpa_name>

SELECT *
FROM films
INNER JOIN mpa_ratings USING (mpa_id)
WHERE mpa.name = '<mpa_name>';


-- get <film_id> likes quantity

SELECT COUNT(likes.film_id)
FROM likes
WHERE likes.film_id = <film_id>;


--get users liked <film_id>

SELECT *
FROM users
INNER JOIN likes USING (film_id)
WHERE likes.film_id = <film_id>;


-- get liked films by <user_id>

SELECT *
FROM films
INNER JOIN likes USING (user_id)
WHERE user_id = <user_id>;


-- get <user_id> friends in status <frendship_status.name>
-- friendship_statuses contains {1 = send_offer, 2 = received_offer, 3 = friends}

SELECT *
FROM users
INNER JOIN friendships USING (user_id)
INNER JOIN friendship_statuses USING (friendship_status_id)
WHERE friendships.user_id = <user_id>
	AND frendship_statuses.name = '<frendship_status.name>';


-- get <user_id> NOT friends

SELECT *
FROM users
WHERE users.user_id NOT IN (SELECT friend_id
			    FROM friendships
			    WHERE friendships.user_id = <user_id>);


-- get <user_id> friends quantity in status <frendship_status.name>
-- friendship_statuses contains {1 = send_offer, 2 = received_offer, 3 = friends}

SELECT COUNT(*)
FROM users
INNER JOIN friendships USING (user_id)
INNER JOIN friendship_statuses USING (friendship_status_id)
WHERE friendships.user_id = <user_id>
	AND frendship_statuses.name = '<frendship_status.name>';
```
