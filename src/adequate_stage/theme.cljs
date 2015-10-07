(ns adequate-stage.theme)

(let [colors js/window.MaterialUI.Styles.Colors
      theme-manager  js/window.MaterialUI.Styles.ThemeManager
      app-theme #js {:spacing js/window.MaterialUI.Styles.Spacing
                     :fontFamily "Roboto, sans-serif"
                     :palette #js {:primary1Color (.-teal900 colors)
                                   :primary2Color (.-teal400 colors)
                                   :primary3Color (.-teal900 colors)

                                   :accent1Color "#2C3E50";(.-teal800 colors)
                                   :accent2Color "#40C4C0";(.-teal700 colors)
                                   :accent3Color "#18252D";(.-teal900 colors)

                                   :textColor (.-darkBlack colors)
                                   :canvasColor (.-white colors)
                                   :borderColor (.-grey300 colors)
                                   :disabledColor (.-grey300 colors)}}]
  (def color-theme
    {:child-context-types {:muiTheme js/React.PropTypes.object}
     :child-context       (fn [_]
                            {:muiTheme (.getMuiTheme theme-manager app-theme)})}))
