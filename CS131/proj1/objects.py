from intbase import ErrorType as errno

def deepcopy(nested_content):
    if not isinstance(nested_content,list):
        return nested_content
    else:
        temp = []
        for sub_content in nested_content:
            temp.append(deepcopy(sub_content))
        return temp

class Method:
    def __init__(self, name = "", args = {}):
        self.name = name
        self.args = args
        self.statements = []
        self.variables = { }
        
    def add_statements(self, statements):
        self.statements.extend(statements)

    def bind_args(self, argv):
        i = 0
        for key in self.args:
            self.args[key] = argv[i]
            i += 1

    def add_variable(self, name, value):
        self.variables[name] = value


class Class:
    def __init__(self, name = ""):
        self.name = name
        self.methods = { }
        self.fields = { }

    def add_method(self, name, args):
        self.methods[name] = Method(name, args)

    def add_field(self, name, value):
        self.fields[name] = value


class Operators:
    def __init__(self, object_on_stack = None):
        self.object_on_stack = object_on_stack

    def parse_binary_operator(self, args):
        # first argument is always the operator
        operator = args[0]
        # second argument is always the lhs
        lhs = args[1]
        # third argument is always the rhs
        rhs = args[2]

        if operator == '+':
            return self.add(lhs, rhs)

        elif operator == '-':
            return self.subtract(lhs, rhs)
        
        elif operator == '*':
            return self.multiply(lhs, rhs)

        elif operator == '/':
            return self.divide(lhs, rhs)


        # equality operators
        if operator == "<":
            return self.less(lhs, rhs)

        elif operator == "<=":
            return self.less(lhs, rhs) or self.equal(lhs, rhs)

        elif operator == "==":
            return self.equal(lhs, rhs)
    
        elif operator == ">":
            return self.less(rhs, lhs)
    
        elif operator == ">=":
            return self.less(rhs, lhs) or self.equal(lhs, rhs)

        # invalid operand
        else:
            self.object_on_stack.console.error(errno.TYPE_ERROR)
            
    def add(self, lhs, rhs):
        if isinstance(lhs, list):
            return self.add(self.parse_binary_operator(lhs), rhs)
        if isinstance(rhs, list):
            return self.add(lhs, self.parse_binary_operator(rhs))

        return str(eval(lhs) + eval(rhs))
    
    def subtract(self, lhs, rhs):
        if isinstance(lhs, list):
            return self.subtract(self.parse_binary_operator(lhs), rhs)
        if isinstance(rhs, list):
            return self.subtract(lhs, self.parse_binary_operator(rhs))

        return str(eval(lhs) - eval(rhs))

    def multiply(self, lhs, rhs):
        if isinstance(lhs, list):
            if lhs[0] == self.object_on_stack.console.CALL_DEF:
                print('here')
            return self.multiply(self.parse_binary_operator(lhs), rhs)
        if isinstance(rhs, list):
            if rhs[0] == self.object_on_stack.console.CALL_DEF:
                return self.multiply(lhs, self.object_on_stack.call_statement(rhs[1:]))
            return self.multiply(lhs, self.parse_binary_operator(rhs))

        return str(eval(lhs) * eval(rhs))

    def modulo(self, lhs, rhs):
        if isinstance(lhs, list):
            return self.divide(self.parse_binary_operator(lhs), rhs)
        if isinstance(rhs, list):
            return self.divide(lhs, self.parse_binary_operator(rhs))

        return str(eval(lhs) % eval(rhs))
    
    def divide(self, lhs, rhs):
        if isinstance(lhs, list):
            return self.divide(self.parse_binary_operator(lhs), rhs)
        if isinstance(rhs, list):
            return self.divide(lhs, self.parse_binary_operator(rhs))

        return str(eval(lhs) / eval(rhs))

    def equal(self, lhs, rhs):
        if isinstance(lhs, list):
            return self.equal(self.parse_binary_operator(lhs), rhs)
        if isinstance(rhs, list):
            return self.equal(lhs, self.parse_binary_operator(rhs))

        return eval(lhs) == eval(rhs)

    def less(self, lhs, rhs):
        if isinstance(lhs, list):
            return self.less(self.parse_binary_operator(lhs), rhs)
        if isinstance(rhs, list):
            return self.less(lhs, self.parse_binary_operator(rhs))

        return eval(lhs) < eval(rhs)
    

    

