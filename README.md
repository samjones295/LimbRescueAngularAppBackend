## Backend Environment Setup
- get LimbRescueAngularAppBackend from github
- https://dev.mysql.com/doc/mysql-getting-started/en/
- Download MySQL server and setup, remember the username and password
- In application.properties, change the username and password to your password that you create
- Come up with a username and a password for the web application.
- Download maven if you have not already
- Run mvn spring-boot:run. This will start the spring boot application.
#MySQL database commands.
- Login to mysql (mysql -u root -p)
- mysql
- create database limbrescue;
- use limbrescue;
#Setting configurations
- Download 7-zip and go to ProgramData/MySQL/MySQL Server 8.0
- Check the "secure_file_priv" variable by using "SHOW VARIABLES LIKE 'secure_file_priv'"
- Go to the my.ini configuration file in the MySQL Server file and set it to the empty string.
- Go to the MySQL services under the services setting and click "Properties".
- On the log on tab, click on "local system account"
#Postman to test backend methods
- Download postman and use that to test the backend
- Postman could be used to run GET, POST, PUT, and DELETE requests.

