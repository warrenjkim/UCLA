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
-- whereas partial applications allow for fixed assignments to arguments on those decomposed functions.

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

-- 4 (a) No variables are captured in (1)

-- 4 (b) The variable b is captured in (2)

-- 4 (c) The variables c, d are captured in (3)

-- 4 (d) a = 4, b = 5, c = 4, d = 5, e = 6, f = 7. Only b and e are used in f's implementation. This is because in (1), a = 4 is never used since the lambda 
-- takes in a parameter (in the form \a -> ...). (2) uses b = 5 to define d = b. In (3), e = 6 is used by not f = 7.


-- 5
-- Haskell closures are first-class citizens. Closures can capture any free variable in scope, whereas function pointers cannot, since they are just a pointer
-- to the memory address of the function. So, they are limited to the parameters that are passed into the function. In contrast, since Haskell is immutable, 
-- closures will contain a copy of the variable when capturing free variables in scope.

-- 6 (a)
data InstagramUser = Influencer | Normie

-- -- 6 (b)
lit_collab :: InstagramUser -> InstagramUser -> Bool
lit_collab Influencer Influencer = True
list_collab _ _ = False

-- -- 6 (c)
data InstagramUser = Influencer [String] | Normie

-- -- 6 (d)
is_sponsor :: InstagramUser -> String -> Bool
is_sponsor Normie _ = False
is_sponsor (InstagramUser sponsors) sponsor = sponsor `elem` sponsors

-- 6 (e)
data InstagramUser = Influencer [String] [InstagramUser] | Normie

-- 6 (f)
count_influencers :: InstagramUser -> Integer
count_influencers Normie = 0
count_influencers (Influencer _ followers) =
  foldl count 0 followers

  where
    count curr (Influencer _ _) = curr + 1
    count curr Normie = 0
-- 6 (g)
-- Custom value constructors are functions that take in properties and return that custom data type


-- 7 (a)
data LinkedList = EmptyList | ListNode Integer LinkedList
  deriving Show

ll_contains :: LinkedList -> Integer -> Bool
ll_contains (EmptyList) _ = False
ll_contains (ListNode node_val next) val =
  if node_val == val then
    True
  else
    ll_contains next val

-- 7 (b)
ll_insert :: Integer -> Integer -> LinkedList -> LinkedList
-- we take in an integer as our first parameter since we need to know the index we want to insert.
-- we take in an integer as our second parameter since it's the value we want to insert into the list.
-- we take in a LinkedList as our third parameter since we need to have the list we want to insert into.
-- we return a new LinkedList with the inserted value. We need to do this since data is immutable in Haskell.

-- 7 (c)
ll_insert _ val EmptyList = ListNode val EmptyList
ll_insert index val (ListNode node_val next) =
  if index <= 0 then
    ListNode val (ListNode node_val next)
  else
    ListNode node_val (ll_insert val (index - 1) next)


-- 8 (a)
{-
int longestRun(std::vector<bool>& arr) {
    int max_so_far = 0;
    int curr_max = 0;

    for(const auto& x : arr) {
        if(!x)
            curr_max = 0;
        else
            curr_max++;
        max_so_far = max_so_far < curr_max ? curr_max : max_so_far;
    }

    return max_so_far;
}
-}

-- 8 (b)
longest_run :: [Bool] -> Integer
longest_run list =
  helper 0 0 list

  where
    helper max_so_far curr_max [] = max max_so_far curr_max
    helper max_so_far curr_max (x:xs) =
      if x then
        helper max_so_far (curr_max + 1) xs
      else
        helper (max max_so_far curr_max) 0 xs

-- 8 (c)
{- 
unsigned maxValue(Tree* root) {
    unsigned max = 0;
    std::queue<Tree*> q;
    q.push(root);
    
    while(!q.empty()) {
        Tree* curr = q.front();
        q.pop();
        
        if(!curr)
            continue;
        
        unsigned curr_value = curr->value;
        std::vector<Tree*> children = curr->children;
        
        if(curr_value > max)
            max = curr_value;
            
        for(const auto& child : children)
            q.push(child);
    }
    
    return max;
 }
-}

-- 8 (d)
data Tree = Empty | Node Integer [Tree]
  deriving Show

max_tree_value :: Tree -> Integer
max_tree_value Empty = 0
max_tree_value (Node val []) = val
max_tree_value (Node val tree) =
  max val (helper tree)

  where
    helper [] = 0
    helper (x:xs) = max (max_tree_value x) (helper xs)
  

-- 9
fibonacci :: Int -> [Int]
fibonacci n =
  if n < 0 then
    []
  else  
    [1, 1] ++ (helper 1 1 (n - 1) [])

  where
    helper _ _ 0 list = list
    helper _ _ 1 list = list
    helper pprev prev n list =
      (prev + pprev) : (helper prev (prev + pprev) (n - 1) list)


-- 10
data Event = Travel Integer | Fight Integer | Heal Integer
  deriving Show

-- helper function: determines if we are dead
dead :: Integer -> Bool
dead health = health <= 0

-- helper function: determines if we are in defensive mode
defensive_mode :: Integer -> Bool
defensive_mode health = health < 40

-- helper function: handles the travel event
  -- * if we are in defensive mode, do not heal
  -- * else, we heal for (int) 1/4 of the distance traveled
travel_handler :: Integer -> Integer -> Integer
travel_handler distance health =
  if (defensive_mode health) then
    health
  else
    heal_handler (distance `div` 4) health

-- helper function: handles the fight event
  -- * if we are in defensive mode, take half dmg (int)
  -- * else, we do health - loss
fight_handler :: Integer -> Integer -> Integer
fight_handler loss health =
  if (defensive_mode health) then
    heal_handler (-loss `div` 2) health
  else
    heal_handler (-loss) health

-- helper function: handles the heal event
  -- * we take the minimum of the healed amount and 100 since we can never exceed 100 hp
heal_handler :: Integer -> Integer -> Integer
heal_handler hp health =
  min (health + hp) 100


-- helper function: implements fold with a health check
  -- * if we are dead, we return -1
  -- * else, we continue folding
fold :: (Integer -> Event -> Integer) -> Integer -> [Event] -> Integer
fold f accum [] = if (dead accum) then -1 else accum
fold f accum (x:xs) =
  if (dead accum) then
    -1
  else
    fold f (f accum x) xs

-- game function: plays the game
super_giuseppe :: [Event] -> Integer
super_giuseppe events = 
  fold helper 100 events

  where
    -- helper function: determines which helper function we call
    helper health (Travel distance) = travel_handler distance health
    helper health (Fight loss) = fight_handler loss health
    helper health (Heal hp) = heal_handler hp health
