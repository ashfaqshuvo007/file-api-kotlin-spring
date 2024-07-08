# File API

Simple API for file Upload/Download.

## Functionalities
- User can upload files and a token for this file is returned
- User can download a file using a token with appropriate headers (some additional metadata headers are also needed).
- User can request meta data for a single file using token
- User can request meta data for multiple file in a single request using list of tokens

### Additional Features
- Documentation describing all the endpoints and responses. Details are [here](src/main/resources/public/docs/openapi.yml)
- Invalid request returns `400 Bad Request` in response body.
- Internal exceptions are handled, logged and returns `503 Service Unavailable`

## Start-up

### Starting the database
    docker-compose up -d

### Configuration

Environment variables are set in `variables.env` file.

## Usage
To test the app locally, add

    127.0.0.1    filedb
to your `/etc/hosts` file

For basic auth, username is `admin` and password is `hunter2`

### Start from CLI

The shell script makes it easy
    ./do.sh start

For API documentation go to http://localhost:6011/docs
