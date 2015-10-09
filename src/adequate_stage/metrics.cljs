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

(defn table [attrs & cols]
  (apply
   js/React.createElement
   Table
   (clj->js attrs)
   cols))

(def rows (vec (repeat 1000 ["a1" (mui/raised-button {:label "foo"}) "c1"])))

(defn row-getter [row-index]
  (clj->js (rows row-index))
  )

;(inspect (clj->js (repeat 20 [1 2 3])))

(defcs metrics-table [state]
  [:div
   [:h2 "filler"]
   (table
    {:rowHeight 50
     :rowGetter row-getter
     :rowsCount (count rows)
     :width 1000
     :height 500
     :headerHeight 50}
    (column {:lable "col 1"
             :width 300
             :dataKey 0})
    (column {:lable "col 2"
             :width 300
             :dataKey 1})
    (column {:lable "col 3"
             :width 300
             :dataKey 2})
    )
   ]
  )

