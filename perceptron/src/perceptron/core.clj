(ns perceptron.core
  (:gen-class)
  (:use [clojure.java.io]
     [clojure.string :only [join]]
     [clojure.tools.trace :only [dotrace]]
     [clojure.data.json :exclude [pprint]]))

; macro magic
(defmacro dbg
  [x]
  `(let [x# ~x] (println "dbg:" '~x "=" x#) x#))


; math helpers
(defn ^:dynamic dot
  [x y]
  (reduce + (map #(* (get x % 0) (get y % 0)) (keys y))))


(defn ^:dynamic scale [a v]
  (zipmap (keys v) (map * (vals v) (repeat a))))


(defn ^:dynamic vadd
  [x y]
  (merge-with + x y))


; file io
(defn ne-lines
  "Non-empty lines of filename fn."
  [fn]
  (filter not-empty
    (with-open [r (reader fn)]
      (doall (line-seq r) ))))


; feature parsing
(defn distribution [items] (persistent!
   (reduce #(assoc! %1 %2 (inc (%1 %2 0))) (transient {}) items)))


(defn word-count  [samples]
  (map #(distribution (re-seq #"[\wäöüß]+" %)) samples))


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

  (let [ [_ label features] (re-matches #"^(-?\d+)(.*)$" line) ]
    {:y (Float/parseFloat label), :x (svmlight-features features)}))


(defn feat-extract
  [sample]
  {
   :Verband (get sample "Verband"  0)
   :Struktur (get sample "Struktur" 0)
   :Lehre (get sample "Lehre"  0)
   :Körper (get sample "Körper"  0)
   :Teilgebiet (get sample "Teilgebiet" 0)
   :Person_en (+ (get sample "Person" 0) (get sample "Personen" 0))
   :Krankheit (get sample "Krankheit" 0)
   :Medizin (get sample "Medizin" 0)
   :Sinne (get sample "Sinne" 0)
   })


; machine learning
(defn ^:dynamic hinge-loss?
  "Returns true if the sample x misses the is-in-class
  predicate with weights w and threshold rho."
  [w rho is-in-class x]
    (let [y (if is-in-class 1 -1)]
      (<= (* y (+ (dot x w) rho)) 0)))


(defmacro defun
  [sym args & body]
  (let [
        [pargs [_ & kargs]] (split-with (fn [x] (not (= x ':key))) args)
        gkeys (gensym "gkeys__")
        letk (fn [k]
               (let [[nm val] (if (vector? k) k [k])
                     kname (keyword (name nm))]
                 `(~nm (or (~gkeys ~kname) ~val))))]
    `(defn ~sym [~@pargs & k#]
       (let [~gkeys (apply hash-map k#)
             ~@(apply concat (map letk kargs))]
         ~@body))))

(defun foo [:key [a 0] b c [d 5]] [a b c d])

(defun foo-add
  [x y] (+ x y))

(foo 1 2 :c 3 :d 4)


(defn perceptron
  "Trains the perceptron on the train sample set
  trn-smpls with boolean classifier clfr,
  penalty function pen with weights w_init, threshold
  rho_init, and learning rate alpha. Returns state
  as a map."
  [trn-smpls clfr pen w_init rho_init alpha]
  (defn ^:dynamic correct
    "Returns corrected intermediate result ir to
    classify s correctly."
    [ir s]
    (let [w (ir :w)
          rho (ir :rho)
          errs (ir :errs)
          x (s :x)
          in-class (clfr (s :y))
          y (if in-class 1 -1)]
      (if (pen w rho in-class x)
        (assoc! ir
                :w (vadd w (scale (* alpha y) x))
                :rho (+ rho (* alpha y))
                :errs (inc errs)
                :count (ir :count) )
        ir)))

  (loop [rslt (transient {:w w_init :rho rho_init :errs 0 :count 0})]
    ;; actual computation as reduction over sample set
    (let [n-rslt (reduce correct rslt trn-smpls)
          c (n-rslt :count)
          errs (n-rslt :errs)]
      (if (or (= errs 0) (= c 1000)) (persistent! n-rslt)
          (recur (assoc! rslt
                         :w (n-rslt :w)
                         :rho (n-rslt :rho)
                         :errs 0
                         :count (inc c)))))))


(defn missclassified
  "Returns all missclassified samples by model mdl
   training set trn, predicate classifier clfr and
   a predicate function pn for the penalty."
  [mdl trn clfr pn]
  (let [p (partial pn (mdl :w) (mdl :rho))]
    ;; false classified if penalty and in class (and vice versa)
    (filter #(if (p (clfr (%1 :y)) (%1 :x)) %1) trn)))


(defn classify
  "Classify samples smpl with model mdl.
   Returns a list of maps containing the
   classification :c and the original entry
   of :y."
  [mdl smpls]
  (let [w (mdl :w)
        rho (mdl :rho)]
    (map (fn [a] {:c (+ (dot w (:x a)) rho) :y (:y a)}) smpls)))


; data
(def base-dir "/home/void/Dokumente/Studium/Computerlinguistik/Statistische Methoden/Übungen/iris_dataset")

(def train-data {
           0 (map parse-svmlight (ne-lines (str base-dir "/iris.setosa-v-rest")))
           1 (map parse-svmlight (ne-lines (str base-dir "/iris.versicolor-v-rest")))
           2 (map parse-svmlight (ne-lines (str base-dir "/iris.virginica-v-rest")))
           })

(def test-data (map parse-svmlight (ne-lines (str base-dir "/test.data"))))


(def texts (ne-lines "/home/void/corpus.txt"))

(def freqs (word-count texts))

(def samples
  (into ()
        (map (fn [x y] {:x (feat-extract x) :y y})
             freqs
             [1 -1 -1 1 1 1 1 -1 1 -1])))

(def w_start (zipmap (keys (feat-extract {})) (repeat 1)))

(def w_new {1 1, 2 1, 3 1, 4 1})

#_(persistent! (reduce
              #(let [rslt (perceptron (train-data %2) (partial == %2) hinge-loss? w_new 0 0.5)]
                 (if (= (:errs rslt) 0) (assoc! %1 %2 rslt)))
              (transient {})
              (keys train-data)))

#_(/ (count (missclassified (perceptron (train-data 2) (partial == 1) hinge-loss? w_new 0 0.5)
                       test-data
                       (partial == 1)
                       hinge-loss?))
   (count test-data))

;(let [clfr (partial == 1.0)]
; (dotrace [penalty? dot]
;   (trace-false (new-train-model perceptron-new clfr) clfr)))

; as java entry point (without tracing)
(defn -main [& args]
  (println (perceptron test-data (partial == 0.0) hinge-loss? w_new 0 0.5)))

;(def json-article (read-str (slurp "https://de.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&&titles=Impfung")))

;(keys (get (((json-article "query") "pages") "31940") "revisions"))
