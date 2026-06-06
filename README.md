# droppa
Spring boot version of droppa back end.

Droppa is an on-demand delivery hub that makes it convinient and safer to transport your parcels,goods and furniture by using a mobile app and interective system.

# droppa clone

Droppa clone works similar to droppa but it has some few features that aren't implemented. i have developed this sysytem as a reference since i was working at droppa as a java/android developer.

# Security

This system uses Json web token as part of security but i have disabled it just incase you want clone the source code and test it on you local machine.

# How to use the system
Make use of post man or download the apk under droppa_clone repository. Once you have cloned the system, you can just run it to activated the server and start using the system. Embeded tomcat uses port 8080 so you'll have to make sure that the port isn't used or you can just add a new port under application.properties file.

# Features
You can open the screenshot file to view images of the swager ui. 
-The system allow users to register. Users needs to use real email when registering because they'll have to confirm their account using the OTP that they got from their email.
-once the user is registered and activated, they have options to book a vehicle,rent or use sky net.

# Docker

The Docker Compose stack starts MySQL, all three application services, and the API gateway. Only the gateway is published to the host; internal services and MySQL remain private to the Docker network.

1. Create a local environment file and replace the development credentials:

   ```powershell
   Copy-Item .env.example .env
   ```

2. Build and start the stack:

   ```powershell
   docker compose up --build
   ```

3. Open the aggregated Swagger UI at `http://localhost:8089/swagger-ui.html`.

Stop the stack with `docker compose down`. To also remove the MySQL data volume, use `docker compose down --volumes`.
