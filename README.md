# IP Blocklist Service

## Overview

The IP Blocklist Service is a Scala-based microservice designed to manage a blocklist of IP addresses.
Using the Http4s framework for the REST API and Redis for the data access and persistence, the service monitors and fetches a daily updated list of suspicious and/or malicious IP addresses from a public source, and provides an endpoint to check is a given IP address is the list.
The service is containerized using Docker and Docker Compose to facilitate deployment and portability.

This [README](README.md) covers the installation and usage instructions for the service.
For an explanation of the decisions made when designing the project refer to [Explanation.md](Explanation.md).

## Installation instructions

### Prerequisites

- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)

### Installation

1. **Clone the repository:**
2. **Standing in the project's root directory, build and run the service using Docker Compose:**
   ```sh
   docker compose up
   ```
    - Optionally can add the `-d` flag to run in detached mode, regaining the control of your terminal. 

To check if the service is up and running, you can try:
```sh
curl http://127.0.0.1:8080/health
```

## Usage
The service should be accessible through localhost or any valid IP address, and port 8080.
For example, you can send requests to `http://127.0.0.1:8080/{endpoint}`.


### Endpoints

- **Check IP Block Status:**
    - **GET /ips/{ip}**
        - **Response:** Returns `"true"` if the IP is blocked, `"false"` otherwise.
        - Requests that send anything other than a valid IPv4 address return a Bad Request error.

- **Health Check:**
    - **GET /health**
        - **Response:** Returns `"Service is up and running"` if the service is operational.

## Configuration

Some behaviour of the service can be managed through environment variables:
- `SOT_URL`: URL of the source of truth for the IP blocklist. Should point to one of the files listed [here](https://github.com/stamparm/ipsum/tree/master/levels). 
- `REDIS_HOST`: Redis host. Default for production environment is "redis", but "localhost" is used for development.  
- `REDIS_PORT`: Redis port. Usually 6379.
- `SYNCHRONIZER_SECONDS_REFRESH`: Time interval (in seconds) for the service to check if the Source of Truth was updated.

The values for these variables are set in the [docker-compose file](docker-compose.yml).

## Development

### Running the service outside of Docker
You must have the following:
- Java Development Kit (JDK) 11 or higher
- Scala 2.13
- sbt (Scala Build Tool)

In order to run the service, you will need access to a redis server. You can run: 
   ```sh
   docker compose up redis -d
   ```

You will also have to provide the values for the configuration environment variables, such as the ones in [this .env](.env). 

### Consulting redis from your terminal
To communicate directly with the DB inside the container, run:
```sh
docker exec -it redis-blocklist redis-cli
```
Once in the cli, you can run redis commands such as:
```redis
> GET blocklist:identifier
> SMEMBERS blocklist:ips
> SCARD blocklist:ips
```


## Performance Testing

Some basic performance tests where done using [Vegeta](https://github.com/tsenart/vegeta).

Should you try to replicate them, you should start by installing it following [some instructions](https://www.scaleway.com/en/docs/tutorials/load-testing-vegeta/)

### Usage

1. A target file is provided in [vegeta_targets.txt](src/test/resources/vegeta_targets.txt). Modify it if you want.

2. Run the attack and analyze the report:
    ```sh
    vegeta attack -duration=10s -rate=10000 -targets=./src/test/resources/vegeta_targets.txt | vegeta report -type=text
    ``````
You should get a response in this format:
```commandline
Requests      [total, rate, throughput]         99999, 10000.74, 10000.22
Duration      [total, attack, wait]             10s, 9.999s, 518.254µs
Latencies     [min, mean, 50, 90, 95, 99, max]  53.542µs, 1.095ms, 582.224µs, 1.217ms, 2.509ms, 14.679ms, 106.726ms
Bytes In      [total, mean]                     547615, 5.48
Bytes Out     [total, mean]                     0, 0.00
Success       [ratio]                           100.00%
Status Codes  [code:count]                      200:99999  
Error Set:
```

3. Modify the parameters, especially with the rate (requests per second), to find out how the service behaves for your use case.   