class Object:
    BINARY_OPERATORS = ['+', '-', '*', '%', '/', '<', '<=', '==', '>', '>=']
    def __init__(self, method, console):
        self.method = method
        self.method_args = self.method.args.copy()
        self.variables = method.variables.copy()
        self.statements = deepcopy(method.statements)
        self.fields = { }
        self.console = console
        self.operator = Operators(self)

    def run(self):
        for key in self.method_args:
            self.replace_arg_with_argv(self.statements, key, self.method_args[key])
        for statement in self.statements:
            # print(f'now running: {statement}')
            return_value = self.run_statement(statement)

        if return_value is not None:
            return return_value

    def run_statement(self, statement):
        print(f'\nnow running: {statement}')
        # we check each token
        for i, token in enumerate(statement):
            # token is print
            if token == self.console.PRINT_DEF:
                self.print_statement(statement[i + 1])
            # token is a binary operator
            elif token in self.BINARY_OPERATORS:
                try:
                    return self.operator.parse_binary_operator(statement)
                except TypeError:
                    self.console.error(errno.TYPE_ERROR)
                    
            # token is inputi
            elif token == self.console.INPUT_INT_DEF:
                self.inputi_statement(statement[i + 1])
            # token is inputs
            elif token == self.console.INPUT_STRING_DEF:
                self.inputs_statement(statement[i + 1])
            # token is call
            elif token == self.console.CALL_DEF:
                return self.call_statement(statement[i + 1:])
            # token is return
            elif token == self.console.RETURN_DEF:
                if isinstance(statement[i + 1], list):
                    return self.run_statement(statement[i + 1])
                else:
                    return statement[i + 1]
            # token is if
            elif token == self.console.IF_DEF:
                if not isinstance(statement[i + 1], list):
                    self.console.error(errno.TYPE_ERROR)
                else:
                    return self.if_statement(statement[i + 1:])
            # token is begin
            elif token == self.console.BEGIN_DEF:
                for nested_statement in statement[i + 1:]:
                    self.run_statement(nested_statement)

                
    def print_statement(self, args):
        # no nested calls
        if not isinstance(args, list):
            evaluated_variable = self.variables.get(args)
            if evaluated_variable is not None:
                self.console.output(evaluated_variable)
            else:
                self.console.output(args.strip('"'))
        # there are nested calls
        else:
            #print(f'output: {self.console.get_output()}')
            self.console.output(self.run_statement(args))


    # integer input
    def inputi_statement(self, variable_name):
        value = self.console.get_input()
        if not isinstance(eval(value), int):
            self.console.error(errno.TYPE_ERROR)
        self.variables[variable_name] = value

    # string input
    def inputs_statement(self, variable_name):
        value = self.console.get_input()
        if not isinstance(eval(value), str):
            self.console.error(errno.TYPE_ERROR)
        self.variables[variable_name] = value

    # call statement
    # args takes the form:
    # args[0] = who
    # args[1] = method name
    # args[2] = method arguments
    def call_statement(self, args = []):
        who = self.console.MAIN_CLASS_DEF if args[0] == "me" else args[0]
        method_name = args[1]        
        method_args = args[2]
        
        if isinstance(args[2], list):
            method_args = self.run_statement(args[2])

        method = self.find_method(who, method_name)
        method.bind_args(method_args)

        method_object = Object(method, self.console)

        return method_object.run()


    # if statement
    # statement takes the form:
    # statement[0] = boolean expression
    # statement[1] = expression was true
    # statement[2] = expression was false
    def if_statement(self, statement):
        if self.operator.parse_binary_operator(statement[0]):
            return self.run_statement(statement[1])
        else:
            return self.run_statement(statement[2])

    

    def find_method(self, class_name, method_name):
        return self.console.classes[class_name].methods[method_name]
        
    def replace_arg_with_argv(self, tokens, arg, argv):
        for i, token in enumerate(tokens):
            if isinstance(token, list):
                self.replace_arg_with_argv(token, arg, argv)
            else:
                if token == arg:
                    tokens[i] = argv
