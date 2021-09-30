## Run Instructions
### Without Docker
* Open root directory in terminal
* Run following command: ``` mvn clean install ```
* Open target directory. ```courier-0.0.1-SNAPSHOT.jar``` file must be created.
* Run following command: ``` java -jar courier-0.0.1-SNAPSHOT.jar```

### With Docker
* Open root directory in terminal
* Run the command: ``` mvn clean install ```
* Run the command: ``` docker build -t courier .```
* After image is created, run the command: ``` docker run -p 8080:8080 courier```

### With script
* Just double click ```run.bat``` file.

Application is ready to use. There are 2 endpoints:
* ```/api/courier/track-courier``` : takes courier info and returns a store that the courier enters it if it is valid entrance
* ```/api/courier/total-travel-distance``` : takes courier id and returns total travel distance of the courier.

