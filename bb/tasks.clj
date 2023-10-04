(ns tasks
  (:require [babashka.fs :as fs]
            [babashka.tasks :as tasks]
            [babashka.process :as process]
            [clojure.edn :as edn]
            [clojure.tools.cli :refer [parse-opts]]))

(def locations (-> "locations.edn" slurp edn/read-string))

(defn prep
  "Retrieve dependent assets needed for build."
  [& _args]
  (when-not (fs/exists? "themes/hello-friend")
    (tasks/shell "git clone --branch endot2 git@github.com:justone/hugo-theme-hello-friend.git themes/hello-friend"))
  (when-not (fs/exists? "themes/hugo-atom-feed")
    (tasks/shell "git clone https://github.com/kaushalmodi/hugo-atom-feed.git themes/hugo-atom-feed")))

(defn clean
  "Clean built artifacts."
  [& _args]
  (fs/delete-tree "public"))

(defn build
  "Build site with hugo."
  [& args]
  (process/shell (cons "hugo" args)))

(defn dev
  "Start hugo dev server. Provide a location as the lone argument."
  [location]
  (let [dev-loc (get-in locations [:dev (keyword location)])]
    (when-not dev-loc
      (throw (ex-info "need remote host id" {:babashka/exit 1})))
    (process/shell "hugo server -D --bind 0.0.0.0 -p 1313 -F --baseURL" dev-loc)))

(defn deploy
  "Build and push site. Provide a location as the lone argument."
  [location]
  (let [{:keys [site remote]} (get-in locations [:deploy (keyword location)])]
    (build "--baseURL" site)
    (process/shell "rsync -var --delete public/" remote)))
