module Hw1 where

-- Returns the larger of the two strings (first string if equal)
largest str1 str2 =
  if (length str2) <= (length str1) then
    str1
  else
    str2
    

-- Returns all factors of n (inclusive)
all_factors n =
  [x | x <- [1..n], n `mod` x == 0]


-- Returns the set with the following property: for all factors 'factors' of n, sum the first n - 1 elements,
-- appending to the list iff the sum is equal to n
perfect_numbers =
  [sum (init factors) | factors <- [all_factors n | n <- [1..]], sum (init factors) == factors !! ((length factors) - 1)]


-- Takes in an integer and returns a Bool s.t. the int is either odd or even
-- note: even numbers take the form n = 2k
-- note: odd numbers take the form n = 2k + 1

-- (1)
-- is_odd n =
--   if n == 0 then
--     False
--   else if n < 0 then
--     True
--   else 
--     is_odd (n - 2)
    
-- is_even n =
--   if n == 0 then
--     True
--   else if n < 0 then
--     False
--   else
--     is_even (n - 2)


-- (2)
-- is_odd n
--   | n == 0    = False
--   | n < 0     = True
--   | otherwise = is_odd (n - 2)

-- is_even n
--   | n == 0    = True
--   | n < 0     = False
--   | otherwise = is_even (n - 2)


-- (3)
is_odd 0 = False
is_odd 1 = True
is_odd n = is_odd (n - 2)

is_even 0 = True
is_even 1 = False
is_even n = is_even (n - 2)


-- count occurrences will return the number of ways all elements of a_1 appear in a_2
count_occurrences [] a_2 = 1
count_occurrences a_2 [] = 0
count_occurrences a_1 a_2 =
  if (head a_1) == (head a_2) then
    count_occurrences a_1 (tail a_2) + count_occurrences (tail a_1) (tail a_2)
  else
    count_occurrences a_1 (tail a_2)
