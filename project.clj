(defproject sample-donkey-api "0.1.0-SNAPSHOT"
  :description "A sample Clojure http server"
  :url "https://github.com/evg-tso/sample-donkey-api"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [; Core clojure
                 [org.clojure/clojure "1.10.3"]
                 [org.clojure/core.async "1.3.618"]

                 ; Validation
                 [metosin/malli "0.5.1"]
                 [commons-validator/commons-validator "1.7"]

                 ; Authorization
                 [commons-codec/commons-codec "1.15"]

                 ; HTTP server
                 [com.appsflyer/donkey "0.5.1"]
                 [metosin/reitit "0.5.13"]
                 [ring/ring-core "1.9.3"]

                 ; State management
                 [integrant "0.8.0"]

                 ; Logging
                 [com.brunobonacci/mulog "0.7.1"]

                 ; Other
                 [danlentz/clj-uuid "0.1.9"]]
  :main ^:skip-aot sample-donkey-api.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot      :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}
             :dev     {:dependencies   [[clj-kondo "2021.04.23"]
                                        [org.testcontainers/kafka "1.15.3"]
                                        [metosin/jsonista "0.3.3"]]
                       :test-selectors {:default     (complement :integration)
                                        :integration :integration
                                        :all         (constantly true)}}})
