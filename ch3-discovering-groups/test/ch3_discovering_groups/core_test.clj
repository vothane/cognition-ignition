(ns ch3-discovering-groups.core-test
  (:require [clojure.test :refer :all]
            [ch3-discovering-groups.core :refer :all]))

(def v1
  [0.0 1.0 0.0 0.0 3.0 3.0 0.0 0.0 3.0 0.0 6.0 0.0 1.0 0.0 4.0 3.0 0.0 0.0 0.0 0.0 0.0 4.0 0.0])
(def v2
  [0.0 2.0 1.0 0.0 6.0 2.0 1.0 0.0 4.0 5.0 25.0 0.0 0.0 0.0 6.0 12.0 4.0 2.0 1.0 4.0 0.0 3.0 0.0])

(def k-data [[1.0 1.0] [1.5 2.0] [3.0 4.0] [5.0 7.0] [3.5 5.0] [4.5 5.0] [3.5 4.5]])

(deftest pearson-test
  (testing "Pearson score"
    (is (= (pearson v1 v2) 0.25004925261947253))))

(deftest make-ranges-test
  (testing "making ranges"
    (is (= (make-ranges k-data) 
           '((1.0 5.0) (1.0 7.0))))))

(deftest make-kclusters-test
  (testing "making kclusters"
    (is (= (make-kclusters 2 k-data) 
           '((1.9414881374080548 6.977366215048392) (1.4885417715569864 5.5302676090029905))))))

(deftest find-closet-centroids-test
  (testing "finding closet centroids"
    (is (= (find-closet-centroids '((2.1703800354942318 4.944804329369732) (2.0361218298131245 4.915444634854198) 
                                   (1.2161059075686835 4.510667599602532) (3.4388029068605683 5.6953197662282005)) 
                                  k-data) 
           '(0 1 1 1 0 3 2)))))

(deftest group-by-cluster-test
  (testing "grouping by cluster"
    (is (= (group-by-cluster '(0 1 1 1 0 3 2) k-data) 
           {0 ['(0 [1.0 1.0]) '(0 [3.5 5.0])] 1 ['(1 [1.5 2.0]) '(1 [3.0 4.0]) '(1 [5.0 7.0])] 3 ['(3 [4.5 5.0])] 2 ['(2 [3.5 4.5])]}))))

(deftest mov-avg-test
  (testing "calculating moving average of one centroid"
    (is (= (mov-avg 3 {0 ['(0 [1.0 1.0]) '(0 [5.0 7.0]) '(0 [3.5 5.0]) '(0 [4.5 5.0])] 3 ['(3 [1.5 2.0]) '(3 [3.0 4.0]) '(3 [3.5 4.5])]}) 
           '(2.6666666666666665 3.5)))))

(deftest mov-avgs-test
  (testing "calculating moving averages of all centroids"
    (is (= (mov-avgs {0 ['(0 [1.0 1.0]) '(0 [5.0 7.0]) '(0 [3.5 5.0]) '(0 [4.5 5.0])] 3 ['(3 [1.5 2.0]) '(3 [3.0 4.0]) '(3 [3.5 4.5])]}
                     '((1.4403700322645108 6.840619899668888) (2.566189471677537 3.5252066495835264) 
                       (2.1364933187471453 4.494755136224027) (1.8956229375996099 3.308222343494289))) 
           '((3.5 4.5) (2.566189471677537 3.5252066495835264) (2.1364933187471453 4.494755136224027) (2.6666666666666665 3.5))))))

(deftest kmeans-test
  (testing "hackery kmeans test major refactor later"
    (let [rows [[1.0 1.0] [1.5 2.0] [3.0 4.0] [5.0 7.0] [3.5 5.0] [4.5 5.0] [3.5 4.5]]
          kc1  '((4.963488708361788 3.015060941045377) (4.551427069718808 4.1892221931742535) (2.1511313835837096 6.371573143944422) (4.6161749317250855 6.6981062862404475))        
          bm1  (find-closet-centroids kc1 rows)]
      (is (= (get (last (take 99 (iterate (kmean rows) [kc1 bm1]))) 1) '(0 2 2 2 2 2 2))))))