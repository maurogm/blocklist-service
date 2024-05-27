# IP Blocklist Service Assignment
We need to create a microservice that manages a blocklist of IPs. This service would be used to prevent abuse in different applications to ban IPs that are known to be used for malicious purposes.

While this is a simple project to respect your time, please approach it as you would in a real production environment. 

You should be able to complete this project within 6 to 8 hours. This is not a hard limit. Once completed, please provide a rough estimate of how much time you invested into it.

## Functional requirements

The service should have a single REST endpoint that should take an IP v4 encoded as a string (e.g. `"127.0.0.1"`), and return `"true"` if the IP is part of the blacklist, and `"false"` otherwise.

This is an example of how calling the microservice can look like, but not a strict requirement:

```bash
$ curl http://blocklist/ips/127.0.0.1
false
```

### Data source

Instead of creating our own list of IPs, we are going to take advantage of [this public list](https://github.com/stamparm/ipsum), which gets updated every 24hs. The microservice should be in sync with it.

## Non-functional requirements

This service should be highly available, minimizing the time it takes to restart it and the downtime when updating the blocklist.

The service should remain operational under heavy load, and be able to respond in a reasonably low time. While we don't have a fixed and quantifiable performance requirement at the moment, its performance should be considered an important aspect.

## Technical requirements

Any technical choice, like which REST/HTTP framework or library to use, is up to you.

If you are familiar with Java, we recommend using it.

## Deliverables

A Github repository that contains:

1. The implementation of the microservice
2. Instructions on how to install it, test it, and run it
3. A written explanation of the design choices you made, and how it meets both the functional and non-functional requirements.
4. A written explanation of any compromise or trade-off that you took because of time constraints.

All of the writing should be in english.

## Slack channel

Alongside this assignment, you should have an invitation to a Slack channel that should be used for any discussion around the assignment.
Please let us know if you didn't receive the invitation.