1a) 
SELECT name FROM ta_restaurant WHERE name ~ '.* Lotus .*'; -- returns 10 results

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
    JOIN ta_cuisine r ON l.id = r.id 
    WHERE r.cuisine = 'Indian'
    GROUP BY city 
    ORDER BY avg desc
) tmp 
WHERE tmp.avg > 4.2;


2a) 
SELECT l.id AS trip_id, (EXTRACT(EPOCH FROM (r.time - l.time)) / 60)::integer AS time 
FROM sf_trip_start l 
JOIN sf_trip_end r ON l.id = r.id 
ORDER BY trip_id;

2b) 
SELECT COUNT(*) 
FROM sf_trip_start l 
LEFT OUTER JOIN sf_trip_end r ON l.id = r.id 
WHERE r.time IS NULL;

2c) 
SELECT l.id AS trip_id, COALESCE(3.49 + (0.30 * ((EXTRACT(EPOCH FROM (r.time - l.time)) / 60)::integer)), 1000.00) AS trip_charge 
FROM sf_trip_start l 
FULL OUTER JOIN sf_trip_end r ON l.id = r.id 
ORDER BY trip_id;

2d) 
SELECT l.id AS trip_id, u.user_type AS user_type, 
CASE WHEN user_type = 'Customer' THEN COALESCE(3.49 + (0.30 * ((EXTRACT(EPOCH FROM (r.time - l.time)) / 60)::integer)), 1000.00)
WHEN user_type = 'Subscriber' THEN COALESCE(0.20 * ((EXTRACT(EPOCH FROM (r.time - l.time)) / 60)::integer), 1000.00)
END AS trip_cost
FROM sf_trip_start l
FULL JOIN sf_trip_end r ON l.id = r.id
JOIN sf_trip_user u ON l.id = u.trip_id ORDER BY trip_id;

2e)
Because we do a full outer join, placing the condition in the ON clause will do the following:
- Matched rows from both tables will be included. 
- Unmatched rows from the start_time table will have NULL's for the end_time columns. 
- Unmatched rows from the end_time table will have NULL's for the start_time columns. 
So, it will not filter out any rows from the result table.
