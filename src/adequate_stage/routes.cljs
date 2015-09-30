(ns adequate-stage.routes
  (:require
   [adequate-stage.navbar :refer [navbar]]
   [adequate-stage.storage :as store :refer [conn set-system-attrs! system-attr]]
   [adequate-stage.theme :refer [color-theme]]
   [goog.events :as events]
   [goog.history.EventType :as EventType]
   [rum.core :as rum :refer-macros [defc defcs defcc] :include-macros true]
   [secretary.core :as secretary :refer-macros [defroute]])
  (:import goog.History))

(defroute "/" []
  (set-system-attrs! :page :home))
(defroute "/twitter" []
  (set-system-attrs! :page :twitter-dash))
(defroute "/all_campaigns" []
  (set-system-attrs! :page :all-campaigns))
(defroute "/twitter_creation" []
  (set-system-attrs! :page :twitter-creation))

;; History
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))
