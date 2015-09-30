(ns adequate-stage.global-dash
  (:require
   [adequate-stage.metrics :as met]
   [rum.core :as rum :refer-macros [defc defcs defcc] :include-macros true]))

(defcs global-dash [state db]
  [:div
   [:h1 "Welcome to AdequateStage!"]

   [:div.section.group
    [:div.col.span_1_of_3
     "Chart 1"]
    [:div.col.span_1_of_3
     "Chart 2"]
    [:div.col.span_1_of_3
     "Chart 3"]]

   [:div.section.group
    [:div.col.span_1_of_10]
    [:div.col.span_8_of_10
     (met/metrics-table)]
    [:div.col.span_1_of_10]]])

