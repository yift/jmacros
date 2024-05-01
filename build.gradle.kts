plugins {
    `java-library`
    jacoco
    `maven-publish`
    signing
    id("com.diffplug.spotless") version "6.25.0";
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.0-M1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.0-M1")
    testImplementation("io.hotmoka:toml4j:0.7.3")
    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("org.mockito:mockito-core:5.11.0")
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform()
}

version = "0.1.2"

spotless {
    java {
        googleJavaFormat()
    }
}

allprojects {
    repositories {
        mavenCentral()
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
    options.compilerArgs.add("--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED")
}


tasks.withType<Javadoc> () {
    excludes.add("me/ykaplan/jmacros/processor/**")
    var opts = options;
    if(opts is StandardJavadocDocletOptions) {
        opts.links("https://docs.oracle.com/en/java/javase/11/docs/api/")
    }
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
        val dir = "${layout.buildDirectory.get()}/classes/java/main/META-INF/services/";
        mkdir(dir)
        File("${dir}/javax.annotation.processing.Processor").writeText(processors);
    }
}
tasks.test {
    this.testLogging {
        this.showStandardStreams = true
    }
    finalizedBy(tasks.jacocoTestReport)
}
tasks.jacocoTestReport {
    dependsOn(tasks.test)
}
tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.8".toBigDecimal()
            }
        }

    }
}

tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allJava)
}

tasks.register<Jar>("javadocJar") {
    dependsOn(tasks.javadoc)
    archiveClassifier.set("javadoc")
    from(tasks.javadoc.get().destinationDir)
}

publishing {
    repositories {
        maven {
            name = "MavenCentral"
            val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
            val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            credentials {
                username = System.getenv("MAVEN_UPLOAD_USER")
                password = System.getenv("MAVEN_UPLOAD_PWD")
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "me.ykaplan.jmacros"
            artifactId = "jmacros"
            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])

            pom {
                name.set("jMacros")
                description.set("A library to allow meta-programming in Java.")
                url.set("https://jmacros.ykaplan.me/")
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://github.com/yift/jmacros/blob/master/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("yift")
                        name.set("Yiftach Kaplan")
                        email.set("yift@ykaplan.me")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/yift/jmacros.git")
                    developerConnection.set("scm:git:https://github.com/yift/jmacros.git")
                    url.set("https://github.com/yift/jmacros")
                }

            }
        }
    }
}

signing {
    useInMemoryPgpKeys(System.getenv("PGP_SIGNING_KEY"), System.getenv("PGP_SIGNING_PASSWORD"))
    sign(publishing.publications["mavenJava"])
}
tasks.publish {
    dependsOn(tasks.build)
    dependsOn(tasks.findByPath("sourcesJar"))
    dependsOn(tasks.findByPath("javadocJar"))
}
