plugins {
    java
    `java-library`
    id("com.github.johnrengelman.shadow") version "7.1.2"

}

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

repositories {
    maven("https://repo.maven.apache.org/maven2/")
    mavenLocal()
    maven("https://repo.kingtux.me/storages/maven/kingtux-repo")
    maven("https://repo.kingtux.me/storages/maven/ryandw11")
    maven("https://repo.kingtux.me/storages/maven/kakara")

    maven("https://jitpack.io")
    maven("https://repo.ryandw11.com/repository/maven-releases/")
}

dependencies {
    //Common Game Depends Depends
    implementation("org.slf4j:slf4j-api:2.0.1")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.google.guava:guava:31.0.1-jre")
    //ODS - Game Storage
    implementation(group = "me.ryandw11", name = "ods", version = "1.0.5")
    implementation(group = "me.ryandw11", name = "ODSCompressionPlus", version = "1.0.1")
    //Engine
    compileOnly(group = "org.kakara", name = "engine", version = "1.0-SNAPSHOT");

    //Core
    implementation("org.kakara.core:common:1.0-SNAPSHOT")
    implementation("org.kakara.core:client:1.0-SNAPSHOT")
    implementation("org.kakara.core:server:1.0-SNAPSHOT")
// https://mvnrepository.com/artifact/org.jetbrains/annotations
    implementation( group="org.jetbrains", name= "annotations", version= "23.0.0")

}
