(ns cleanup
  (:require
    [clojure.java.io :as io]
    [clojure.set :as cs]
    [clojure.string :as string]

    [clj-yaml.core :as yaml]
    )
  (:gen-class))

;; General utilities

(defn load-post
  [file]
  (let [contents (slurp file)
        parts (string/split contents #"---\n")
        [front body] (if (= 2 (count parts))
                       parts
                       (drop 1 parts))
        absolute-path (.getAbsolutePath file)]
    {:raw/file file
     :raw/path absolute-path
     :raw/name (.getName file)
     :raw/body body
     :raw/front front
     :raw/type (cond
                 (string/includes? absolute-path "/notes/") :note
                 :else :post)}))

(defn parse-front
  [info]
  (if-some [{:keys [raw/front]} info]
    (assoc info :parsed/front (yaml/parse-string front))
    info))

(defn save-post
  [info]
  (let [filename (:raw/path info)
        front (yaml/generate-string (:parsed/front info) :dumper-options {:flow-style :block})]
    (spit filename
          (str
            "---\n"
            front
            "---\n"
            (:raw/body info)))))


;; Predicates and fixes

(defn has-categories?
  [front]
  (contains? front :categories))

(defn rename-categories-to-tags
  [info]
  (if (has-categories? (:parsed/front info))
    (do (println "renaming categories to tags")
        (update info :parsed/front cs/rename-keys {:categories :tags}))
    info))

(defn has-uppercase-tags?
  [front]
  (let [{:keys [tags]} front
        lowered (map string/lower-case tags)]
    (and (seq tags)
         (not= tags lowered))))

#_(has-uppercase-tags? {})
#_(has-uppercase-tags? {:tags ["foo"]})

(defn lowercase-tags
  [info]
  (if (has-uppercase-tags? (:parsed/front info))
    (do (println "lowercasing tags")
        (update-in info [:parsed/front :tags] #(map string/lower-case %)))
    info))

(defn needs-seconds-in-date?
  [front]
  (let [{:keys [date]} front]
    (and
      (string? date)
      (= 1 (count (filter #(= \: %) date))))))

#_(needs-seconds-in-date? {:date "2014-01-01 13:14"})
#_(needs-seconds-in-date? {:date "2014-01-01 13:14:00 -0700"})

(defn add-seconds-to-date
  [info]
  (if (needs-seconds-in-date? (:parsed/front info))
    (do (println "adding seconds to the date")
        (update-in info [:parsed/front :date] #(str % ":00")))
    info))

(defn has-author?
  [front]
  (contains? front :author))

(defn remove-author
  [info]
  (if (has-author? (:parsed/front info))
    (do (println "removing author")
        (update info :parsed/front dissoc :author))
    info))

(defn has-slug?
  [front]
  (contains? front :slug))

(defn name->slug
  [name]
  (second (re-matches #"\d{4}-\d{2}\-\d{2}-([^\.]+)\.(?:markdown|md)" name)))

#_(name->slug "2008-01-27-on-organizing.markdown")

(defn add-slug
  [info]
  (if (and (= :post (:raw/type info))
           (not (has-slug? (:parsed/front info))))
    (do (println "adding slug")
        (update info :parsed/front assoc :slug (name->slug (:raw/name info))))
    info))


;; Apply all the fixes

(defn apply-fixes
  [info]
  (-> info
      (rename-categories-to-tags)
      (lowercase-tags)
      (add-seconds-to-date)
      (remove-author)
      (add-slug)
      ))

(defn markdown?
  [file]
  (let [filename (.getName file)]
    (or (string/ends-with? filename ".markdown")
        (string/ends-with? filename ".md"))))

(defn fix-markdown-files
  [basepath]
  (let [markdown-files (->> (file-seq (io/file basepath))
                            (filter markdown?))]
    (doseq [file markdown-files]
      (try
        (println "Working on" file)
        (-> (load-post file)
            (parse-front)
            (apply-fixes)
            (save-post))
        (catch Exception e
          (println e))))))


;; Main

(defn -main
  [& _args]
  (println "Fixing markdown files in post")
  (fix-markdown-files "content/post/")
  (println "Fixing markdown files in note")
  (fix-markdown-files "content/note/")
  (System/exit 0))


;; Fiddling

(defn legacy
  [filename]
  (str "content/posts/" filename))

(defn migrated
  [filename]
  (str "../endot.org/source/_posts/" filename))

(defn fixed-front
  [filename]
  (->> (load-post (io/file filename))
       (parse-front)
       (apply-fixes)
       (:parsed/front)
       (into {})))

(def current "2014-07-05-my-note-taking-workflow.markdown")
; (def current "2011-12-04-remotecopy-copy-from-remote-terminals-into-your-local-clipboard.markdown")
; (def current "2012-02-12-git-subtree-tracking-made-easy.markdown")
; (def current "2014-01-01-git-annex-tips.markdown")
; (def current "2008-01-27-on-organizing.markdown")


#_(fixed-front (migrated current))
#_(->> (load-post (io/file (migrated current)))
       (parse-front)
       :raw/type
       )

(def current-note "../endot.org/source/notes/2014-01-10-using-datomic-with-riak/index.markdown")


#_(fixed-front current-note)
