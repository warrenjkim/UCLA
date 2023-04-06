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


check = do
  print("Testing Question 1")
  print("")
  print(test_1 "cat" "banana" "banana")
  print(test_1 "Carey" "rocks" "Carey")
  print("")
  print("Testing Question 3")
  print(test_3a 1 [1])
  print((test_3a 42 [1, 2, 3, 6, 7, 14, 21, 42]))
  print(test_3b [6, 28, 496, 8128])
  print("")
  print("Testing Question 4")
  print(test_4a 8 False)
  print(test_4b 8 True)
