(ns adequate-stage.global-dash
  (:require-macros [adequate-stage.macros :refer [inspect]]
                   [adequate-stage.material :as mui])
  (:require
   [adequate-stage.metrics :as met]
   [rum.core :as rum :refer-macros [defc defcs defcc] :include-macros true]))

(def metrics-data
  {:columns ["status" "campaign" "network" "account"]
   :rows [["Active" "Foo" "Twitter" "Kabir"]]})

(def table-row-column js/window.MaterialUI.TableRowColumn)

(defn data->table-row [row]
  (js/React.createElement
   js/window.MaterialUI.TableRow
   nil
   (->> row
        (map #(js/React.createElement table-row-column nil %)))))

(def table-header-column js/window.MaterialUI.TableHeaderColumn)

(defn data->table-header [{columns :columns}]
  (mui/table-header
   (js/React.createElement
    js/window.MaterialUI.TableRow
    nil
    (->> columns
         (map clojure.string/upper-case)
         (map #(js/React.createElement table-header-column nil %))))))

(defc metrics-table [data]
  (let [rows* (map data->table-row (:rows data))
        rows (->> rows* (repeat 30) flatten)]
    (mui/table
     {:fixedHeader     true
      :multiSelectable true}
     (data->table-header data)
     (js/React.createElement
      js/window.MaterialUI.TableBody
      #js {:deselectOnClickAway false
           :showRowHover        true
           :stripedRows         true
           :preScanRows         false}
      rows))))

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
     (metrics-table metrics-data)
     ]
    [:div.col.span_1_of_10]]])

