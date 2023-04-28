merge other [] = other
merge [] other = other

merge (x:xs) (y:ys) =
  if x <= y then
    x : merge (xs) (y:ys)
  else
    y : merge (x:xs) (ys)

split list =
  ((take pivot list), (drop pivot list))
  where pivot = (length list) `div` 2

mergesort [] = []
mergesort [x] = [x]

mergesort list =
  merge (mergesort lhs) (mergesort rhs)
  where (lhs, rhs) = (split list)
