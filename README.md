# Sample Clojure web api

[![GitHub CI](https://github.com/evg-tso/sample-donkey-api/actions/workflows/push_ci.yml/badge.svg)](https://github.com/evg-tso/sample-donkey-api/actions/workflows/push_ci.yml)

This is a sample Clojure web api that will:
- Display a [swagger](https://swagger.io/) page.
- Validate or reject http requests based on a schema.
- Do an async http request to resolve the client's IP address.
- Map the request to a Kafka message.
- Enqueue and publish that message.
- Integration tests, relying on [testcontainers](https://www.testcontainers.org).


## Requirements
- [Java](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)
  11+
- [Leiningen](https://leiningen.org/) 2.9.3+

## Installation

`lein deps`

## Usage

FIXME: explanation

    $ java -jar sample-donkey-api-0.1.0-standalone.jar [args]

## Used libraries
- [Donkey](https://github.com/appsflyer/donkey) as the http server.
- [clj-test-containers](https://github.com/javahippie/clj-test-containers) for integration tests.
- [malli](https://github.com/metosin/malli) for request validation.
- [reitit](https://github.com/metosin/reitit) for routing requests.
- [Integrant](https://github.com/weavejester/integrant) for state management.
- [Ketu](https://github.com/appsflyer/ketu) as the Kafka library.

## Examples

...

## Testing

- `lein test` - To run all unit tests.
- `lein test :integration` - To run all integration tests.
- `lein test :all` - To run all types of tests.

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
