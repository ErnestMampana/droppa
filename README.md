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

# Development profiles

The project provides two Docker Compose profiles:

- `dev`: runs MySQL, Kafka, and Zookeeper in Docker. Run the four Spring applications from IntelliJ.
- `qa`: builds and runs the complete system in Docker.

Stop the currently active profile before switching profiles. Compose does not
automatically stop containers that belong only to the previous profile.

1. Create a local environment file and replace the development credentials:

   ```powershell
   Copy-Item .env.example .env
   ```

## Dev

Start the infrastructure:

```powershell
docker compose --profile dev up -d
```

Run these main classes from IntelliJ:

- `DroppaUserServiceApplication` on port `8081`
- `DroppaBookingServiceApplication` on port `8082`
- `DroppaDriverServiceApplication` on port `8083`
- `DroppaApiGatewayApplication` on port `8089`

The Spring `dev` profile is the default. It connects to MySQL at `localhost:3308`
and Kafka at `localhost:29092`.

When `.env` uses non-default database credentials or host ports, add the matching
environment variables to the IntelliJ run configurations because IntelliJ does
not automatically load the Compose `.env` file.

For automatic DevTools restarts in IntelliJ, enable **Build project automatically**
and **Allow auto-make to start even if developed application is currently running**.

Stop the development infrastructure with:

```powershell
docker compose --profile dev down
```

## QA

Build and start the complete Docker stack:

```powershell
docker compose --profile qa up --build -d
```

The application containers activate the Spring `qa` profile and communicate using
Docker service names. Open the aggregated Swagger UI at
`http://localhost:8089/swagger-ui.html`.

Stop QA with:

```powershell
docker compose --profile qa down
```

Add `--volumes` to either `down` command to remove the MySQL data volume.
