(ns ^:figwheel-always adequate-stage.core
    (:require-macros [adequate-stage.macros :refer [inspect]]
                     [cljs.core.async.macros :as asyncm :refer (go go-loop)]
                     [adequate-stage.material :as mui])
    (:require
     [adequate-stage.all-campaigns :refer [all-campaigns]]
     [adequate-stage.global-dash :refer [global-dash]]
     [adequate-stage.metrics :as metrics]
     [adequate-stage.navbar :refer [navbar]]
     [adequate-stage.routes :refer [hook-browser-navigation!]]
     [adequate-stage.storage :as store :refer [conn set-system-attrs! system-attr]]
     [adequate-stage.theme :refer [color-theme]]
     [adequate-stage.twitter-dash :refer [twitter-dash]]
     [ajax.core :refer [GET POST]]
     [cljs.core.async :as async :refer (<! >! put! chan)]
     [taoensso.sente  :as sente :refer (cb-success?)]
     cljsjs.fixed-data-table
     [rum.core :as rum :refer-macros [defc defcs defcc] :include-macros true]))

(enable-console-print!)

;; (let [query           "collections/karantoor.accounts.facebook.100540310128849.campaigns,karantoor.accounts.facebook.473126772864672.campaigns,karantoor.accounts.linkedin.228929389.campaigns,karantoor.accounts.linkedin.502260647.campaigns,karantoor.accounts.linkedin.502260795.campaigns,karantoor.accounts.linkedin.502307123.campaigns,karantoor.accounts.linkedin.502365931.campaigns,karantoor.accounts.linkedin.500043833.campaigns,karantoor.accounts.linkedin.500082547.campaigns,karantoor.accounts.twitter.18ce53z7hjq.campaigns,karantoor.accounts.twitter.18ce53y6or0.campaigns,karantoor.accounts.twitter.18ce53waavf.campaigns/summary/2015-10-05..2015-10-11?&limit=10&cljs=true&group_by=folder_id&group_limit=5&offset=0&sort_by=clicks&order=desc&filters=%5B%7B%22op%22%3A%22iin%22%2C%22path%22%3A%22meta%2Fstatus%22%2C%22value%22%3A%5B%22active%22%2C%22ad_group_inactive%22%2C%22campaign_inactive%22%2C%22completed%22%2C%22empty%22%2C%22inactive%22%2C%22not_empty%22%5D%7D%5D
;; "
;;       staging-uri     "https://adstage-staging-metrics-v3.herokuapp.com:443/"
;;       dev-uri         "http://localhost:5002/"
;;       staging-headers {"Authorization" "Basic bWV0cmljczptZXRyaWNzLnBhc3N3b3Jk"}
;;       dev-headers     {"Authorization" "Basic YWQ6c3RhZ2U="}]
;;   (GET (str dev-uri query)
;;        {:headers dev-headers
;;         :handler #(inspect %)}))

(defc app < rum/reactive color-theme [conn]
  (let [db   (rum/react conn)
        page (system-attr db :page)]
    [:div
     (navbar)
     (condp = page
       :all-campaigns (all-campaigns db)
       :twitter-dash  (twitter-dash db)
       (global-dash db))
     ]))


(rum/mount (app conn) (js/document.getElementById "app"))

(defonce history
  (hook-browser-navigation!))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
)

