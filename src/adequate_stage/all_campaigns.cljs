(ns adequate-stage.all-campaigns
  (:require
   [rum.core :as rum :refer-macros [defc defcs defcc] :include-macros true]))

(defcs all-campaigns [state db]
  [:div
   [:h1 "All Campaigns!"]])
