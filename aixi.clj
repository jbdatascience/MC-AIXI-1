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

(defn sample [psi h m C]
  ;Recursively sample a single future trajectory.
  ;Return a vector of the total reward expected and the updated search tree.
  ;psi is the search tree.
  ;h is the history.
  ;m is the search horizon.
  ;Alas, we cannot directly implement tail-recursion in clojure... But loop/recur will suffice.
  (loop [horizon m hist h reward 0 searchtree psi]
    (cond
      (= horizon 0) [reward searchtree] ;We're at the end of our search horizon, so we stop adding to the reward value.
      (chance-node? (psi hist)) (let
                                  [hor nil r nil psi nil]
                                  ;TODO: generate (o, r) from p(or|h) and add a node hor to psi if T(hor) = 0.
                                  (recur (- horizon 1) hor (+ reward r) searchtree));Recurse with the decreased horizon, new history (with percept node added), and new total reward value. 
      (= (T hist) 0) (rollout hist horizon);If the node has not been visited at all, rollout from this point up to the remaining horizon. 
      :else (let [a (select-action psi hist C m) ha (conj hist a) C m] ;We're not at the end of the search horizon, and the current node is not a chance node, and the node has been visited before (thus rollout sampled enough to give us some idea of the search space), so we can apply our actual decision procedure to choose an action and recurse.
              (recur horizon ha reward searchtree);Select an action, append it to the history, and recurse.
              )
      ;TODO: For each node sampled, set new value for V(h), then increment the visit count T(h).
      ))
  (defn select-action [psi h C m]
    ;Takes a search tree and a history; outputs a vector containing the action chosen and the modified search tree.
    ;psi is the search tree
    ;h is the history
    ;C is a constant determining the shape of the search tree; smaller values of C create shorter, bushier trees, while large values create deeper, narrower trees.
    ;m is the search horizon
    (let [A (possible-actions psi h) U (filter (fn [a] (= (T (conj h a)) 0)) A)]
      ;A is the set of all actions under consideration (derived from psi).
      (defn V [ha] (get-in psi (conj ha :V)))
      (defn T [ha] (get-in psi (conj ha :T)))
      ;U is the set of all actions a in A for which T(ha) = 0; that is, all the actions which have not even been considered yet.
      (if (empty? U)
        (apply max-key (+ (/ (V ha) (* m (beta - alpha))) (* C (sqrt (/ (ln (T h)) (T ha))))) A);empty, so return argmax of our expected utility function. TODO: cash out beta, alpha, ha
        (rand-nth (sequence U));not empty, so pick an a from U at random. TODO:  add a node ha to psi
        )
      ))

  (defn possible-actions [psi h]
    ;TODO: This function needs to accept a search-tree and a history and output a list of all the child actions available from the node of the search-tree corresponding to that history.
    nil)
  (defn rollout [h m psi]
    ;h is the history
    ;m is the remaining search horizon
    ;pi is a rollout function, which serves to explore parts of the search tree that have not previously been encountered. In this version, it chooses actions at random with uniform probability.
    ;This function returns a modified search tree containing the newly-explored nodes.
    (loop [history h reward 0 i 1 searchtree psi]
      (if (<= i m)
        (let [
              a (pi h) ;TODO: implement pi
              o nil
              r nil
              ha (conj h a) ;ha results from appending a to h
              o-r nil ;TODO: generate (o,r) from p(or|ha)
              haor (conj ha o-r) ;;haor results from appending or to ha
              ]
          (recur haor (+ reward r) (+ i 1) searchtree)
          )
        [reward searchtree]))
    )

  ;;TODO: Model generation!
  ;;TODO: Just saying that one again because it's gigantic. TODO TODO TODO TODO
  ;;Seriously TODO

  (defrecord PST [zero one theta V T]); In this implementation, a PST is a binary tree where each node contains a visit count, an estimate of the future reward at that node, and a probability theta
