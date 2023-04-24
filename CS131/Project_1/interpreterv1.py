from intbase import InterpreterBase
from bparser import BParser

class Method:
    def __init__(self, name = "", args = []):
        self.name = name
        self.args = args
        self.statements = []

    def add_statement(self, statement):
        self.statements.append(statement)


class Field:
    def __init__(self, name = "", value = None):
        self.name = name
        self.value = value
        self.type = bool if value == "true" or value == "false" else type(eval(value))

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



class Interpreter(InterpreterBase):
    def __init__(self, console_output = True, inp = None, wtfisthis = None):
        super().__init__(console_output, inp)
        self.classes = { }

    def p_input(self, tokens = None, class_name = None, method_name = None):

        if tokens is None:
            tokens = self.inp
            
        for i, token in enumerate(tokens):
            # recurse if nested
            if type(token) is list:
                self.p_input(token, class_name, method_name)

            # new class
            if token == self.CLASS_DEF:
                class_name = tokens[i + 1]
                self.classes[class_name] = Class(class_name)

            # new field
            elif token == self.FIELD_DEF:
                field_name = tokens[i + 1]
                field_value = tokens[i + 2]
                self.classes[class_name].add_field(field_name, field_value)
                
            # new method
            elif token == self.METHOD_DEF:
                method_name = tokens[i + 1]
                method_args = tokens[i + 2]

                self.classes[class_name].add_method(method_name, method_args)

            # new print statement
            elif token == self.PRINT_DEF:
                self.classes[class_name].methods[method_name].add_statement((tokens[i], tokens[i + 1]))



def main():
    program_source = ['(class main',
                      '(field x true)',
                      ' (method main ()',
                      '   (print (call me hello))',
                      ' ) # end of method',
                      ' (method other (one)',
                      '  (print "other")',
                      ')',
                      ') # end of class',
                      '(class other_class',
                      '(method second (ard)',
                      '(print "lol")'
                      ')',
                      '(field ffo 12.33)',
                      ')']

    _, program = BParser.parse(program_source)

    x = Interpreter(inp = program)
    print(x.inp)
    print()
    x.p_input()

    print(f"class(es): {x.classes}")

    for c in x.classes:
        for f in x.classes[c].fields:
            print(f"field {f}: {x.classes[c].fields[f].name}, {x.classes[c].fields[f].value}, {x.classes[c].fields[f].type}")
            
        print(f"class '{c}' method(s): {x.classes[c].methods}")
        for m in x.classes[c].methods:
            print(f"method '{m}' statements: {x.classes[c].methods[m].statements}")

        print()

    


main()
