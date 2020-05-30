# jmacros
Macros for Java


# TODO - Phase 1
* Unit tests
* Java 11
* Java 14
* Support for Eclipse
* Support for Idea
* Windows
* OSX
* CI
* Docs

# Future Phase 2
* Extends
* Operators
* DynamicClass
* Wrapper?


# To test with different JDKs:
* JDK 11: ```git clean -xfd && docker run --rm -u gradle -v "$PWD":/home/gradle/project -w /home/gradle/project gradle:jdk11 gradle build```
* JDK 12: ```git clean -xfd && docker run --rm -u gradle -v "$PWD":/home/gradle/project -w /home/gradle/project gradle:jdk12 gradle build```
* JDK 13: ```git clean -xfd && docker run --rm -u gradle -v "$PWD":/home/gradle/project -w /home/gradle/project gradle:jdk13 gradle build```
* JDK 14: ```git clean -xfd && docker run --rm -u gradle -v "$PWD":/home/gradle/project -w /home/gradle/project gradle:jdk14 gradle build```

