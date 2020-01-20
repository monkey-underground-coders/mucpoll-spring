<p align="center">
  <a href="https://github.com/monkey-underground-coders/mucpoll-ts">
    <img src="https://avatars3.githubusercontent.com/u/54907581?s=200&v=4" />
  </a>
</p>

<p align="center">
 MUCPoll Backend repository. This project works with <a href="https://github.com/monkey-underground-coders/mucpoll-ts">MUCPoll frontend</a>
</p>

# mucpoll-spring
Backend for mucpoll - realtime voting website. Made for fun in TvSU :D  
Built with Spring, WebSocket + Stomp. Using Redis, FS and SQL DB.
## Features
* Realtime voting with graphs
* History saving
* Tag system
## Installing
1. Install Redis and favorite SQL database (PostgreSQL for example)
2. Fill application.yml
3. Run with Maven
4. Run [mucpoll-ts](https://github.com/monkey-underground-coders/mucpoll-ts) (frontend)
Visit http://localhost:8080/swagger-ui.html for Swagger.
