(ns clj-figlet.core
  (:import (java.io File))
  (:require [clojure.string :as str]))

(defn- parse-flf-header [header-line]
  (let [parts (str/split header-line #" ")]
    {:hardblank    (-> parts first last)
     :height       (Integer/parseInt (nth parts 1))
     :num-comments (Integer/parseInt (nth parts 5))}))

(defn load-flf [file]
  (let [flf-file (slurp file)]
    (if-not (= "flf2a" (subs flf-file 0 5))
      (throw (new Exception (str "Not a valid flf font file: " file))))
    (let [lines      (str/split (slurp file) #"\n")
          header     (parse-flf-header (first lines))
          char-lines (drop (inc (:num-comments header)) lines)]
      )))
