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
    implementation("org.slf4j:slf4j-api:1.8.0-beta4")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("com.google.code.gson:gson:2.10")
    implementation("com.google.guava:guava:31.0.1-jre")
    implementation(project(":game"))

}
