(ns adequate-stage.navbar
  (:require-macros [adequate-stage.macros :refer [inspect]]
                   [adequate-stage.material :as mui])
  (:require
   [adequate-stage.storage :as store :refer [conn set-system-attrs! system-attr]]
   [rum.core :as rum :refer-macros [defc defcs defcc] :include-macros true]))

(defn radio-button
  ([label] (radio-button label true))
  ([label disabled?]
   (mui/radio-button {:value    label
                      :disabled disabled?
                      :style    {:marginBottom 16}
                      :label    label})))

(defcs navbar < (rum/local false) [state]
  (let [show-drop-down? (:rum/local state)
        this            (:rum/react-component state)
        menu-items      [{ :route "#/" :text "Global Dashboard" }
                         { :route "#/all_campaigns" :text "All Campaigns" }
                         { :type js/window.MaterialUI.MenuItem.Types.SUBHEADER :text "Networks" }
                         { :route "#/twitter" :text "Twitter Dashboard" }]]
    [:div
     (mui/app-bar
      {:title                    "AdequateStage"
       :onLeftIconButtonTouchTap (fn []
                                   (.toggle (.. this -refs -leftNav)))
       :iconElementRight         (mui/raised-button
                                  {:label   "Creation"
                                   :primary true
                                   :onClick #(.show (.. this -refs -creationModal))})})
     (mui/left-nav
      {:menuItems menu-items
       :docked    false
       :ref       "leftNav"
       :onChange  (fn [_ selected-idx menu-item]
                    (aset js/window.location "href" (.-route menu-item)))})
     (mui/dialog {:title   "Creation"
                  :actions [{:text "cancel"}
                            {:text       "submit"
                             :onTouchTap #(inspect 'todo)}]
                  :ref     "creationModal"}
                 (mui/radio-button-group
                  {:name            "curationOptions"
                   :defaultSelected "twitter"}
                  (radio-button "Twitter" false)
                  (radio-button "Linkedin")
                  (radio-button "Facebook")
                  (radio-button "Google")
                  (radio-button "Bing")))]))
