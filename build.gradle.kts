// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
    id ("io.realm.kotlin") version ("1.11.0") apply false

}

buildscript{
    dependencies{
        // classpath ("io.realm:realm-gradle-plugin:10.7.0")
        classpath ("io.realm:realm-gradle-plugin:10.13.3-transformer-api")

    }
}