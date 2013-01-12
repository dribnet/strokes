(ns blade.examples.tutorial1
  (:use [blade.core :only [L]]))

; this is unfortunate and temporary until i split things out
(if L (do

(def tile-url
  "http://{s}.tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/997/256/{z}/{x}/{y}.png")

(defn ^:export launch []
  (let [mappy (-> L (.map "mappy") (.setView [51.505 -0.09] 13))]

    (-> L (.tileLayer tile-url {
        :maxZoom 18
        :attribution "Map data &copy; <a href=\"http://openstreetmap.org\">OpenStreetMap</a> contributors, <a href=\"http://creativecommons.org/licenses/by-sa/2.0/\">CC-BY-SA</a>, Imagery © <a href=\"http://cloudmade.com\">CloudMade</a>"
      }) (.addTo mappy))))

))