(ns perceptron.macro)


; macro magic
(defmacro dbg
  "Prints dbg: function=value on each call to fn x."
  [x]
  `(let [x# ~x] (println "dbg:" '~x "=" x#) x#))

(dbg (+ 3 (+ 1 2)))


(defmacro defun
  "Define a function macro with keyword parameters :name value.
   taken from https://cynojure.posterous.com/keyword-args-0"
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
