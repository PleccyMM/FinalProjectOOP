# Vexillum Management

## How to run

* Navigate to /src/main/java/hibernate.cfg.xml
* Fill the connection.password property to your MySQL password
* Import the database schema located in /DatabaseBackup - you can either import the single .sql file or the folder with each table individually stored **!! ensure the new schema has the name "vexillumschema" !!**
* Now the program can be run from IntelliJ or by compiling it and then running it that way, as long as the MySQL server is running
* Test cases can be run from inside IntelliJ by running each file individually, or using the provided pre-created AllTests configuration, from the configurations dropdown
