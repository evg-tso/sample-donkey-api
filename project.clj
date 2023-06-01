(def proto-version "3.23.2")

(defproject sample-donkey-api "0.1.0-SNAPSHOT"
  :description "A sample Clojure http server"
  :url "https://github.com/evg-tso/sample-donkey-api"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :java-source-paths ["src/java" "src/java/generated"]
  :dependencies [; Core clojure
                 [org.clojure/clojure "1.11.1" :exclusions [org.clojure/spec.alpha]] ; provided by com.appsflyer/donkey

                 ; Async
                 [funcool/promesa "11.0.671"]

                 ; Validation
                 [metosin/malli "0.11.0"] ; provided by com.appsflyer/ketu
                 [commons-validator/commons-validator "1.7"]

                 ; HTTP server
                 [com.appsflyer/donkey "0.5.2" :exclusions [metosin/jsonista  ; is provided by metosin/reitit
                                                            org.slf4j/slf4j-api]] ; provided by nonseldiha/slf4j-mulog
                 [metosin/reitit "0.6.0" :exclusions [com.fasterxml.jackson.core/jackson-core]] ; provided by com.appsflyer/donkey
                 [ring/ring-core "1.10.0"]

                 ; State management
                 [integrant "0.8.1"]
                 [com.walmartlabs/dyn-edn "0.2.0"]

                 ; Logging
                 [com.brunobonacci/mulog "0.9.0"]
                 [nonseldiha/slf4j-mulog "0.2.1"]

                 ; Kafka messaging
                 [com.appsflyer/ketu "1.0.0" :exclusions [expound ; provided by metosin/reitit
                                                          org.clojure/tools.reader ; provided by metosin/malli
                                                          org.slf4j/slf4j-api]] ; provided by nonseldiha/slf4j-mulog

                 ; Protobuf
                 [com.google.protobuf/protobuf-java ~proto-version]
                 [com.appsflyer/pronto "2.1.1"]]
  :pedantic? :abort
  :main ^:skip-aot sample-donkey-api.core
  :target-path "target/%s"
  :lein-protodeps {:output-path   "src/java/generated"
                   :proto-version ~proto-version
                   :compile-grpc? false
                   :repos         {:local-proto {:repo-type    :filesystem
                                                 :config       {:path ""}
                                                 :proto-paths  ["schemas"]
                                                 :dependencies [[""]]}}}
  :profiles {:uberjar {:aot      :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}
             :dev     {:plugins      [[lein-eftest "0.6.0"]
                                      [lein-cloverage "1.2.4" :exclusions [org.clojure/clojure]]

                                      ; Protobuf
                                      [com.appsflyer/lein-protodeps "1.0.5"]]
                       :dependencies [; Core clojure
                                      [org.clojure/core.async "1.6.673" :exclusions [org.clojure/tools.reader]]

                                      [criterium "0.4.6"]

                                      ; Code coverage
                                      [cloverage "1.2.4" :exclusions [org.clojure/tools.reader]]

                                      ; test containers
                                      [clj-test-containers "0.7.4" :exclusions [org.testcontainers/testcontainers]]
                                      [org.testcontainers/kafka "1.18.3" :exclusions [org.slf4j/slf4j-api]]]
                       :eftest       {:multithread?    false
                                      :capture-output? false
                                      :report          eftest.report.junit/report
                                      :report-to-file  "target/junit.xml"}}})
