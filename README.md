# Sample Clojure web api

[![GitHub CI](https://github.com/evg-tso/sample-donkey-api/actions/workflows/push_ci.yml/badge.svg)](https://github.com/evg-tso/sample-donkey-api/actions/workflows/push_ci.yml)
[![Coverage Status](https://coveralls.io/repos/github/evg-tso/sample-donkey-api/badge.svg?branch=master)](https://coveralls.io/github/evg-tso/sample-donkey-api?branch=master)

This is a sample Clojure web api that will:
- Display a [swagger](https://swagger.io/) page.
- Validate or reject http requests based on a schema.
- Do an async http request to resolve the client's IP address.
- Map the request to a Kafka proto message.
- Enqueue and publish that message.
- Integration tests, relying on [testcontainers](https://www.testcontainers.org).


## Requirements
- [Java](https://www.oracle.com/java/technologies/downloads/#JDK17)
  17+
- [Leiningen](https://leiningen.org/) 2.9.3+

## Installation

    $ lein protodeps generate
    $ lein uberjar

## Usage

    $ java -jar sample-donkey-api-0.1.0-standalone.jar

### Configuration

The configuration is loaded using [walmartlabs/dyn-edn](https://github.com/walmartlabs/dyn-edn).  
These parts are configurable:
```shell
export CHANNEL_SIZE_PER_CORE=250
export KAFKA_BROKERS=my-broker:9093
export IP_STACK_ACCESS_KEY=CHANGE ME
export INTERNAL_PORT=8081
export EXTERNAL_PORT=8080
```

## Used libraries

- [AppsFlyer/Donkey](https://github.com/appsflyer/donkey) as the http server.
- [clj-test-containers](https://github.com/javahippie/clj-test-containers) for integration tests.
- [Metosin/malli](https://github.com/metosin/malli) for request validation.
- [Metosin/reitit](https://github.com/metosin/reitit) for routing requests.
- [Integrant](https://github.com/weavejester/integrant) for state management.
- [walmartlabs/dyn-edn](https://github.com/walmartlabs/dyn-edn) for dynamic configuration.
- [AppsFlyer/Ketu](https://github.com/appsflyer/ketu) as the Kafka library.
- [AppsFlyer/lein-protodeps](https://github.com/AppsFlyer/lein-protodeps) to generate java classes from .proto files.
- [AppsFlyer/pronto](https://github.com/AppsFlyer/pronto) to use protobuf simply in Clojure.

## Examples

    $ export KAFKA_BROKERS=my-broker:9092
    $ lein uberjar
    $ java -jar target/uberjar/sample-donkey-api-0.1.0-SNAPSHOT-standalone.jar
    .. in another terminal session
    $ curl --location --request POST 'http://localhost:8080/api/v1.0/stocks/order/AAPL' \
      --header 'Content-Type: application/json' \
      --data-raw '{
      "amount_usd": 3.47,
      "request_id": "71dad7da-7926-40d8-9b15-b94a6d46e15a",
      "ip": "35.244.183.10",
      "direction": "buy"
      }'

## Testing

- `lein test` - To run all unit & integration tests.

## License

Copyright © 2021 Yevgeni Tsodikov

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
