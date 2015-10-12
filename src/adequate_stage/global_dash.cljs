(ns adequate-stage.global-dash
  (:require-macros [adequate-stage.macros :refer [inspect]]
                   [adequate-stage.material :as mui])
  (:require
   [adequate-stage.storage :as store :refer [conn set-system-attrs! system-attr]]
   [adequate-stage.metrics :as met]
   [ajax.core :refer [GET POST]]
   [rum.core :as rum :refer-macros [defc defcs defcc] :include-macros true]
   [goog.string :as gstring]
   [goog.string.format :as gformat]))

(def query-prefix "collections/karantoor.accounts.facebook.100540310128849.campaigns,karantoor.accounts.facebook.473126772864672.campaigns,karantoor.accounts.linkedin.228929389.campaigns,karantoor.accounts.linkedin.502260647.campaigns,karantoor.accounts.linkedin.502260795.campaigns,karantoor.accounts.linkedin.502307123.campaigns,karantoor.accounts.linkedin.502365931.campaigns,karantoor.accounts.linkedin.500043833.campaigns,karantoor.accounts.linkedin.500082547.campaigns,karantoor.accounts.twitter.18ce53z7hjq.campaigns,karantoor.accounts.twitter.18ce53y6or0.campaigns,karantoor.accounts.twitter.18ce53waavf.campaigns/summary/")

(defn query-meat [limit offset sort-by]
  (str "?&limit=" limit "&group_limit=5&cljs=true&offset=" offset "&sort_by=" sort-by "&order=asc&filters=%5B%7B%22op%22%3A%22iin%22%2C%22path%22%3A%22meta%2Fstatus%22%2C%22value%22%3A%5B%22active%22%2C%22ad_group_inactive%22%2C%22campaign_inactive%22%2C%22completed%22%2C%22empty%22%2C%22inactive%22%2C%22not_empty%22%5D%7D%5D"))

(def empty-totals
  {:account_currency_code  ""
   :account_name           ""
   :account_status         ""
   :account_time_zone      ""
   :account_uri            ""
   :campaign_is_draft      ""
   :campaign_name          ""
   :campaign_status        ""
   :campaign_type          ""
   :campaign_uri           ""
   :currency_code          ""
   :field_mappings         ""
   :folder_id              ""
   :folder_name            ""
   :full_remote_account_id ""
   :name                   ""
   :network                ""
   :remote_account_id      ""
   :remote_campaign_id     ""
   :status                 ""
   :type                   ""})

(defn hydrate-totals [totals]
  (into empty-totals totals))

(set-system-attrs! :start-date "2015-10-05"
                   :end-date   "2015-10-12")

(defn date-range []
  (let [[start end] (system-attr @conn :start-date :end-date)]
    (if (and start end)
      (str start ".." end))))

(defn fetch-data []
  (set-system-attrs! :done-loading? false)
  (let [date-range             (or (date-range)
                                   "2015-10-05..2015-10-12")

        [limit offset sort-by] (system-attr @conn :limit :offset :sort-by)
        query-suffix           (query-meat (or limit 30)
                                           (or offset 0)
                                           (or sort-by "clicks"))
        query                  (str query-prefix date-range query-suffix)

        staging-uri            "https://adstage-staging-metrics-v3.herokuapp.com:443/"
        staging-headers        {"Authorization" "Basic bWV0cmljczptZXRyaWNzLnBhc3N3b3Jk"}]
   (GET (str staging-uri query)
        {:headers staging-headers
         :handler #(set-system-attrs! :totals        (hydrate-totals (:totals %))
                                      :rows          (:rows %)
                                      :done-loading? true)})))

(defonce load (fetch-data))

(def keys->name {:social_percentage      "Social"
                 :account_currency_code  "Currency"
                 :campaign_name          "Campaign"
                 :account_uri            "URI"
                 :impressions            "Impressions"
                 :cpr                    "CPR"
                 :folder_name            "Folder"
                 :clicks                 "Clicks"
                 :name                   "Name"
                 :spend                  "Spend"
                 :account_status         "Status"
                 :frequency              "Frequency"
                 :type                   "Type"
                 :campaign_type          "Campaign Type"
                 :campaign_uri           "Campaign URI"
                 :campaign_is_draft      "Draft"
                 :folder_id              "Folder ID"
                 :full_remote_account_id "Remote ID"
                 :field_mappings         "Mappings"
                 :status                 "Status"
                 :campaign_status        "Campaign Status"
                 :conversions            "Conversions"
                 :remote_campaign_id     "Campaign Id"
                 :conversion_rate        "Conversion"
                 :account_time_zone      "Time Zone"
                 :network                "Network"
                 :account_name           "Account"
                 :cpm                    "CPM"
                 :cpa                    "CPA"
                 :cpc                    "CPC"
                 :remote_account_id      "Account ID"
                 :ctr                    "CTR"
                 :currency_code          "Currency Code"
                 :results                "Result"})

(defn allowed-columns []
  (or
   (system-attr @conn :allowed-columns)
   #{:account_currency_code
     :cpr
     :name
     :spend
     :network
     :cpm
     :cpa}))

