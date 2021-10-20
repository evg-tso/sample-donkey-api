(def proto-version "3.18.1")

(defproject sample-donkey-api "0.1.0-SNAPSHOT"
  :description "A sample Clojure http server"
  :url "https://github.com/evg-tso/sample-donkey-api"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :java-source-paths ["src/java" "src/java/generated"]
  :dependencies [; Core clojure
                 [org.clojure/clojure "1.10.3"]
                 [org.clojure/core.async "1.3.622"]

                 ; Validation
                 [metosin/malli "0.6.2"]
                 [commons-validator/commons-validator "1.7"]

                 ; Authorization
                 [commons-codec/commons-codec "1.15"]

                 ; HTTP server
                 [com.appsflyer/donkey "0.5.1"]
                 [metosin/reitit "0.5.15"]
                 [ring/ring-core "1.9.4"]

                 ; State management
                 [integrant "0.8.0"]
                 [com.walmartlabs/dyn-edn "0.2.0"]

                 ; Logging
                 [com.brunobonacci/mulog "0.8.1"]
                 [nonseldiha/slf4j-mulog "0.2.1"]

                 ; Kafka messaging
                 [com.appsflyer/ketu "0.6.0"]

                 ; Protobuf
                 [com.google.protobuf/protobuf-java ~proto-version]
                 [com.appsflyer/pronto "2.0.9"]

                 ; Other
                 [danlentz/clj-uuid "0.1.9"]]
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
             :dev     {:plugins      [[lein-eftest "0.5.9"]
                                      [lein-cloverage "1.2.2" :exclusions [org.clojure/clojure]]

                                      ; Protobuf
                                      [com.appsflyer/lein-protodeps "1.0.2"]]
                       :dependencies [[clj-kondo "2021.10.19"]
                                      [criterium "0.4.6"]
                                      [org.testcontainers/kafka "1.16.1"]
                                      [clj-test-containers "0.5.0"]
                                      [metosin/jsonista "0.3.4"]]
                       :eftest       {:multithread?    false
                                      :capture-output? false
                                      :report          eftest.report.junit/report
                                      :report-to-file  "target/junit.xml"}
                       :aliases      {"lint" ["run" "-m" "clj-kondo.main" "--lint" "src" "test" "--cache" "false" "--parallel"]}}})
