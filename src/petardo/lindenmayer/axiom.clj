(ns petardo.lindenmayer.axiom
  (:require [mikera.image.core :as im]
            [mikera.image.colours :as imc]
            [taoensso.timbre :as t]))

(comment
  "Turtle graphics"
  "A graphical representation of a (generation of) an L-system is given by the
  following turtle graphics interpretation of the symbols of the alphabet. The
  state of the turtle is given by its position and direction, and the color of
  the pencil and whether it is currently up (not drawing) or down (drawing). In
  each step the tile at the turtle's current position is painted by the color of
  the pencil (if the pencil is down) before applying the action of the next
  symbol in the L-system generation."
  "Initial state

  Position: (0, 0).
  Direction: South.
  Pen: (Palette color #1, Up)."

  "Symbol actions

  a, ..., z : No action.
  A, ..., Z and ^ : Move forward.
  ( : Save turtle state.
   ) : Recall last saved state.
  [ : Save turtle state and turn 90° CCW.
   ] : Recall last saved state and turn 90° CW.
  < : Turn 90° CCW.
  > : Turn 90° CW.
  ! : Put pencil down if it is currently not, or vice versa.
  + : Put pencil down.
  - : Take pencil up.
  / : Change pencil color to next palette entry (possibly wrapping around).
  \\ : Change pencil color to previous palette entry (possibly wrapping around).
"

  " Axiom: ////+a^
Rules:

    a → ^B[^A]^A
    b → BB

Gen. 1:
////+^B[^A]^A^
Gen. 2:
////+^BB[^^B[^A]^A]^^B[^A]^A^
Gen. 3:
////+^BBBB[^^BB[^^B[^A]^A]^^B[^A]^A]^^BB[^^B[^A]^A]^^B[^A]^A^
")

(def colors
  [:yellow
   :orange
   :cyan
   :green
   :blue
   :purple
   :red])

(defn turn
  [state direction]
  (mod (+ (:angle state) direction) 360))

(defn change-color
  "Changes color. Use negative numbers for backwards."
  [current-color direction]
  (get colors (mod (+ (.indexOf colors current-color) direction) (count colors))))

(defn move-forward
  [{:keys [x y angle] :as state}]
  (t/spy :info
    (merge
      state
      (case angle
        0   {:y (dec y)}
        90  {:x (inc x)}
        180 {:y (inc y)}
        270 {:x (dec x)}))))

(def default-actions
  (merge
    {\( #(assoc % :saved-state (dissoc % :saved-state))
     \) #(:saved-state %)
     \[ #(assoc %
           :saved-state (dissoc % :saved-state)
           :angle (turn % -90))
     \] #(assoc %
           :saved-state (dissoc % :saved-state)
           :angle (turn % 90))
     \< #(assoc %
           :angle (turn % -90))
     \> #(assoc %
           :angle (turn % 90))
     \! #(assoc %
           :pen-down? (not (:pen-down? %)))
     \+ #(assoc %
           :pen-down? true)
     \- #(assoc %
           :pen-down? false)
     \/ #(assoc %
           :color (change-color (:color %) 1))
     \\ #(assoc %
           :color (change-color (:color %) -1))
     \^ move-forward}
    (zipmap (map char (range (int \A) (int \Z))) (repeat move-forward))))

(defn forward-generation
  [{:keys [rules actions axiom generation]
    :or   {generation 0}
    :as   state}]
  (assoc state
    :generation (inc generation)
    :axiom (clojure.string/join
             (map (fn [x] (get rules x x)) axiom))))

(defn forward-n-generations
  ([n state]
   (->> (iterate forward-generation state)
     (take (inc n))
     (drop n)
     (first))))

(defn keyword->color
  [c]
  (var-get (find-var (symbol "mikera.image.colours" (name c)))))

(defn render-state
  [{:keys [axiom actions x y] :as state}]
  (let [width           800
        height          600
        image           (im/new-image width height)
        ^doubles pixels (im/get-pixels image)]
    (doall
      (reduce
        (fn [{:keys [x y angle color pen-down?] :or {x (/ width 2) y (/ height 2)} :as state} action]
          (if pen-down?
            (if-let [brush (keyword->color color)]
              (im/set-pixel image x y brush)
              (throw
                (ex-info "no such color" {:color color}))))
          (if-let [rule (get actions action)]
            (do
              (t/spy :info (rule state)))
            state))
        {:angle     180
         :pen-down? false
         :color     (first colors)
         :x         x
         :y         y}
        axiom))
    image))

(->> {:axiom   "////+a^"
      :rules   {\a "^B[^A]^A"
                \b "BB"
                \A "^B[^A]^A"
                \B "BB"}
      :x       400
      :y       300
      :actions default-actions}
  (forward-n-generations 3)
  ;; :axiom
  (render-state)
  (im/show)
  )

(forward-generation {:x     0
                     :y     0
                     :pen   :up
                     :color (first colors)
                     :axiom "////+a^"
                     :rules {\a "^b[^a]^a"
                             \b "bb"}})
(forward-generation
{:x 0, :y 0, :pen :up, :color :yellow, :axiom "////+^b[^a]^a^", :rules {\a "^b[^a]^a", \b "bb"}, :generation 1})

[{:x 0, :y 0, :pen :up, :color :yellow, :axiom "////+^bb[^^b[^a]^a]^^b[^a]^a^", :rules {\a "^b[^a]^a", \b "bb"}, :generation 2}]
