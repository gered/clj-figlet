(ns clj-figlet.core
  (:require [clojure.string :as str]))

; ascii 32 through 126 inclusive
(def num-basic-chars (- 127 32))

; ascii 196, 214, 220, 228, 246, 252 and 223
(def num-ext-chars 7)

(defn- parse-flf-header [header-line]
  (let [parts     (str/split header-line #" ")
        hardblank (-> parts first last)
        params    (->> parts
                       (rest)
                       (map #(Integer/parseInt %))
                       (vec))]
    {:hardblank     hardblank
     :hardblank-str (str hardblank)   ; we'll be using this a fair bit, sometimes as a string, not a char
     :height        (get params 0)
     :baseline      (get params 1)
     :max-length    (get params 2)
     :old-layout    (get params 3)
     :num-comments  (get params 4)
     :print-dir     (get params 5)
     :full-layout   (get params 6)
     :codetag-count (get params 7)}))

(defn- parse-basic-chars [header chars]
  (->> chars
       (map
         (fn [ascii-code char-lines]
           [ascii-code char-lines])
         (range 32 127))
       (reduce
         (fn [charmap [ascii-code char-lines]]
           (assoc charmap (char ascii-code) char-lines))
         {})))

(defn- parse-ext-chars [header chars]
  {(char 196) (nth chars 0)
   (char 214) (nth chars 1)
   (char 220) (nth chars 2)
   (char 228) (nth chars 3)
   (char 246) (nth chars 4)
   (char 252) (nth chars 5)
   (char 223) (nth chars 6)})

(defn- parse-other-chars [header chars]
  (->> chars
       (map
         (fn [lines]
           (let [desc-line       (first lines)
                 char-lines      (rest lines)
                 [_ char-code _] (re-find #"^(.*)(?: {2})(.*)$" desc-line)
                 decoded-code    (Integer/decode char-code)]
             (if (pos? decoded-code)
               [decoded-code char-lines]))))
       (reduce
         (fn [charmap [char-code char-lines]]
           (if-not (nil? char-code)
             (assoc charmap (char char-code) char-lines)
             charmap))
         {})))

(defn load-flf
  "Loads a FIGlet font from a file, returning the data needed to render text with it as a map.
   This map can be passed to one of the render functions as the flf argument."
  [file]
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
      {:header header
       :chars  (merge
                 (parse-basic-chars header basic-chars)
                 (parse-ext-chars header ext-chars)
                 (parse-other-chars header other-chars))})))

(defn- get-initial-output-lines [flf]
  (take (get-in flf [:header :height]) (repeat "")))

(defn render-char
  "Renders a single character using the given FIGlet font. A new sequence of strings
   representing the rendered output lines is returned, or if initial-output is provided,
   that set of lines will be appended to and returned. If initial-output is specified,
   that collection should have the same size as the flf font height."
  [flf ^Character c & [initial-output]]
  (let [output-lines (or initial-output (get-initial-output-lines flf))
        char-lines   (get-in flf [:chars c])
        hardblank    (get-in flf [:header :hardblank-str])]
    (if char-lines
      (map
        (fn [^String char-line ^String output-line]
          (.concat output-line
                   (-> char-line
                       (.replace "@" "")    ; endline
                       (.replace "#" "")    ; also used as an endline
                       (.replace hardblank " "))))
        char-lines
        output-lines)
      output-lines)))

(defn render-line
  "Renders a single line of text using the given FIGlet font. A new sequence of strings
   representing the rendered output lines is returned, or if initial-output is provided,
   that set of lines will be appended to and returned. If initial-output is specified,
   that collection should have the same size as the flf font height. Any characters
   not present in the flf font are ignored, except for tab characters which are rendered
   as 8 space characters."
  [flf ^String s & [initial-output]]
  (let [output (or initial-output (get-initial-output-lines flf))]
    (reduce
      (fn [out c]
        (condp = c
          \tab (render-line flf "        " out)
          (render-char flf c out)))
      output
      s)))

(defn render
  "Renders a string of text using the given FIGlet font. The rendered output is returned
   as a sequence of lines. The text given is split up on each newline character found and
   each line is rendered as a new line in the output. So the number of lines in the final
   rendered output will be equal to [number of lines in 's'] * [height of flf font]"
  [flf ^String s]
  (->> (str/split s #"\n")
       (map #(render-line flf %))
       (apply concat)))

(defn render-to-string
  "Renders a string of text using the given FIGlet font. The rendered output is returned
   as a single string which is created by concatenating each line of output from rendering
   the same string using clj-figlet.core/render."
  [flf ^String s]
  (->> (render flf s)
       (str/join \newline)))