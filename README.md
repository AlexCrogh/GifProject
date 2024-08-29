## Gif Aggregator & Poster

This project is a Spring Boot application designed to aggregate popular GIFs from Reddit using the Pushshift API and then post them randomly to Twitter. The project features a RESTful API for managing the GIFs in a MySQL database.

**Important Note**: This version of the project is deprecated as the Pushshift API has restricted access as of July 2023. The application may no longer function as intended without significant modifications to accommodate these changes.

## Table of Contents

- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Scheduled Tasks](#scheduled-tasks)
- [Known Issues](#known-issues)

## Features

- Aggregates GIFs from Reddit based on popularity.
- Stores GIF metadata in a MySQL database.
- Provides a REST API for CRUD operations on the GIFs.
- Posts a random GIF to Twitter at scheduled intervals.
- Supports fetching the most recent, random, or unposted GIFs from the database.

## Installation

### Prerequisites

- Java 11 or higher
- MySQL database
- Twitter API credentials
- Maven

### Setup

1. Clone the repository:

    ```bash
    git clone https://github.com/AlexCrogh/GifProject.git
    cd gif-aggregator-poster
    ```

2. Configure the database connection in `application.properties`:

    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/yourDatabaseName
    spring.datasource.username=yourUsername
    spring.datasource.password=yourPassword
    ```

3. Add your Twitter API credentials to `application.properties`:

    ```properties
    twitter.apiKey=yourApiKey
    twitter.apiSecretKey=yourApiSecretKey
    twitter.accessToken=yourAccessToken
    twitter.accessTokenSecret=yourAccessTokenSecret
    ```

4. Build the project using Maven:

    ```bash
    mvn clean install
    ```

5. Run the application:

    ```bash
    java -jar target/gif-aggregator-poster-0.0.1-SNAPSHOT.jar
    ```

## Usage

### API Endpoints

The following endpoints are available:

- **Create GIF**
    - `POST /createGif`
    - **Request Body**: JSON representation of a `Gif` object.
    - **Response**: The created `Gif` object or an error message.

- **Get GIF by ID**
    - `GET /getGif/{id}`
    - **Response**: A `Gif` object or an error message.

- **Get All GIFs**
    - `GET /getAllGifs`
    - **Response**: A list of all `Gif` objects in the database.

- **Get Random GIF**
    - `GET /getRandomGif`
    - **Response**: A random `Gif` object from the database.

- **Get Unposted GIF for Twitter**
    - `GET /getRandomUnpostedGif`
    - **Response**: A random `Gif` object that hasn't been posted to Twitter yet.

- **Get Most Recent GIF**
    - `GET /getMostRecentGif`
    - **Response**: The most recent `Gif` object added to the database.

- **Update GIF**
    - `PUT /updateGif`
    - **Request Body**: JSON representation of a `Gif` object.
    - **Response**: The updated `Gif` object or an error message.

- **Delete GIF**
    - `DELETE /deleteGif/{id}`
    - **Response**: Success or error message.

### Scheduled Tasks

The application includes scheduled tasks to post GIFs to Twitter. These tasks are configured in the `ScheduledTasks` class and run at specified intervals using Spring’s scheduling annotations.

### Known Issues

- **Pushshift API Changes**: The project relies on the Pushshift API to aggregate GIFs from Reddit. As of July 2023, Pushshift has restricted access to its API, which may cause the application to fail in fetching new GIFs. Consider exploring alternative APIs or datasets to replace Pushshift.



