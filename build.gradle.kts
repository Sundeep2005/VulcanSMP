import net.minecrell.pluginyml.paper.PaperPluginDescription;

plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("com.gradleup.shadow") version "9.3.1"
    id("de.eldoria.plugin-yml.paper") version "0.7.1"
}

group = "nl.sundeep"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://jitpack.io")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.aikar.co/content/groups/aikar/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")

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

paper {
    name = "VulcanSMP"
    version = project.version.toString()
    apiVersion = "1.21"
    main = "nl.sundeep.vulcansmp.VulcanSMP"
    authors = listOf("Sundeep2005", "TheBathDuck")

    serverDependencies {
        register("Vault") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = false
        }
        register("PlaceholderAPI") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = false
        }
        register("LuckPerms") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = false
        }
        register("WorldGuard") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = false
        }
        register("HeadDatabase") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = false
        }
    }
}

tasks {
    shadowJar {
        relocate("com.zaxxer.hikari", "nl.sundeep.vulcansmp.libs.hikari")
        relocate("revxrsal.commands", "nl.sundeep.vulcansmp.libs.lamp")
        relocate("org.spongepowered.configurate", "nl.sundeep.vulcansmp.libs.configurate")
        minimize()
    }

    runServer {
        minecraftVersion("1.21.1")
        jvmArgs("-Dcom.mojang.eula.agree=true", "-Dfile.encoding=UTF-8")
    }

    compileJava {
        options.release = 21
        options.compilerArgs.add("-parameters")
    }
}


