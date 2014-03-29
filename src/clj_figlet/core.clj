(ns clj-figlet.core
  (:import (java.io File))
  (:require [clojure.string :as str]))

(defn- parse-flf-header [header-line]
  (let [parts  (str/split header-line #" ")
        params (->> parts
                    (rest)
                    (map #(Integer/parseInt %))
                    (vec))]
    {:hardblank    (-> parts first last)
     :height        (get params 0)
     :baseline      (get params 1)
     :max-length    (get params 2)
     :old-layout    (get params 3)
     :num-comments  (get params 4)
     :print-dir     (get params 5)
     :full-layout   (get params 6)
     :codetag-count (get params 7)}))

(defn load-flf [file]
  (let [flf-file (slurp file)]
    (if-not (= "flf2a" (subs flf-file 0 5))
      (throw (new Exception (str "Not a valid flf font file: " file))))
    (let [lines      (str/split (slurp file) #"\n")
          header     (parse-flf-header (first lines))
          char-lines (drop (inc (:num-comments header)) lines)]
      )))
