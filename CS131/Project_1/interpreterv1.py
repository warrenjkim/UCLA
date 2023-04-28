from intbase import InterpreterBase as base
from bparser import BParser

class Method:
    def __init__(self, name = "", args = []):
        self.name = name
        self.args = args
        self.statements = []
        self.variables = { }

    def add_statement(self, statement):
        self.statements.append(statement)

    def add_variable(self, name, value):
        self.variables[name] = value

        
class Field:
    def __init__(self, name = "", value = None):
        self.name = name
        self.value = value
        self.type = bool if value == base.TRUE_DEF or value == base.FALSE_DEF else type(eval(value))

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
    def __init__(self, console_output = True, inp = None, wtfisthis = None):
        super().__init__(console_output, inp)
        self.classes = { }

    def run(self, program):
        self.reset()
        self.p_input(program)
        terminated = False
        
        while(not terminated):
            terminated = self.interpret()
        

    def call(self, who, method, args):
        print(who)
        print(method.name + ", " + method.args.__str__() + ", " + method.statements.__str__())
        print(args)

        
    
    def interpret(self):
        main = self.classes["main"].methods["main"]
        for statement in main.statements:
            if statement[0] == self.PRINT_DEF:
                if type(statement[1]) is not list:
                    self.output(statement[1].replace("\"", ""))
                else:
                    subcall = statement[1]
                    if subcall[0] == "call":
                        who = subcall[1]
                        method = self.classes["main"].methods[subcall[2]]
                        args = subcall[3:]

                        self.call(who, method, args)
                        
        return True
    
    def p_input(self, tokens = None, class_name = None, method_name = None):
        for i, token in enumerate(tokens):
            # recurse if nested
            if type(token) is list:
                self.p_input(token, class_name, method_name)

            # class
            if token == self.CLASS_DEF:
                class_name = tokens[i + 1]
                self.classes[class_name] = Class(class_name)

            # method
            elif token == self.METHOD_DEF:
                method_name = tokens[i + 1]
                method_args = tokens[i + 2]
                self.classes[class_name].add_method(method_name, method_args)

            # field
            elif token == self.FIELD_DEF:
                field_name = tokens[i + 1]
                field_value = tokens[i + 2]
                self.classes[class_name].add_field(field_name, field_value)

            # begin
            elif token == self.BEGIN_DEF:
                # for each statement in the begin block, recurse to parse the statement
                for statement in tokens[i + 1]:
                    self.p_input(statement, class_name, method_name)

            # null
            elif token == self.SET_DEF:
                self.classes[class_name].methods[method_name].add_variable(tokens[i + 1], tokens[ i + 2])

            # null
            elif token == self.NEW_DEF:
                pass

            # null
            elif token == self.IF_DEF:
                pass

            # null
            elif token == self.WHILE_DEF:
                pass

            # print statement
            elif token == self.PRINT_DEF:
                self.classes[class_name].methods[method_name].add_statement((tokens[i], tokens[i + 1]))



def main():
    x = Interpreter()
    program_source = ['(class main',
                      '(field x true)',
                      ' (method main ()',
                      '(begin',
                      '   (print (call me other "hello"))',
                      '(print "something else")',
                      ' ) # end of method',
                      ')',
                      ' (method other (one)',
                      '  (print one)',
                      ')',
                      ') # end of class',
                      '(class other_class',
                      '(method second (ard)',
                      '(print "lol")'
                      ')',
                      '(field ffo 12.33)',
                      ')']

    
    _, program = BParser.parse(program_source)
    x.run(program)
    # x = Interpreter(inp = program)
    # x.p_input()

    # print(f"class(es): {x.classes}")

    # for c in x.classes:
    #     for f in x.classes[c].fields:
    #         print(f"field {f}: {x.classes[c].fields[f].name}, {x.classes[c].fields[f].value}, {x.classes[c].fields[f].type}")
            
    #     print(f"class '{c}' method(s): {x.classes[c].methods}")
    #     for m in x.classes[c].methods:
    #         print(f"method '{m}' statements: {x.classes[c].methods[m].statements}")

    #     print()

    


main()