(defn in?
  "true if seq contains elm"
  [seq elm]
  (some #(= elm %) seq))

(defn filter-by-allowed [maps]
  (->> maps
       (filter (fn [[k _]]
                 (in? (allowed-columns) k)))
       (map (fn [[_ n]] n))))

(defn column-names []
  (->> keys->name
       filter-by-allowed))

(defn all-rows []
  (->> (system-attr @conn :rows)
       (map filter-by-allowed)
       (map vec)))

(defn all-totals []
  (->> (system-attr @conn :totals)
       filter-by-allowed))

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
  (let [rows* (map data->table-row (all-rows))
        rows rows* ;(take 10 rows*)
        ]
    (time (mui/table
      {:fixedHeader     true
       :height          "570px"
       :fixedFooter     true
       :multiSelectable true}
      (data->table-header (column-names))
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
        (data->table-row (all-totals)))))))

(defcs filter-dropdown [state]
  (let [menu-items (mapv (fn [v1 v2] {:payload v1 :text v2}) (:values filters) (:texts filters))]
    (mui/drop-down-menu {:style {:width "100%" } :menuItems menu-items})))

(defcs sort-by-dropdown [state]
  (let [sub-map    (select-keys keys->name (into [] (allowed-columns)))
        menu-items (mapv (fn [[k v]] {:payload k :text v}) sub-map)]
    (mui/drop-down-menu {:style {:width "100%" }
                         :onChange #(and (set-system-attrs! :sort-by (.-payload %3))
                                         (fetch-data))
                         :menuItems menu-items})))

(def paging-sizes [30 50 100 500 1000])

(defcs paging-dropdown [state page-size]
  (let [menuItems (mapv (fn [v1] {:payload v1 :text v1}) paging-sizes)
        selectedIndex (.indexOf (to-array paging-sizes) page-size)]
    (mui/drop-down-menu {:style         {:width "90px" :position "relative" :bottom "5px" }
                         :selectedIndex selectedIndex
                         :autoWidth     false
                         :onChange #(and (set-system-attrs! :limit (.-payload %3))
                                         (fetch-data))
                         :menuItems     menuItems})))

(defcs paging [state db]
  (let [page-number (or (system-attr db :page-number) 1)
        page-size (or (system-attr db :limit) 50)]
    [:div {:style {:font-size "12px"}}
      [:div.col {:style {:margin-right "15px"}}
       [:div {:style {:display "inline" :position "relative" :top "5px" :margin-right "10px"}}
        "GO TO PAGE:"]
       (mui/text-field {:style        {:width "35px" :position "relative" :top "3px"}
                        :defaultValue page-number})
       ]
      [:div.col
       [:div {:style {:display "inline" :position "relative" :bottom "15px"}}
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
         :tooltipPosition "top-left"
         :tooltip         "Next Page"
         ;;:onClick         prev
         }
        "arrow_forward")
       ]
     ])
)

(defn toggle-in-set [s v]
  (if (s v)
    (disj s v)
    (conj s v)))

(defcs selected-column-list [state]
  (let [col-selection (allowed-columns)
        res
        (->>
         keys->name
         (map (fn [[k v]]
                [:div
                 (mui/toggle
                  {:name  "?"
                   :value k
                   :defaultToggled (col-selection k)
                   :onToggle (fn [_]
                               (set-system-attrs! :allowed-columns
                                                  (toggle-in-set col-selection
                                                                 k)))
                   :label v})]))
         (cons :div)
         vec)]
    res))

(defn on-date-change [part event date]
  (let [year      (.getFullYear date)
        month     (gstring/format "%02d" (inc (.getMonth date)))
        date      (gstring/format "%02d" (.getDate date))
        formatted (str year "-" month "-" date)]
    (if (= part "start")
      (set-system-attrs! :start-date formatted)
      (set-system-attrs! :end-date formatted))
    (fetch-data)))

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
                :autoOk true
                :onChange (partial on-date-change "start")
                :defaultDate (new js/Date. 2015 9 5)
                :showYearSelector true
                :textFieldStyle {:width "100%"}})]
             [:div.col.span_1_of_2
              (mui/date-picker
               {:hintText "End"
                :autoOk true
                :onChange (partial on-date-change "end")
                :defaultDate (new js/Date. 2015 9 12)
                :textFieldStyle {:width "100%"}})]]]

         [:div.section.group
          [:div.col.span_1_of_5
            (filter-dropdown)]
          [:div.col.span_1_of_5
           (mui/text-field {:hintText          "Ex: Campaign Name"
                            :floatingLabelText "Search"
                            :style {:width "100%" :position "relative" :bottom "17px"}})]
          [:div.col.span_1_of_5]

          [:div.col.span_1_of_8 {:style {:position "relative" :top "10px" :float "right"}}
            (mui/raised-button
             {:label "Columns"
              :onClick #(.show (.. this -refs -selectColumnsModal))
              :style {:width "100%" :float "right"}})

            (mui/dialog {:title   "Selected Columns"
                         :autoDetectWindowHeight true
                         :autoScrollBodyContent true
                         :ref     "selectColumnsModal"}
                        (selected-column-list))]

          [:div.col.span_1_of_5 {:style {:float "right"} }
            (sort-by-dropdown)]]

         [:div.section.group
          (if (system-attr @conn :done-loading?)
            (metrics-table nil)
            [:div.center.progress-indicator
             (mui/circular-progress {:mode "indeterminate"})
             (mui/circular-progress {:mode "indeterminate"})
             (mui/circular-progress {:mode "indeterminate"})])
          [:div.col.span_1_of_10]]

         [:div.section.group
          [:div.col.span_1_of_4]
          [:div.col.span_1_of_2
           (paging db)
           ]
          ]
         ]]]
    )
  )

