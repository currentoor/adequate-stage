(ns adequate-stage.macros)

(defn- inspect-1 [expr]
  `(let [result# ~expr]
     (js/console.info (str (pr-str '~expr) " => " (pr-str result#)))
     result#))

(defmacro inspect
  "Debugging macro for inspecting a value in the js console."
  [& exprs]
  `(do ~@(map inspect-1 exprs)))
