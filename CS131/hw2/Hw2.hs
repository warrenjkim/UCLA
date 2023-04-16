module Hw2 where
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
count_if_with_fold f (x:xs) =
  foldl (+) (if f x then 1 else 0) (map (\d -> if f d then 1 else 0) xs)


-- 3 (a) currying vs. partial application
-- Currying is the process of representing a function as a group of individual, nested, functions that each take in one parameter and
-- return its nested function. Partial application is the process of fixing a certain parameter(s) of a function and calling the "partial"
-- or remainder of the function (with the fixed arguments filling in for the missing parameter(s)). Currying decomposes a function,
-- whereas partial applications allow for fixed assignments to arguments.

-- 3 (b) function equivalence
-- (i)  (a -> b) -> c
-- (ii) a -> (b -> c)

-- a -> b -> c is equivalent to (ii) only. This is because Haskell will implicitly curry functions with more than one parameter. Recall
-- that currying is right associative. Therefore, we have that a -> b -> c <==> a -> (b -> c). A similar argument is made to show that
-- (i) is not equivalent to a -> b -> c.

-- 3 (c) currying
-- foo :: Integer -> Integer -> Integer -> (Integer -> a) -> [a]
-- foo x y z t = map t [x, x + z..y]

-- Rewritten as a chain of lambda expressions that take in **one** variable (curried version of foo):
foo :: Integer -> (Integer -> (Integer -> ((Integer -> a) -> [a])))
foo = \x -> (\y -> (\z -> (\t -> map t [x, x + z..y])))

-- 4 Consider the following Haskell function:
-- f a b =
-- let c = \a -> a  -- (1)
--     d = \c -> b  -- (2)
-- in \e f -> c d e -- (3)

-- 4 (a) The variables a and b are captured in (1)

-- 4 (b) The variables 
