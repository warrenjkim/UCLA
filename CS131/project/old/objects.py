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
        self.console = console                                # interpreter console
        self.class_name = class_name                          # current class name
        self.current_class = console.classes.get(class_name)  # current class object
        self.methods = self.current_class.methods             # dictionary of methods
        self.fields = deepcopy(self.current_class.fields)     # dictionary of fields
        self.operator = Operators(self)                       # operator object
        self.current_method = None                            # current method object



    def __deepcopy__(self, memo):
        obj = self.__class__.__new__(self.__class__)
        memo[id(self)] = obj
        for k, v in self.__dict__.items():
            if k in ['fields']:
                setattr(obj, k, deepcopy(v, memo))
            else:
                setattr(obj, k, v)
        return obj



    def run(self, method_name, method_args = []):
        self.current_method = self.methods.get(method_name)      # define the current method
        self.__validate_method(self.current_method, method_args) # validate current method
        self.current_method.bind_args(method_args)               # bind the current method arguments
        self.operator.set_object(self)                           # tbh idk why i need to do this but i do
        statements = deepcopy(self.current_method.statements)    # deep copy statements

        for statement in statements:                             # run each statement         
            return_value = self.run_statement(statement)         # check for a return value
            if return_value == self.console.NOTHING_DEF:         # if there is no return value, return None
                return
            if return_value is not None:                         # terminate the loop if we return a value
                return return_value
            
        return return_value                                      # return a value (if any)



    def run_statement(self, statement):
        for i, token in enumerate(statement):                    # check each token
            if token == self.console.RETURN_DEF:                 # (return) return the return value
                return self.return_statement(statement)
            elif token == self.console.CALL_DEF:                 # (call) return evaluated method
                return self.call_statement(statement[i + 1:])
            elif token == self.console.IF_DEF:                   # (if) return evaluated statement(s)
                return self.if_statement(statement[i + 1:])
            elif token == self.console.WHILE_DEF:                # (while) return evaluated statement(s)
                return self.while_statement(statement[i + 1:])
            elif token == self.console.BEGIN_DEF:                # (begin) return evaluated statement(s)
                return self.begin_statement(statement[i + 1:])            
            elif token == self.console.NEW_DEF:                  # (new) return object reference to new object
                return self.new_statement(statement[i + 1])      
            elif token == self.console.PRINT_DEF:                # (print) [noreturn]
                self.print_statement(statement[i + 1:])
            elif token == self.console.SET_DEF:                  # (set) [noreturn]
                self.set_statement(statement[i + 1:])                
            elif token == self.console.INPUT_INT_DEF:            # (inputi) [noreturn]
                self.inputi_statement(statement[i + 1])  
            elif token == self.console.INPUT_STRING_DEF:         # (inputs) [noreturn]
                self.inputs_statement(statement[i + 1])
            elif token in self.OPERATORS:                        # (operator) return evaluated expression
                return self.operator.parse_operator(statement)                       
            



    # RETURN STATEMENT
    def return_statement(self, statement):
        statement = deepcopy(statement)               # deep copy the statement
        self.evaluate_all_identifiers(statement)      # evaluate all identifiers in the statement

        if len(statement) == 1:                       # there is no return value
            return self.console.NOTHING_DEF
        if isinstance(statement[1], list):            # there are nested calls
            return self.run_statement(statement[1])
        else:                                         # singular return value (potentially an indirection)
            method_var = self.current_method.args.get(statement[1])  # method identifier evaluated
            field_var = self.fields.get(statement[1])                # field indentifier evaluated

            if method_var is not None:                # check local first
                return method_var 
            if field_var is not None:                 # check fields next
                return field_var
            else:                                     # finally return the value
                return statement[1]



    # CALL STATEMENT
    def call_statement(self, statement):
        statement = deepcopy(statement)                 # deep copy statement
        self.evaluate_all_identifiers(statement)
        who = statement[0]                              # caller
        method_name = statement[1]                      # method name
        method_args = statement[2:]                     # method arguments
        owner = None                                    # calling object
        
        if isinstance(who, list):                       # nested call
            who = self.run_statement(who)

        method_var = self.current_method.args.get(who)  # method identifier
        field_var = self.fields.get(who)                # field identifier

        # determine who the owner is
        if who == self.console.ME_DEF:                  # me (main)
            owner = self
        elif isinstance(who, Object):                   # object itself
            owner = who
        elif isinstance(method_var, Object):            # method identifier
            owner = method_var
        elif isinstance(field_var, Object):             # field identifier
            owner = field_var
        elif who == self.console.NULL_DEF:              # null reference: error
            self.console.error(errno.FAULT_ERROR)
        else:                                           # unknown identifier
            self.console.error(errno.NAME_ERROR)

        evaluated_method_args = []                      # evaluate the method arguments

        self.__validate_method(owner.methods.get(method_name), method_args)

        for arg in method_args:                         # for each argument, evaluate each argument if nested
            if isinstance(arg, list):                   # argument is nested
                evaluated_method_args.append(self.run_statement(arg))
            else:                                       # argument is a constant
                evaluated_method_args.append(arg)
        
        return owner.run(method_name, evaluated_method_args) # return the return value of the method



    # IF STATEMENT
    def if_statement(self, statement):
        statement = deepcopy(statement)                      # deep copy the statement       
        condition = statement[0]                             # if condition
        true_statement = statement[1]                        # statement to run if true

        if not self.valid_boolean(condition):                # if the condiiton is not a valid boolean, error
            self.console.error(errno.TYPE_ERROR)
 
        if len(statement) < 3:                               # if there is no false statement, don't try and run one
            false_statement = None                           # there is no false statement
        else:                                                # there is a false statement
            false_statement = statement[2]

        if self.evaluate_condition(condition):               # the evaluated condition is true
            return self.run_statement(true_statement)
        else:                                                # the evaluated condition is false
            if false_statement is not None:                  # if there is a false statement, run it
                return self.run_statement(false_statement)



    # WHILE STATEMENT
    def while_statement(self, statement):
        statement = deepcopy(statement)                         # deep copy the statement
        condition = statement[0]                                # boolean condition
        true_statement = statement[1]                           # statement to run if true
    
        if not self.valid_boolean(condition):                   # if the condition is not a valid boolean, error
            self.console.error(errno.TYPE_ERROR)

        while self.evaluate_condition(condition):               # run the while loop
            return_value = self.run_statement(true_statement)   # check for a return value
            if return_value is not None:                        # if there is a return value, terminate and return
                return return_value



    # BEGIN STATEMENT
    def begin_statement(self, statement):
        for nested_statement in statement:                       # run each statement in the begin block
            return_value = self.run_statement(nested_statement)  # check for a return value
            if return_value is not None:                         # if there is a return value, terminate and return
                return return_value



    # NEW STATEMENT
    def new_statement(self, class_name):
        if self.console.classes.get(class_name) is None:  # fetch class
            self.console.error(errno.TYPE_ERROR)          # invalid identifier
            
        return Object(class_name, self.console)           # return a new Object instance



    # [noreturn]
    def print_statement(self, args):
        args = deepcopy(args)                    # deep copy args
        to_print = ""                            # value(s) to print
        self.evaluate_all_identifiers(args)    
        
        for arg in args:                         # for each argument
            if isinstance(arg, list):            # check for nested calls
                value = self.run_statement(arg)  # value = evaluated expression(s)
                if isinstance(value, bool):      # if value is a boolean, convert it to Brewin boolean types
                    value = self.console.TRUE_DEF if value else self.console.FALSE_DEF
    
                to_print += str(value).strip('"')    # strip double quotes
            else:                                    # no nested calls
                try:                                 # try to evaluate argument
                    eval(arg)
                    to_print += str(arg).strip('"')  # strip double quotes
                except:
                    if arg in self.current_method.args:                            # check for method identifiers first
                        to_print += str(self.current_method.args[arg]).strip('"')  #  strip double quotes
                        continue
                    if arg in self.fields:                                         # then check for field identifiers
                        to_print += str(self.fields[arg]).strip('"')               # strip double quotes
                        continue
                    if arg == self.console.TRUE_DEF:                               # check for boolean types
                        to_print += str(self.console.TRUE_DEF).strip('"')
                        continue
                    if arg == self.console.FALSE_DEF:
                        to_print += str(self.console.FALSE_DEF).strip('"')
                        continue
                    else:                                                          # unknown identifier
                        self.console.error(errno.NAME_ERROR)

        self.console.output(to_print)                                              # print the value(s) to the console



    # SET STATEMENT
    def set_statement(self, statement):
        set_name = statement[0]                        # set name
        set_value = statement[1]                       # set value
        
        if isinstance(set_value, list):                # check for nested calls
            set_value = deepcopy(set_value)
            self.evaluate_all_identifiers(set_value)
            set_value = self.run_statement(set_value)  # evaluate expression(s)
        else:
            set_value = [deepcopy(set_value)]          # evaluate identifier
            self.evaluate_all_identifiers(set_value)
            set_value = set_value[0]

        method_var = self.current_method.args.get(set_name)  # check for method identifiers first
        if method_var is not None:                           # method identifier exists
            self.current_method.args[set_name] = set_value
            return

        if self.fields.get(set_name) is None:                # check for field identifiers next
            self.console.error(errno.NAME_ERROR)             # invalid identifier

        self.fields[set_name] = set_value



    # [noreturn]
    def inputi_statement(self, name):
        method_var = self.current_method.args.get(name) # method identifier
        field_var = self.fields.get(name)               # field identifier
        value = self.console.get_input()                # inputted value

        if type(eval(value)) != int:                    # check if the value is an int
            self.console.error(errno.TYPE_ERROR)
        if method_var is not None:                      # bind to local first
            self.current_method.args[name] = value
        elif field_var is not None:                     # bind to field next
            self.fields[name] = value
        else:                                           # invalid identifier
            self.console.output(errno.NAME_ERROR)



    # [noreturn]
    def inputs_statement(self, name):
        method_var = self.current_method.args.get(name)        # method identifier
        field_var = self.fields.get(name)                      # field identifier
        value = self.console.get_input()                       # inputted value

        if not isinstance(value, str):                         # check if the value is a str
            self.console.error(errno.TYPE_ERROR)
        if method_var is not None:                             # bind to local first
            self.current_method.args[name] = '"' + value + '"'
        elif field_var is not None:                            # bind to field next
            self.fields[name] = '"' + value + '"'
        else:                                                  # invalid identifier
            self.console.output(errno.NAME_ERROR)



    ##### HELPER FUNCTIONS #####

    def evaluate_all_identifiers(self, statement):
        self.__evaluate_args(statement, self.current_method.args)
        self.__evaluate_args(statement, self.fields)


    
    def __evaluate_args(self, statement, args):
        for key in args:
            self.replace_arg_with_argv(statement, key, args[key])



    def replace_arg_with_argv(self, tokens, arg, argv):
        for i, token in enumerate(tokens):
            if isinstance(token, list):
                self.replace_arg_with_argv(token, arg, argv)                
            else:
                if token == arg:
                    tokens[i] = argv


    def __validate_method(self, method, method_args):
        if method is None:
            return self.console.error(errno.NAME_ERROR)
        if len(method.args) != len(method_args):
            return self.console.error(errno.TYPE_ERROR)


        
    def valid_boolean(self, statement):
        statement = deepcopy(statement)
        self.evaluate_all_identifiers(statement)
        
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

