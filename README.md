# Distributed Systems - Energy Community (Group D)

Our project consists of the following components:

- **energy-producer** – sends PRODUCER messages, the kWh value depends on the weather (Weather API)
- **energy-user** – sends USER messages, the kWh value depends on the time of day
- **usage-service** – stores the messages in the `energy_data` table and forwards an update message
- **current-percentage-service** – calculates `community_depleted` / `grid_portion` into `current_percentage`
- **energy-api** – REST API (Spring Boot), reads the data from the database
- **energy-gui** – GUI (JavaFX), fetches the data through the REST API