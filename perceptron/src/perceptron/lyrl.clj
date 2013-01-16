(ns perceptron.lyrl
  (:use [perceptron.io]))

(def lyrl-dir "/home/void/Dokumente/Studium/Computerlinguistik/Statistische Methoden/Übungen/lyrl2004")

(def lyrl-train (map parse-svmlight (ne-lines (str lyrl-dir "/lyrl2004_vectors_train.dat"))))

;(def lyrl-test {
;                0 (map parse-svmlight (ne-lines (str lyrl-dir "/lyrl2004_vectors_test_pt0.dat")))
;                }  )
