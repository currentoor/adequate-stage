(ns adequate-stage.twitter-dash
  (:require
   [rum.core :as rum :refer-macros [defc defcs defcc] :include-macros true]))

(defcs twitter-dash [state]
  [:div
   [:h1 "Twitter Dashboard!"]])
