import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import io.papermc.paperweight.tasks.RemapJar
import org.gradle.api.file.DuplicatesStrategy

plugins {
    java
    id("io.papermc.paperweight.userdev") version "1.7.1"
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("com.gradleup.shadow") version "9.3.1"
}

group = "nl.sundeep"
version = "1.0.0"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    maven { setUrl("https://repo.papermc.io/repository/maven-public/") }
    maven { setUrl("https://oss.sonatype.org/content/groups/public/") }
    maven { setUrl("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
    maven { setUrl("https://jitpack.io") }
    maven { setUrl("https://repo.codemc.org/repository/maven-public/") }
    maven { setUrl("https://maven.enginehub.org/repo/") }
    maven { setUrl("https://repo.aikar.co/content/groups/aikar/") }
}

dependencies {
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")

    implementation("com.github.Revxrsal.Lamp:common:3.2.1")
    implementation("com.github.Revxrsal.Lamp:bukkit:3.2.1")

    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.xerial:sqlite-jdbc:3.45.1.0")
    implementation("com.mysql:mysql-connector-j:8.3.0")

    implementation("org.spongepowered:configurate-yaml:4.1.2")
    implementation("org.spongepowered:configurate-hocon:4.1.2")

    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("me.clip:placeholderapi:2.11.5")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9")
    compileOnly("com.arcaniax:HeadDatabase-API:1.3.2")
}

tasks {
    // We gebruiken shadow + reobf als eindproduct
    jar {
        enabled = false
    }

    assemble {
        dependsOn("reobfJar")
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }

    compileTestJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
        val props = mapOf(
            "name" to "VulcanSMP",
            "version" to project.version,
            "description" to "Een uitgebreide core plugin voor je Minecraft server",
            "apiVersion" to "1.21"
        )
        inputs.properties(props)
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }

    named<ShadowJar>("shadowJar") {
        archiveClassifier.set("") // geen "-all"
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        relocate("com.zaxxer.hikari", "nl.sundeep.vulcansmp.libs.hikari")
        relocate("revxrsal.commands", "nl.sundeep.vulcansmp.libs.lamp")
        relocate("org.spongepowered.configurate", "nl.sundeep.vulcansmp.libs.configurate")

        minimize()
    }

    named("reobfJar") {
        dependsOn("shadowJar")
    }

    runServer {
        minecraftVersion("1.21.1")
    }
}

// Laat reobfJar de shadowJar als input gebruiken
tasks.named<RemapJar>("reobfJar") {
    val shadow = tasks.named<ShadowJar>("shadowJar")
    inputJar.set(shadow.flatMap { it.archiveFile })
}
