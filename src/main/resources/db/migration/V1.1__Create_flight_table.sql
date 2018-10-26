CREATE TABLE flight (
  id            INT PRIMARY KEY AUTO_INCREMENT,
  flight_name   VARCHAR(50),
  schedule_date DATE
  destination VARCHAR (3),
  gate          VARCHAR(3),
  is_departure  BIT
);
