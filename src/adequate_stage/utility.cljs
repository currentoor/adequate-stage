(ns adequate-stage.utility
  (:require-macros [adequate-stage.macros :refer [inspect]]
                   [adequate-stage.material :as mui])
  (:require
   [adequate-stage.storage :as store :refer [conn set-system-attrs! system-attr]]
   [rum.core :as rum :refer-macros [defc defcs defcc] :include-macros true]))

;; (def relay-mixin
;;   {:state (fn [] {:loading? true
;;                  :data        nil})
;;    :did-mount (fn [props]
;;                 (fetch (:query props)
;;                        (fn [payload] (.set-state payload))))})

;; (defcs poor-mans-relay [state & {:keys [query child-fn]}]
;;   (if (:loading? state)
;;     (mui/circular-progress)
;;     (child-fn (:data state))))
