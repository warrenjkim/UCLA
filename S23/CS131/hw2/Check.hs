module Check where

import Hw2

test_1a :: [Integer] -> Integer -> [Integer] -> String
test_1a list factor expected =
  if output == expected then
    "Passed"
  else
    "Failed: Expected " ++ show expected ++ " but " ++ show output ++ " was returned"

  where
    output = Hw2.scale_nums list factor


test_1b :: [[Integer]] -> [[Integer]] -> String
test_1b matrix expected =
  if output == expected then
    "Passed"
  else
    "Failed: Expected " ++ show expected ++ " but " ++ show output ++ " was returned"

  where
    output = Hw2.only_odds matrix


test_1c :: [String] -> String -> String
test_1c list expected =
  if output == expected then
    "Passed"
  else
    "Failed: Expected " ++ show expected ++ " but " ++ show output ++ " was returned"

  where
    output = Hw2.largest_in_list list


test_2a :: (a -> Bool) -> [a] -> Int -> String
test_2a f list expected =
  if output == expected then
    "Passed"
  else
    "Failed: Expected " ++ show expected ++ " but " ++ show output ++ " was returned"

  where
    output = Hw2.count_if f list


test_2b :: (a -> Bool) -> [a] -> Int -> String
test_2b f list expected =
  if output == expected then
    "Passed"
  else
    "Failed: Expected " ++ show expected ++ " but " ++ show output ++ " was returned"

  where
    output = Hw2.count_if_with_filter f list
    

test_2c :: (a -> Bool) -> [a] -> Int -> String
test_2c f list expected =
  if output == expected then
    "Passed"
  else
    "Failed: Expected " ++ show expected ++ " but " ++ show output ++ " was returned"

  where
    output = Hw2.count_if_with_fold f list

check :: IO ()
check = do
  putStrLn("Testing Question 1")
  putStrLn("(a): " ++ test_1a [1, 4, 9, 10] 3 [3, 12, 27, 30])
  putStrLn("(b): " ++ test_1b [[1, 2, 3], [3, 5], [], [8, 10], [11]] [[3, 5], [], [11]])
  putStrLn("(c): " ++ test_1c ["how", "now", "brown", "cow"] "brown")
  putStrLn("     " ++ test_1c ["cat", "mat", "bat"] "cat")
  putStrLn("")
  putStrLn("Testing Question 2")
  putStrLn("(a): " ++ test_2a (\x -> mod x 2 == 0) [2, 4, 6, 8, 9] 4)
  putStrLn("     " ++ test_2a (\x -> length x > 2) ["a", "ab", "abc"] 1)
  putStrLn("(b): " ++ test_2b (\x -> mod x 2 == 0) [2, 4, 6, 8, 9] 4)
  putStrLn("     " ++ test_2b (\x -> length x > 2) ["a", "ab", "abc"] 1)
  putStrLn("(c): " ++ test_2c (\x -> mod x 2 == 0) [2, 4, 6, 8, 9] 4)
  putStrLn("     " ++ test_2c (\x -> length x > 2) ["a", "ab", "abc"] 1)
  
