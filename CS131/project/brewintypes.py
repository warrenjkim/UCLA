from enum import Enum
from intbase import InterpreterBase as base
from intbase import ErrorType as errno

class Type(Enum):
    INT = base.INT_DEF
    BOOL = base.BOOL_DEF
    STRING = base.STRING_DEF
    NULL = base.NULL_DEF
    VOID = base.VOID_DEF
    OBJECT = "object"


def typeof(value):
    if value == Type.OBJECT:
        return Type.OBJECT
    if value == base.NULL_DEF or value == Type.NULL:
        return Type.NULL
    elif value == base.VOID_DEF:
        return Type.VOID
    elif value == base.TRUE_DEF or value == base.FALSE_DEF:
        return Type.BOOL
    elif isinstance(eval(value), int):
        return Type.INT
    if isinstance(value, str):
        try:
            if isinstance(eval(value), int):
                return Type.INT
        except:
            return errno.NAME_ERROR
        
        return Type.STRING



def type_to_enum(value):
    if value == base.NULL_DEF:
        return Type.NULL
    if value == base.VOID_DEF:
        return Type.VOID
    if value == base.INT_DEF:
        return Type.INT
    if value == base.BOOL_DEF:
        return Type.BOOL
    if value == base.STRING_DEF:
        return Type.STRING
    else:
        return Type.OBJECT
    
