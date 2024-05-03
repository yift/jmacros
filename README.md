# jmacros
This project provides a list of "macros" for java.

To use it, add the [jar](https://jmacros.ykaplan.me/libs/jmacros-0.1-SNAPSHOT.jar) to  the compilation classpath. If the compilation won't fail, there should be no need to add it to the execution classpath.

Detailed javadoc can be found [here](https://jmacros.ykaplan.me/docs/javadoc/).


Development reports can be found in:
* [Test](https://jmacros.ykaplan.me/reports/tests/test/)
* [Coverage](https://jmacros.ykaplan.me/reports/jacoco/test/html/)


# IDE support
 
## IntelliJ:
While working with IntelliJ:
* [Alias](https://jmacros.ykaplan.me/docs/javadoc/me/ykaplan/jmacros/Alias.html), [Macro](https://jmacros.ykaplan.me/docs/javadoc/me/ykaplan/jmacros/Macro.html) or [LiteralMacro](https://jmacros.ykaplan.me/docs/javadoc/me/ykaplan/jmacros/LiteralMacro.html) might show errors and the auto complete will not work.

* [LiteralMacro](https://jmacros.ykaplan.me/docs/javadoc/me/ykaplan/jmacros/LiteralMacro.html) methods, [DebugPrint](https://jmacros.ykaplan.me/docs/javadoc/me/ykaplan/jmacros/DebugPrint.html) and [Interpolation](https://jmacros.ykaplan.me/docs/javadoc/me/ykaplan/jmacros/Interpolation.html) should work.

This should be fixed by the [plug-in](https://github.com/yift/jmacros/issues/5).

To open the project with IntelliJ:
* Import gradle project.
* Add add-exports - see [here](https://intellij-support.jetbrains.com/hc/en-us/community/posts/360001797820-How-to-add-add-exports-) to:
- jdk.compiler
    - com.sun.tools.javac.tree
    - com.sun.tools.javac.util
    - com.sun.tools.javac.processing
    - com.sun.tools.javac.code
    - com.sun.tools.javac.api


## Eclipse:
While working with the Eclipse compiler:
* [Alias](https://jmacros.ykaplan.me/docs/javadoc/me/ykaplan/jmacros/Alias.html), [Macro](https://jmacros.ykaplan.me/docs/javadoc/me/ykaplan/jmacros/Macro.html) or [LiteralMacro](https://jmacros.ykaplan.me/docs/javadoc/me/ykaplan/jmacros/LiteralMacro.html) will not compile.

* [LiteralMacro](https://jmacros.ykaplan.me/docs/javadoc/me/ykaplan/jmacros/LiteralMacro.html) methods will be invoked instead of replaced.

* [DebugPrint](https://jmacros.ykaplan.me/docs/javadoc/me/ykaplan/jmacros/DebugPrint.html) and [Interpolation](https://jmacros.ykaplan.me/docs/javadoc/me/ykaplan/jmacros/Interpolation.html) will compile but do nothing.

This should be fixed by the [plug-in](https://github.com/yift/jmacros/issues/4).

To open the project with Eclipse:
* Import gradle project.
* Add add-exports - see [here](https://stackoverflow.com/questions/54068992/how-to-tell-eclipse-to-add-exports-when-compiling) to:
- jdk.compiler
    - com.sun.tools.javac.tree
    - com.sun.tools.javac.util
    - com.sun.tools.javac.processing
    - com.sun.tools.javac.code
    - com.sun.tools.javac.api

# To test with different JDKs:
* JDK 11: ```git clean -xfd && docker run --rm -u gradle -v "$PWD":/home/gradle/project -w /home/gradle/project gradle:jdk11 gradle build```
* ~~JDK 12: ```git clean -xfd && docker run --rm -u gradle -v "$PWD":/home/gradle/project -w /home/gradle/project gradle:jdk12 gradle build```~~
* JDK 13: ```git clean -xfd && docker run --rm -u gradle -v "$PWD":/home/gradle/project -w /home/gradle/project gradle:jdk13 gradle build```
* JDK 14: ```git clean -xfd && docker run --rm -u gradle -v "$PWD":/home/gradle/project -w /home/gradle/project gradle:jdk14 gradle build```
* JDK 15: ```git clean -xfd && docker run --rm -u gradle -v "$PWD":/home/gradle/project -w /home/gradle/project gradle:jdk15 gradle build```
* JDK 16: ```git clean -xfd && docker run --rm -u gradle -v "$PWD":/home/gradle/project -w /home/gradle/project gradle:jdk16 gradle build```
* JDK 17: ```git clean -xfd && docker run --rm -u gradle -v "$PWD":/home/gradle/project -w /home/gradle/project gradle:jdk17 gradle build```
* JDK 18: ```git clean -xfd && docker run --rm -u gradle -v "$PWD":/home/gradle/project -w /home/gradle/project gradle:jdk18 gradle build```
* JDK 19: ```git clean -xfd && docker run --rm -u gradle -v "$PWD":/home/gradle/project -w /home/gradle/project gradle:jdk19 gradle build```
* JDK 20: ```git clean -xfd && docker run --rm -u gradle -v "$PWD":/home/gradle/project -w /home/gradle/project gradle:jdk20 gradle build```
* JDK 21: ```git clean -xfd && docker run --rm -u gradle -v "$PWD":/home/gradle/project -w /home/gradle/project gradle:jdk21 gradle build```
* JDK 22: ```./scripts/build-22.sh```

docker run --rm -it alpine

apk add curl bash zip
curl -s "https://get.sdkman.io" | bash
