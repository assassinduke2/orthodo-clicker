(ns orthodocljs.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [ajax.core :refer [GET POST]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [orthodocljs.active :as act]
            [cljs.core.async :refer [put! chan poll! <! >!]]
            [clojure.string :as string]))

(enable-console-print!)

(println "This text is printed from src/clj/guestbook/routes/core.cljs.
    Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defn handler [response]
  (.log js/console (str response)))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))


(defonce app-state
    (atom {:coins 1500000
           :coinMod 1
           :clickers 0
           :archpriest 0
           :bishop 0
           :archbishop 0
           :patriarh 0
           :churches 0
           :shrines 0
           :cathedrals 0
           :patriarchates 0
           :religiousEvents 1
           :reLock 10
           :pamphlets 0
           :atheists 0
           :freeDays 0
           :menu "true"
           :shop "Prists"
           :menu2 "Prists"
           :genSec 0}))

(defn change [coins owner]
    (let [Mod (om/get-state owner :coinMod)]
        (+ coins Mod)))

(defn manualGen [state]
    (om/update! state :coins (act/add state)))

(defn displayManu [state]
    (om/update! state :menu (act/stateMenu state)))

(defn displayPrists [state]
    (om/update! state :shop "Prists"))

(defn displayPrists2 [state]
    (om/update! state :menu2 "Prists"))

(defn displayBuild [state]
    (om/update! state :shop "Buildings"))

(defn displayBuild2 [state]
    (om/update! state :menu2 "Buildings"))

(defn displayShop [state]
    (om/update! state :menu (act/stateShop state)))

(defn displayExtras [state]
    (om/update! state :shop "Extras"))

(defn periodicly [state owner]
    (let [pula (rand-int 300)]
    (om/transact! state :coins
    (fn [coins]
        (let [ver ((om/get-state owner) :LocState)]
        (+ coins (/ (ver :genSec) 20)))))
    (let [ver ((om/get-state owner) :LocState)]
        (if-not (= (ver :live) 0)
            ((println pula)
             (println (ver :trigger))
             (println (ver :live))
            (if (and (= pula 5) (= (ver :trigger) 0))
                ((om/set-state! owner :LocState {:genSec (* (ver :genSec) 2)
                                                 :live (ver :live)
                                                 :trigger 1})
                 (om/update! state :genSec (* (ver :genSec) 2))
                 (om/update! state :religiousEvents 2)))
            (if (= (ver :trigger) 1)
                (om/set-state! owner :LocState {:genSec (ver :genSec)
                                                :live (- (ver :live ) 1)
                                                :trigger 1}))
            (if (= (ver :live) 1)
                ((om/set-state! owner :LocState {:genSec (/ (ver :genSec) 2)
                                                 :live 100
                                                 :trigger 0})
                 (om/update! state :genSec (/ (ver :genSec) 2))
                 (om/update! state :religiousEvents 1))))))))

(defn button-comp [state]
  (dom/div #js
      {:className "col-md-3"}
      (dom/p #js
          {:className "coinsGenerated"} (int (:coins state)))
      (dom/div #js
          {:className "coinsSec"} "Coins/Sec: "(:genSec state))
      (dom/div #js
          {:className "coinsSec"} "Atheists: "(:atheists state))
      (dom/img #js
          {:onClick (fn [e] (manualGen state))
           :className "Generator"
           :src "/img/OrthodoCoin.png"})))

