# Baclava Lite
The normal Baclava bot without a dashboard or Redis support.

## About
Baclava is written with [JDA](https://github.com/DV8FromTheWorld/JDA), in [Kotlin](https://kotlinlang.org/). As for the JDK, it **must** run on JDK 17.

## Building
Simply clone this repository, run `./gradlew shadowJar` and you'll have a compiled jar file in `build/libs`.

## Running
All you need is an environment variable called `TOKEN` with your bot token.

After this is done, run the bot with `java -jar JarFileName.jar`.

# License
This software is licensed under the Apache License 2.0. More information [here](https://www.apache.org/licenses/LICENSE-2.0).