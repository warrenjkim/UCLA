from intbase import InterpreterBase as base
from intbase import ErrorType as errno
from bparser import BParser
from objects import Variable, Class, Method, Object
from copy import deepcopy


class Interpreter(base):
    classes = { }
    def __init__(self, console_output = True, inp = None, trace_output = None):
        super().__init__(console_output, inp)

    def run(self, program):
        self.reset()
        self.classes = { }
        
        valid, parsed_program = BParser.parse(program)  # parse the program
        print(parsed_program)
        if not valid:                                   # if not a valid Brewin program, error
            self.error(errno.SYNTAX_ERROR)

        self.itemize_input(parsed_program)              # itemize input into classes
        terminated = False                              # terminated flag

        while(not terminated):                          # while the program is still running
            terminated = self.interpret()               # interpret the program


    def interpret(self):
        entry_point = self.classes.get(self.MAIN_CLASS_DEF)  # entry point of the program (main class)

        if entry_point is None:                              # check to see if there is a main class
            self.error(errno.TYPE_ERROR)                     # type error if we don't have a main class

        main = Object(self.MAIN_CLASS_DEF, self)             # create main class object
        main.run(self.MAIN_FUNC_DEF)                         # run the main method

        return True                                          # return true when we finish executing the program


    # parse input into classes, fields, and methods
    def itemize_input(self, tokens = [], class_name = None, method_name = None):
        self.check_duplicates(tokens)
        
        # for each token, itemize it into its respective class (class, field, method)
        for i, token in enumerate(tokens):
            if isinstance(token, list):            # recurse if nested
                self.itemize_input(token, class_name, method_name)

            # ***** OBJECTS ******            
            elif token == self.CLASS_DEF:                     # token is 'class'
                class_name = tokens[i + 1]
                self.classes[class_name] = Class(class_name)  # add the class to the dictionary with key = class name
            elif token == self.FIELD_DEF:                     # token is 'field'
                field_name = tokens[i + 1]
                field_type = tokens[i + 2]
                field_value = tokens[i + 3]
                self.classes[class_name].add_field(field_name, field_type, field_value)
            elif token == self.METHOD_DEF:                    # token is 'method'
                method_name = tokens[i + 1]
                method_type = tokens[i + 2]
                method_args = self.format_method_args(tokens[i + 3])
                self.classes[class_name].add_method(method_name, method_args)
                self.parse_method(class_name, method_name, tokens[i + 3:])
                return


    def format_method_args(self, args = []):
        formatted_args = { }
        for arg in args:
            arg_name = arg[0]
            arg_type = arg[1]
            formatted_args[arg_name] = Variable(arg_name, arg_type, None)

    def parse_method(self, class_name, method_name, tokens):
        tokens = tokens[0]
        if tokens[0] == self.BEGIN_DEF:
            self.classes[class_name].methods[method_name].add_statements(tokens[1:])

        else:
            self.classes[class_name].methods[method_name].add_statements([tokens[0:]])

    def check_duplicates(self, parsed_program):
        # deep copy parsed program
        parsed_program = deepcopy(parsed_program)

        # flatten the program
        flattened_program = [item for sub_list in parsed_program for item in sub_list]

        # get all class/method/field names
        class_names = []
        method_names = []
        field_names = []

        # add each class/method/field name into their respective lists
        for i, token in enumerate(flattened_program):
            if token == self.CLASS_DEF:
                class_names.append(flattened_program[i + 1])
            elif token == self.METHOD_DEF:
                method_names.append(flattened_program[i + 2])
            elif token == self.FIELD_DEF:
                field_names.append(flattened_program[i + 2])

        # check for duplicates
        class_set = set(class_names)
        method_set = set(method_names)
        field_set = set(field_names)

        # if the lengths are different, error respectively
        if len(class_set) != len(class_names):
            return self.error(errno.TYPE_ERROR)
        if len(method_set) != len(method_names):
            return self.error(errno.NAME_ERROR)
        if len(field_set) != len(field_names):
            return self.error(errno.NAME_ERROR)
