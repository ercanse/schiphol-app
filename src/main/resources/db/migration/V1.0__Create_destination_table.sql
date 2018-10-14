CREATE TABLE destination (
  id           INT PRIMARY KEY AUTO_INCREMENT,
  country      VARCHAR(50),
  city         VARCHAR(50),
  iata         VARCHAR(3),
  dutch_name   VARCHAR(256),
  english_name VARCHAR(256),
  CONSTRAINT UQ_destination_iata UNIQUE (iata)
);
