from intbase import InterpreterBase as base
from bparser import BParser
from objects import Field, Class, Method
from operators import add


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
                add(statement[1], statement[2])
        return True

    # add typechecking to inputi
    def inputi_statement(self, who, variable_name):
        value = self.get_input()
        if not isinstance(eval(value), int):
            print('not an int')
        who.add_variable(variable_name, value)

    # add typechecking to inputs
    def inputs_statement(self, who, variable_name):
        value = self.get_input()
        if not isinstance(eval(value), str):
            print('not a string')
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
        else:
            self.output(add(args[1], args[2]))
            

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


            # ***** STATEMENTS *****
            # we parse statements in these if blocks

            # token is 'begin'
            elif token == self.BEGIN_DEF:
                # we parse the rest of the statements inside the begin block
                self.classes[class_name].methods[method_name].add_statements(tokens[i + 1:])

