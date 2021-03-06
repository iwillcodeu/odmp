;; Copyright 2020 James Adam and the Open Data Management Platform contributors.

;; Licensed under the Apache License, Version 2.0 (the "License");
;; you may not use this file except in compliance with the License.
;; You may obtain a copy of the License at

;; http://www.apache.org/licenses/LICENSE-2.0

;; Unless required by applicable law or agreed to in writing, software
;; distributed under the License is distributed on an "AS IS" BASIS,
;; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;; See the License for the specific language governing permissions and
;; limitations under the License.

(ns odmp-ui.views.processor.collect-fields
  (:require
   [re-frame.core :as rf]
   [reagent.core :as r]
   [odmp-ui.views.processor.events :as proc-events]
   [odmp-ui.views.processor.subs :as proc-subs]
   [odmp-ui.views.processor.styles :refer [proc-styles]]
   [odmp-ui.subs :as subs]
   [odmp-ui.events :as events]
   [odmp-ui.util.ui :refer [ignore-return]]
   [odmp-ui.util.styles :as style]
   ["@material-ui/core/Box" :default Box]
   ["@material-ui/core/Grid" :default Grid]
   ["@material-ui/core/FormGroup" :default FormGroup]
   ["@material-ui/core/FormControl" :default FormControl]
   ["@material-ui/core/InputLabel" :default InputLabel]
   ["@material-ui/core/Select" :default Select]
   ["@material-ui/core/Typography" :default Typography]
   ["@material-ui/core/MenuItem" :default MenuItem]
   ["@material-ui/core/TextField" :default TextField]))

