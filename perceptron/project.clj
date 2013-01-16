(defproject perceptron "0.1.0-SNAPSHOT"
  :description "Perceptron implementation with some samples."
  :url "https://github.com/ghubber/statistical-methods"
  :license {:name "AGPL"
            :url "https://www.gnu.org/licenses/agpl-3.0.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/tools.trace "0.7.5"]
                 [org.clojure/tools.cli "0.2.2"]
                 [org.clojure/data.json "0.2.1"]]
  :repl-options {:port 4555}
  :main perceptron.core
  )
