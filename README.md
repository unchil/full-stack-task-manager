# Full-Stack Task Manager

This project is a full-stack application built with Kotlin Multiplatform. It collects sea water data from public APIs, serves it through a Ktor backend, and displays it on Android, iOS, and Desktop clients built with Compose Multiplatform.

## Modules

This project is divided into the following modules:

*   **:client**: This module is responsible for collecting data from the National Institute of Fisheries Science (NIFS) and the Ministry of Oceans and Fisheries (MOF) public APIs. It periodically fetches real-time observation data, observatory information, and ocean water quality, and stores it in a database. Data collection can be run as a batch job or as a scheduled task.

*   **:server**: This module provides the collected data to the client applications through a Ktor-based REST API. It exposes several endpoints to query sea water information, statistics, and observatory locations.

*   **:shared**: This module contains the common code shared between the `client`, `server`, and `composeApp` modules. This includes data models, business logic, and network communication code.

*   **:composeApp**: This module implements the user interface for Android, iOS, and Desktop using Compose Multiplatform. It communicates with the `server` module to fetch and display the sea water data in a user-friendly way, including tables and charts.

## How to Run

### Data Collection

To collect data, run the `client` module. You can configure the data collection mode (batch or schedule) in the configuration file.

```
./gradlew :client:run
```

### Backend Server

To start the backend server, run the following Gradle task:

```
./gradlew :server:run
```

The server will be available at `http://localhost:8080`.

### Client Applications

*   **Desktop**: `./gradlew :composeApp:run`
*   **Android**: Open the project in Android Studio and run the `composeApp` configuration.
*   **iOS**: Open the `iosApp` project in Xcode and run it on a simulator or a physical device.

## Video

|                                                              Desktop                                                               |
|:----------------------------------------------------------------------------------------------------------------------------------:|
| [![Alt text](https://github.com/unchil/full-stack-task-manager/blob/main/screenshot/screenshot.png)](https://youtu.be/AUMMcXOPThE) |