(defn prists_shop [state owner]
  (dom/div nil
  (dom/div nil
      (dom/button #js
          {:onClick (fn [e] (act/clickUPG state owner))
           :className "buy col-md-12 btn-extras"}
           (dom/div nil
           (dom/div #js {:className "ShopText col-md-10"}
             "Upgrade Belief Power: "
             (let [{coinMod :coinMod} state]
               (+ 100 (* coinMod (* 50 coinMod)))))
           (dom/div #js {:className "ShopText3 col-md-1"}
             (state :coinMod)))))
  (dom/div nil
      (dom/button #js
                  {:onClick (fn [e] (act/clickerInc state owner))
                   :className "buy col-md-12"}
        (dom/div nil
          (dom/div #js {:className "ShopText col-md-10"}
            (dom/img #js {:src "/img/Prist.png"
                          :className "imgShop"})
            "Buy Priests: "
            (+ 150 (* 50 (state :clickers) (state :clickers))))
            (dom/div {:className "col-md-1"})
          (dom/div #js {:className "ShopText3 col-md-1"}
            (state :clickers)))))
  (dom/div nil
      (dom/button #js
                  {:onClick (fn [e] (act/ArchpriestInc state owner))
                   :className "buy col-md-12"}
          (dom/div nil
            (dom/div #js {:className "ShopText col-md-10"}
              (dom/img #js {:src "/img/Archpriest.png"
                            :className "imgShop"})
              "Buy Archpriests: "
              (+ 450 (* 50 (state :archpriest) (state :archpriest))))
            (dom/div #js {:className "ShopText3 col-md-1"}
              (state :archpriest)))))
  (dom/div nil
      (dom/button #js
                  {:onClick (fn [e] (act/BishopInc state owner))
                   :className "buy col-md-12"}
           (dom/div nil
             (dom/div #js {:className "ShopText col-md-10"}
              (dom/img #js {:src "/img/Bishop.png"
                            :className "imgShop"})
           "Buy Bishops: "
           (+ 1070 (* 78 (state :bishop) (state :bishop))))
           (dom/div #js {:className "ShopText3 col-md-1"}
             (state :bishop)))))
  (dom/div nil
      (dom/button #js
                  {:onClick (fn [e] (act/ArchbishopInc state owner))
                   :className "buy col-md-12"}
          (dom/div nil
           (dom/div #js {:className "ShopText col-md-10"}
            (dom/img #js {:src "/img/Archbishop.png"
                          :className "imgShop"})
             "Buy Archbishops: "
             (+ 2570 (* 162 (state :archbishop) (state :archbishop))))
             (dom/div #js {:className "ShopText3 col-md-1"}
               (state :archbishop)))))
  (dom/div nil
      (dom/button #js
                  {:onClick (fn [e] (act/PatriarhsInc state owner))
                   :className "buy col-md-12"}
          (dom/div nil
            (dom/div #js {:className "ShopText col-md-10"}
              (dom/img #js {:src "/img/Daniel.png"
                            :className "imgShop"})
                "Buy Patriarchs: "
                (+ 5394 (* 462 (state :patriarh) (state :patriarh))))
                (dom/div #js {:className "ShopText3 col-md-1"}
                  (state :patriarh)))))))

(defn buildings_shop [state owner]
  (dom/div nil
  (dom/div nil
      (dom/button #js
                  {:onClick (fn [e] (act/churchesInc state owner))
                   :className "buy col-md-12"}
        (dom/div nil
          (dom/div #js {:className "ShopText col-md-10"}
            (dom/img #js {:src "/img/Church.png"
                          :className "imgShop2"})
          "Buy Churches: "
          (+ 3500 (* 762 (state :churches) (state :churches))))
          (dom/div #js {:className "ShopText3 col-md-1"}
            (state :churches)))))
  (dom/div nil
      (dom/button #js
                  {:onClick (fn [e] (act/shrineInc state owner))
                   :className "buy col-md-12"}
        (dom/div nil
          (dom/div #js {:className "ShopText col-md-10"}
            (dom/img #js
                    {:src "/img/Shrine.png"
                     :className "imgShop2"})
              "Buy Shrines: "
              (+ 6700 (* 862 (state :shrines) (state :shrines))))
          (dom/div #js {:className "ShopText3 col-md-1"}
            (state :shrines)))))
  (dom/div nil
      (dom/button #js
                  {:onClick (fn [e] (act/cathedralInc state owner))
                   :className "buy col-md-12"}
        (dom/div nil
          (dom/div #js {:className "ShopText col-md-10"}
            (dom/img #js
                    {:src "/img/Cathedral.png"
                     :className "imgShop2"})
              "Buy Cathedrals: "
              (+ 8800 (* 1362 (state :cathedrals) (state :cathedrals))))
          (dom/div #js {:className "ShopText3 col-md-1"}
            (state :cathedrals)))))
  (dom/div nil
      (dom/button #js
                  {:onClick (fn [e] (act/patriarchateInc state owner))
                   :className "buy col-md-12"}
        (dom/div nil
          (dom/div #js {:className "ShopText col-md-10"}
            (dom/img #js
                     {:src "/img/Patriarchate.png"
                      :className "imgShop2"})
              "Buy Patriarchate: "
              (+ 13600 (* 1743 (state :patriarchates) (state :patriarchates))))
          (dom/div #js {:className "ShopText3 col-md-1"}
            (state :patriarchates)))))))

