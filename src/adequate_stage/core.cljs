(ns ^:figwheel-always adequate-stage.core
    (:require-macros [adequate-stage.macros :refer [inspect]]
                     [cljs.core.async.macros :as asyncm :refer (go go-loop)]
                     [adequate-stage.material :as mui])
    (:require
     [clojure.walk :as walk]
     [adequate-stage.all-campaigns :refer [all-campaigns]]
     [adequate-stage.global-dash :refer [global-dash]]
     [adequate-stage.metrics :as metrics]
     [adequate-stage.navbar :refer [navbar]]
     [adequate-stage.routes :refer [hook-browser-navigation!]]
     [adequate-stage.storage :as store :refer [conn set-system-attrs! system-attr]]
     [adequate-stage.theme :refer [color-theme]]
     [adequate-stage.twitter-dash :refer [twitter-dash]]
     [ajax.core :refer [GET POST]]
     [cljs.core.async :as async :refer (<! >! put! chan)]
     [cognitect.transit :as t]
     cljsjs.fixed-data-table
     [rum.core :as rum :refer-macros [defc defcs defcc] :include-macros true]))

(enable-console-print!)

(defc app < rum/reactive color-theme [conn]
  (let [db   (rum/react conn)
        page (system-attr db :page)]
    [:div
     (navbar)
     (condp = page
       :all-campaigns (all-campaigns db)
       :twitter-dash  (twitter-dash db)
       (global-dash db))
     ]))


(rum/mount (app conn) (js/document.getElementById "app"))

(defonce history
  (hook-browser-navigation!))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
)

