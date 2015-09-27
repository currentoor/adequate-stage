(ns ^:figwheel-always adequate-stage.core
    (:require-macros [adequate-stage.macros :refer [inspect]]
                     [adequate-stage.material :as mui])
    (:require
     ;[adequate-stage.storage :as store :refer [conn set-system-attrs! system-attr]]
     [secretary.core :as secretary :refer-macros [defroute]]
     [goog.events :as events]
     [goog.history.EventType :as EventType]
     [rum.core :as rum :refer-macros [defc defcs defcc] :include-macros true]
     )
    (:import goog.History))

(enable-console-print!)

(defn set-system-attrs! [& params]
  (println "todo set system attrs"))

(defroute "/" []
  (set-system-attrs! :page :home))
(defroute "/login" []
  (set-system-attrs! :page :login))
(defroute "/study" []
  (set-system-attrs! :page :study))
(defroute "/new" []
  (set-system-attrs! :page :new))
(defroute "/all_decks" []
  (set-system-attrs! :page :all-decks))
(defroute "/profile" []
  (set-system-attrs! :page :profile))
(defroute "/logout" []
  (set-system-attrs! :page :logout))

;; History
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(def color-theme {})

(defc app-wrapper []
  (mui/raised-button {:backgroundColor "#820000" :primary true :label "Wrong"})
  )

(rum/mount (app-wrapper) (js/document.getElementById "app"))

(defonce history
  (hook-browser-navigation!))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
)

