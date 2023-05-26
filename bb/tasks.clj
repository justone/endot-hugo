(ns tasks
  (:require [babashka.fs :as fs]
            [babashka.tasks :as tasks]
            [babashka.process :as process]
            [clojure.edn :as edn]
            [clojure.tools.cli :refer [parse-opts]]))

(def locations (-> "locations.edn" slurp edn/read-string))

(defn prep
  [& _args]
  (when-not (fs/exists? "themes/hello-friend")
    (tasks/shell "git clone --branch endot2 git@github.com:justone/hugo-theme-hello-friend.git themes/hello-friend"))
  (when-not (fs/exists? "themes/hugo-atom-feed")
    (tasks/shell "git clone https://github.com/kaushalmodi/hugo-atom-feed.git themes/hugo-atom-feed")))

(defn clean
  [& _args]
  (fs/delete-tree "public"))

(defn build
  [& args]
  (process/shell (cons "hugo-extended" args)))

(def dev-cli-opts
  [["-r" "--remote-host HOST-ID" "Which baseURL should we use."]])

(defn dev
  [& args]
  (let [{:keys [options]} (parse-opts args dev-cli-opts)
        dev-loc (get-in locations [:dev (keyword (:remote-host options))])]
    (when-not dev-loc
      (throw (ex-info "need remote host id" {})))
    (process/shell "hugo-extended server -D --bind 0.0.0.0 -p 1313 -F --baseURL" dev-loc)))

(defn push
  [location]
  (let [{:keys [site remote]} (get-in locations [:deploy (keyword location)])]
    (build "--baseURL" site)
    (process/shell "rsync -var --delete public/" remote)))
