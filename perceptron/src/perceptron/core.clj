(ns perceptron.core
  (:gen-class)
  (:use [clojure.java.io]
        [clojure.tools.trace :only [dotrace]]
        [clojure.tools.cli :only [cli]]
        [perceptron.sparse]
        [perceptron.iris]
        [perceptron.io])
  (:import [java.io BufferedReader]))

;; feature parsing
(defn distribution
  "Returns a map with the distribution of collection items."
  [items]
  (persistent!
   (reduce #(assoc! %1 %2 (inc (%1 %2 0))) (transient {}) items)))

(defn word-count
  "Splits string samples smpls into a list of words."
  ([smpls] (word-count smpls #"[\wäöüß]+"))
  ([smpls rgx] (map #(distribution (re-seq rgx %)) smpls)))

;; machine learning
(defn ^:dynamic hinge-loss?
  "Returns true if the sample x misses the is-in-class
  predicate with weights w and threshold rho."
  [w rho is-in-class x]
    (let [y (if is-in-class 1 -1)]
      (<= (* y (+ (dot x w) rho)) 0)))


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
          (recur (assoc! n-rslt
                         :errs 0
                         :count (inc c)))))))


(defn dual-perceptron
  "Taking a kernel and samples and giving back
   a model."
  [krnl smpls]
    (defn ^:dynamic kernel-correct
      [mdl s]
      (let [w (:w mdl)
            sum (* (:y s)
                 (reduce +
                        (map #(* (w %) ; alpha
                                 (:y %)
                                 (krnl (:x %) (:x s)))
                             (keys w))))
            err (<= sum 0)
            alpha (or (w s) 0)
            new-alpha (if err (inc alpha) alpha)
            new-w (assoc (:w mdl) s new-alpha)]
        (assoc mdl :errs (or (:errs mdl) err)
                   :w new-w)))

    (loop [mdl {:w {} :errs true :count 0}]
      (if (and (:errs mdl) (< (:count mdl) 100))
        (recur
         (reduce kernel-correct
                 (assoc mdl :errs false :count (inc (mdl :count)))
                 smpls))
        mdl)))

(def dual-rslt
  (dual-perceptron dot (iris-train 2)))

(filter #(> ((dual-rslt :w) %) 0) (keys (dual-rslt :w)))


(defn not-missclassified
  "Returns all missclassified samples by model mdl
   training set trn, predicate classifier clfr and
   a predicate function pn for the penalty."
  [mdl trn clfr pn]
  (let [p (partial pn (mdl :w) (mdl :rho))]
    (filter #(if-not (p (clfr (%1 :y)) (%1 :x)) %1) trn)))


(defn classify
  "Classify samples smpl with model mdl.
   Returns a list of maps containing the
   classification :c and the original entry
   of :y."
  [mdl smpls]
  (let [w (mdl :w)
        rho (mdl :rho)]
    (map (fn [a] {:c (+ (dot w (:x a)) rho) :y (:y a)}) smpls)))


#_(persistent! (reduce
              #(let [mdl (perceptron (iris-train %2) (partial == %2) hinge-loss? w_new 0 0.5)]
                 (if (= (:errs mdl) 0) (assoc! %1 %2 mdl)))
              (transient {})
              (keys iris-train)))


;; as java entry point (without tracing)
(defn -main [& args]
  (let [[options args banner] (cli args
                                   ["--class" "-c" "REQUIRED. Classification (y value) for positive samples." :parse-fn #(Integer. %)]
                                   ["--learn" "-l" "Learn model from std input." :flag true]
                                   ["--test" "-t" "Test model (format of the modell has to be the same as one returned from learn.) Returns precision." :parse-fn read-string]
                                   ["--help" "-h" "Print this help message." :flag true])]
    (if-let [cls (:c options)]
      (if-let [smpls (map parse-svmlight (-> *in* BufferedReader. line-seq))]
        (cond (:l options)
              (println (perceptron smpls (partial == cls) hinge-loss? {} 0 0.5))
              (:t options)
              (let [w (:t options)]
                (println (/ (count (not-missclassified w smpls (partial == cls) hinge-loss?))
                            (count smpls))))
              :else (println banner))
        (println banner))
      (println banner))))
