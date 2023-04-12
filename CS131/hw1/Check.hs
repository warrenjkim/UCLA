module Check where

import Hw1
test_1 :: String -> String -> String -> String
test_1 str1 str2 expected = do
  let output = Hw1.largest str1 str2
  if output == expected then
    "Passed"
  else
    "Failed: Expected " ++ expected ++ " but " ++ output ++ " was returned"

test_3a :: Integer -> [Integer] -> String
test_3a n expected = do
  let output = Hw1.all_factors n
  if output == expected then
    "Passed"
  else
    "Failed: Expected " ++ (show expected) ++ " but " ++ (show [x | x <- output]) ++ " was returned"

test_3b :: [Integer] -> String
test_3b expected = do
  let output = take 4 Hw1.perfect_numbers
  if output == expected then
    "Passed"
  else
    "Failed: Expected " ++ (show expected) ++ " but " ++ (show [x | x <- output]) ++ " was returned"

test_4a :: Integer -> Bool -> String
test_4a n expected = do
  let output = Hw1.is_odd n
  if output == expected then
    "Passed"
  else
    "Failed: Expected " ++ (if expected then "True" else "False") ++ " but " ++ (if output then "True" else "False") ++ " was returned"

test_4b :: Integer -> Bool -> String
test_4b n expected = do
  let output = Hw1.is_even n
  if output == expected then
    "Passed"
  else
    "Failed: Expected " ++ (if expected then "True" else "False") ++ " but " ++ (if output then "True" else "False") ++ " was returned"

test_5 :: [Integer] -> [Integer] -> Integer -> String
test_5 a_1 a_2 expected = do
  let output = Hw1.count_occurrences a_1 a_2
  if output == expected then
    "Passed"
    else
    "Failed: Expected " ++ show expected ++ " but " ++ show output ++ " was returned"


check = do
  putStrLn("Testing Question 1")
  putStrLn(test_1 "cat" "banana" "banana")
  putStrLn(test_1 "Carey" "rocks" "Carey")
  putStrLn("")
  putStrLn("Testing Question 3")
  putStrLn(test_3a 1 [1])
  putStrLn(test_3a 42 [1, 2, 3, 6, 7, 14, 21, 42])
  putStrLn(test_3b [6, 28, 496, 8128])
  putStrLn("")
  putStrLn("Testing Question 4")
  putStrLn(test_4a 8 False)
  putStrLn(test_4b 8 True)
  putStrLn("")
  putStrLn("Testing Question 5")
  putStrLn(test_5 a_1 a_2 1)
  putStrLn(test_5 b_1 a_2 2)
  putStrLn(test_5 c_1 a_2 0)
  putStrLn(test_5 d_1 a_2 3)
  putStrLn(test_5 [] a_2 1)
  putStrLn(test_5 [] [] 1)
  putStrLn(test_5 [5] [] 0)

  where
    a_1 = [10, 20, 40]
    b_1 = [10, 40, 30]
    c_1 = [20, 10, 40]
    d_1 = [50, 40, 30]
    a_2 = [10, 50, 40, 20, 50, 40, 30]
