(defproject axiom-of-choice "0.1.0-SNAPSHOT"
  :description "Playing around with Lindenmayer systems"
  :url "https://petardo.dk/~/projects/lindenmayer"
  :license {:name "GPL-3.0"
            :url  "https://spdx.org/licenses/GPL-3.0.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [net.mikera/imagez "0.12.0"]
                 [com.taoensso/timbre "4.10.0"]]
  :main ^:skip-aot petardo.lindenmayer.axiom
  :source-paths ["src"]
  :test    {:global-vars {*warn-on-reflection* true
                          *assert*             true}}

  :profiles
  {:dev     {:debug        true
             ;; (load user ns during dev for Stuart Sierra's Reloaded workflow)
             :repl-options {:init-ns petardo.lindenmayer.axiom}}
   :uberjar {:aot :all}})
