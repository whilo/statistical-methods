(ns perceptron.core
  (:gen-class))

(use 'clojure.java.io)
(use 'clojure.string)

; math helpers

(defn dot
  [a b]
  (reduce + (map * a b)))

(defn scalarp
  [s v]
  (map #(* s %) v))

(defn vadd
  [v1 v2]
  (map + v1 v2))

; word count
(defn distribution [items]
  (persistent!
   (reduce #(assoc! %1 %2 (inc (%1 %2 0))) (transient {}) items)))

; file io
(def texts
  (filter #(not (empty? %))
    (with-open [r (reader "/home/void/corpus.txt")]
      (doall (line-seq r) ))))

(defn process-training-data [samples]
  (map #(distribution (re-seq #"[\wäöüß]+" %)) samples))

(def freqs (process-training-data texts))

(defn extract
  [sample]
  [
   (get sample "Verband"  0)
   (get sample "Struktur" 0)
   (get sample "Lehre"  0)
   (get sample "Körper"  0)
   (get sample "Teilgebiet" 0)
   (+ (get sample "Person" 0) (get sample "Personen" 0))
   (get sample "Krankheit" 0)
   (get sample "Medizin" 0)
   (get sample "Sinne" 0)
   ])

; hardcode ranking
(def samples (interleave (map extract freqs) [1 -1 -1 1 1 1 1 -1 1 -1]))

; write word count to file
(defn tab-format [a]
  (join \newline (map #(join \tab %) a)))

;(spit "/home/void/alphabetically" (tab-format freqs) )

; perceptron algorithm
(defn perceptron [train w_init rho_init alpha]
  (println "new iteration with w: " (pr-str w_init) " rho: " rho_init)
  (defn iter
    [s w rho errors]
    (println "w: " (pr-str w) " rho: " rho " errors: " errors)
    (let [x (first s) y (first (rest s)) r (rest (rest s))]
;      (println (pr-str x))
      (if (not (empty? s))
        (let [err (<= (* y (+ (dot x w) rho)) 0)]
              (iter r
                    (if (true? err) (vadd w (scalarp (* alpha y) x)) w)
                    (if (true? err) (+ rho (* alpha y)) rho)
                    (if (true? err) (inc errors) errors)))
        {:w w :rho rho :errors errors})))
  ; actual recursion repeating each over whole trianing set
  (let [res (iter train w_init rho_init 0)]
    (if (> (res :errors) 0)
      (perceptron train (res :w) (res :rho) alpha)
      res)))

; calculate
;(pr-str (perceptron samples [1 1 1 1 1 1 1 1 1] 0 0.5))
(defn -main [& args]
  (println (pr-str (perceptron samples [1 1 1 1 1 1 1 1 1] 0 0.5))))
