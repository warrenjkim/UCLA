-- Homework 2

-- 1 (a): map a list: for all x in list, x * factor
scale_nums :: [Integer] -> Integer -> [Integer]
scale_nums list factor =
  map (\x -> x * factor) list


-- 1 (b): filter a list: for all x in list, x is odd
only_odds :: [[Integer]] -> [[Integer]]
only_odds matrix =
  filter (\x -> all odd x) matrix


-- 1 (c): largest function from HW 1
largest :: String -> String -> String
largest first second =
  if length first >= length second then
    first
  else
    second

-- 1 (c): fold a list: find the largest element in the list
largest_in_list :: [String] -> String
largest_in_list (x:xs) =
  foldl largest x xs


-- 2 (a): recursion: count the number of times f is true over a list
count_if :: (a -> Bool) -> [a] -> Int
count_if f [] = 0
count_if f (x:xs) =
  if (f x) then
    (count_if f xs) + 1
  else
    count_if f xs


-- 2 (b): filter: 2(a) but with a filter
count_if_with_filter :: (a -> Bool) -> [a] -> Int
count_if_with_filter f list =
  length (filter f list)


-- 2 (c) foldl/r: 2(a) but with foldl/r
count_if_with_fold :: (a -> Bool) -> [a] -> Int
count_if_with_fold f list =
  
