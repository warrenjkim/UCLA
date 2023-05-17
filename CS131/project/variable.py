from intbase import InterpreterBase as base
from intbase import ErrorType as errno
from brewintypes import Type

class Variable:
    def __init__(self, name, vtype, value):
        self.name = name
        self.vtype = vtype
        self.value = value

    def __repr__(self):
        return str(self.__dict__)
    
    def __str__(self):
        return str(self.__dict__)

    def set_value(self, value):
        self.value = value

    def type(self):
        return self.vtype




def evaluate(value):
    primitives = (int, bool, str)
    
    if isinstance(value, Variable):
        return evaluate(value.value)

    if not isinstance(value, primitives):
        return value

    if value == Type.NULL:
        return Type.NULL
    elif value == base.TRUE_DEF:
        return True
    elif value == base.FALSE_DEF:
        return False
    try:
        value = eval(value)
        if isinstance(value, int):
            return value
        if isinstance(value, str):
            if value == 'True' or value == 'False':
                value = eval(value)
        return value
    except:
        return errno.NAME_ERROR



            
def stringify(value):
    primitives = (int, str, bool)
    
    if isinstance(value, int):
        return str(value)
    if isinstance(value, Type):
        return value
    if isinstance(value, str):
        if len(value) != 0:
            if value[0] == '"':
                return value
        try:
            if isinstance(eval(value), int):
                return str(eval(value))
        except:
            return '"' + str(value) + '"'

    if not isinstance(value, primitives):
        return value
    return '"' + str(value) + '"'
