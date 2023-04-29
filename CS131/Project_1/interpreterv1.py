from intbase import InterpreterBase as base
from bparser import BParser

class Method:
    def __init__(self, name = "", args = []):
        self.name = name
        self.args = args
        self.statements = []
        self.variables = { }
        
    def add_statements(self, statements):
        self.statements = statements

    def add_variable(self, name, value):
        self.variables[name] = value


class Field:
    def __init__(self, name = "", value = None):
        self.name = name
        self.value = value
        # self.type = bool if value == base.TRUE_DEF or value == base.FALSE_DEF else type(eval(value))

    def change_value(self, new_value):
        self.value = new_value


class Class:
    def __init__(self, name = ""):
        self.name = name
        self.methods = { }
        self.fields = { }

    def add_method(self, name, args):
        self.methods[name] = Method(name, args)

    def add_field(self, name, value):
        self.fields[name] = Field(name, value)


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
    
        return True

    # add typechecking to inputi
    def inputi_statement(self, who, variable_name):
        who.add_variable(variable_name, self.get_input())

    # add typechecking to inputs
    def inputs_statement(self, who, variable_name):
        who.add_variable(variable_name, self.get_input())

    
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

