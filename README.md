# MapCleaner
Minetest tool to minimize the map.sqlite

## Version
1.0; Release

## Use
* Download [MapCleaner](https://github.com/TalkLounge/MapCleaner/archive/master.zip "Link to master.zip")
* Create a new mod: mapcleaner
* Move [init.lua](https://github.com/TalkLounge/MapCleaner/blob/master/init.lua "Link to init.lua") to the new created mod
* Move [areas.txt](https://github.com/TalkLounge/MapCleaner/blob/master/areas.txt "Link to areas.txt") to the world directory
* Open [areas.txt](https://github.com/TalkLounge/MapCleaner/blob/master/areas.txt "Link to areas.txt") and input spawn details
* Activate the mod and start the server
* After server will be shutdown move [MapCleaner.jar](https://github.com/TalkLounge/MapCleaner/blob/master/MapCleaner.jar "Link to MapCleaner.jar") to the world directory
* Run it by: java -jar MapCleaner.jar

## Development
### Run
* java -jar MapCleaner.jar

### Build
* jar xf sqlite-jdbc-3.23.1.jar
* javac -cp . MapCleaner.java
* jar cfe MapCleaner.jar MapCleaner META-INF org sqlite-jdbc.properties MapCleaner.class

## Contributors
**TalkLounge**  
E-Mail: talklounge@yahoo.de  
GitHub: [TalkLounge](https://github.com/TalkLounge/ "Link to TalkLounge's GitHub account")  

**Other**  
See: [Other contributors](https://github.com/TalkLounge/MapCleaner/graphs/contributors "Link to other contributors")

## License
MIT License | See [LICENSE](https://github.com/TalkLounge/MapCleaner/blob/master/LICENSE "Link to LICENSE")
