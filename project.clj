(defproject snowcrash "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [io.pedestal/pedestal.service       "0.5.9"]
                 [io.pedestal/pedestal.service-tools "0.5.9"]
                 [io.pedestal/pedestal.jetty         "0.5.9"]
                 [io.pedestal/pedestal.immutant      "0.5.9"]
                 [io.pedestal/pedestal.tomcat        "0.5.9"]
                 [io.pedestal/pedestal.aws           "0.5.9"]
                 [com.novemberain/monger             "3.1.0"]
                 [buddy                              "2.0.0"]
                 [cheshire                           "5.3.1"]
                 [ring-cors/ring-cors                "0.1.13"]]
  :main ^:skip-aot snowcrash.main
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
