def get_season_info(city, lat=None):
    # Default: assume northern hemisphere
    hemisphere = "north"
    if lat is not None and lat < 0:
        hemisphere = "south"

    if hemisphere == "north":
        return {
            "summer": ["June", "July", "August"],
            "winter": ["December", "January", "February"],
            "best_months": ["April", "May", "September", "October"],
            "crowded": ["July", "August"]
        }
    else:
        return {
            "summer": ["December", "January", "February"],
            "winter": ["June", "July", "August"],
            "best_months": ["March", "April", "September", "October"],
            "crowded": ["December", "January"]
        }
