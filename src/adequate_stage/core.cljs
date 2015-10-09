(ns ^:figwheel-always adequate-stage.core
    (:require-macros [adequate-stage.macros :refer [inspect]]
                     [cljs.core.async.macros :as asyncm :refer (go go-loop)]
                     [adequate-stage.material :as mui])
    (:require
     [adequate-stage.all-campaigns :refer [all-campaigns]]
     [adequate-stage.global-dash :refer [global-dash]]
     [adequate-stage.metrics :as metrics]
     [adequate-stage.navbar :refer [navbar]]
     [adequate-stage.routes :refer [hook-browser-navigation!]]
     [adequate-stage.storage :as store :refer [conn set-system-attrs! system-attr]]
     [adequate-stage.theme :refer [color-theme]]
     [adequate-stage.twitter-dash :refer [twitter-dash]]
     [cljs.core.async :as async :refer (<! >! put! chan)]
     [taoensso.sente  :as sente :refer (cb-success?)]
     cljsjs.fixed-data-table
     [rum.core :as rum :refer-macros [defc defcs defcc] :include-macros true]))

(enable-console-print!)

;(let [{:keys [chsk ch-recv send-fn state]}
;      (sente/make-channel-socket! "/chsk" ; Note the same path as before
;                                  {:type :auto ; e/o #{:auto :ajax :ws}
;                                   :host "localhost:5002"
;                                   })]
;  (def chsk       chsk)
;  (def ch-chsk    ch-recv) ; ChannelSocket's receive channel
;  (def chsk-send! send-fn) ; ChannelSocket's send API fn
;  (def chsk-state state)   ; Watchable, read-only atom
;  )

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

