# Simple banking system
Application responsible for managing and storing users' bank accounts (cards).
In learning purposes CLI used as UI.

### Main Business Features:
1. Opening/closing bank accounts
2. Funding bank accounts
3. Checking account balance
4. Doing bank transfers
5. Storing clients data in DB

### Installing
To install app you need:
- Install jdk 17

### Build
- To create JAR artifact use command `./gradlew clean shadowJar`

### Configuration parameters
- Set starting program arguments `-fileName [name]`, where `[name]` is any valid DB name

### Run with IDE (__Intellij IDEA__)
1. Open the project in IDE
2. IDE Menu -> Run -> Edit configuration -> Program arguments -> set parameters (see Configuration parameters section)
3. Go to Application class -> Run Application.main(String[]) method
4. New DB will be created automatically (if not existed) with name provided from step 2
5. Main menu appears on the screen
6. Choose a command by entering menu number

### Run with terminal
run artifact by `jar` command.
when starting artifact pass parameters (see Configuration parameters section)
Example: `java -jar build/libs/Simple-Banking-System-1.0-SNAPSHOT.jar -fileName card.s3db`




### ToDo
- coming soon

