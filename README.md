# Trump

## Table of Contents

1. [Introduction](#introduction)
2. [Configuration](#configuration)
3. [Development and Running](#development-and-running)
4. [Testing](#testing)
5. [API](#api)
6. [Notes](#notes)

## Introduction

Trump is an API written in Scala with Akka Http and backed by Redis cache. It is for "shouting out" recent tweets from a Twitter account.

## Configuration

Trump can be configured via [application.conf](src/main/resources/application.conf) and [test.conf](src/test/resources/test.conf) files for running and testing respectively. You can also override config values with following environment variables.

| Variable Name            | Data Type | Description                                       | Required                    |
| ------------------------ | --------- | ------------------------------------------------- | --------------------------- |
| PORT                     | Int       | Running port of Trump                             | No, defaults to `9000`      |
| REDIS_ENABLED            | Boolean   | Switch to enable Redis                            | No, defaults to `true`      |
| REDIS_HOST               | String    | Host of Redis                                     | No, defaults to `localhost` |
| REDIS_PORT               | Int       | Port of Redis                                     | No, defaults to `6379`      |
| REDIS_TTL                | Int       | Default time-to-live in seconds for Redis entries | No, defaults to `300`       |
| TWITTER_ACCESS_TOKEN_TTL | Int       | Time-to-live for Twitter access key in seconds    | No, defaults to `3600`      |
| TWITTER_API_KEY          | String    | API key for Twitter APIs                          | Yes, unless mock is enabled |
| TWITTER_API_SECRET       | String    | API secret for Twitter APIs                       | Yes, unless mock is enabled |
| TWITTER_MOCK             | Boolean   | Switch to mock Twitter APIs                       | No, defaults to `false`     |

API key and secret values can also be provided by creating a [secret.conf](src/main/resources/secret.conf) which is gitignored by default.

## Development and Running

Trump is built with SBT. So, standard SBT tasks like `clean`, `compile` and `run` can be used.

In order to get the Redis set up, you may simply use `docker-compose` by doing

```docker-compose up -d```

This will fire up a Redis cache for running the application.

## Testing

To run all the tests, use `test` task of SBT.

To run specific test(s), use `testOnly fullyQualifiedTestClassName1 fullyQualifiedTestClassName2 ...`

## API

Here is an overview of the APIs:

| Method | URL                             | Link                                 |
| ------ | ------------------------------- | ------------------------------------ |
| GET    | /shout/`username`?limit=`limit` | [Jump](#get-shoutusernamelimitlimit) |

All handled errors return an error Json in following format:

```json
{
  "error": "some-error-type",
  "details": "A human readable description of the error"
}
```

with a corresponding HTTP status code depending on the error.

All successful responses will have `200 OK` status unless explicitly mentioned.

---

### GET /shout/`username`?limit=`limit`

Returns a list of shouted tweets of user `username` as a Json array limited to `limit` items

#### Example Successful Response

```json
[
  "COOKIE 😻 #KEDI #CAT HTTPS://T.CO/BOZULKJEK6 HTTPS://T.CO/OM817MBLED!",
  "@SELCUKERMAYA EN AZINDAN ZSH KULLANIYOR. ÇOK DA ŞEY YAPMAMAK LAZIM. 😄!",
  "@THUSEYINSAHIN DÜN YAŞADIM BEN DE. HALA ÇOK IYI DEĞILIM. GEÇMIŞ OLSUN!",
  "I'M AT ETIKET KEBAP IN OSMANGAZI, BURSA HTTPS://T.CO/02067VPNK1!",
  "THE BIG BANG THEORY FINALI ÇOK GÜZEL OLMUŞ. DARISI GAME OF THRONES'UN BAŞINA!"
]
```

#### Possible Errors

| What               | When                                   |
| ------------------ | -------------------------------------- |
| Invalid Limit      | Limit is not between configured values |
| Twitter Connection | An error occurs with Twitter APIs      |

## Notes

* It only works with public accounts. If an account is locked, the result will be empty.
* Capitalizing does not handle all types of tweets well, especially tweets with links and emojis.
