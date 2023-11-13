[version]: 0.1.0

<br/>
<p align="center">
    <img src="https://github.com/vendelieu/telegram-bot/assets/3987067/a96e2a39-60f2-4d7a-8270-a0c60d4fe6c3" height="90" alt="jooq plugin extension logo" />
</p>

[![Gradle Plugin](https://img.shields.io/gradle-plugin-portal/v/eu.vendeli.jooq.extension?label=Gradle&logo=gradle)](https://plugins.gradle.org/plugin/eu.vendeli.jooq.extension)

## Introduction

This repository contains a Gradle plugin that integrates a custom jOOQ generator for usage in Spring environment.

The custom generator extends the functionality of jOOQ generated DAOs (Data Access Objects) with helpful functions.

## Usage

### Applying the Plugin

To apply the plugin to your project, add the following to your `build.gradle.kts` file:

```kotlin
plugins {
    id("eu.vendeli.jooq.extension") version "0.1.0"
}
```

## Examples

Below are basic examples of extended dao functions:

```kotlin
fun getUserDocument(id: Long, userId: Long) = getOne {
    DOCUMENT.ID.eq(id).and(DOCUMENT.USER_ID.eq(userId))
}

fun getAllUserDocuments(userId: Long) = getAll {
    DOCUMENT.USER_ID.eq(userId)
}

fun create(uuid: String): User = create { this.uuid = uuid }

fun get(uuid: String): User = getOne { USER.UUID.eq(uuid) } ?: create(uuid)

fun update(uuid: String, block: User.() -> Unit): User? = update(USER.UUID.eq(uuid), block)

fun countUsers() = count {
    USER.CREATED_AT.lt(Instant.now())
}
```

### Configuring the Plugin

The plugin can be configured using the `jooq` extension.

You need to set as provided custom generator and some
generation flags to make it work well.

Here is an example configuration:

```kotlin
generate.apply {
    name = "eu.vendeli.jooq.generator.ExtendedJavaJooqGenerator"
    isDeprecated = false
    isRecords = true
    isPojos = true
    isImmutablePojos = false
    isFluentSetters = true
    isInterfaces = true
    isDaos = true
    isSpringAnnotations = true
}
```

After this, the generated DAO will be expanded with an interface that covers most use cases,
you can also inherit from generated DAO and be able to supplement it using convenient functions.
Note also that the interface itself has built-in `dslContext`.

### Running the Plugin

To generate the jOOQ classes, you can use usual generate command:

```shell
./gradlew generateJooq
```

## Custom Generator

The custom generator extends the functionality of jOOQ generated DAOs with custom functions. For example, every table
and view in your database will generate a `org.jooq.DAO` implementation that includes custom fetch
methods with convenient Kotlin DSL, you can see interface that will be
extended [there](https://github.com/vendelieu/jooq-extension/blob/master/src/main/resources/DAOExtendedImpl.kt).

## License

This project is licensed under the [Apache License, Version 2.0](LICENSE).

## Contact

If you have any questions about this project, please open an issue on GitHub.

## Acknowledgements

This project was inspired by
the  [jOOQ documentation](https://www.jooq.org/doc/latest/manual/code-generation/codegen-configuration/).
