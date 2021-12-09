# LimbRescueAngularAppBackendBackend

- get LimbRescueAngularAppBackend from github
- https://dev.mysql.com/doc/mysql-getting-started/en/
- Download MySQL server and setup, remember the username and password
- In application.properties, change the username and password to your password that you create
- Come up with a username and a password for the web application.
- Download maven if you have not already
- mvn spring-boot:run
- Go to localhost:8081 and type in the username and password for the web application.
- 
- Login to mysql (mysql -u root -p)
- mysql
- create database limbrescue;
- use limbrescue;
- describe limbrescue;
- Download 7-zip and go to ProgramData/MySQL/MySQL Server 8.0
- Check the "secure_file_priv" by using "SHOW VARIABLES LIKE 'secure_file_priv'"
- Go to the my.ini configuration file in the MySQL Server file and set it to the empty string.
- Go to the MySQL services under the services setting and click "Properties".
- On the log on tab, click on "local system account"
- 
- Download postman and use that to test the backend

