(ns perceptron.sparse)

;; adjusted from http://mark.reid.name/sap/online-learning-in-clojure.html

;; math helpers
(defn ^:dynamic dot
  [x y]
  (reduce + (map #(* (get x % 0) (get y % 0)) (keys y))))


(defn ^:dynamic scale [a v]
  (zipmap (keys v) (map * (vals v) (repeat a))))


(defn ^:dynamic vadd
  [x y]
  (merge-with + x y))


(defn parse-svmlight
  "Returns a map {:y label, :x sparse-feature-vector} parsed from given line"
  [line]
  (defn svmlight-feature
    [string]
    (let [ [_ key val] (re-matches #"(\d+):(.*)" string)]
      [(Integer/parseInt key) (Float/parseFloat val)]))

  (defn svmlight-features
    [string]
    (into {} (map svmlight-feature (re-seq #"[^\s]+" string))))

  (if-let [ [_ label features] (re-matches #"^(-?\d+)(.*)$" line) ]
    {:y (Float/parseFloat label), :x (svmlight-features features)}))
