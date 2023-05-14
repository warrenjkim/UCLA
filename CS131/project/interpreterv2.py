from intbase import InterpreterBase as base
from intbase import ErrorType as errno
from bparser import BParser
from objects import Class, Method, Object
from copy import deepcopy
from variable import Variable

from brewintypes import Type, typeof, type_to_enum


class Interpreter(base):
    PRIMITIVES = [base.INT_DEF, base.BOOL_DEF, base.STRING_DEF, base.VOID_DEF]
    DEFINED_TYPES = set(PRIMITIVES)
    classes = { }
    def __init__(self, console_output = True, inp = None, trace_output = None):
        super().__init__(console_output, inp)

    def run(self, program):
        self.reset()
        self.classes = { }
        
        valid, parsed_program = BParser.parse(program)  # parse the program
        if not valid:                                   # if not a valid Brewin program, error
            self.error(errno.SYNTAX_ERROR)

        self.itemize_input(parsed_program)              # itemize input into classes
        terminated = False                              # terminated flag

        #self.print_class_tree()
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
            if isinstance(token, list):                       # recurse if nested
                self.itemize_input(token, class_name, method_name)

            # ***** OBJECTS ******            
            elif token == self.CLASS_DEF:                     # token is 'class'
                class_name = tokens[i + 1]
                if not isinstance(tokens[i + 2], list):
                    super_class = tokens[i + 3]
                    self.classes[class_name] = Class(class_name, super_class)
                    continue
                    
                self.classes[class_name] = Class(class_name)  # add the class to the dictionary with key = class name
            elif token == self.FIELD_DEF:                     # token is 'field'
                self.parse_field(class_name, tokens[i + 1:])
                return
            elif token == self.METHOD_DEF:                    # token is 'method'
                self.parse_method(class_name, tokens[i + 1:])
                return


    def parse_field(self, class_name, tokens):
        vtype = tokens[0]
        name = tokens[1]  
        value = tokens[2]

        self.__validate_type(vtype, value)

        if value == self.NULL_DEF:
            value = Type.NULL
        else:
            value = value.strip('"')
        
        self.classes[class_name].set_field(name, type_to_enum(vtype), value)

    def parse_method(self, class_name, tokens):
        vtype = tokens[0]
        
        self.__validate_type(vtype)
        
        name = tokens[1]
        args = self.__format_method_args(tokens[2])
        statements = tokens[3]
        
        self.classes[class_name].add_method(name, type_to_enum(vtype), args, statements)

    def __format_method_args(self, args = []):
        formatted_args = { }
        for arg in args:
            arg_type = arg[0]
            
            self.__validate_type(arg_type)
            
            arg_name = arg[1]
            formatted_args[arg_name] = Variable(arg_name, type_to_enum(arg_type), None)

        return formatted_args

        
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

    def __validate_type(self, vtype, value = None):
        if vtype not in self.PRIMITIVES:                  # not a primitive type
            if vtype not in self.DEFINED_TYPES:           # not in existing class types
                if self.classes.get(vtype) is None:       # if invalid class type
                    return self.error(errno.TYPE_ERROR)
                else:
                    self.DEFINED_TYPES.add(vtype)

        if value is None:
            return
        if value == self.NULL_DEF:                       # type null is ok
            return
        elif vtype == self.INT_DEF:                       # int
            if not isinstance(eval(value), int):
                return self.error(errno.TYPE_ERROR)
        elif vtype == self.BOOL_DEF:                      # bool
            if value != self.TRUE_DEF or value != self.FALSE_DEF:
                return self.error(errno.TYPE_ERROR)
        elif vtype == self.STRING_DEF:                    # string
            if not isinstance(eval(value), str):
                return self.error(errno.TYPE_ERROR)





    def print_class_tree(self):
        for k, v in self.classes.items():
            print(f'\n\nclass: {k}')
            for kk, vv in v.fields.items():
                print(f'field: {vv}')
                print()
                for kk, vv in v.methods.items():
                    print(f'method: (name, vtype) {kk}, {vv.vtype}')
                    for kkk, vvv in vv.args.items():
                        print(f'args: {vvv}')
                    print(f'statements: {vv.statements}')
                            
        print()
