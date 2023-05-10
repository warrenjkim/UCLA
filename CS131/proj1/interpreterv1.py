from intbase import InterpreterBase as base
from intbase import ErrorType as errno
from bparser import BParser
from objects import Class, Method, Object
from copy import deepcopy


class Interpreter(base):
    classes = { }
    def __init__(self, console_output = True, inp = None, trace_output = None):
        super().__init__(console_output, inp)

    def run(self, program):
        self.reset()
        self.classes = { }
        
        # parse the program
        valid, parsed_program = BParser.parse(program)

        # if not a valid Brewin program, error
        if not valid:
            self.error(errno.FAULT_ERROR)

        # itemize input into classes
        self.itemize_input(parsed_program)

        # terminated flag
        terminated = False

        # while the program is still running
        while(not terminated):
            # interpret the program
            terminated = self.interpret()


    def interpret(self):
        # entry point of the program (main class)
        entry_point = self.classes.get(self.MAIN_CLASS_DEF)

        # check to see if there is a main class
        if entry_point is None:
            # type error if we don't have a main class
            self.error(errno.TYPE_ERROR)

        # create main class object
        main = Object(self.MAIN_CLASS_DEF, self)
        # run the main method
        main.run(self.MAIN_FUNC_DEF)

        # return true when we finish executing the program
        return True


    # parse input into classes, fields, and methods
    def itemize_input(self, tokens = [], class_name = None, method_name = None):
        # check for duplicates
        self.check_duplicates(tokens)
        
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
                
            # token is 'field'
            elif token == self.FIELD_DEF:
                field_name = tokens[i + 1]
                field_value = tokens[i + 2]
                self.classes.get(class_name).add_field(field_name, field_value)
                
            # token is 'method'
            elif token == self.METHOD_DEF:
                # name of the method must be tokens[i + 1]
                method_name = tokens[i + 1]

                # arguments of the method must be tokens[i + 2]
                method_args = {key: None for key in tokens[i + 2]}
                
                # add method to the current class (class_name)
                self.classes.get(class_name).add_method(method_name, method_args)
                self.parse_method(class_name, method_name, tokens[i + 3:])
                return

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
                method_names.append(flattened_program[i + 1])
            elif token == self.FIELD_DEF:
                field_names.append(flattened_program[i + 1])

        # check for duplicates
        class_set = set(class_names)
        method_set = set(method_names)
        field_set = set(field_names)

        # if the lengths are different, error respectively
        if len(class_set) != len(class_names):
            self.error(errno.TYPE_ERROR)
        if len(method_set) != len(method_names):
            self.error(errno.NAME_ERROR)
        if len(field_set) != len(field_names):
            self.error(errno.NAME_ERROR)
