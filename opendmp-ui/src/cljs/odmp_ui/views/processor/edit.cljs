;; Copyright 2020 The Open Data Management Platform contributors.

;; Licensed under the Apache License, Version 2.0 (the "License");
;; you may not use this file except in compliance with the License.
;; You may obtain a copy of the License at

;; http://www.apache.org/licenses/LICENSE-2.0

;; Unless required by applicable law or agreed to in writing, software
;; distributed under the License is distributed on an "AS IS" BASIS,
;; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;; See the License for the specific language governing permissions and
;; limitations under the License.

(ns odmp-ui.views.processor.edit
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [odmp-ui.subs :as subs]
            [odmp-ui.events :as events]
            [odmp-ui.util.styles :as style]
            [odmp-ui.components.common :as tcom]
            [odmp-ui.components.icons :refer [processor-type-icon]]
            [clojure.string :as str]
            ["@material-ui/core/Box" :default Box]
            ["@material-ui/core/Paper" :default Card]
            ["@material-ui/core/CardHeader" :default CardHeader]
            ["@material-ui/core/Typography" :default Typography]
            ["@material-ui/core/Grid" :default Grid]
            ["@material-ui/core/Tooltip" :default Tooltip]
            ["@material-ui/core/Button" :default Button]
            ["@material-ui/core/FormControl" :default FormControl]
            ["@material-ui/core/InputLabel" :default InputLabel]
            ["@material-ui/core/Select" :default Select]
            ["@material-ui/core/MenuItem" :default MenuItem]
            ["@material-ui/core/IconButton" :default IconButton]
            ["@material-ui/icons/DeleteTwoTone" :default DeleteIcon]
            ["@material-ui/icons/SaveTwoTone" :default SaveIcon]))

(rf/reg-event-db
 ::set-processor-edit-field
 (fn [db [_ field value]]
   (assoc-in db [:edit-processor-fields field] value)))

(rf/reg-sub
 ::updating-processor-errors
 (fn [db _]
   (get-in db [:request-errors :put-processor])))

(rf/reg-sub
 ::updating-processor
 (fn [db _]
   (get-in db [:loading :put-processor])))

(rf/reg-sub
 ::delete-processor-dialog-open
 (fn [db _]
   (:delete-processor-dialog-open db)))

(rf/reg-sub
 ::deleting-processor
 (fn [db _]
   (get-in db [:loading :delete-processor])))

(rf/reg-sub
 ::deleting-processor-errors
 (fn [db _]
   (get-in db [:request-errors :delete-processor])))

;;Editor fields
(rf/reg-sub
 ::edit-processor-type
 (fn [db _]
   (get-in db [:edit-processor-fields :processor-type])))

(rf/reg-event-db
 ::toggle-delete-processor-dialog
 (fn [db [_ _]]
   (-> db
       (assoc :delete-processor-dialog-open
              (not (:delete-processor-dialog-open db))))))

;; DELETE PROCESSOR
(defn confirm-delete-processor
  "Shows a confrmation dialog when delete a processor"
  [processor]
  (let [open (rf/subscribe [::delete-processor-dialog-open])
        is-deleting (rf/subscribe [::deleting-processor])
        errors (rf/subscribe [::deleting-processor-errors])]
    (tcom/confirm-dialog open {:question "Confirm Processor Deletion"
                               :text (str "Are you sure you wish to delete the Processor " (:name processor) "?")
                               :confirm-action (fn [_]
                                                 (rf/dispatch [::events/delete-processor
                                                               (:id processor)
                                                               (:flowId processor)])
                                                 (rf/dispatch [::toggle-delete-processor-dialog]))
                               :cancel-action #(rf/dispatch [::toggle-delete-processor-dialog])})))


(defn proc-styles [^js/Mui.Theme theme]
  (let [palette (js->clj (.. theme -palette) :keywordize-keys true)
        p-type (keyword (:type palette))]
    {:edit-processor-wrapper {}
     :delete-processor-wrapper {:float :right
                                :margin-top 0}
     :description-wrapper {:max-width 600
                           :margin-bottom 20
                           :overflow-wrap :break-word}
     :proc-wrapper {:min-height 400
                    ;:padding 10
                    :margin-top 10
                    }
     :save-action-button {:margin-right 10
                          :margin-top 5}
     :card-detail-header {}}))

(defn common-fields
  "Displays fields common to all processors"
  [processor]
  (let []
    (style/let [classes proc-styles]
      [:> CardHeader {:avatar (r/as-element (processor-type-icon (:type @processor)))
                      :title (str (str/capitalize (:type @processor)) " Processor")
                      :titleTypographyProps {:variant :h6 :gutterBottom true}
                      ;:subheader "Cool stuff"
                      :action (r/as-element [:> Button {:size :medium
                                                        :class (:save-action-button classes)
                                                        :color :primary
                                                        :disableElevation true
                                                        :variant :contained
                                                        :startIcon (r/as-element [:> SaveIcon])} "Save"])}])))

(defn processor-editor
  "Main Component for editing processors"
  []
  (let [processor (rf/subscribe [::subs/current-processor])
        dataflow  (rf/subscribe [::subs/current-dataflow])
        delete-dialog? (rf/subscribe [::delete-processor-dialog-open])
        errors    (rf/subscribe [::updating-processor-errors])
        is-updating? (rf/subscribe [::updating-processor])]
    (style/let [classes proc-styles]
      [:<>
       (if @delete-dialog? (confirm-delete-processor @processor))
       [:> Box
        [tcom/breadcrumbs (list {:href "#/dataflows" :text "Dataflow Index"}
                                {:href (str "#/dataflows/" (:id @dataflow)) :text (:name @dataflow)}
                                {:href (str "#/processors/" (:id @processor)) :text (:name @processor)})]
        [:div {:class (:delete-processor-wrapper classes)}
         [:> Tooltip {:title "Delete this processor" :placement :left-end}
          [:> IconButton {:class (:delete-processor-button classes)
                          :color :secondary
                          :onClick #(rf/dispatch [::toggle-delete-processor-dialog])
                          :size :small}
           [:> DeleteIcon]]]]]
       [tcom/full-content-ui {:title (:name @processor)}
        (if (nil? @processor) [tcom/loading-backdrop])
        [:> Box {:class {:description-wrapper classes}}
         [:> Typography {:variant :subtitle1} (:description @processor)]]
        [:> Card {:class (:proc-wrapper classes)}
         (if (some? @processor)
           [common-fields processor])]]])))