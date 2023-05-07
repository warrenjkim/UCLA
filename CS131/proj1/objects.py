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
        # print(f'now evaluating: {args}')
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

        elif operator == '%':
            return self.modulo(lhs, rhs)
        
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
            if lhs[0] == self.object_on_stack.console.CALL_DEF:
                return self.add(self.object_on_stack.run_statement(lhs), rhs)
            return self.add(self.parse_binary_operator(lhs), rhs)
        if isinstance(rhs, list):
            if rhs[0] == self.object_on_stack.console.CALL_DEF:
                return self.add(lhs, self.object_on_stack.run_statement(rhs))
            return self.add(lhs, self.parse_binary_operator(rhs))

        evaluated_lhs = self.object_on_stack.fields.get(lhs)
        evaluated_rhs = self.object_on_stack.fields.get(rhs)
        if evaluated_lhs is not None:
            lhs = evaluated_lhs
        if evaluated_rhs is not None:
            rhs = evaluated_rhs
            
        return str(eval(lhs) + eval(rhs))
    
    def subtract(self, lhs, rhs):
        if isinstance(lhs, list):
            if lhs[0] == self.object_on_stack.console.CALL_DEF:
                return self.subtract(self.object_on_stack.run_statement(lhs), rhs)
            return self.subtract(self.parse_binary_operator(lhs), rhs)
        if isinstance(rhs, list):
            if rhs[0] == self.object_on_stack.console.CALL_DEF:
                return self.subtract(lhs, self.object_on_stack.run_statement(rhs))
            return self.subtract(lhs, self.parse_binary_operator(rhs))

        evaluated_lhs = self.object_on_stack.fields.get(lhs)
        evaluated_rhs = self.object_on_stack.fields.get(rhs)
        if evaluated_lhs is not None:
            lhs = evaluated_lhs
        if evaluated_rhs is not None:
            rhs = evaluated_rhs
            
        evaluated_lhs = self.object_on_stack.fields.get(lhs)
        evaluated_rhs = self.object_on_stack.fields.get(rhs)
        if evaluated_lhs is not None:
            lhs = evaluated_lhs
        if evaluated_rhs is not None:
            rhs = evaluated_rhs
            
        return str(eval(lhs) - eval(rhs))

    def multiply(self, lhs, rhs):
        if isinstance(lhs, list):
            if lhs[0] == self.object_on_stack.console.CALL_DEF:
                return self.multiply(self.object_on_stack.run_statement(lhs), rhs)
            return self.multiply(self.parse_binary_operator(lhs), rhs)
        if isinstance(rhs, list):
            if rhs[0] == self.object_on_stack.console.CALL_DEF:
                return self.multiply(lhs, self.object_on_stack.run_statement(rhs))
            return self.multiply(lhs, self.parse_binary_operator(rhs))

        evaluated_lhs = self.object_on_stack.fields.get(lhs)
        evaluated_rhs = self.object_on_stack.fields.get(rhs)
        if evaluated_lhs is not None:
            lhs = evaluated_lhs
        if evaluated_rhs is not None:
            rhs = evaluated_rhs
        
        return str(eval(lhs) * eval(rhs))

    def modulo(self, lhs, rhs):
        if isinstance(lhs, list):
            if lhs[0] == self.object_on_stack.console.CALL_DEF:
                return self.modulo(self.object_on_stack.run_statement(lhs), rhs)
            return self.modulo(self.parse_binary_operator(lhs), rhs)
        if isinstance(rhs, list):
            if rhs[0] == self.object_on_stack.console.CALL_DEF:
                return self.modulo(lhs, self.object_on_stack.run_statement(rhs))
            return self.modulo(lhs, self.parse_binary_operator(rhs))

        # print(f'in modulo, lhs: {lhs}, rhs: {rhs}')
        evaluated_lhs = self.object_on_stack.fields.get(lhs)
        evaluated_rhs = self.object_on_stack.fields.get(rhs)
        if evaluated_lhs is not None:
            lhs = evaluated_lhs
        if evaluated_rhs is not None:
            rhs = evaluated_rhs
        
        return str(eval(lhs) % eval(rhs))
    
    def divide(self, lhs, rhs):
        if isinstance(lhs, list):
            if lhs[0] == self.object_on_stack.console.CALL_DEF:
                return self.divide(self.object_on_stack.run_statement(lhs), rhs)
            return self.divide(self.parse_binary_operator(lhs), rhs)
        if isinstance(rhs, list):
            if rhs[0] == self.object_on_stack.console.CALL_DEF:
                return self.divide(lhs, self.object_on_stack.run_statement(rhs))
            return self.divide(lhs, self.parse_binary_operator(rhs))

        evaluated_lhs = self.object_on_stack.fields.get(lhs)
        evaluated_rhs = self.object_on_stack.fields.get(rhs)
        if evaluated_lhs is not None:
            lhs = evaluated_lhs
        if evaluated_rhs is not None:
            rhs = evaluated_rhs
            
        return str(eval(lhs) / eval(rhs))

    def equal(self, lhs, rhs):
        if isinstance(lhs, list):
            if lhs[0] == self.object_on_stack.console.CALL_DEF:
                return self.equal(self.object_on_stack.run_statement(lhs), rhs)
            return self.equal(self.parse_binary_operator(lhs), rhs)
        if isinstance(rhs, list):
            if rhs[0] == self.object_on_stack.console.CALL_DEF:
                return self.equal(lhs, self.object_on_stack.run_statement(rhs))
            return self.equal(lhs, self.parse_binary_operator(rhs))

        evaluated_lhs = self.object_on_stack.fields.get(lhs)
        evaluated_rhs = self.object_on_stack.fields.get(rhs)
        if evaluated_lhs is not None:
            lhs = evaluated_lhs
        if evaluated_rhs is not None:
            rhs = evaluated_rhs
            
        return eval(lhs) == eval(rhs)

    def less(self, lhs, rhs):
        if isinstance(lhs, list):
            if lhs[0] == self.object_on_stack.console.CALL_DEF:
                return self.less(self.object_on_stack.run_statement(lhs), rhs)
            return self.less(self.parse_binary_operator(lhs), rhs)
        if isinstance(rhs, list):
            if rhs[0] == self.object_on_stack.console.CALL_DEF:
                return self.less(lhs, self.object_on_stack.run_statement(rhs))
            return self.less(lhs, self.parse_binary_operator(rhs))

        evaluated_lhs = self.object_on_stack.fields.get(lhs)
        evaluated_rhs = self.object_on_stack.fields.get(rhs)
        if evaluated_lhs is not None:
            lhs = evaluated_lhs
        if evaluated_rhs is not None:
            rhs = evaluated_rhs
            
        return eval(lhs) < eval(rhs)
    


