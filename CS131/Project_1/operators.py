from intbase import InterpreterBase as base
from intbase import ErrorType as errno
from objects import Field, Method

def parse_binary_operator(operator):
    lhs = operator[1]
    rhs = operator[2]

    ##### arithmetic operators #####
    
    # addition
    if operator[0] == '+':
        return add(lhs, rhs)

    # subtracttion
    elif operator[0] == '-':
        return subtract(lhs, rhs)

    # multiplication
    elif operator[0] == '*':
        return multiply(lhs, rhs)

    # division
    elif operator[0] == '/':
        return divide(lhs, rhs)


    ##### control flow #####

    # less than
    if operator[0] == "<":
        return less(lhs, rhs)
    
    # less than or equal
    elif operator[0] == "<=":
        return less(lhs, rhs) or eq(lhs, rhs)
    
    # equality
    elif operator[0] == "==":
        return equal(lhs, rhs)
    
    # greater than
    elif operator[0] == ">":
        return less(rhs, lhs)
    # greater than or equal
    
    elif operator[0] == ">=":
        return less(rhs, lhs) or eq(lhs, rhs)


# add
def add(lhs, rhs):
    if isinstance(lhs, list):
        return add(parse_binary_operator(lhs), rhs)
        
    if isinstance(rhs, list):
        return add(lhs, parse_binary_operator(rhs))

    return str(eval(lhs) + eval(rhs))


# subtract
def subtract(lhs, rhs):
    if isinstance(lhs, list):
        return subtract(parse_binary_operator(lhs), rhs)
        
    if isinstance(rhs, list):
        return subtract(lhs, parse_binary_operator(rhs))

    return str(eval(lhs) - eval(rhs))


# multiply
def multiply(lhs, rhs):
    if isinstance(lhs, list):
        return multiply(parse_binary_operator(lhs), rhs)
        
    if isinstance(rhs, list):
        return multiply(lhs, parse_binary_operator(rhs))

    return str(eval(lhs) * eval(rhs))


# divide
def divide(lhs, rhs):
    if isinstance(lhs, list):
        return divide(parse_binary_operator(lhs), rhs)
        
    if isinstance(rhs, list):
        return divide(lhs, parse_binary_operator(rhs))

    
    return str(eval(lhs) / eval(rhs))
    

# less than
def less(lhs, rhs):
    return eval(lhs) < eval(rhs)

# equal to
def equal(lhs, rhs):
    return eval(lhs) == eval(rhs)
