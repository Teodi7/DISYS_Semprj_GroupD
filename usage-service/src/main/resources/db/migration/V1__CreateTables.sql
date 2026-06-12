CREATE TABLE energy_data (
    hour               TIMESTAMP PRIMARY KEY,
    community_produced NUMERIC(10,3),
    community_used     NUMERIC(10,3),
    grid_used          NUMERIC(10,3)
);

CREATE TABLE current_percentage (
    hour               TIMESTAMP PRIMARY KEY,
    community_depleted NUMERIC(10,3),
    grid_portion       NUMERIC(10,3)
);
