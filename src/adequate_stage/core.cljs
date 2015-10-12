(ns ^:figwheel-always adequate-stage.core
    (:require-macros [adequate-stage.macros :refer [inspect]]
                     [cljs.core.async.macros :as asyncm :refer (go go-loop)]
                     [adequate-stage.material :as mui])
    (:require
     [clojure.walk :as walk]
     [adequate-stage.global-dash :refer [global-dash]]
     [adequate-stage.navbar :refer [navbar]]
     [adequate-stage.routes :refer [hook-browser-navigation!]]
     [adequate-stage.storage :as store :refer [conn set-system-attrs! system-attr]]
     [adequate-stage.theme :refer [color-theme]]
     [rum.core :as rum :refer-macros [defc defcs defcc] :include-macros true]))

(enable-console-print!)

(defc app < rum/reactive color-theme [conn]
  (let [db   (rum/react conn)
        page (system-attr db :page)]
    [:div
     (navbar)
     (condp = page
       (global-dash db))
     ]))


(rum/mount (app conn) (js/document.getElementById "app"))

(defonce history
  (hook-browser-navigation!))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
)

