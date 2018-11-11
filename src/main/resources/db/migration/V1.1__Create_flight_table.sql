CREATE TABLE flight (
  id                 INT PRIMARY KEY AUTO_INCREMENT,
  api_id             BIGINT,
  flight_name        VARCHAR(20),
  schedule_date      DATE,
  terminal           BIGINT,
  gate               VARCHAR(3),
  destination        VARCHAR(3),
  is_departure       BIT,
  airline_iata       VARCHAR(2),
  airline_icao       VARCHAR(3),
  aircraft_main_type VARCHAR(10),
  aircraft_sub_type  VARCHAR(10),
  UNIQUE (api_id)
);
