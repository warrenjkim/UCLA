import psycopg2

connection = psycopg2.connect(
        user = 'cs143', 
        password = 'cs143', 
        host = 'localhost', 
        port = '5432',
        database = 'cs143'
        )

connection.autocommit = True

with connection.cursor() as cur:
    cur.execute('''
                SELECT l.id AS trip_id, u.user_type AS user_type, 
                CASE WHEN user_type = 'Customer' THEN COALESCE(3.49 + (0.30 * ((EXTRACT(EPOCH FROM (r.time - l.time)) / 60)::integer)), 1000.00)
                WHEN user_type = 'Subscriber' THEN COALESCE(0.20 * ((EXTRACT(EPOCH FROM (r.time - l.time)) / 60)::integer), 1000.00)
                END AS trip_cost
                FROM sf_trip_start l
                FULL JOIN sf_trip_end r ON l.id = r.id
                JOIN sf_trip_user u ON l.id = u.trip_id ORDER BY trip_id;
                ''')

    print('SAN FRANCISCO BIKE SHARE')
    print('Roster of Charges')
    print('Trip ID       User Type     Charge')
    print('-----------   -----------   -----------')
    rows = cur.fetchall()
    for row in rows:
        trip_id, user_type, trip_charge = row
        print('%-11s   %-11s   %-11s' % (trip_id, user_type, trip_charge))

connection.close()
