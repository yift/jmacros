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

# To Open in IntelliJ:
* Import gradle project.
* Add add-exports - see [here](https://intellij-support.jetbrains.com/hc/en-us/community/posts/360001797820-How-to-add-add-exports-) to:
- jdk.compiler
    - com.sun.tools.javac.tree
    - com.sun.tools.javac.util
    - com.sun.tools.javac.processing
    - com.sun.tools.javac.code


# To Open in Eclipse:
* Import gradle project.
* Add add-exports - see [here](https://stackoverflow.com/questions/54068992/how-to-tell-eclipse-to-add-exports-when-compiling) to:
- jdk.compiler
    - com.sun.tools.javac.tree
    - com.sun.tools.javac.util
    - com.sun.tools.javac.processing
    - com.sun.tools.javac.code


# To run from Eclipse:
* Open eclipse project
* Add jar to classpath
* Add the jar to the Annotation Processing see [here](https://stackoverflow.com/questions/43404891/how-to-configure-java-annotation-processors-in-eclipse)

