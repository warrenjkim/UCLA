from intbase import ErrorType as errno
from operators import Operators
from copy import copy, deepcopy


class Method:
    def __init__(self, name = "", args = {}):
        self.name = name
        self.args = args
        self.statements = []
        
    def add_statements(self, statements):
        self.statements.extend(statements)

    def bind_args(self, argv):
        i = 0
        for key in self.args:
            self.args[key] = argv[i]
            i += 1


class Class:
    def __init__(self, name = ""):
        self.name = name
        self.methods = { }
        self.fields = { }

    def add_method(self, name, args):
        self.methods[name] = Method(name, args)

    def add_field(self, name, value):
        self.fields[name] = value    


class Object:
    UNARY_OPERATOR = ['!']
    BINARY_OPERATORS = ['+', '-', '*', '%', '/', '<', '<=', '==', '!=', '>', '>=', '&', '|']
    OPERATORS = BINARY_OPERATORS + UNARY_OPERATOR
    
    def __init__(self, class_name, console):
        self.console = console                               # interpreter console
        self.class_name = class_name                         # current class name
        self.current_class = console.classes.get(class_name) # current class object
        self.methods = self.current_class.methods            # dictionary of methods
        self.fields = deepcopy(self.current_class.fields)    # dictionary of fields
        self.operator = Operators(self)                      # operator object
        self.current_method = None                           # current method object


    def __deepcopy__(self, memo):
        obj = self.__class__.__new__(self.__class__)
        memo[id(self)] = obj
        for k, v in self.__dict__.items():
            if k in ['fields']:
                setattr(obj, k, deepcopy(v, memo))
            else:
                setattr(obj, k, v)
        return obj


    def __validate_method(self, method, method_args):
        if method is None:
            return self.console.error(errno.NAME_ERROR)
        if len(method.args) != len(method_args):
            return self.console.error(errno.TYPE_ERROR)

            
    def run(self, method_name, method_args = []):
        # define the current method
        self.current_method = self.methods.get(method_name)

        # validate current method
        self.__validate_method(self.current_method, method_args)

        # bind the current method arguments
        self.current_method.bind_args(method_args)
        
        # deep copy statements
        statements = deepcopy(self.current_method.statements)
                
        # run each statement         
        for statement in statements:
            # check for a return value
            return_value = self.run_statement(statement)
            if return_value == self.console.RETURN_DEF:
                return
            if return_value is not None:
                # terminate the loop if we return a value
                return return_value

        # return a value (if any)
        return return_value

    def run_statement(self, statement):
        #print(f'now running: {statement}')
        # check each token
        for i, token in enumerate(statement):

            # [noreturn]
            # token is print
            if token == self.console.PRINT_DEF:
                self.print_statement(statement[i + 1:])

            # return the evaluated expression
            # token is an operator
            elif token in self.OPERATORS:
                return self.operator.parse_operator(statement)

            # [noreturn]
            # token is inputi
            elif token == self.console.INPUT_INT_DEF:
                self.inputi_statement(statement[i + 1])

            # [noreturn]
            # token is inputs
            elif token == self.console.INPUT_STRING_DEF:
                self.inputs_statement(statement[i + 1])

            # return the evaluated call statement
            # token is call
            elif token == self.console.CALL_DEF:
                return self.call_statement(statement[i + 1:])

            # return the return value
            # token is return
            elif token == self.console.RETURN_DEF:
                return self.return_statement(statement)

            # return the evaluated if block(s)
            # token is if
            elif token == self.console.IF_DEF:
                return self.if_statement(statement[i + 1:])

            # return the evaluated begin block
            # token is begin
            elif token == self.console.BEGIN_DEF:
                return self.begin_statement(statement[i + 1:])

            # return the evaluated while block
            # token is while
            elif token == self.console.WHILE_DEF:
                return self.while_statement(statement[i + 1:])

            # [noreturn]
            # token is set
            elif token == self.console.SET_DEF:
                self.set_statement(statement[i + 1:])

            # return the new object reference
            # token is new
            elif token == self.console.NEW_DEF:
                return self.new_statement(statement[i + 1])


    def return_statement(self, statement):
        # print(f'methods: {self.methods}')
        # print(f'returning from: {self.current_method}')
        # deep copy the statement
        statement = deepcopy(statement)

        # evaluate all identifiers in the statement
        self.evaluate_all_identifiers(statement)

        # there is no return value
        if len(statement) == 1:
            return self.console.RETURN_DEF

        # there are nested calls
        if isinstance(statement[1], list):
            return self.run_statement(statement[1])

        # singular return value (potentially an indirection)
        else:
            evaluated_method = self.current_method.args.get(statement[1]) # method identifier evaluated
            evaluated_field = self.fields.get(statement[1])               # field indentifier evaluated

            # check local first
            if evaluated_method is not None:
                return evaluated_method

            # check fields next
            if evaluated_field is not None:
                return evaluated_field

            # finally return the value
            else:
                return statement[1]
    

    def print_statement(self, args):
        # deep copy args
        args = deepcopy(args)
        to_print = "" # value(s) to print
        
        for arg in args:            
            # no nested calls
            if not isinstance(arg, list):
                evaluated_method = self.current_method.args.get(arg) # method identifier evaluated
                evaluated_field = self.fields.get(arg)               # field identifier evaluated

                # check local first
                if evaluated_method is not None:
                    to_print += evaluated_method.strip('"')

                # check fields next
                elif evaluated_field is not None:
                    to_print += evaluated_field.strip('"')

                # if true
                elif arg == self.console.TRUE_DEF:
                    to_print += self.console.TRUE_DEF
                    
                # if false
                elif arg == self.console.FALSE_DEF:
                    to_print += self.console.FALSE_DEF

                # argument is a constant (maybe)
                else:
                    # if the argument is an unknown identifier
                    try:
                        eval(arg)
                    except:
                        self.console.error(errno.NAME_ERROR)

                    # argument is a constant
                    to_print += str(arg.strip('"'))
            # there are nested calls
            else:
                value = self.run_statement(arg)
                # if value is a boolean, convert it to Brewin boolean types
                if isinstance(value, bool):
                    value = self.console.TRUE_DEF if value else self.console.FALSE_DEF
                    
                # strip the value of double quotes
                to_print += str(value).strip('"')

        # print the value(s) to the console
        self.console.output(to_print)


    # integer input
    def inputi_statement(self, name):
        method_var = self.current_method.args.get(name) # method identifier
        field_var = self.fields.get(name)               # field identifier
        value = self.console.get_input()                # inputted value

        # check if the value is an int
        if type(eval(value)) != int:
            self.console.error(errno.TYPE_ERROR)

        # bind to local first
        if method_var is not None:
            self.current_method.args[name] = value
            
        # bind to field next
        elif field_var is not None:
            self.fields[name] = value

        # invalid identifier
        else:
            self.console.output(errno.NAME_ERROR)


    # string input
    def inputs_statement(self, name):
        method_var = self.current_method.args.get(name) # method identifier
        field_var = self.fields.get(name)               # field identifier
        value = self.console.get_input()                # inputted value

        # check if the value is a str
        if not isinstance(value, str):
            self.console.error(errno.TYPE_ERROR)

        # bind to local first
        if method_var is not None:
            self.current_method.args[name] = value

        # bind to field next
        elif field_var is not None:
            self.fields[name] = value

        # invalid identifier
        else:
            self.console.output(errno.NAME_ERROR)


    # call statement
    def call_statement(self, statement):
        #print(f'calling: {statement}')
        # deep copy statement
        statement = deepcopy(statement)

        self.evaluate_all_identifiers(statement)
        #print(f'calling after: {statement}')
        who = statement[0]          # calling object
        method_name = statement[1]  # method name
        method_args = statement[2:] # method arguments

        # nested call
        if isinstance(who, list):
            who = self.run_statement(who)

        # if an object is passed in, run that object's method_name
        if isinstance(who, Object):
            return who.run(method_name, method_args)

        evaluated_method = self.current_method.args.get(who) # method identifier
        evaluated_field = self.fields.get(who)               # field identifier
        class_itself = self.console.classes.get(who)         # class object

        if isinstance(evaluated_method, Object):
            return evaluated_method.run(method_name, method_args)
        
        if isinstance(evaluated_field, Object):
            return evaluated_field.run(method_name, method_args)

        # who is 'me'
        if who == self.console.ME_DEF:
            class_name = self.console.MAIN_CLASS_DEF
            
        # who is the class exists, bind to that class
        elif class_itself is not None:
            class_name = who

        elif who == self.console.NULL_DEF:
            self.console.error(errno.FAULT_ERROR)
        # invalid class identifier
        else:
            self.console.error(errno.TYPE_ERROR)

        # evaluate the method arguments
        evaluated_method_args = []

        # if invalid identifier
        if self.find_method(class_name, method_name) is None:
            self.console.error(errno.NAME_ERROR)

        # if the wrong number of parameters was passed in
        if len(method_args) != len(self.find_method(class_name, method_name).args):
            self.console.error(errno.TYPE_ERROR)

        # evaluate all method arguments
        self.evaluate_all_identifiers(method_args)

        # for each argument, evaluate each argument if nested
        for arg in method_args:
            # argument is nested
            if isinstance(arg, list):
                evaluated_method_args.append(self.run_statement(arg))

            # argument is a constant
            else:
                evaluated_method_args.append(arg)


        if who == self.console.ME_DEF:
            return self.run(method_name, evaluated_method_args)
        
        # create a new class object
        class_object = Object(class_name, self.console)

        # return the return value of the method
        return class_object.run(method_name, evaluated_method_args)

    # if statement
    # statement takes the form:
    def if_statement(self, statement):
        # deep copy the statement
        statement = deepcopy(statement)
        
        condition = statement[0]      # if condition
        true_statement = statement[1] # statement to run if true

        # if the condiiton is not a valid boolean, error
        if not self.valid_boolean(condition):
            self.console.error(errno.TYPE_ERROR)

        # if there is no false statement, don't try and run one
        if len(statement) < 3:
            false_statement = None

        # there is a false statement
        else:
            false_statement = statement[2]

        # the evaluated condition is true
        if self.evaluate_condition(condition):
            return self.run_statement(true_statement)

        # the evaluated condition is false
        else:
            # if there is a false statement, run it
            if false_statement is not None:
                return self.run_statement(false_statement)


    def begin_statement(self, statement):
        # run each statement in the begin block
        for nested_statement in statement:
            # check for a return value
            return_value = self.run_statement(nested_statement)
            
            # if there is a return value, terminate and return
            if return_value is not None:
                return return_value

    
    def while_statement(self, statement):
        # deep copy the statement
        statement = deepcopy(statement)

        condition = statement[0]
        true_statement = statement[1]
        
        # if the condition is not a valid boolean, error
        if not self.valid_boolean(condition):
            self.console.error(errno.TYPE_ERROR)

        # run the while loop
        while self.evaluate_condition(condition):
            # check for a return value
            return_value = self.run_statement(true_statement)

            # if there is a return value, terminate and return
            if return_value is not None:
                return return_value

            
    def set_statement(self, statement):
        set_name = statement[0]
        set_value = statement[1]
        
        if isinstance(set_value, list):
            set_value = deepcopy(set_value)
            self.evaluate_all_identifiers(set_value)
            set_value = self.run_statement(set_value)
        else:
            set_value = [deepcopy(set_value)]
            self.evaluate_all_identifiers(set_value)
            set_value = set_value[0]
            
        if self.fields.get(set_name) is None and self.current_method.args.get(set_name) is None:
            self.console.error(errno.NAME_ERROR)

        method_var = self.current_method.args.get(set_name)
        if method_var is not None:
            self.current_method.args[set_name] = set_value
            return

        if self.fields.get(set_name) is None:
            self.console.error(errno.NAME_ERROR)

        self.fields[set_name] = set_value
        return


    def new_statement(self, class_name):
        if self.find_class(class_name) is None:
            self.console.error(errno.TYPE_ERROR)
            
        return Object(class_name, self.console)


    def evaluate_args(self, statement, args):
        for key in args:
            self.replace_arg_with_argv(statement, key, args[key])

    
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
        #print(f'all classes: {self.console.classes}')
        return self.console.classes.get(class_name)


    def evaluate_all_identifiers(self, statement):
        self.evaluate_args(statement, self.current_method.args)
        self.evaluate_args(statement, self.fields)

    def valid_boolean(self, statement):
        statement = deepcopy(statement)
        self.evaluate_args(statement, self.current_method.args)
        self.evaluate_args(statement, self.fields)
        #print(f'valid boolean statement: {statement}')
        if isinstance(statement, list):
            if isinstance(self.evaluate_condition(statement), bool):
                return True
            else:
                return False
        else:
            evaluated_statement = self.fields.get(statement)
            if evaluated_statement is not None:
                return evaluated_statement == self.console.TRUE_DEF or evaluated_statement == self.console.FALSE_DEF
            else:
                return statement == self.console.TRUE_DEF or statement == self.console.FALSE_DEF        

    
    def evaluate_condition(self, condition):
        condition = deepcopy(condition)

        self.evaluate_all_identifiers(condition)
        if isinstance(condition, list):
            return self.operator.parse_operator(condition)
        else:
            evaluated_condition = self.fields.get(condition)
            if evaluated_condition is not None:
                return evaluated_condition == self.console.TRUE_DEF or evaluated_condition != self.console.FALSE_DEF
            else:
                return condition == self.console.TRUE_DEF or condition != self.console.FALSE_DEF
