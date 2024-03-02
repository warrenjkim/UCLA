// 1
db.airbnb.countDocuments();

// 2
db.airbnb.aggregate(
    {
        $match:
        {
            'address.country_code': "US",
            $or:
            [
                { 'reviews.comments': { $regex: "beach" } },
                { 'reviews.comments': { $regex: "ocean" } }
            ]
        }
    },
    {
        $group:
        {
            _id: null,
            avgBedrooms: { $avg: "$bedrooms" }
        }
    },
    {
        $project:
        {
            _id: 0,
            avgBedrooms: 1
        }
    }
);

// 3
db.airbnb.aggregate(
    {
        $group:
        {
            _id: "$address.country_code",
            TotalRentals: { $count: {} }
        }
    },
    { $sort: { TotalRentals: -1 } },
    { $limit: 5 }
);

// 4
db.airbnb.aggregate(
    {
        $match:
        {
            'address.market': "The Big Island",
            'address.suburb': "Kailua/Kona",
            accommodates: { $gte: 4 },
            bedrooms: { $gte: 2 },
            bathrooms: { $gte: 2 },
            $or:
            [
                { amenities: "Wifi" },
                { amenities: "Internet" }
            ],
            room_type: "Entire home/apt"
        }
    },
    {
        $project:
        {
            _id: 0,
            name: 1,
            listing_url: 1,
            property_type: 1,
            number_of_reviews: 1,
            price: 1
        }
    },
    { $sort: { price: 1 } }
);
