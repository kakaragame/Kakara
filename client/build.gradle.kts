plugins {
    java
    `java-library`
    id("com.github.johnrengelman.shadow") version "6.1.0"

}

group = "org.kakara"
version = "1.0-SNAPSHOT"
version = "1.0-SNAPSHOT"
if (hasProperty("buildNumber")) {
    version = "1.0-" + properties.get("buildNumber") + "-SNAPSHOT";
}
java {
    targetCompatibility = org.gradle.api.JavaVersion.VERSION_11
    sourceCompatibility = org.gradle.api.JavaVersion.VERSION_11

}
tasks.withType<Jar> {
    // Otherwise you'll get a "No main manifest attribute" error
    manifest {
        attributes["Main-Class"] = "org.kakara.client.Main"
        attributes["Launcher-Agent-Class"] = "org.kakara.client.Agent"
    }

    // To add all of the dependencies
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

repositories {
    maven("https://repo.maven.apache.org/maven2/")
    mavenLocal()
    jcenter()
    maven("https://repo.kingtux.me/storages/maven/kingtux-repo")
    maven("https://repo.kingtux.me/storages/maven/ryandw11")
    maven("https://jitpack.io")
    maven("https://repo.ryandw11.com/repository/maven-releases/")
}

dependencies {
    //Logging
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("org.slf4j:slf4j-log4j12:1.7.5")
    implementation("log4j:apache-log4j-extras:1.2.17")
    implementation("com.jcabi:jcabi-log:0.17.3")

    implementation("org.apache.commons:commons-lang3:3.11")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("commons-cli:commons-cli:1.4")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.google.guava:guava:30.1-jre")
    implementation("com.github.Carleslc:Simple-YAML:1.7.2")
    //ODS - Game Storage
    implementation(group = "me.ryandw11", name = "ods", version = "1.0.4")
    implementation(group = "me.ryandw11", name = "ODSCompressionPlus", version = "1.0.1")
    //Engine
    compileOnly("org.kakara:engine:1.0-PRE4-SNAPSHOT")
    //Core
    implementation("org.kakara.core:common:1.0-SNAPSHOT")
    implementation("org.kakara.core:client:1.0-SNAPSHOT")
    implementation("org.kakara.core:server:1.0-RW-SNAPSHOT")
    //Game - The Core Game
    implementation(project(":game"))
}
