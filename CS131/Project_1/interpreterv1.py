from intbase import InterpreterBase as base
from intbase import ErrorType as errno
from bparser import BParser
from objects import Field, Class, Method

def deepcopy(nested_content):
    if not isinstance(nested_content,list):
        return nested_content
    else:
        temp = []
        for sub_content in nested_content:
            temp.append(deepcopy(sub_content))
        return temp


    
BINARY_OPERATORS = ['+', '-', '*', '/', '<', '<=', '==', '>', '>=']

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
        if lhs[0] in BINARY_OPERATORS:
            return add(parse_binary_operator(lhs), rhs)
        elif lhs[0] == base.CALL_DEF:
            return add(Interpreter.call_statement(lhs), rhs)
        
    if isinstance(rhs, list):
        if rhs[0] in BINARY_OPERATORS:
            return add(lhs, parse_binary_operator(rhs))
        elif rhs[0] == base.CALL_DEF:
            return add(lhs, Interpreter.call_statement(rhs))

    return str(eval(lhs) + eval(rhs))


# subtract
def subtract(lhs, rhs):
    if isinstance(lhs, list):
        if lhs[0] in BINARY_OPERATORS:
            return subtract(parse_binary_operator(lhs), rhs)
        elif lhs[0] == base.CALL_DEF:
            return subtract(Interpreter.call_statement(Interpreter, lhs), rhs)
        
    if isinstance(rhs, list):
        if rhs[0] in BINARY_OPERATORS:
            return subtract(lhs, parse_binary_operator(rhs))
        elif rhs[0] == base.CALL_DEF:
            return subtract(lhs, Interpreter.call_statement(Interpreter, rhs))

    return str(eval(lhs) - eval(rhs))


# multiply
def multiply(lhs, rhs):
    if isinstance(lhs, list):
        if lhs[0] in BINARY_OPERATORS:
            return multiply(parse_binary_operator(lhs), rhs)
        elif lhs[0] == base.CALL_DEF:
            print(f'LHS: {lhs}')
            return multiply(Interpreter.call_statement(lhs), rhs)
        
    if isinstance(rhs, list):
        if rhs[0] in BINARY_OPERATORS:
            return multiply(lhs, parse_binary_operator(rhs))
        elif rhs[0] == base.CALL_DEF:
            print(f'RHS: {rhs}')
            print(f'INTERPRETER: {Interpreter.classes["main"].methods["fact"].statements}')
            return multiply(lhs, Interpreter.call_statement(Interpreter, rhs))

    return str(eval(lhs) * eval(rhs))


# divide
def divide(lhs, rhs):
    if isinstance(lhs, list):
        if lhs[0] in BINARY_OPERATORS:
            return divide(parse_binary_operator(lhs), rhs)
        elif lhs[0] == base.CALL_DEF:
            return divide(Interpreter.call_statement(lhs), rhs)
        
    if isinstance(rhs, list):
        if rhs[0] in BINARY_OPERATORS:
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