class Object:
    BINARY_OPERATORS = ['+', '-', '*', '%', '/', '<', '<=', '==', '>', '>=']
    
    def __init__(self, class_name, console):
        self.class_name = class_name
        self.console = console
        self.methods = console.classes.get(class_name).methods
        self.fields = console.classes.get(class_name).fields
        self.operator = Operators(self)
        self.current_method = None

    def run(self, method_name, method_args = { }):
        self.current_method = self.methods[method_name]
        self.current_method.bind_args(method_args)
        statements = deepcopy(self.current_method.statements)
                
        for key in self.current_method.args:
            self.replace_arg_with_argv(statements, key, self.current_method.args[key])
                
        for statement in statements:
            return_value = self.run_statement(statement)
            if return_value == self.console.RETURN_DEF:
                return

        return return_value

    def run_statement(self, statement):
        print(f'now running: {statement}')
        # we check each token
        for i, token in enumerate(statement):
            # token is print
            if token == self.console.PRINT_DEF:
                return self.print_statement(statement[i + 1:])
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
                return self.return_statement(statement)
            # token is if
            elif token == self.console.IF_DEF:
                if not self.valid_boolean(statement[i + 1]):
                    self.console.error(errno.TYPE_ERROR)
                else:
                    return self.if_statement(statement[i + 1:])
            # token is begin
            elif token == self.console.BEGIN_DEF:
                return self.begin_statement(statement[i + 1:])
            # token is while
            elif token == self.console.WHILE_DEF:
                if not self.valid_boolean(statement[i + 1]):
                    self.console.error(errno.TYPE_ERROR)
                else:
                    self.while_statement(statement[i + 1:])
            # token is set
            elif token == self.console.SET_DEF:
                # statement takes the form:
                # statement[i + 1] = set name
                # statement[i + 2] = set value
                set_name = statement[i + 1]
                set_value = statement[i + 2]
                self.set_statement(set_name, set_value)
            # token is new
            elif token == self.console.NEW_DEF:
                return self.new_statement(statement[i + 1])


    def return_statement(self, statement):
        if len(statement) < 2:
            return
        if isinstance(statement[1], list):
            return self.run_statement(statement[1])
        else:
            evaluated_field = self.fields.get(statement[1])
            if evaluated_field is not None:
                return evaluated_field
            else:
                return statement[1]
    

    def print_statement(self, args):
        # print(f'now printing: {args}')
        to_print = ""
        
        for arg in args:            
            # no nested calls
            if not isinstance(arg, list):
                evaluated_field = self.fields.get(arg)
                if evaluated_field is not None:
                    to_print += str(evaluated_field)
                    # self.console.output(evaluated_field)
                else:
                    to_print += str(arg.strip('"'))
                    # self.console.output(arg.strip('"'))
            # there are nested calls
            else:
                to_print += str(self.run_statement(arg))
            # self.console.output(self.run_statement(arg))

        self.console.output(to_print)


    # integer input
    def inputi_statement(self, field_name):
        value = self.console.get_input()
        if not isinstance(eval(value), int):
            self.console.error(errno.TYPE_ERROR)
        self.fields[field_name] = value


    # string input
    def inputs_statement(self, field_name):
        value = self.console.get_input()
        if not isinstance(eval(value), str):
            self.console.error(errno.TYPE_ERROR)
        self.fields[field_name] = value

    # call statement
    
    def call_statement(self, statement):
        # statement takes the form:
        # statement[0] = who
        # statement[1] = method name
        # statement[2:] = method arguments
        who = statement[0]
        method_name = statement[1]
        method_args = statement[2:]

        evaluated_who = self.fields.get(who)
        
        if who == self.console.ME_DEF:
            class_name = self.console.MAIN_CLASS_DEF
        elif evaluated_who is not None:
            class_name = evaluated_who.class_name
        else:
            self.console.error(errno.NAME_ERROR)

        evaluated_method_args = []
        
        for arg in method_args:
            if isinstance(arg, list):
                evaluated_method_args.append(self.run_statement(arg))
            else:
                evaluated_method_args.append(arg)
            

        class_object = Object(class_name, self.console)

        return class_object.run(method_name, evaluated_method_args)

    # if statement
    # statement takes the form:
    # statement[0] = boolean expression
    # statement[1] = expression was true
    # statement[2] = expression was false
    def if_statement(self, statement):
        # print(f'now running: {statement}')
        condition = statement[0]
        true_statement = statement[1]

        if len(statement) < 3:
            false_statement = None
        else:
            false_statement = statement[2]

        if self.evaluate_condition(condition):
            return self.run_statement(true_statement)
        else:
            if false_statement is not None:
                return self.run_statement(false_statement)


    def begin_statement(self, statement):
        for nested_statement in statement:
            self.run_statement(nested_statement)

        
    def while_statement(self, statement):
        condition = statement[0]
        true_statement = statement[1]
        while self.operator.parse_binary_operator(condition):
            self.run_statement(true_statement)


    def set_statement(self, set_name, set_value):
        # print(f'arg is: {set_name}, {set_value}')
        if isinstance(set_value, list):
            self.fields[set_name] = self.run_statement(set_value)
        else:
            self.fields[set_name] = set_value.strip('"')

    def new_statement(self, class_name):
        # statement takes the form:
        # statement[0] = class_name

        if self.find_class(class_name) is None:
            self.console.error(errno.TYPE_ERROR)
            
        return Object(class_name, self.console)
        
    def replace_arg_with_argv(self, tokens, arg, argv):
        for i, token in enumerate(tokens):
            if isinstance(token, list):
                self.replace_arg_with_argv(token, arg, argv)
            else:
                if token == arg:
                    tokens[i] = argv

    def find_method(self, class_name, method_name):
        return self.console.classes.get(class_name).methods.get(method_name)

    def find_class(self, class_name):
        return self.console.classes.get(class_name)
                    
    def valid_boolean(self, statement):
        if isinstance(statement, list):
            return True
        else:
            evaluated_statement = self.fields.get(statement)
            if evaluated_statement is not None:
                return evaluated_statement == self.console.TRUE_DEF or evaluated_statement == self.console.FALSE_DEF
            else:
                return statement == self.console.TRUE_DEF or statement == self.console.FALSE_DEF        
    
    def evaluate_condition(self, condition):
        if isinstance(condition, list):
            return self.operator.parse_binary_operator(condition)
        else:
            evaluated_condition = self.fields.get(condition)
            if evaluated_condition is not None:
                return evaluated_condition == self.console.TRUE_DEF or evaluated_condition != self.console.FALSE_DEF
            else:
                return condition == self.console.TRUE_DEF or condition != self.console.FALSE_DEF
