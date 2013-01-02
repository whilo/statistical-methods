(ns perceptron.core)

(use 'clojure.java.io)
(use 'clojure.string)

; math helpers

(defn dot
  [a b]
  (reduce + (map * a b))
  )

(defn scalarp
  [s v]
  (map #(* s %) v)
  )

(defn vadd
  [v1 v2]
  (map + v1 v2)
  )

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
  (pmap #(distribution (re-seq #"[\wäöüß]+" %)) samples))

(def freqs (process-training-data texts))

(defn extract
  [sample]
  {
   :Verband (get sample "Verband" 0)
   :Struktur (get sample "Struktur" 0)
   :Lehre (get sample "Lehre" 0)
   :Körper (get sample "Körper" 0)
   :Teilgebiet (get sample "Teilgebiet" 0)
   :Person_en (+ (get sample "Person" 0) (get sample "Personen" 0))
   :Krankheit (get sample "Krankheit" 0)
   :Medizin (get sample "Medizin" 0)
   :Sinne (get sample "Sinne" 0)
   })

; hardcode ranking
(def samples (interleave (map extract freqs) [1 -1 -1 1 1 1 1 -1 1 -1]))

; write word count to file

(defn format [a]
  (join \newline (map #(join \tab %) a)))


;(spit "/home/void/alphabetically" (format freqs) )

(defn perceptron [train w_init rho_init alpha]
  (println "new iteration with w:" (pr-str w_init) "rho:" rho_init)
  (defn iter
    "Iteration over all samples. Returns map with weights rho errors."
    [s w rho errors]
    (println "w: " (pr-str w) "rho: " rho "errors: " errors)
    (let [x (vals (first s))  y (first (rest s)) r (rest (rest s))]
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
(defn -main []
  (println (pr-str (perceptron samples [1 1 1 1 1 1 1 1 1] 0 0.5))))