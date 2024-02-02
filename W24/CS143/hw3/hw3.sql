1a) SELECT name FROM ta_restaurant WHERE name ~ '.* *Lotus .*' -- returns 29 results
1a) SELECT name FROM ta_restaurant WHERE name ~ '.* Lotus .*' -- returns 10 results
1a) SELECT name FROM ta_restaurant WHERE name ~ 'Lotus' -- returns 42 results

1b) 
SELECT rating, COUNT(rating) AS total 
FROM ta_restaurant 
GROUP BY rating 
ORDER BY rating;

1c) 
SELECT city, AVG(rating)::decimal(4, 3) AS average_rating, COUNT(*) AS num_rest 
FROM ta_restaurant 
GROUP BY city 
ORDER BY average_rating DESC;

1d) 
SELECT city 
FROM (
    SELECT city, AVG(rating) AS avg 
    FROM ta_restaurant l 
    JOIN ta_cuisine r ON l.id = r.id AND r.cuisine = 'Indian' 
    GROUP BY city 
    ORDER BY avg desc
) tmp 
WHERE tmp.avg > 4.2;


2a) 
SELECT l.id AS trip_id, (60 * EXTRACT(HOUR FROM (r.time - l.time)) + EXTRACT(MINUTE FROM (r.time - l.time))) AS time 
FROM sf_trip_start l 
JOIN sf_trip_end r ON l.id = r.id 
ORDER BY trip_id;

2b) 
SELECT COUNT(*) 
FROM sf_trip_start l 
LEFT OUTER JOIN sf_trip_end r ON l.id = r.id 
WHERE r.time IS NULL;

2c) SELECT l.id AS trip_id, COALESCE(3.49 + (0.3 * (60 * EXTRACT(HOUR FROM (r.time - l.time)) + EXTRACT(MINUTE FROM (r.time - l.time)))), 1000.00) AS trip_charge 
FROM sf_trip_start l 
FULL OUTER JOIN sf_trip_end r ON l.id = r.id 
ORDER BY trip_id;

2d)
SELECT l.id AS trip_id, u.user_type AS user_type, 
CASE WHEN user_type = 'Customer' THEN COALESCE(3.49 + (0.30 * (60 * EXTRACT(HOUR FROM (r.time - l.time)) + EXTRACT(MINUTE FROM (r.time - l.time)))), 1000.00)
when user_type = 'Subscriber' THEN COALESCE(0.20 * (60 * EXTRACT(HOUR FROM (r.time - l.time)) + EXTRACT(MINUTE FROM (r.time - l.time))), 1000.00) 
END AS trip_charge
FROM sf_trip_start l
FULL JOIN sf_trip_end r ON l.id = r.id
JOIN sf_trip_user u ON l.id = u.trip_id ORDER BY trip_id;

2e)

