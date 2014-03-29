(ns clj-figlet.core
  (:import (java.io File))
  (:require [clojure.string :as str]))

; ascii 32 through 126 inclusive
(def num-basic-chars (- 127 32))

; ascii 196, 214, 220, 228, 246, 252 and 223
(def num-ext-chars 7)

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

(defn- parse-basic-chars [header char-lines]
  )

(defn- parse-ext-chars [header char-lines]
  )

(defn- parse-other-chars [header char-lines]
  )

(defn load-flf [file]
  (let [flf-file (slurp file)]
    (if-not (= "flf2a" (subs flf-file 0 5))
      (throw (new Exception (str "Not a valid flf font file: " file))))
    (let [lines                (str/split (slurp file) #"\n")
          header               (parse-flf-header (first lines))
          char-height          (:height header)
          char-lines           (drop (inc (:num-comments header)) lines)
          basic-chars          (->> char-lines
                                    (take (* char-height num-basic-chars))
                                    (partition char-height))
          num-basic-char-lines (* (count basic-chars) char-height)
          ext-chars            (->> char-lines
                                    (drop num-basic-char-lines)
                                    (take (* char-height num-ext-chars))
                                    (partition char-height))
          num-ext-char-lines   (* (count ext-chars) char-height)
          other-chars          (->> char-lines
                                    (drop (+ num-basic-char-lines num-ext-char-lines))
                                    (partition (inc char-height)))]
      {:header      header
       :basic-chars (parse-basic-chars header basic-chars)
       :ext-chars   (parse-ext-chars header ext-chars)
       :other-chars (parse-other-chars header other-chars)})))

