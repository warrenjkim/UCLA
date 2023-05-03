from intbase import InterpreterBase as base
from objects import Field, Method

def parse_binary_operator(operator):
    lhs = operator[1]
    rhs = operator[2]
    
    # addition
    if operator[0] == '+':
        return add(lhs, rhs)

    # subtracttion
    elif operator[0] == '-':
        subtract(lhs, rhs)

    # multiplication
    elif operator[0] == '*':
        multiply(lhs, rhs)

    # division
    elif operator[0] == '/':
        divide(lhs, rhs)

def add(lhs, rhs):
    if isinstance(lhs, list):
        return add(parse_binary_operator(lhs), rhs)
        
    if isinstance(rhs, list):
        return add(lhs, parse_binary_operator(rhs))
    
    return str(eval(lhs) + eval(rhs))

def subtract(lhs, rhs):
    if isinstance(lhs, list):
        return subtract(parse_binary_operator(lhs), rhs)
        
    if isinstance(rhs, list):
        return subtract(lhs, parse_binary_operator(rhs))
    
    return str(eval(lhs) - eval(rhs))

def multiply(lhs, rhs):
    if isinstance(lhs, list):
        return multiply(parse_binary_operator(lhs), rhs)
        
    if isinstance(rhs, list):
        return multiply(lhs, parse_binary_operator(rhs))
    
    return str(eval(lhs) * eval(rhs))

def divide(lhs, rhs):
    if isinstance(lhs, list):
        return divide(parse_binary_operator(lhs), rhs)
        
    if isinstance(rhs, list):
        return divide(lhs, parse_binary_operator(rhs))
    
    return str(eval(lhs) / eval(rhs))
