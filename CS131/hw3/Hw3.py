# 1
from functools import reduce
def convert_to_decimal(bits):
    exponents = range(len(bits)-1, -1, -1)
    nums = [bit * 2**exponent for bit, exponent in zip(bits, exponents)]
    return reduce(lambda acc, num: acc + num, nums)





# 2a
def parse_csv(lines):
    return [(word, int(num)) for word, num in (item.split(", ") for item in lines)]





# 2b
def unique_characters(sentence):
    return {char for char in sentence}





# 2c
def squares_dict(lower_bound, upper_bound):
    return {num: num**2 for num in range(lower_bound, upper_bound + 1)}





# 3
def strip_characters(sentence, chars_to_remove):
    return "".join(char if char not in chars_to_remove else "" for char in sentence)





# 4
# box's value attribute was mutated because Python passes the object reference of box into the function, and so
# we are mutating box's value attribute (in the Box class). This is in contrast to when num's object reference
# gets passed into the function. the local variable num inside the function points to another object after
# multiplying by 5.





# 5
# Python uses duck typing to determine if a function is valid. Foo and str both have a __len__(self) function
# defined, whereas int does not. Therefore, when calling len(5), it tries to find a __len__(self) in the int
# type, but won't find one, returning an error.





# 6(a)
class Duck:
    def __init__(self):
        pass

class question_six_a:
    def __init__(self):
        pass

    def quack():
        return
    
def is_duck_a(duck):
    try:
        duck.quack()
        return True
    except:
        return False

def is_duck_b(duck):
    return isinstance(duck, Duck)





# 6(b)
class question_six_b(Duck):
    def __init__():
        super().__init__()





# 6(c)
# is_duck_a is more pythonic since it uses duck typing and the EAFP coding style.





# 7(a)
# def largest_sum(nums, k):
#     if k < 0 or k > len(nums):
#         raise ValueError
#     elif k == 0:
#         return 0

#     max_sum = None
#     for i in range(len(nums) - k + 1):
#         sum = 0
#         for num in nums[:k]:
#             sum += num
#         if sum is not None:
#             max_sum = sum
#     return max_sum





# 7(b)
def largest_sum(nums, k):
    if k < 0 or len(nums) < k:
        raise ValueError
    elif k == 0:
        return 0

    sum = 0
    for num in nums[:k]:
        sum += num

    max_sum = sum
    for i in range(0, len(nums) - k - 1):
        sum -= nums[i]
        sum += nums[i + k]
        max_sum = max(sum, max_sum)

    return max_sum





# 8(a)
class Event:
    def __init__(self, start_time, end_time):
        if end_time <= start_time:
            raise ValueError
        
        self.start_time = start_time
        self.end_time = end_time





# 8(b)
class Calendar:
    def __init__(self):
        self.__events = []

    def get_events(self):
        return self.__events

    def add_event(self, event):
        if type(event) is not Event:
            raise TypeError
        self.__events.append(event)





# 8(c)
class AdventCalendar(Calendar):
    def __init__(self, year):
        super().__init__()
        self.year = year

advent_calendar = AdventCalendar(2022)

# Running the code as-is will raise an AttributeError since we never called the super class's constructor.
# To fix this code, we can add the super constructor in the __init__().





# 9
def outer(outer_var):
    def inner():
        print(outer_var)

    inner()

# Python supports closures. In the example above, we have an inner function that uses the outer function's
# parameters.





# 10(a)
# C-Lang would be faster since compiled languages translate directly into btyecode whereas interpreted languages
# do not. Therefore, there is more overhead for interpreted languages than there are for compiled languages.





# 10(b)
# Jamie would get the server running faster since she is writing in an interpreted language which executes via
# an interpreter while Tim is writing in a compiled language, so he would have to compile into an executable
# before being able to get the server running.





# 10(c)
# Connie will be able to execute Jamie's script since there is a native copy of the I-Lang interpreter on her
# computer. Tim's executable will not be able to run on Connie's computer since the executable is not portable
# as it is compiled for Intel chips.





