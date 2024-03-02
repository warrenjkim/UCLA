// 1
MATCH (movie:Movie { released: 2008 }) RETURN movie.title AS `title`, movie.tagline AS `tagline`;


// 2
MATCH (:Movie { title: "Jerry Maguire" })<-[:ACTED_IN]-(actor:Person) RETURN actor.name AS `Name`, actor.born AS `Year Born`;

// 3
MATCH (person:Person)-[:DIRECTED]->(movie:Movie)<-[:ACTED_IN]-(person:Person) RETURN person, movie;

// 4
MATCH (:Person { name: "Tom Cruise" })-[relationship]->(m:Movie) RETURN m.title, type(relationship) AS `relationship`;

// 5
MATCH (person:Person)-[:ACTED_IN]->(movie:Movie { title: "Apollo 13" }) RETURN avg(movie.released - person.born) AS `average_age`;