class Interpreter(base):
    classes = { }
    def __init__(self, console_output = True, inp = None, trace_output = None):
        super().__init__(console_output, inp)

    def run(self, program):
        self.reset()
        valid, parsed_program = BParser.parse(program)
        
        if not valid:
            self.output('bro you fucked up')
            
        self.itemize_input(parsed_program)
        terminated = False
        
        while(not terminated):
            terminated = self.interpret()


    def interpret(self):
        entry_point = self.classes[self.MAIN_CLASS_DEF]
        main = entry_point.methods[self.MAIN_FUNC_DEF]
        fact = self.classes['main'].methods.get('fact')
        if fact is not None:
            print(fact.statements)
            
        # sequentially call statements
        for statement in main.statements:
            # statement is 'print'
            if statement[0] == self.PRINT_DEF:
                self.print_statement(main, statement[1])

            # statement is 'inputi'
            elif statement[0] == self.INPUT_INT_DEF:
                self.inputi_statement(main, statement[1])

            # statement is 'inputs'
            elif statement[0] == self.INPUT_STRING_DEF:
                self.inputs_statement(main, statement[1])
                
            elif statement[0] in BINARY_OPERATORS:
                try:
                    parse_binary_operator(statement)
                except:
                    self.error(self, errno.TYPE_ERROR)
        return True


        # parse input into classes, fields, and methods
    def itemize_input(self, tokens = [], class_name = None, method_name = None):
        # for each token, itemize it into its respective class (class, field, method)
        for i, token in enumerate(tokens):
            
            # recurse if nested
            if isinstance(token, list):
                self.itemize_input(token, class_name, method_name)


            # ***** OBJECTS ******
            # we parse into objects in these if blocks
            
            # token is 'class'
            elif token == self.CLASS_DEF:
                # name of the class must be tokens[i + 1]
                class_name = tokens[i + 1]
                # add the class to the dictionary with key = class name
                self.classes[class_name] = Class(class_name)
                continue
                
            # token is 'field' **TODO**
            elif token == self.FIELD_DEF:
                pass
                
            # token is 'method'
            elif token == self.METHOD_DEF:
                # name of the method must be tokens[i + 1]
                method_name = tokens[i + 1]
                # arguments of the method must be tokens[i + 2]
                method_args = {key: None for key in tokens[i + 2]}
                # add method to the current class (class_name)
                self.classes[class_name].add_method(method_name, method_args)
                self.parse_method(class_name, method_name, tokens[i + 3:])
                return

    def parse_method(self, class_name, method_name, tokens):
        tokens = tokens[0]
        if tokens[0] == self.BEGIN_DEF:
            self.classes[class_name].methods[method_name].add_statements(tokens[1:])

        else:
            self.classes[class_name].methods[method_name].add_statements([tokens[0:]])













    def inputi_statement(self, who, variable_name):
        value = self.get_input()
        if not isinstance(eval(value), int):
            base.error(self, errno.TYPE_ERROR)
        who.add_variable(variable_name, value)


    def inputs_statement(self, who, variable_name):
        value = self.get_input()
        if not isinstance(eval(value), str):
            base.error(self, errno.TYPE_ERROR)
        who.add_variable(variable_name, value)

    
    # finish function calls
    def print_statement(self, who, args = []):
        # if the arguments are not nested, we print the argument itself
        if not isinstance(args, list):
            # if the argument is a variable, print that variable's value
            if who.variables.get(args):
                self.output(who.variables.get(args))
            # the argument is a constant
            else:
                self.output(args.strip("\""))
        # the argument is nested: auxiliary function call(s)
        else:
            # binary operator
            if args[0] in BINARY_OPERATORS:
                try:
                    self.output(parse_binary_operator(args))
                except:
                    base.error(self, errno.TYPE_ERROR)
            # function call
            else:
                if args[0] == "call":
                    self.output(self.call_statement(args))

    def run_statement(self, tokens):
        for i, token in enumerate(tokens):
            # return statement
            if token == self.RETURN_DEF:
                if isinstance(tokens[i + 1], list):
                    return self.run_statement(tokens[i + 1])
                else:
                    return tokens[i + 1]
            elif token in BINARY_OPERATORS:
                return parse_binary_operator(tokens)
            elif token == self.IF_DEF:
                if parse_binary_operator(tokens[i + 1]):
                    self.run_statement(tokens[i + 2])
                else:
                    self.run_statement(tokens[i + 3])


    def replace_arg_with_argv(self, tokens, arg, argv):
        for i, token in enumerate(tokens):
            if isinstance(token, list):
                self.replace_arg_with_argv(token, arg, argv)
            else:
                if token == arg:
                    tokens[i] = argv
        

                    
    def call_statement(self, args = []):
        print(f'STMTS: {args}')
        # find the class
        who = self.MAIN_CLASS_DEF if args[1] == "me" else args[1]
        # find the method name
        method_name = args[2]
        
        # get method object
        method = self.classes[who].methods[method_name]

        # parse arguments
        print(args[3])
        evaluated_argv = []
        if isinstance(args[3], list):
            evaluated_argv = parse_binary_operator(args[3])

        print(evaluated_argv)
        # assign argument values to the arguments
        method.assign_args(args[3])

        stack_statements = deepcopy(method.statements)
        
        for key in method.args:
            self.replace_arg_with_argv(stack_statements, key, method.args[key])


        print(f'STACK STMTS: {stack_statements}')
        print(f'MTHD STMTS: {method.statements}')
        # execute statements in method
        for statement in stack_statements:
            return_value = self.run_statement(statement)
            if return_value is not None:
                return return_value
