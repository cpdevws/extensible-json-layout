# Extensible Log4j2 Json Layout
[![Build Status](https://travis-ci.org/crosslibs/extensible-json-layout.svg?branch=master)](https://travis-ci.org/crosslibs/extensible-json-layout)

Extensible Log4j2 JSON Layout (`extensible-json-layout`) provides the ability to add custom attributes to the JSON logs.

## How to use
In your `log4j2` configuration file (`log4j2.xml`, `log4j2.yml`, `log4j2.yaml` or `log4j2.json`), please use `ExtensibleJsonLayout` instead of `JsonLayout`. Please note that all configuration properties of `JsonLayout` are supported by `ExtensibleJsonLayout`.

In addition, you may inject custom JSON properties into your code by passing the configuration property `adapter` in `<ExtensibleJsonLayout />`

##### 1. Example `log4j2.xml` snippet with configuration set to defaults:

```
    <Appenders>
        <Console name="Console-Appender" target="SYSTEM_OUT">
            <ExtensibleJsonLayout />
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n" />
        </Console>
    </Appenders>
```


##### 2. Example `log4j2.xml` snippet with custom log adapter:

```
    <Appenders>
        <Console name="Console-Appender" target="SYSTEM_OUT">
            <ExtensibleJsonLayout adapter="org.crosslibs.extensible.json.layout.Adapter" />
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n" />
        </Console>
    </Appenders>
```


### Build Configuration
#### Gradle
If you are using `gradle`, please include the following snippet into your `build.gradle` file

```
buildScript {
    ext {
        log4j2Version = '2.9.0'
    }
}

dependencies {
  compile group: 'org.crosslibs', artifact: 'extensible-json-layout', version: log4jVersion
}
```

#### Maven
If you are using `maven`, please include the following snippet into your `pom.xml` file

```
<properties>
    <log4j2.version>2.9.0</log4j2.version>
</properties>

<dependency>
    <groupId>org.crosslibs</groupId>
    <arifactId>extensible-json-layout</artifactId>
    <version>${log4j2.version}</version>
</dependency>
```

### Contact
In case of any questions or feedback, please reach out to [Chaitanya Prakash N](cpdevws@gmail.com) or log an [issue](/issues/new).


Pull requests welcome.

