plugins {
    java
}

group = "org.kakara"
version = "1.0-SNAPSHOT"


subprojects {
    repositories {
        mavenLocal()
        maven("https://repo.maven.apache.org/maven2")
        maven("https://repo.kingtux.me/storages/maven/kakara")
    }
}
