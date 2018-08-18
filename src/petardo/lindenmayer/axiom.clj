(ns petardo.lindenmayer.axiom)

(comment
  "Turtle graphics"
  "A graphical representation of a (generation of) an L-system is given by the following turtle graphics interpretation of the symbols of the alphabet. The state of the turtle is given by its position and direction, and the color of the pencil and whether it is currently up (not drawing) or down (drawing). In each step the tile at the turtle's current position is painted by the color of the pencil (if the pencil is down) before applying the action of the next symbol in the L-system generation."
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
   :brown
   :green
   :blue
   :purple
   :red])

(defn turn
  [state direction]
  (mod (+ (:angle state) direction) 360))

(def default-actions
  {\( #(assoc % :saved-state %)
   \) #(do nil)
   \[ #(assoc %
         :saved-state %
         (turn % -90))
   \] #(assoc %
         :saved-state %
         (turn % 90))
   \< #(turn % -90)
   \> #(turn % 90)
   \! #(assoc %
         :pen-down? (not (:pen-down? %)))
   \+ #(assoc %
         :pen-down? true)
   \- #(assoc %
         :pen-down? false)
   \/ #(assoc %
         :color (change-color (:color %) 1))
   \\ #(assoc %
         :color (change-color (:color %) -1))})

(defn change-color
  "Changes color. Use negative numbers for backwards."
  [current-color direction]
  (get colors (mod (+ (.indexOf colors :orange) direction) (count colors))))

(defn forward-generation
  [{:keys [x y angle pen-down? color rules actions axiom generation]
    :or   {generation 0
           canvas     '()}
    :as   state}]
  (assoc state
    :generation (inc generation)
    :axiom (apply str (map (fn [x] (get rules x x)) axiom))
    :canvas (concat canvas
              ())))

(defn forward-n-generations
  ([n state]
   (->> (iterate forward-generation state)
     (take (inc n))
     (drop n))))

(forward-n-generations 3 {:x       0
                          :y       0
                          :pen     :up
                          :color   (first colors)
                          :axiom   "////+a^"
                          :rules   {\a "^b[^a]^a"
                                    \b "bb"}
                          :actions default-actions})

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
