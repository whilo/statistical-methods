(ns perceptron.med
  (:use [perceptron.io]))


(defn feat-extract
  "Extract interesting features from the medical sample."
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


(def med-articles (ne-lines "/home/void/corpus.txt"))
  "Medical articles and others taken from Wikipedia."


(def freqs (word-count med-articles))


(def samples
  "Extract features and assign hardwired rating."
  (into ()
        (map (fn [x y] {:x (feat-extract x) :y y})
             freqs
             [1 -1 -1 1 1 1 1 -1 1 -1])))