# 11
# numpy is written in C++ whereas our hand-coded function is written in Python. Python has more overhead than
# C so it is not optimal for computationally intensive operations such as matrix multiplication. However, C has
# very little overhead compared to Python. Moreover, since C is a compiled language, it has been converted into
# machine code. Therefore, the C version of the function will run faster than the Python implementation of the
# same function





# 12(a)
class Joker:
    joke = "I dressed as a UDP packet at the party. Nobody got it."
    def change_joke(self):
        print(f'self.joke = {self.joke}')
        print(f'Joker.joke = {Joker.joke}')
        Joker.joke = "How does an OOP coder get wealthy? Inheritance."
        self.joke = "Why do Java coders wear glasses? They can't C#."
        print(f'self.joke = {self.joke}')
        print(f'Joker.joke = {Joker.joke}')

# j = Joker()
# print(f'j.joke = {j.joke}')
# print(f'Joker.joke = {Joker.joke}')
# j.change_joke()
# print(f'j.joke = {j.joke}')
# print(f'Joker.joke = {Joker.joke}')


# The program should print out:
# j.joke = I dressed as a UDP packet at the party Nobody got it.
# Joker.joke = I dressed as a UDP packet at the party Nobody got it.
# self.joke = I dressed as a UDP packet at the party Nobody got it.
# Joker.joke = I dressed as a UDP packet at the party Nobody got it.
# self.joke = Why do Java coders wear glasses? They can't C#.
# Joker.joke = How does an OOP coder get wealthy? Inheritance.
# j.joke = Why do Java coders wear glasses? They can't C#.
# Joker.joke = How does an OOP coder get wealthy? Inheritance.


# 12(b)
# The program actually prints:
# j.joke = I dressed as a UDP packet at the party. Nobody got it.
# Joker.joke = I dressed as a UDP packet at the party. Nobody got it.
# self.joke = I dressed as a UDP packet at the party. Nobody got it.
# Joker.joke = I dressed as a UDP packet at the party. Nobody got it.
# self.joke = Why do Java coders wear glasses? They can't C#.
# Joker.joke = How does an OOP coder get wealthy? Inheritance.
# j.joke = Why do Java coders wear glasses? They can't C#.
# Joker.joke = How does an OOP coder get wealthy? Inheritance.
#
# this is because class variables and member variables (self) are different
        
def test(flag):
    print("Passed" if flag else "Failed")

print("TESTS")
print("***** Question 1 *****")
test(convert_to_decimal([1, 0, 1, 1, 0]) == 22)
test(convert_to_decimal([1, 0, 1]) == 5)

print("\n***** Question 2(a) *****")
test(parse_csv(["apple, 8", "pear, 24", "gooseberry, -2"]) == [("apple", 8), ("pear", 24), ("gooseberry", -2)])

print("\n***** Question 2(b) *****")
test(unique_characters("happy") == {"h", "a", "p", "y"})

print("\n***** Question 2(c) *****")
test(squares_dict(1, 5) == {1: 1, 2: 4, 3: 9, 4: 16, 5: 25})

print("\n***** Question 3 *****")
test(strip_characters("Hello, world!", {"o", "h", "l"}) == "He, wrd!")

print("\n***** Question 7 *****")
test(largest_sum([3,5,6,2,3,4,5], 3) == 14)
test(largest_sum([10,-8,2,6,-1,2], 4) == 10)

print("\n***** Question 8 *****")
event = Event(10, 20)
if event.start_time == 10 and event.end_time == 20:
    print("Passed")
else:
    print("Failed")
try:
    invalid_event = Event(20, 10)
    print("Failed")
except ValueError:
    print("Passed")

calendar = Calendar()
test(calendar.get_events() == [])
calendar.add_event(Event(10, 20))
test(calendar.get_events()[0].start_time == 10)
try:
    calendar.add_event("not an event")
except TypeError:
    print("Passed")

test(advent_calendar.get_events() == [])

print("\n***** Question 9 *****")
outer(10)
