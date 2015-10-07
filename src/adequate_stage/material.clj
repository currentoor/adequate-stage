(ns adequate-stage.material
  (:require
   [clojure.string :as str]))

(defn ->kebab [s]
  (str/join "-" (map str/lower-case (re-seq #"\w[a-z]+" s))))

(def components '[AppBar
                  Card
                  CardActions
                  CardHeader
                  CardText
                  CardTitle
                  Checkbox
                  CircularProgress
                  DatePicker
                  Dialog
                  DropDownMenu
                  FlatButton
                  FontIcon
                  IconButton
                  IconMenu
                  LeftNav
                  LinearProgress
                  List
                  ListItem
                  ListDivider
                  Menu
                  MenuItem
                  Paper
                  RadioButton
                  RadioButtonGroup
                  RaisedButton
                  RefreshIndicator
                  SelectField
                  SnackBar
                  Slider
                  Tab
                  Tabs
                  Table
                  TableBody
                  TableHeader
                  TableHeaderColumn
                  TableFooter
                  TableRow
                  TableRowColumn
                  TextField
                  TimePicker
                  Toggle
                  Toolbar
                  ToolbarGroup
                  ToolbarSeparator
                  ToolbarTitle
                  ])

(defn conditional-compile [children]
  (if (seq children)
    `(sablono.core/html ~@children)))

(defn gen-wrapper [component]
  `(defmacro ~(symbol (->kebab (str component))) [& args#]
     (let [[opts# & [children#]] (if (-> args# first map?)
                                   [(first args#) (rest args#)]
                                   [nil args#])]
       `(js/React.createElement
         ~(symbol "js" (str "MaterialUI." (name '~component)))
         (cljs.core/clj->js ~opts#)
         ~(conditional-compile children#)))))

(defmacro gen-wrappers []
  `(do
     ~@(clojure.core/map gen-wrapper components)))

(gen-wrappers)
