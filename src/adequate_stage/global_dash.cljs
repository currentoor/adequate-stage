(ns adequate-stage.global-dash
  (:require-macros [adequate-stage.macros :refer [inspect]]
                   [adequate-stage.material :as mui]
                   )

  (:require
   [adequate-stage.storage :as store :refer [conn set-system-attrs! system-attr]]
   [adequate-stage.metrics :as met]
   [rum.core :as rum :refer-macros [defc defcs defcc] :include-macros true]))

(def columns ["status" "x" "campaign" "network" "account"])
(def rows [["Active" "x" "Foo" "Twitter" "Kabir"]])

(def col1 {"Campaign Name" {"data" ["123" "456"]
                            "totals" []}})
(def col2 {"status" {"data" ["active" "deleted"]
                     "totals" []}})

(def metrics-data {"Campaign Name" {"data" ["123" "456"] "totals" []}
                   "Status" {"data" ["active" "deleted"] "totals" []}
                   "foo" {"data" ["foo" "foo"] "totals" []}
                   })

; ["123" "active"]
; ["456" "deleted"]

(inspect (->> (vals metrics-data)
              (map #(get % "data"))
              (apply map vector)
              ))

(def filters
  {:values ["all_visible" "all_active" "all_with_deleted" "all_inactive"]
   :texts ["All But Deleted" "All Active" "All" "Paused/Completed"]})

(def table-row-column js/window.MaterialUI.TableRowColumn)

(defn data->table-row [row]
  (js/React.createElement
   js/window.MaterialUI.TableRow
   nil
   (->> row
        (map #(js/React.createElement table-row-column nil %)))))

(def table-header-column js/window.MaterialUI.TableHeaderColumn)

(defn data->table-header [columns]
  (mui/table-header
   (js/React.createElement
    js/window.MaterialUI.TableRow
    nil
    (->> columns
         (map clojure.string/upper-case)
         (map #(js/React.createElement table-header-column nil %))))))

(defcs metrics-table < (rum/local nil) [state data]
  (let [rows* (map data->table-row (:rows data))
        rows (->> rows* (repeat 15) flatten)]
    (time (mui/table
      {:fixedHeader     true
       :height          "570px"
       :fixedFooter     true
       :multiSelectable true}
      (data->table-header (keys data))
      (js/React.createElement
       js/window.MaterialUI.TableBody
       #js {:deselectOnClickAway false
            :showRowHover        true
            :stripedRows         true
            :preScanRows         false}
       rows)
       (js/React.createElement
        js/window.MaterialUI.TableFooter
        nil
        (data->table-row ["123" "121242" "124" "1242" "12e13"])     )))))

(defcs filter-dropdown [state]
  (let [menuItems (mapv (fn [v1 v2] {:payload v1 :text v2}) (:values filters) (:texts filters))]
    (mui/drop-down-menu {:style {:width "200px" } :menuItems menuItems})))

(def paging-sizes [10 25 50 100])

(defcs paging-dropdown [state page-size]
  (let [menuItems (mapv (fn [v1] {:payload v1 :text v1}) paging-sizes)
        selectedIndex (.indexOf (to-array paging-sizes) page-size)]
    (mui/drop-down-menu {:style         {:width "90px" }
                         :selectedIndex selectedIndex
                         :autoWidth     false
                         :menuItems     menuItems})))

(defcs paging [state db]
  (let [page-number (or (system-attr db :page-number) 1)
        page-size (or (system-attr db :page-size) 50)]
    [:div
      [:div.col {:style {:margin-right "10px"}}
       [:div {:style {:display "inline" :position "relative" :top "5px" :margin-right "10px"}}
        "GO TO PAGE:"]
       (mui/text-field {:style        {:width "35px"}
                        :defaultValue page-number})
       ]
      [:div.col
       [:div {:style {:display "inline" :position "relative" :bottom "10px"}}
        "NUMBER OF ROWS:"]
       (paging-dropdown page-size)]
      [:div.col
       (mui/icon-button
        {:iconClassName   "material-icons"
         :tooltipPosition "top-right"
         :tooltip         "Previous Page"
         ;:onClick         prev
         }
        "arrow_back")
       [:div {:style {:position "relative" :bottom "5px" :display "inline" }} "1-10 of 10"]
       (mui/icon-button
        {:iconClassName   "material-icons"
         :tooltipPosition "top-right"
         :tooltip         "Previous Page"
         ;;:onClick         prev
         }
        "arrow_forward")
       ]
     ])
)

(defcs global-dash [state db]
  (let [this (:rum/react-component state)]
    [:div
     [:h1 "Welcome to AdequateStage!"]

     [:div.section.group
      [:div.col.span_1_of_12]
        [:div.col.span_10_of_12
         [:div.section.group
          [:div.col.span_1_of_3]
          [:div.col.span_1_of_3
            [:div.col.span_1_of_2
              (mui/date-picker
               {:hintText "Start"
                :showYearSelector true
                :textFieldStyle {:width "120px"}})]
             [:div.col.span_1_of_2
              (mui/date-picker
               {:hintText "End"
                :textFieldStyle {:width "120px"}})]]]

         [:div.section.group
          [:div.col.span_1_of_5
            (filter-dropdown)]
          [:div.col.span_1_of_5
           (mui/text-field {:hintText          "Ex: Campaign Name"
                            :floatingLabelText "Search"
                            :style {:width "200px" :position "relative" :bottom "17px"} })]
          [:div.col.span_1_of_5]

          [:div.col.span_1_of_8 {:style {:position "relative" :top "10px" :float "right"} }
            (mui/raised-button
             {:label "Columns"
              :onClick #(.show (.. this -refs -selectColumnsModal))
              :style {:float "right"} })


            (mui/dialog {:title   "Selected Columns"
                         :actions [{:text "cancel"}
                                   {:text       "submit"
                                    :onTouchTap #(inspect 'todo)}]
                         :ref     "selectColumnsModal"}
                        [:div (mui/toggle {:name "Col1" :value 1 :label "foo"})]
                        [:div (mui/toggle {:name "Col1" :value 1 :label "foo"})]
                        [:div (mui/toggle {:name "Col1" :value 1 :label "foo"})]
                        )
           ]

          [:div.col.span_1_of_5 {:style {:float "right"} }
            (mui/drop-down-menu {:menuItems [{:payload 1 :text "foo"}]
                                 :style {:width "200px" }})
            ]
           ]


         [:div.section.group
           (metrics-table metrics-data)
          [:div.col.span_1_of_10]]

         [:div.section.group
          [:div.col.span_2_of_5]
          [:div.col.span_3_of_5
           (paging db)
           ]
          ]
         ]]]
    )
  )

