(defn pUCT [h m]
  ;h is the history of past actions
  ;m is the search horizon
  (best-action ;When search terminates, compute best available action
   (reduce ;recursively sample from the search try psi given h and m
    (fn [psi step] (sample psi h m))
    empty-psi ; begin with an empty search tree. TODO: Implement saving search tree information from one timestep to the next.
    (range 20) ;TODO: Implement sampling based on time limits to adhere to Veness et al. specifications. More sophisticated mechanisms possible.
    )
   h)
  )

(defn sample [psi h m]
  ;Recursively sample a single future trajectory.
  ;TODO: Decide whether to return the extended search tree, the reward value, both, or something else.
  ;psi is the search tree.
  ;h is the history.
  ;m is the search horizon.
  ;Alas, we cannot directly implement tail-recursion in clojure... But loop/recur will suffice.
  (loop [horizon m hist h reward 0]
    (cond
     (= horizon 0) reward ;We're at the end of our search horizon, so we stop adding to the reward value.
     (chance-node? (psi h)) (let
                              [hor nil r nil psi nil]
                              ;TODO: generate (o, r) from p(or|h) and add a node hor to psi if T(hor) = 0.
                              (sample psi hor (- m 1)))
     (= (T h) 0) (rollout h m) ;TODO: figure out what that means.
     :else (let [a (select-action psi h) ha nil] ;TODO: compute ha from h and a...
             (recur psi ha m))
     )
    ;TODO: Within the loop, after the cond block, set new value for V(h), then increment the visit count T(h).
    ))
(defn select-action [psi h]
  ;psi is the search tree
  ;TODO: decide whether to return a (the action chosen) or psi (the modified tree) or both. Or maybe refactor this function?
  ;h is the history
  ;C is a constant determining the shape of the search tree; smaller values of C create shorter, bushier trees, while large values create deeper, narrower trees.
  (let [a (set nil) u (set nil)]
    ;TODO: Implement A properly. A is the set o fall actions under consideration (derived from psi).
    ;TODO: Implement U properly. U is defined as the set of all actions in A for which T(ha) = 0.
    (if (empty? u)
      ;TODO: empty, so return argmax of
      ;TODO: not empty, so pick an a from U at random, add a node ha to psi, return a
      )
    ))

(defn rollout [h m]
  ;h is the history
  ;m is the remaining search horizon
  ;pi is a rollout function
  ;TODO: Figure out what that means.
  ;TODO: Are the modifications to h in this function supposed to backpropagate?
  (loop [history h reward 0 i 1]
    (if (<= i m)
      (let [a (pi h) o nil r nil]
        ;TODO: implement pi
        ;TODO: generate (o,r) from p(or|ha)
        ;TODO: haor
        (recur haor (+ reward r) (+ i 1))
      )
      reward))
  )

;;TODO: Model generation!
