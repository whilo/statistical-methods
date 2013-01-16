(ns perceptron.io
  (:use [clojure.java.io]
        [clojure.data.json :exclude [pprint]]))

; file io
(defn ne-lines
  "Non-empty lines of filename fn."
  [fn]
  (filter not-empty
    (with-open [r (reader fn)]
      (doall (line-seq r) ))))


;(def json-article (read-str (slurp "https://de.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&&titles=Impfung")))

;(keys (get (((json-article "query") "pages") "31940") "revisions"))
