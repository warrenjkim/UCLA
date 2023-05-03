from intbase import InterpreterBase as base
from intbase import ErrorType as errno
from bparser import BParser
from objects import Field, Class, Method
from operators import *


class Interpreter(base):
    def __init__(self, console_output = True, inp = None, trace_output = None):
        super().__init__(console_output, inp)
        self.classes = { }

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
        main = self.classes[self.MAIN_CLASS_DEF].methods[self.MAIN_FUNC_DEF]
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
                
            elif statement[0] == '+':
                try:
                    add(statement[1], statement[2])
                except:
                    self.error(self, errno.TYPE_ERROR)
        return True

    # add typechecking to inputi
    def inputi_statement(self, who, variable_name):
        value = self.get_input()
        if not isinstance(eval(value), int):
            base.error(self, errno.TYPE_ERROR)
        who.add_variable(variable_name, value)

    # add typechecking to inputs
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
            # binary operators
            if args[0] == '+':
                try:
                    self.output(add(args[1], args[2]))
                except:
                    base.error(self, errno.TYPE_ERROR)
            elif args[0] == '-':
                try:
                    self.output(subtract(args[1], args[2]))
                except:
                    base.error(self, errno.TYPE_ERROR)
            elif args[0] == '*':
                try:
                    self.output(multiply(args[1], args[2]))
                except:
                    base.error(self, errno.TYPE_ERROR)
            elif args[0] == '/':
                try:
                    self.output(divide(args[1], args[2]))
                except:
                    base.error(self, errno.TYPE_ERROR)
            # function call
            else:
                if args[0] == "call":
                    self.output(self.call_method(args[1], args[2], args[3]))

    def call_method(self, who, method_name, args = []):
        # call internally
        if who == "me":
            method = self.classes['main'].methods[method_name]
            for statement in method.statements:
                if statement[0] == self.RETURN_DEF:
                    # nested call(s)
                    if isinstance(statement[1], list):
                        # TODO
                        pass
                    return statement[1]
                    
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
                method_args = tokens[i + 2]
                # add method to the current class (class_name)
                self.classes[class_name].add_method(method_name, method_args)
                self.parse_method(class_name, method_name, tokens[i + 3:])
                return


            # ***** STATEMENTS *****
            # we parse statements in these if blocks

            # # token is 'begin'
            # elif token == self.BEGIN_DEF:
            #     print('read begin')
            #     # we parse the rest of the statements inside the begin block
            #     self.classes[class_name].methods[method_name].add_statements(tokens[i + 1:])
            #     print(f'in {class_name} class, {method_name} method')
            #     return
                
            # elif token == self.PRINT_DEF:
            #     print('only one statement')
            #     self.classes[class_name].methods[method_name].add_statements([tokens[i:]])
            #     print(f'in {class_name} class, {method_name} method')


    def parse_method(self, class_name, method_name, tokens):
        tokens = tokens[0]
        if tokens[0] == self.BEGIN_DEF:
            self.classes[class_name].methods[method_name].add_statements(tokens[1:])

        else:
            self.classes[class_name].methods[method_name].add_statements([tokens[0:]])

        return
