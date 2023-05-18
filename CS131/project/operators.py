from intbase import ErrorType as errno
from copy import deepcopy
from brewintypes import Type, typeof, type_to_enum
from variable import Variable, evaluate, stringify

    
class Operators:
    def __init__(self, curr_object = None):
        self.__curr_object = curr_object


    def set_object(self, obj):
        self.__curr_object = obj



    def parse_operator(self, args):
        if len(args) == 2:
            return self.__parse_unary_operator(args)
        else:
            return self.__parse_binary_operator(args)


    
    def add(self, lhs, rhs):
        self.__validate_operands('+', lhs, rhs)
        return str(lhs + rhs)



    def subtract(self, lhs, rhs):
        self.__validate_operands('-', lhs, rhs)            
        return str(lhs - rhs)



    def multiply(self, lhs, rhs):
        self.__validate_operands('*', lhs, rhs)
        return str(int(lhs * rhs))


    def modulo(self, lhs, rhs):
        self.__validate_operands('%', lhs, rhs)
        return str(int(lhs) % int(rhs))



    def divide(self, lhs, rhs):
        self.__validate_operands('/', lhs, rhs)        
        return str(int(lhs / rhs))


    def equal(self, lhs, rhs):
        self.__validate_operands('==', lhs, rhs)
        if isinstance(lhs, type(self.__curr_object)):
            return lhs is rhs
        return lhs == rhs


    
    def less(self, lhs, rhs):
        self.__validate_operands('<', lhs, rhs)
        return lhs < rhs



    def bitwise_and(self, lhs, rhs):
        self.__validate_operands('&', lhs, rhs)
        return lhs & rhs
    


    def bitwise_or(self, lhs, rhs):
        self.__validate_operands('|', lhs, rhs)
        return lhs | rhs



    def unary_not(self, expression):
        if type(expression) == bool:
            return not expression



    def __replace_identifiers_with_primitives(self, tokens):
        for i, token in enumerate(tokens):
            if isinstance(token, list):
                self.__replace_identifiers_with_primitives(token)
            else:
                if token == self.__curr_object.console.FALSE_DEF:
                    tokens[i] = "False"
                elif token == self.__curr_object.console.TRUE_DEF:
                    tokens[i] = "True"



    def __same_types(self, lhs, rhs):
        if typeof(lhs) == Type.OBJECT and typeof(rhs) == Type.OBJECT:
            return self.compare_objects(lhs, rhs)
            
        return typeof(lhs) == typeof(rhs)


    def __parse_unary_operator(self, args):
        args = deepcopy(args)

        self.__replace_identifiers_with_primitives(args)

        operator = args[0]     # first argument is always the operator
        expression = args[1]   # second argument is always the expression

        # reduce expression to primitives
        if isinstance(expression, list):
            expression = self.__curr_object.run_statement(expression)

        expression = evaluate(expression) # reduce to primitive values (if possible)
        
        if operator == '!':
            return stringify(self.unary_not(expression))

        else:
            return self.__curr_object.console.error(errno.TYPE_ERROR)



    def __parse_binary_operator(self, args):
        args = deepcopy(args)

        self.__replace_identifiers_with_primitives(args)

        operator = args[0]   # first argument is always the operator
        lhs = args[1]        # second argument is always the lhs
        rhs = args[2]        # third argument is always the rhs

        # reduce lhs to object
        if isinstance(lhs, list):
            lhs = self.__curr_object.run_statement(lhs)
        # reduce rhs to object
        if isinstance(rhs, list):
            rhs = self.__curr_object.run_statement(rhs)

        self.validate_classes(lhs, rhs)
            
        # reduce to primitive values (if possible)
        lhs = evaluate(lhs)
        rhs = evaluate(rhs)
        #print(f'lhs,rhs: {lhs, rhs}')
        
        # arithmetic operators
        if operator == '+':
            return stringify(self.add(lhs, rhs))
        elif operator == '-':
            return stringify(self.subtract(lhs, rhs))
        elif operator == '*':
            return stringify(self.multiply(lhs, rhs))
        elif operator == '%':
            return stringify(self.modulo(lhs, rhs))
        elif operator == '/':
            return stringify(self.divide(lhs, rhs))

        # equality operators
        if operator == "<":
            return stringify(self.less(lhs, rhs))
        elif operator == "<=":
            return stringify(self.less(lhs, rhs) or self.equal(lhs, rhs))
        elif operator == "==":
            return stringify(self.equal(lhs, rhs))
        elif operator == '!=':
            return stringify(not self.equal(lhs, rhs))
        elif operator == ">":
            return stringify(self.less(rhs, lhs))
        elif operator == ">=":
            return stringify(self.less(rhs, lhs) or self.equal(rhs, lhs))

        # bitwise operators
        elif operator == '&':
            return stringify(self.bitwise_and(lhs, rhs))
        elif operator == '|':
            return stringify(self.bitwise_or(lhs, rhs))

        # invalid operator
        else:
            return self.__curr_object.console.error(errno.TYPE_ERROR)



    # figure this out
    def __validate_operands(self, operator, lhs, rhs):
        match operator:
            # addition
            case '+':
                # incompatible types
                if not self.__same_types(lhs, rhs):
                    return self.__curr_object.console.error(errno.TYPE_ERROR)

                # invalid types
                if not (type(lhs) == int or isinstance(lhs, str)):
                    return self.__curr_object.console.error(errno.TYPE_ERROR)
                
            # arithmetic operations (except addition)
            case '-' | '*' | '%' | '/':
                # incompatible types
                if not self.__same_types(lhs, rhs):
                    return self.__curr_object.console.error(errno.TYPE_ERROR)

                # invalid types
                if type(lhs) != int:
                    return self.__curr_object.console.error(errno.TYPE_ERROR)

            # equality
            case '==':
                # incompatible types
                if not self.__same_types(lhs, rhs):
                    # check for null
                    if isinstance(lhs, Variable):
                        if lhs.value == Type.NULL:
                            return True
                    if isinstance(rhs, Variable):
                        if rhs.value == Type.NULL:
                            return True
                    if lhs == Type.NULL or rhs == Type.NULL:
                        return True
                    else:
                        return self.__curr_object.console.error(errno.TYPE_ERROR)

            # inequality
            case '<' | '>':
                # print(f'lhs, rhs: {lhs, rhs}')
                # incompatible types
                if not self.__same_types(lhs, rhs):
                    return self.__curr_object.console.error(errno.TYPE_ERROR)
                
                # invalid types
                if not (type(lhs) == int or isinstance(lhs, str)):
                    return self.__curr_object.console.error(errno.TYPE_ERROR)

            # bitwise
            case '&' | '|':
                # incompatible types
                if not self.__same_types(lhs, rhs):
                    return self.__curr_object.console.error(errno.TYPE_ERROR)
                
                # invalid types
                if not isinstance(lhs, bool):
                    return self.__curr_object.console.error(errno.TYPE_ERROR)


    def compare_objects(self, lhs, rhs):
        if lhs.parent is None and rhs.parent is None:
            return lhs.vtype == rhs.vtype
        else:
            if lhs.vtype != rhs.vtype:
                if lhs.parent is None:
                    return self.compare_objects(lhs, rhs.parent)
                if rhs.parent is None:
                    return self.compare_objects(lhs.parent, rhs)

        return True



    def validate_classes(self, lhs, rhs):
        lhs_type = None
        rhs_type = None
        if isinstance(lhs, Variable):
            if not isinstance(lhs.vtype, Type):
                lhs_type = lhs.vtype
        if isinstance(rhs, Variable):
            if not isinstance(rhs.vtype, Type):
                rhs_type = rhs.vtype

        if isinstance(lhs, type(self.__curr_object)):
            lhs_type = lhs.vtype
        if isinstance(rhs, type(self.__curr_object)):
            rhs_type = rhs.vtype

        if lhs_type is None or rhs_type is None:
            return

        if not self.same_classes(lhs_type, rhs_type):
            return self.__curr_object.console.error(errno.TYPE_ERROR)
    

    def same_classes(self, lhs_type, rhs_type):
        lhs_class = self.__curr_object.console.classes.get(lhs_type)
        rhs_class = self.__curr_object.console.classes.get(rhs_type)
        
        if lhs_class.super_class is None and rhs_class.super_class is None:
            return lhs_class.name == rhs_class.name
        else:
            if lhs_class.name != rhs_class.name:
                if lhs_class.super_class is None:
                    return self.same_classes(lhs_type, rhs_class.super_class)
                if rhs_class.super_class is None:
                    return self.same_classes(lhs_class.super_class, rhs_type)

        return True
