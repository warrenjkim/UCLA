from intbase import InterpreterBase as base
from intbase import ErrorType as errno
from bparser import BParser
from objects import Class, Method, Object

def deepcopy(nested_content):
    if not isinstance(nested_content,list):
        return nested_content
    else:
        temp = []
        for sub_content in nested_content:
            temp.append(deepcopy(sub_content))
        return temp



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
        main = Object(self.MAIN_CLASS_DEF, self)
        main.run(self.MAIN_FUNC_DEF)
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
                
            # token is 'field'
            elif token == self.FIELD_DEF:
                field_name = tokens[i + 1]
                field_value = tokens[i + 2]
                self.classes[class_name].add_field(field_name, field_value)
                
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
