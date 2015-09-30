(ns adequate-stage.metrics
    (:require-macros [adequate-stage.macros :refer [inspect]]
                     [adequate-stage.material :as mui])
    (:require
     [adequate-stage.storage :as store :refer [conn set-system-attrs! system-attr]]
     cljsjs.fixed-data-table
     [rum.core :as rum :refer-macros [defc defcs defcc] :include-macros true]))

(def Table js/FixedDataTable.Table)
(def Column js/FixedDataTable.Column)

(defn column [attrs]
  (js/React.createElement
   Column
   (clj->js attrs)))

(defn table [attrs col1 col2]
  (js/React.createElement
   Table
   (clj->js attrs)
   col1
   col2))

(def rows [["a1" "b1" "c1"]
           ["a2" "b2" "c2"]
           ["a2" "b2" "c2"]
           ["a3" "b3" "c3"]])

(defn row-getter [row-index]
  (clj->js ["a2" "b2" "c2"])
  )

(defcs metrics-table [state]
  [:div
   [:h2 "filler"]
   (table
    {:rowHeight 50
     :rowGetter row-getter
     :rowsCount (count rows)
     :width 500
     :height 500
     :headerHeight 50}
    (column {:lable "col 1"
             :width 300
             :dataKey 0})
    (column {:lable "col 2"
             :width 300
             :dataKey 1})
    )
   ]
  )

