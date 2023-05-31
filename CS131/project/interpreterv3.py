from intbase import InterpreterBase as base
from intbase import ErrorType as errno
from bparser import BParser
from brewinclass import Class, Template
from objects import Method, Object
from copy import deepcopy
from variable import Variable, stringify, evaluate

from brewintypes import Type, typeof, type_to_enum


class Interpreter(base):
    PRIMITIVES = [base.INT_DEF, base.BOOL_DEF, base.STRING_DEF, base.VOID_DEF]
    DEFINED_TYPES = set()
    classes = { }
    templates = { }

    def __init__(self, console_output = True, inp = None, trace_output = None):
        super().__init__(console_output, inp)

    def __deepcopy__(self, memo = {}):
        return self

    def run(self, program):
        self.reset()
        self.classes = { }
        self.templates = { }
        
        valid, parsed_program = BParser.parse(program)  # parse the program
        if not valid:                                   # if not a valid Brewin program, error
            self.error(errno.SYNTAX_ERROR)

        self.check_duplicates(parsed_program)
        self.itemize_input(parsed_program)              # itemize input into classes
        terminated = False                              # terminated flag

        #self.print_class_tree()
        while(not terminated):                          # while the program is still running
            terminated = self.interpret()               # interpret the program


    def interpret(self):
        entry_point = self.classes.get(self.MAIN_CLASS_DEF)  # entry point of the program (main class)

        if entry_point is None:                              # check to see if there is a main class
            self.error(errno.TYPE_ERROR)                     # type error if we don't have a main class
        if entry_point.methods.get(self.MAIN_FUNC_DEF) is None:
            self.error(errno.NAME_ERROR)

        main = Object(self.MAIN_CLASS_DEF, self)             # create main class object
        main.run(self.MAIN_FUNC_DEF)                         # run the main method

        return True                                          # return true when we finish executing the program


    # parse input into classes, fields, and methods
    def itemize_input(self, tokens = [], class_name = None, method_name = None):
        # for each token, itemize it into its respective class (class, field, method)
        for i, token in enumerate(tokens):
            if isinstance(token, list):                       # recurse if nested
                self.itemize_input(token, class_name, method_name)

            # ***** TEMPLATES *****
            elif token == self.TEMPLATE_CLASS_DEF:
                class_name = tokens[i + 1]
                types = tokens[i + 2]
                self.templates[class_name] = Template(class_name, types)
    
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


    def parse_template_field(self, class_name, tokens):
        split = tokens[0].split(self.TYPE_CONCAT_CHAR)
        if len(split) < 2:
            template_name = class_name
            vtype = split[0]
        else:
            template_name = split[0]
            vtype = split[1]
        name = tokens[1]

        print(f'here: {template_name, vtype, name}')
        if len(tokens) == 3:
            value = tokens[2]
        else:
            value = self.__default_value(vtype)

        self.__validate_type(vtype, value)

        if value == self.NULL_DEF:
            value = Type.NULL
        else:
            if vtype == self.BOOL_DEF:
                value = True if value == self.TRUE_DEF else False
            value = stringify(value)
        
        self.templates[class_name].set_field(name, type_to_enum(vtype), value)
    
    def parse_template_method(self, class_name, tokens):
        split = tokens[0].split(self.TYPE_CONCAT_CHAR)
        if len(split) < 2:
            template_name = class_name
            vtype = split[0]
        else:
            template_name = split[0]
            vtype = split[1]

        name = tokens[1]
        args = self.__format_method_args(tokens[2])
        if len(tokens) >= 4:
            statements = tokens[3]
        else:
            statements = []
        if type_to_enum(vtype) != Type.OBJECT:
            vtype = type_to_enum(vtype)
        
        self.templates[class_name].add_method(name, vtype, args, statements)
    
    def parse_field(self, class_name, tokens):
        if self.templates.get(class_name) is not None:
            return self.parse_template_field(class_name, tokens)
        
        vtype = tokens[0]
        name = tokens[1]
        if len(tokens) == 3:
            value = tokens[2]
        else:
            value = self.__default_value(vtype)

        self.__validate_type(vtype, value)

        if value == self.NULL_DEF:
            value = Type.NULL
        else:
            if vtype == self.BOOL_DEF:
                value = True if value == self.TRUE_DEF else False
            value = stringify(value)
        
        self.classes[class_name].set_field(name, type_to_enum(vtype), value)

    def parse_method(self, class_name, tokens):
        if self.templates.get(class_name) is not None:
            return self.parse_template_method(class_name, tokens)
        
        vtype = tokens[0]
        
        self.__validate_type(vtype)

        name = tokens[1]
        args = self.__format_method_args(tokens[2])
        if len(tokens) >= 4:
            statements = tokens[3]
        else:
            statements = []
        if type_to_enum(vtype) != Type.OBJECT:
            vtype = type_to_enum(vtype)
        
        self.classes[class_name].add_method(name, vtype, args, statements)

        
    def __format_method_args(self, args = []):
        arg_names = [arg[1] for arg in args]
        if len(set(arg_names)) != len(arg_names):
            return self.error(errno.NAME_ERROR)
        
        formatted_args = { }
        for arg in args:
            arg_type = arg[0]
            
            self.__validate_type(arg_type)
            
            arg_name = arg[1]
            formatted_args[arg_name] = Variable(arg_name, type_to_enum(arg_type), None)

        return formatted_args



    def flatten_list(self, nested_list):
        flattened = []
        for item in nested_list:
            if isinstance(item, list):
                flattened.extend(self.flatten_list(item))
            else:
                flattened.append(item)
        return flattened

    
    def check_duplicates(self, parsed_program):
        # deep copy parsed program
        parsed_program = deepcopy(parsed_program)

        # get all class/method/field names
        class_names = []
        method_names = []
        field_names = []
        classes = []

        # add each class/method/field name into their respective lists
        for class_body in parsed_program:
            for i, token, in enumerate(class_body):
                if token == self.CLASS_DEF:
                    class_names.append(class_body[i + 1])
                    classes.append(self.flatten_list(class_body[i + 2:]))
                    self.DEFINED_TYPES.add(class_body[i + 1])
                    break


        class_set = set(class_names)
        
        if len(class_set) != len(class_names):
                return self.error(errno.TYPE_ERROR)
            
        for class_body in classes:
            for i, token in enumerate(class_body):
                if token == self.METHOD_DEF:
                    method_names.append(class_body[i + 2])
                elif token == self.FIELD_DEF:
                    field_names.append(class_body[i + 2])

            method_set = set(method_names)
            field_set = set(field_names)

            # print(f'fields: {field_names, field_set}')
            # print(f'methods: {method_names, method_set}')
            
            # if the lengths are different, error respectively
            if len(method_set) != len(method_names):
                return self.error(errno.NAME_ERROR)
            if len(field_set) != len(field_names):
                return self.error(errno.NAME_ERROR)

            method_names = []
            field_names = []
        

    def __validate_type(self, vtype, value = None):
        if self.TYPE_CONCAT_CHAR in vtype:
            return self.__validate_template(vtype, value)
        if vtype not in self.PRIMITIVES:                  # not a primitive type
            if vtype not in self.DEFINED_TYPES:           # not in existing class types
                if self.classes.get(vtype) is None:       # if invalid class type
                    return self.error(errno.TYPE_ERROR)
                else:
                    self.DEFINED_TYPES.add(vtype)

        if value is None:
            return
        if value == self.NULL_DEF:                        # type null is ok
            if vtype not in self.DEFINED_TYPES:
                return self.error(errno.TYPE_ERROR)
        elif vtype == self.INT_DEF:                       # int
            if not isinstance(eval(value), int):
                return self.error(errno.TYPE_ERROR)
        elif vtype == self.BOOL_DEF:                      # bool
            if value != self.TRUE_DEF and value != self.FALSE_DEF:
                return self.error(errno.TYPE_ERROR)
        elif vtype == self.STRING_DEF:                    # string
            if not isinstance(eval(value), str):
                return self.error(errno.TYPE_ERROR)
        elif typeof(evaluate(vtype)) == Type.OBJECT:
            if value != self.NULL_DEF:
                return self.error(errno.TYPE_ERROR)


    def __validate_template(self, vtype, value):
        parsed_type = vtype.split(self.TYPE_CONCAT_CHAR)
        template_name = parsed_type[0]
        
        



        
    def __default_value(self, vtype):
        if vtype == self.INT_DEF:
            return '0'
        elif vtype == self.BOOL_DEF:
            return self.FALSE_DEF
        elif vtype == self.STRING_DEF:
            return '""'
        elif typeof(evaluate(vtype)) == Type.OBJECT:
            return self.NULL_DEF


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
