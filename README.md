# Vavr Hands-on Lab

This repository contains a simple project that is refactored using
[Vavr](http://vavr.io).

The [`master`](https://github.com/koenighotze/vavr-kata-demo) branch contains the unrefactored code. 
The `stepN-TOPIC` branches each introduces a Vavr concept into the
refactored `master` branch.

* Note 1: `stepN` includes the refactorings of `step1` to `stepN-1`.
* Note 2: you need to use a [Lombok](https://projectlombok.org/) plugin in your IDE 

## Topics

* Using [`Option`](http://www.vavr.io/vavr-docs/#_option) 
* Introducing Vavr [Collections](http://www.vavr.io/vavr-docs/#_collections) and interoperability
* Introducing [Vavr-Jackson](https://github.com/vavr-io/vavr-jackson), Vavr at your boundary
* Functional exception handling using [`Lift`](http://www.vavr.io/vavr-docs/#_lifting)
* Functional exception handling using [`Try`](http://www.vavr.io/vavr-docs/#_try)
* Functional exception handling using [`Either`](http://www.vavr.io/vavr-docs/#_either)
* Using Vavr [Streams](http://www.vavr.io/vavr-docs/#_stream) with exceptions 
* [Property testing](http://www.vavr.io/vavr-docs/#_property_checking) the REST interface

## Application overview

The application in question is a simple microservice, that exposes Teams via REST-CRUD interface.

Start the application by running

`$ ./gradlew  bootRun`

### Fetching teams

Then fetch the teams using for example [HTTPie](https://httpie.org/):

```bash
$ http localhost:8080/teams
HTTP/1.1 200
Content-Type: application/json;charset=UTF-8
Date: Mon, 25 Sep 2017 09:25:40 GMT
Transfer-Encoding: chunked

[
    {
        "foundedOn": [
            1895,
            5,
            5
        ],
        "id": "1",
        "logoUrl": "https://tmssl.akamaized.net//images/wappen/head/38.png?lm=1405514004",
        "name": "Fortuna Düsseldorf"
    },
    ...
```

A single team is fetched using

```bash
$ http localhost:8080/teams/1
HTTP/1.1 200
Content-Type: application/json;charset=UTF-8
Date: Mon, 25 Sep 2017 09:27:12 GMT
Transfer-Encoding: chunked

{
    "coach": "Friedhelm Funkel",
    "estimatedMarketValue": 13000000,
    "foundedOn": [
        1895,
        5,
        5
    ],
    "id": "1",
    "logoUrl": "https://tmssl.akamaized.net//images/wappen/head/38.png?lm=1405514004",
    "name": "Fortuna Düsseldorf"
}
```

Using [jq](https://stedolan.github.io/jq/) you can select for example the team names

```bash
$ http localhost:8080/teams | jq '.[].name'
"Fortuna Düsseldorf"
"1. FC Kaiserslautern"
"FC St Pauli"
"Eintracht Braunschweig"
```

or the logo URLs, which contain invalid entries:

```bash
$ http localhost:8080/teams | jq '.[].logoUrl'
"https://tmssl.akamaized.net//images/wappen/head/38.png?lm=1405514004"
"This is not a valid url"
"https://this.will.point.nowhere.de/"
"http://localhost:8080/timeout"
```

### Fetching logos

The logo is exposed as a sub-resource (Base64 encoded)

```bash
$ http localhost:8080/teams/1/logo
HTTP/1.1 200
Content-Type: application/json
Date: Mon, 25 Sep 2017 18:55:39 GMT
Transfer-Encoding: chunked



+-----------------------------------------+
| NOTE: binary data not shown in terminal |
+-----------------------------------------+
```

If you want the actual logo as a picture, just store it like this

`$ http localhost:8080/teams/1/logo > ~/tmp/out.png`

You will notice, that some teams feature invalid URLs as their logo
This will result in a timeout:

```bash
$ http localhost:8080/teams/3/logo
HTTP/1.1 400
Connection: close
Content-Length: 0
Date: Mon, 25 Sep 2017 18:57:56 GMT
``` 

This is expected and used to demo, how exceptions can be handled.

### About timeout

If you try to access the logo of team number 4, then you will run into a timeout.

```bash
$ http localhost:8080/teams/4/logo
HTTP/1.1 408
Connection: close
Content-Length: 0
Date: Mon, 25 Sep 2017 18:56:54 GMT
```

This is expected and used to demo, how time outs can be handled.

## Bucket list
* https://www.oreilly.com/ideas/handling-checked-exceptions-in-java-streams?imm_mid=0f6a21&cmp=em-prog-na-na-newsltr_20170923
* https://stackoverflow.com/questions/46225954/vavr-howto-flatmap-collection-inside-optional-object/46226814#46226814
* Refactor to java 9?

