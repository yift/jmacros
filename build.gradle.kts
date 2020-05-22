plugins {
    `java-library`

    id("com.diffplug.gradle.spotless") version "4.0.0";
}

repositories {
    jcenter()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    testImplementation("com.moandjiezana.toml:toml4j:0.7.2")
    testImplementation("org.assertj:assertj-core:3.16.1")
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform()
}

spotless {
    java {
        googleJavaFormat()
    }
}

allprojects {
    repositories {
        jcenter()
    }
}

tasks.withType<JavaCompile>() {
    options.compilerArgs.add("-Xlint:all")
    options.compilerArgs.add("-Xlint:-path")
    options.compilerArgs.add("-Werror")
    options.compilerArgs.add("--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED")
    options.compilerArgs.add("--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED")
    options.compilerArgs.add("--add-exports=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED")
    options.compilerArgs.add("--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED")
}

tasks.jar {
    dependsOn(":createMetaInfService")
}

tasks.register("createMetaInfService") {
    doFirst {
        val processors =  File("${projectDir}/src/main/java/me/ykaplan/jmacros/processor")
                .listFiles()
                .map {it.name}
                .filter { it.endsWith("Processor.java") }
                .map{it.dropLast(5)}
                .map{"me.ykaplan.jmacros.processor." + it}
                .joinToString(separator = "\n")
        val dir = "${buildDir}/classes/java/main/META-INF/services/";
        mkdir(dir)
        File("${dir}/javax.annotation.processing.Processor").writeText(processors);
    }
}