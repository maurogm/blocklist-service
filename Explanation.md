# Design explanations

### Choice of framework
The assigned task is quite tangential to what I'm used to.
In fact, throughout my professional career there was only one time when I built a REST API, and I did it with the support of a colleague.
So I didn't have any framework at hand that I was already familiar with, and therefore knew from the start that I would need to consult examples from other applications to guide myself.

A second factor was that since this is not my alley, there are surly a lot of things that I'm not aware of, a lot of mistakes that I would be prone to make.
So I reasoned that it would be desirable to choose a tool that is known for being robust and doing things right out of the box, in contrast with
maybe a more lightweight option, or one that calls for a lot of manual configurations.

These reasons led me to choose HTTP4s as the framework because I had read [tutorials from a blog I quite like](https://blog.rockthejvm.com/http4s-tutorial/),
and it was the framework used in a book I partially read a while back, called [Practical FP in Scala](https://leanpub.com/pfp-scala).
The downside of choosing this framework is that it heavily relies on the Typelevel pure functional programming ecosystem,
which can be counterintuitive if you are not familiar with it.
Despite this, I decided to prioritize having a few references and the fact that it gave me a head start.
Moreover, HTTP4s is known for its robustness and adherence to best practices in building RESTful APIs, and it's Effect System is specially suited to candle concurrency, 
which aligns well with my need for a reliable and well-supported tool, and the need to handle heavy traffic.

Also, the assignment showed a preference for Java. Scala was as adjacent to it as I could manage. 

## Project architecture
I tried to achieve modularity and separation of responsibilities when designing the project:
- **Blocklist**: Handles the local blocklist storage. It's the only one concerned with the storage implementation.  
- **SoTFetcher**: Handles the communication with the remote blocklist of IPs.
- **BlocklistService**: Implements the business logic related to the blocklist.     
- **SynchronizerService**: Manages the synchronization of the local blocklist with the remote source of truth.


## Satisfaction of requirements

### Functional Requirements

- **The service should take an IP v4 encoded as a string**.
  - Before reacting to the request, the input is validated to verify that it is indeed an IPv4. If it is not, an error code is returned to the user. This validation step is cleaner and avoids exploits.
- **[...] public list, which gets updated every 24hs. The microservice should be in sync with it**.
  - In parallel with the server, there is a process running with periodically sees if our Source of Truth (the public list) has been updated. If it has, it gets the new lists of IPs and updates our database.
    - The frequency of this process is configurable.
    - To know if the list has changed the service just sees the ETag of the txt file on github, which is a hash-like property on the headers. This way, this check is practically free, and we only have to download the file when we are sure that the file has changed.

### Non-functional Requirements

- **The service should remain operational under heavy load, and be able to respond in a reasonably low time**
    - The combination of HTTP4S and Redis was chosen so that the service would be able to have a good performance in this aspect.
    - Cats Effect, the engine HTTP4S is built on, can seamlessly handle thousands of concurrent processes, efficiently managing the scheduling and resource allocation behind the curtains with its lightweight fiber-based concurrency model.
    - Redis offers in-memory data storage, which allows for very quick access to the blocklist, minimizing latency and ensuring fast responses.
- **Minimize the time it takes to restart it**
  - Redis was set to restart automatically if it ever shuts down.
    - It was also configured to back up a snapshot of the database after it makes any changes (which should happen once a day). This allows Redis to immediately be able to answer queries once it's restarted, using the most recent data available.  
- **Minimize the downtime when updating the blocklist**
  - The blocklist synchronization process does not interfere with the availability of the server, since it runs in parallel in a separate fiber.
  - Once the new data is fetched and the database has to be modified, Redis handles the updating operations while still remaining available to answer queries form the blocklist service.


## Trade-offs and time constrains
- **Test suit**. The most important thing that was left out was an actual test suit. I am well exceeded of the 6 to 8 hours proposed time, and as I mentioned this kind of project is not something I'm used to. The most important tests for this kind of application would involve databases and web servers, for which I have no experience in testing. I could do some research and look for examples, but that would add even more time to my tally.
- **Error handling**. There is some basic error handling in some components like the Fetcher or the case scenarios of the synchronizer, but probably this could use a more comprehensive and better use of error handling.
- **Logging**. Almost no logging was implemented, it could use more.
- **HTTP configurations**. Both the server and the client (for the Fetcher) are built with almost default configurations. Mostly because of very basic knowledge on my part. They could surly use more thought put into it, like in the implementation of retry policies, timeouts and various other optimizations.
- **Security**. I only implemented the validation of inputs, and not much else. I know there is a lot that I don't know here, but I didn't have the time to do any research.


### Time estimation
I knew from the start that this would take me more than the proposed 6 to 8 hours, given my lack of experience in building this sort of services.
So I decided not to even try to meet a time constraint that I already knew was beyond my capabilities, which in my case would result in a bad solution.
Instead, I aimed to develop something that met the specifications as well as I could manage, even if it involved taking extra time to learn to use some technologies for the first time.
As a result, the implementation took significantly longer than anticipated, approximately 14 hours.
