plugins {
    java
    `java-library`
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.kakara.versionfile") version "1.0.0"

}
val engineVersion = "1.0-SNAPSHOT"
group = "org.kakara"
var branch = "";
if (hasProperty("branch")) {
    branch = properties.get("branch").toString()
}
if (hasProperty("buildNumber")) {
    version = org.kakara.engine.Version.getGameVersion(properties.get("buildNumber").toString(), branch);
} else {
    version = org.kakara.engine.Version.getGameVersion("", branch);
}
java {
    targetCompatibility = org.gradle.api.JavaVersion.VERSION_17
    sourceCompatibility = org.gradle.api.JavaVersion.VERSION_17

}
tasks.withType<Jar> {
    // Otherwise you'll get a "No main manifest attribute" error
    manifest {
        attributes["Main-Class"] = "org.kakara.client.Main"
        attributes["Launcher-Agent-Class"] = "org.kakara.client.Agent"
    }

    // Define how to handle duplicates.
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    // To add all of the dependencies
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        dependsOn(project.tasks.getByName("vftask"));
    }
    "jar"{
        dependsOn(project.tasks.getByName("vftask"));
    }
}

versionFileConfig {
    isCompileIntoJar = true;
    jarDirectory = "kakara"
    customValues["engine.version"] = engineVersion;
}

repositories {
    maven("https://repo.maven.apache.org/maven2/")
    mavenLocal()
    jcenter()
    maven("https://repo.kingtux.me/storages/maven/kingtux-repo")
    maven("https://repo.kingtux.me/storages/maven/kakara")
    maven("https://repo.kingtux.me/storages/maven/ryandw11")
    maven("https://jitpack.io")
    maven("https://repo.ryandw11.com/repository/maven-releases/")
}

dependencies {
    //Logging
    implementation("org.slf4j:slf4j-log4j12:2.0.0-beta1")
    implementation("log4j:apache-log4j-extras:1.2.17")
    implementation("com.jcabi:jcabi-log:0.20.1")

    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("commons-cli:commons-cli:1.5.0")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.google.guava:guava:31.0.1-jre")
    implementation("com.github.Carleslc:Simple-YAML:1.7.2")
    //ODS - Game Storage
    implementation(group = "me.ryandw11", name = "ods", version = "1.0.5")
    implementation(group = "me.ryandw11", name = "ODSCompressionPlus", version = "1.0.1")
    //Engine
    compileOnly(group = "org.kakara", name = "engine", version = engineVersion);
    compileOnly("io.imgui.java:binding:1.77-0.17.2")
    compileOnly("io.imgui.java:lwjgl3:1.77-0.17.2")
    //Core
    implementation("org.kakara.core:common:1.0-SNAPSHOT")
    implementation("org.kakara.core:client:1.0-SNAPSHOT")
    implementation("org.kakara.core:server:1.0-SNAPSHOT")
    //Game - The Core Game
    implementation(project(":game"))
}
