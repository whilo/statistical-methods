(ns perceptron.iris
  (:use [perceptron.io]
        [perceptron.sparse]))

;; sample set in classes 0, 1, 2 and testing data
(def iris-dir "/home/void/Dokumente/Studium/Computerlinguistik/Statistische Methoden/Übungen/iris_dataset")

(def iris-train {
           0 (map parse-svmlight (ne-lines (str iris-dir "/iris.setosa-v-rest")))
           1 (map parse-svmlight (ne-lines (str iris-dir "/iris.versicolor-v-rest")))
           2 (map parse-svmlight (ne-lines (str iris-dir "/iris.virginica-v-rest")))
           })

(def iris-test (map parse-svmlight (ne-lines (str iris-dir "/test.data"))))
