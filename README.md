# eKoolikott

# Installation

No need to install, just download dop.jar and you are ready to go!

# Start application

By default, application will run at port 8080. The rest API is available under context path **rest/**. After starting application you can access it at:

	http://localhost:8080/rest/path/to/resource

## Default configuration

	java -jar dop.jar
	
## Custom configuration

The custom configuration file can be placed anywhere visible to the application and may have any name.

	java -jar -Dconfig="/path/to/custom.properties" dop.jar

# Configuration

The application can be configured using a custom properties file. It can be placed anywhere visible to the application and may have any name.

The available properties are:

### Database

* **db.url** - the database url
* **db.username** - the database username
* **db.password** - the database password for given username 

### Server

* **server.port** - the port that server starts.
* **command.listener.port** - the port used to listening for commands to be executed on server. Currently only shutdown command is available.