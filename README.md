# schiphol-app
An application to collect and analyze flight data using the Schiphol API.

## Schiphol API
Makes use of Schiphol's [Flight API](https://www.schiphol.nl/en/developer-center/page/our-flight-api-explored/).

## Functionality
The application collects data about destinations and flights and stores this in a database. It is set up as a Spring Boot application.

## Configuration
The API ID and key should be placed in a `config.properties` file within a `config` folder in the 
root of the project, in the format:

appId = _your_app_id_
<br />
appKey = _your_app_key_