(defn extras_shop [state owner]
  (dom/div nil
      (dom/div nil
          (if (= (state :reLock) 10)
          (dom/div #js {:className "toltip"}
              (dom/button #js
                          {:onClick (fn [e]
                              (act/ReligiousEventsInc state owner))
                           :className "buy ShopText"}
                  "Add Religious events: 1750")
              (dom/span #js {:className "tooltiptext"}
                  "Adds a chance to start a religious event
                      that doubles coins/sec for a while"))))
      (dom/div #js {:className "toltip"}
          (dom/button #js
                      {:onClick (fn [e]
                                (act/pamphletsInc state owner))
                       :className "buy ShopText"}
                  "Send pamphlets: "
                  (+ 4000 (* 1576 (state :pamphlets) (state :pamphlets))))
          (dom/span #js {:className "tooltiptext"}
              "Sending pamphlets has a chance to increase
              people's belief power but it can also make
              more atheists (more pamphlets may attract more
                              atheists)"))
      (dom/div #js {:className "toltip"}
          (dom/button #js
                      {:onClick (fn [e]
                                (act/FreeDaysInc state owner))
                       :className "buy ShopText"}
                  "Get Free Days from work: "
                  (+ 7500 (* 5839 (state :freeDays) (state :freeDays))))
          (dom/span #js {:className "tooltiptext"}
              "Free days could increase people's belief power,
              chances of that happening will decrease with
              every day added"))))

(defn shop-comp [state owner]
  (dom/div nil
      (dom/div #js
          {:className "btn-group"
           :role "group"}
          (dom/button #js
              {:type "button"
               :className "btn btn-default
                           btnColor ShopText2"
               :onClick (fn [e]
                        (displayPrists state))} "Priests")
          (dom/button #js
              {:type "button"
               :className "btn btn-default
                           btnColor ShopText2"
               :onClick (fn [e]
                        (displayBuild state))} "Buildings")

          (dom/button #js
              {:type "button"
               :className "btn btn-default
                           btnColor ShopText2"
               :onClick (fn [e]
                        (displayExtras state))} "Extras"))

      (dom/div nil
      (if (= (state :shop) "Prists")
        (prists_shop state owner))

      (if (= (state :shop) "Buildings")
        (buildings_shop state owner))

      (if (= (state :shop) "Extras")
        (extras_shop state owner)))))

(defn root-comp [state owner]
    (reify
        om/IInitState
        (init-state [_]
            {:LocState {:genSec 0
                        :live 0
                        :trigger 0}})
        om/IWillMount
        (will-mount [this]
            (js/setInterval
                #(periodicly state owner) 50))
        om/IRender
        (render [this]
        (dom/div #js
          {:className "col-md-12"}
            (button-comp state)

            (dom/div #js
              {:className "col-md-4"})

            (dom/div #js
              {:className "col-md-5 btn-poz"}
                (shop-comp state owner))))))

  (om/root root-comp app-state
    {:target (. js/document (getElementById "Coins"))})
