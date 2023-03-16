Any Time
--------

![Java8](https://img.shields.io/badge/Java-8-brightgreen)
![GitHub](https://img.shields.io/github/license/mjcro/anytime)
![Snyk Vulnerabilities for GitHub Repo](https://img.shields.io/snyk/vulnerabilities/github/mjcro/anytime)
[![CircleCI](https://circleci.com/gh/mjcro/anytime/tree/main.svg?style=svg)](https://circleci.com/gh/mjcro/anytime/tree/main)

Parse time in almost any widely used format into Instant using regexps and `DateTimeFormatter`.

## Distribution

```xml
<dependency>
    <groupId>io.github.mjcro</groupId>
    <artifactId>anytime</artifactId>
    <version>1.0.1</version>
</dependency>
```

## Goal

- :tada: Provide simple way to parse commonly used date/time representations in APIs and clearing files into `Instant`
- :twisted_rightwards_arrows: Controversial formats like `d/m/y` and `m/d/y` should be solved by providing corresponding locale.
- :bulb: Provide simple way to convert misc Java date/time representations into `Instant`

## Not a goal

- :alembic: Parse anything using natural language date parser. To do this use [Natty](https://github.com/joestelmach/natty)
- :test_tube: Parse anything using retree. To do this use [dateparser](https://github.com/sisyphsu/dateparser)

## Supported formats (at least):

- Integer unix seconds
- Integer unix milliseconds
- Float unix seconds
- `yyyy-mm-dd`
- `yyyy/mm/dd`
- `yyyy.mm.dd`
- `dd-mm-yyyy` (locale dependent)
- `dd/mm/yyyy` (locale dependent)
- `dd.mm.yyyy` (locale dependent)
- `mm-dd-yyyy` (locale dependent)
- `mm/dd/yyyy` (locale dependent)
- `mm.dd.yyyy` (locale dependent)
- `m-d-yyyy` (locale dependent)
- `m.d.yyyy` (locale dependent)
- `m/d/yyyy` (locale dependent)
- `m-d-yy` (locale dependent)
- `m.d.yy` (locale dependent)
- `m/d/yy` (locale dependent)
- `d-m-yyyy` (locale dependent)
- `d.m.yyyy` (locale dependent)
- `d/m/yyyy` (locale dependent)
- `d-m-yy` (locale dependent)
- `d.m.yy` (locale dependent)
- `d/m/yy` (locale dependent)
- `yyyy-mm-ddThh:mm:ss-03:00` (ISO 8601)
- `yyyy-mm-ddThh:mm:ssZ` (ISO 8601)
- `yyyy-mm-ddThh:mm-03:00` (ISO 8601)
- `yyyy-mm-ddThh:mmZ` (ISO 8601)
- `yyyy-mm-ddThh-03:00` (ISO 8601)
- `yyyy-mm-ddThhZ` (ISO 8601)
- `yyyy-mm-dd hh:mm:ss+02:00` (RFC 3339)
- `yyyy-mm-dd hh:mm:ssZ` (RFC 3339)
- `yyyy-mm-dd hh:mm+02:00` (RFC 3339)
- `yyyy-mm-dd hh:mmZ` (RFC 3339)
- `yyyy-mm-dd hh+02:00` (RFC 3339)
- `yyyy-mm-dd hhZ` (RFC 3339)