(defn folder-property-fields
  "Folder Destination properties"
  [processor]
  (let [location (:location @(rf/subscribe [::proc-subs/edit-properties]))
        loc-field-value (or location
                            (get-in @processor [:properties :location])
                            "")]
    [:> TextField {:margin :dense
                   :variant :filled
                   :required true
                   :fullWidth true
                   :label "Location"
                   :onKeyDown ignore-return
                   :type :text
                   :defaultValue loc-field-value
                   :onBlur #(rf/dispatch [::proc-events/set-processor-property :location (-> % .-target .-value)])}]))

(defn elastic-property-fields
  "Elastic Search destination properties"
  [processor]
  (let [index (:index @(rf/subscribe [::proc-subs/edit-properties]))
        index-field-value (or index
                              (get-in @processor [:properties :index])
                              "")]
    [:> TextField {:margin :dense
                   :variant :filled
                   :required true
                   :fullWidth true
                   :label "Index"
                   :onKeyDown ignore-return
                   :type :text
                   :defaultValue index-field-value
                   :onBlur #(rf/dispatch [::proc-events/set-processor-property :index (-> % .-target .-value)])}]))

(defn s3-property-fields
  "S3 Destination properties"
  [processor]
  (let [bucket    (:bucket @(rf/subscribe [::proc-subs/edit-properties]))
        key       (:key @(rf/subscribe [::proc-subs/edit-properties]))
        mime-type (:mimeType @(rf/subscribe [::proc-subs/edit-properties]))
        bucket-field-value (or bucket
                               (get-in @processor [:properties :bucket])
                               "")
        key-field-value (or key
                            (get-in @processor [:properties :key])
                            "")
        mime-field-value (or mime-type
                             (get-in @processor [:properties :mimeType])
                             "application/octet-stream")]
    [:<>
     [:> Grid {:container true :spacing 2}
      [:> Grid {:item true :xs 4}
       [:> TextField {:margin :dense
                      :variant :filled
                      :required true
                      :fullWidth true
                      :label "S3 Bucket"
                      :onKeyDown ignore-return
                      :type :text
                      :defaultValue bucket-field-value
                      :onBlur #(rf/dispatch [::proc-events/set-processor-property :bucket (-> % .-target .-value)])}]]
      [:> Grid {:item true :xs 8}
       [:> TextField {:margin :dense
                      :variant :filled
                      :required true
                      :fullWidth true
                      :label "S3 Key Prefix"
                      :onKeyDown ignore-return
                      :type :text
                      :defaultValue key-field-value
                      :onBlur #(rf/dispatch [::proc-events/set-processor-property :key (-> % .-target .-value)])}]]]
     [:> TextField {:margin :dense
                    :variant :filled
                    :fullWidth true
                    :style {:margin-bottom 15}
                    :label "MIME Type"
                    :onKeyDown ignore-return
                    :type :text
                    :defaultValue mime-field-value
                    :onBlur #(rf/dispatch [::proc-events/set-processor-property :mimeType (-> % .-target .-value)])}]]))

(defn collect-fields [processor]
  (let [collections (rf/subscribe [::subs/collections])
        dest-types (rf/subscribe [::subs/lookup-destination-types])
        collection (rf/subscribe [::proc-subs/edit-collect-collection])
        dest-type (rf/subscribe [::proc-subs/edit-collect-destination-type])
        location (rf/subscribe [::proc-subs/edit-collect-location])
        prefix   (rf/subscribe [::proc-subs/edit-collect-record-prefix])
        coll-field-value (or @collection
                             (get-in @processor [:properties :collection])
                             "")
        dest-type-field-value (or @dest-type
                                  (get-in @processor [:properties :type])
                                  "NONE")
        
        prefix-field-value (or @prefix 
                               (get-in @processor [:properties :prefix])
                               "")]
    (if (nil? @collections) (rf/dispatch [::events/fetch-collection-list]))
    (if (nil? @dest-types) (rf/dispatch [::events/lookup-destination-types]))
    (style/let [classes proc-styles]
      (if (and (some? @dest-types) (some? @collections))
        [:> Box {:style {:margin-top 10}}
         [:> Typography {:variant :subtitle2} "Choose a Collection"]
         [:> Grid {:container true :spacing 2}
          [:> Grid {:item true :xs 3}
           [:> FormControl {:variant :filled :required true :margin :dense :fullWidth true}
            [:> InputLabel {:id "INPUT_COLLECTION_LABEL"} "Collection"]
            [:> Select {:labelid "INPUT_COLLECTION_LABEL"
                        :value coll-field-value
                        :onChange #(rf/dispatch [::proc-events/set-processor-property :collection (-> % .-target .-value)])}
             [:> MenuItem {:value ""} [:em "NONE"]]
             (map (fn [c] ^{:key (str "INPUT_COLLECTION_" (:id c))}
                    [:> MenuItem {:value (:id c)} (:name c)]) @collections)]]]
          [:> Grid {:item true :xs 9}
           [:> TextField {:margin :dense
                          :variant :filled
                          :required false
                          :fullWidth true
                          :label "Record Prefix (record names will start with this)"
                          :onKeyDown ignore-return
                          :type :text
                          :defaultValue prefix-field-value
                          :onBlur #(rf/dispatch [::proc-events/set-processor-property :prefix (-> % .-target .-value)])}]]]
         [:> Typography {:variant :subtitle2 :style {:marginTop 10}} "Select a destination for the data"]
         [:> FormControl {:variant :filled :required true :margin :dense :fullWidth true}
          [:> InputLabel {:id "INPUT_DESTINATION_TYPE_LABEL"} "Destination Type"]
          [:> Select {:labelid "INPUT_DESTINATION_TYPE_LABEL"
                      :value dest-type-field-value
                      :onChange #(rf/dispatch [::proc-events/set-processor-property :type (-> % .-target .-value)])}
           (map (fn [dt] ^{:key (str "INPUT_TYPE_" dt)}
                  [:> MenuItem {:value dt} dt]) @dest-types)]]
         (case dest-type-field-value
           "FOLDER" (folder-property-fields processor)
           "S3" (s3-property-fields processor)
           "ELASTIC_SEARCH" (elastic-property-fields processor)
           "NONE" "")
         ]))))

