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

```
