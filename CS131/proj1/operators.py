from intbase import InterpreterBase as base
from intbase import ErrorType as errno
from objects import Field, Method
from interpreterv1 import Interpreter

binary_operators = ['+', '-', '*', '/', '<', '<=', '==', '>', '>=']

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
        if lhs[0] in binary_operators:
            return add(parse_binary_operator(lhs), rhs)
        elif lhs[0] == base.CALL_DEF:
            return add(Interpreter.call_statement(lhs), rhs)
        
    if isinstance(rhs, list):
        if rhs[0] in binary_operators:
            return add(lhs, parse_binary_operator(rhs))
        elif rhs[0] == base.CALL_DEF:
            return add(lhs, Interpreter.call_statement(rhs))

    return str(eval(lhs) + eval(rhs))


# subtract
def subtract(lhs, rhs):
    if isinstance(lhs, list):
        if lhs[0] in binary_operators:
            return subtract(parse_binary_operator(lhs), rhs)
        elif lhs[0] == base.CALL_DEF:
            return subtract(Interpreter.call_statement(lhs), rhs)
        
    if isinstance(rhs, list):
        if rhs[0] in binary_operators:
            return subtract(lhs, parse_binary_operator(rhs))
        elif rhs[0] == base.CALL_DEF:
            return subtract(lhs, Interpreter.call_statement(rhs))

    return str(eval(lhs) - eval(rhs))


# multiply
def multiply(lhs, rhs):
    if isinstance(lhs, list):
        if lhs[0] in binary_operators:
            return multiply(parse_binary_operator(lhs), rhs)
        elif lhs[0] == base.CALL_DEF:
            return multiply(Interpreter.call_statement(lhs), rhs)
        
    if isinstance(rhs, list):
        if rhs[0] in binary_operators:
            return multiply(lhs, parse_binary_operator(rhs))
        elif rhs[0] == base.CALL_DEF:
            return multiply(lhs, Interpreter.call_statement(rhs))

    return str(eval(lhs) * eval(rhs))


# divide
def divide(lhs, rhs):
    if isinstance(lhs, list):
        if lhs[0] in binary_operators:
            return divide(parse_binary_operator(lhs), rhs)
        elif lhs[0] == base.CALL_DEF:
            return divide(Interpreter.call_statement(lhs), rhs)
        
    if isinstance(rhs, list):
        if rhs[0] in binary_operators:
            return divide(lhs, parse_binary_operator(rhs))
        elif rhs[0] == base.CALL_DEF:
            return divide(lhs, Interpreter.call_statement(rhs))
    
    return str(eval(lhs) / eval(rhs))
    

# less than
def less(lhs, rhs):
    return eval(lhs) < eval(rhs)

# equal to
def equal(lhs, rhs):
    return eval(lhs) == eval(rhs)
