# Vexillum Management

## About

This is a proof of concept program written in Java utilising JavaFX and Hibernate. It simulates a stock management system designed around storing, selling and buying flags and cushions with flag designs.

## How to run

* Navigate to /src/main/java/hibernate.cfg.xml
* Fill the connection.password property to your MySQL password
* Import the database schema located in /DatabaseBackup - you can either import the single .sql file or the folder with each table individually stored **!! ensure the new schema has the name "vexillumschema" !!**
* Now the program can be run from IntelliJ or by building it and then running it that way, as long as the MySQL server is running
* If you decided that you want to build and run, rather than through IntelliJ, go to Build -> Build Artifacts -> Build/Rebuild then run this in your CMD: java -cp DIRECTORY_LOCATION\FinalProject.main.jar org.vexillum.Main
* Test cases can be run from inside IntelliJ by running each file individually, or using the provided pre-created AllTests configuration, from the configurations dropdown
