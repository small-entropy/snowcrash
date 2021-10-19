(ns utils.nested-documents)

(defn filter-by-id
  "Function for filter nested documents fields by id"
  [collection id]
  (filter (fn [current-property]
            (= id (str (get current-property :_id nil)))) collection))

(defn filter-by-key
  "Function for filter nested documents fields by key"
  [collection key]
  (filter (fn [current-property]
            (= key (get current-property :key nil))) collection))

(defn filter-by-name
  "Function for filter nested documents fields by name"
  [collection name]
  (filter (fn [item]
            (= name (get item :name nil))) collection))

(defn exist-by-name?
  "Function for check exist nested document in collection by id"
  [collection name]
  (let [founded (filter-by-name collection name)]
    (if (= (count founded) 0) false true)))

(defn exist-by-id?
  "Function for check exist nested document in collection by id"
  [collection id]
  (let [founded (filter-by-id collection id)]
    (if (= (count founded) 0) false true)))

(defn exist-by-key?
  "Function for check exist nested document in collection by keu"
  [properties key]
  (let [founded (filter-by-key properties key)]
    (if (= (count founded) 0) false true)))

(defn update-list
  "Function for update list of nested documents"
  [collection property-id key value]
  (map (fn [item]
         (if (= (str (get item :_id nil)) property-id)
           {:_id (get item :_id nil) :key key :value value} item)) collection))

(defn delete-from-list
  [collection property-id]
  (filter (fn [current-property]
            (not= property-id (str (get current-property :_id nil)))) collection))

(defn get-property
  "Function for get nested document for list by id"
  [collection property-id message error-info]
  (let [founded (filter-by-id collection property-id)]
    (if (= (count founded) 0)
      (throw (ex-info message error-info))
      (first founded))))
