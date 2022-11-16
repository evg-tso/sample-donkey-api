(def proto-version "3.21.9")

(defproject sample-donkey-api "0.1.0-SNAPSHOT"
  :description "A sample Clojure http server"
  :url "https://github.com/evg-tso/sample-donkey-api"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :java-source-paths ["src/java" "src/java/generated"]
  :dependencies [; Core clojure
                 [org.clojure/clojure "1.11.1" :exclusions [org.clojure/spec.alpha]] ; org.clojure/spec.alpha is provided by com.appsflyer/donkey

                 ; Async
                 [funcool/promesa "9.1.536"]

                 ; Validation
                 [metosin/malli "0.9.2"]
                 [commons-validator/commons-validator "1.7"]

                 ; HTTP server
                 [com.appsflyer/donkey "0.5.2" :exclusions [metosin/jsonista]] ; metosin/jsonista is provided by metosin/reitit
                 [metosin/reitit "0.5.18" :exclusions [com.fasterxml.jackson.core/jackson-core]] ; com.fasterxml.jackson.core/jackson-core is provided by com.appsflyer/donkey
                 [ring/ring-core "1.9.6"]

                 ; State management
                 [integrant "0.8.0"]
                 [com.walmartlabs/dyn-edn "0.2.0"]

                 ; Logging
                 [com.brunobonacci/mulog "0.9.0"]
                 [nonseldiha/slf4j-mulog "0.2.1"]

                 ; Kafka messaging
                 [com.appsflyer/ketu "0.6.0" :exclusions [expound]] ; expound is provided by metosin/reitit

                 ; Protobuf
                 [com.google.protobuf/protobuf-java ~proto-version]
                 [com.appsflyer/pronto "2.1.0"]]
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
                                      [com.appsflyer/lein-protodeps "1.0.4"]]
                       :dependencies [; Core clojure
                                      [org.clojure/core.async "1.6.673" :exclusions [org.clojure/tools.reader]]

                                      ; Linters
                                      [mvxcvi/cljstyle "0.15.0"]
                                      [clj-kondo "2022.11.02" :exclusions [com.fasterxml.jackson.core/jackson-core]] ; com.fasterxml.jackson.core/jackson-core is provided by com.appsflyer/donkey

                                      [criterium "0.4.6"]

                                      ; Code coverage
                                      [cloverage "1.2.4" :exclusions [org.clojure/tools.reader]]

                                      ; test containers
                                      [clj-test-containers "0.7.3" :exclusions [org.testcontainers/testcontainers]]
                                      [org.testcontainers/kafka "1.17.6" :exclusions [org.slf4j/slf4j-api]]]
                       :eftest       {:multithread?    false
                                      :capture-output? false
                                      :report          eftest.report.junit/report
                                      :report-to-file  "target/junit.xml"}
                       :aliases      {"lint" ["do" "run" "-m" "clj-kondo.main" "--lint" "src" "test" "--cache" "false" "--parallel" "," "run" "-m" "cljstyle.main" "check"]}}})
