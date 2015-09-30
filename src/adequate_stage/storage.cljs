(ns adequate-stage.storage
  (:require-macros [adequate-stage.macros :refer [inspect]])
  (:require
   [datascript.core :as d]))

(def schema {:user {}})

(defonce conn (d/create-conn schema))

(defn set-system-attrs!
  "Entity with id=0 is used for storing auxilary view information
   like filter value and selected group."
  [& args]
  (d/transact! conn
               (for [[attr value] (partition 2 args)]
                 (if value
                   [:db/add 0 attr value]
                   [:db.fn/retractAttribute 0 attr]))))

(defn system-attr
  "Get system attributes."
  ([db attr]
   (get (d/entity db 0) attr))
  ([db attr & attrs]
   (mapv #(system-attr db %) (concat [attr] attrs))))
