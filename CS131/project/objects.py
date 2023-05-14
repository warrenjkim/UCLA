from intbase import ErrorType as errno
from operators import Operators
from copy import copy, deepcopy
from brewintypes import Type, typeof
from variable import Variable



class Method:
    def __init__(self, name, vtype, args = {}, statements = []):
        self.name = name
        self.vtype = vtype
        self.args = args
        self.local_stack = []
        self.statements = [statements]
        
    def bind_args(self, argv): ####### ADD TYPE CHECKING
        i = 0
        for key in self.args:
            self.args[key].set_value(argv[i])
            i += 1

    def __repr__(self):
        return str(self.__dict__)
    
    def __str__(self):
        return str(self.__dict__)

    def type(self):
        return self.vtype



class Class:
    def __init__(self, name, super_class = None):
        self.name = name
        self.super_class = super_class
        self.methods = { }
        self.fields = { }

    def add_method(self, name, vtype, args, statements):
        self.methods[name] = Method(name, vtype, args, statements)

    def set_field(self, name, vtype, value):
        self.fields[name] = Variable(name, vtype, value)

    def __repr__(self):
        return str(self.__dict__)
    
    def __str__(self):
        return str(self.__dict__)




class Object:
    UNARY_OPERATOR = ['!']
    BINARY_OPERATORS = ['+', '-', '*', '%', '/', '<', '<=', '==', '!=', '>', '>=', '&', '|']
    OPERATORS = BINARY_OPERATORS + UNARY_OPERATOR


    
    def __init__(self, vtype, console, parent = None):
        self.console = console                                # interpreter console
        
        self.vtype = vtype                                      # current class vtype
        self.parent = parent                                  # parent class (None by default)
        
        self.current_class = console.classes.get(vtype)  # current class object
        self.fields = deepcopy(self.current_class.fields)     # dictionary of fields        

        self.methods = self.current_class.methods             # dictionary of methods
        self.current_method = None                            # current method object
        self.operator = Operators(self)                       # operator object



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
        self.evaluate_all_identifiers(statements)

        for statement in statements:                             # run each statement         
            return_value = self.run_statement(statement)         # check for a return value
            if return_value == self.console.NOTHING_DEF:         # if there is no return value, return None
                return
            if return_value is not None:                         # terminate the loop if we return a value
                return return_value
            
        return return_value                                      # return a value (if any)



    def run_statement(self, statement):
        # print(f'name: {self.current_method.type()}')
        # print(f'running:{statement}')
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

            #### LET STATEMENT WIP
            elif token == self.console.LET_DEF:
                return self.let_statement(statement[i + 1:])     # (let) return evaluated statement(s)
    



    # LET STATEMENT
    def let_statement(self, statement):
        self.__push_local_variables(statement[0])
        self.evaluate_all_identifiers(statement)
        for statement in statement[1:]:
            return_value = self.run_statement(statement)
            if return_value is not None:
                self.current_method.local_stack.pop(0)
                return return_value
            



    # RETURN STATEMENT
    def return_statement(self, statement):
        if len(statement) == 1:                       # there is no return value
            return self.__return_value()
        if isinstance(statement[1], list):            # there are nested calls
            return self.__return_value(self.run_statement(statement[1]))
        if isinstance(statement[1], Variable):
            return self.__return_value(statement[1].value)
        else:                                         # singular return value (potentially an indirection)
            method_var = self.current_method.args.get(statement[1])  # method identifier evaluated
            field_var = self.fields.get(statement[1])                # field indentifier evaluated


            if method_var is not None:                # check local first
                return self.__return_value(method_var.value)
            if field_var is not None:                 # check fields next
                return self.__return_value(field_var.value)
            if isinstance(statement[1], Variable):
                return self.__return_value(statement[1].value)
            else:                                     # finally return the value
                return self.__return_value(statement[1])



    # CALL STATEMENT
    def call_statement(self, statement):
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
        to_print = ""                            # value(s) to print
        self.evaluate_all_identifiers(args)
        
        for arg in args:                         # for each argument
            if isinstance(arg, list):            # check for nested calls
                value = self.run_statement(arg)  # value = evaluated expression(s)
                if isinstance(value, bool):      # if value is a boolean, convert it to Brewin boolean vtypes
                    value = self.console.TRUE_DEF if value else self.console.FALSE_DEF
    
                to_print += str(value).strip('"')    # strip double quotes
            else:                                    # no nested calls
                if not isinstance(arg, Variable):
                    if '"' not in arg:
                        return self.console.error(errno.NAME_ERROR)
                    
                    to_print += str(arg).strip('"')
                else:
                    to_print += str(arg.value)
                    
        self.console.output(to_print)                                              # print the value(s) to the console


    def typeof(self, value):
        if value == self.console.NULL_DEF:
            return self.console.NULL_DEF
        elif isinstance(value, int):
            return self.console.INT_DEF

        
    def set_statement(self, statement):
        variable = statement[0]  # variable object (name, vtype, value)
        value    = statement[1]  # value to be assigned

        if not isinstance(variable, Variable):
            return self.console.error(errno.NAME_ERROR)

        if isinstance(value, Variable):
            if variable.type() != value.type():
                return self.console.error(errno.TYPE_ERROR)

            variable.value = value.value
            return
        if value == Type.NULL:
            return self.console.error(errno.TYPE_ERROR)
        else:
            if isinstance(value, list):
                value = self.run_statement(value)
            self.__validate_type(variable.type(), value)
            variable.value = value
            return


    # [noreturn]
    def inputi_statement(self, name):
        value = self.console.get_input()
        self.__validate_type(name.type(), value)
        name.value = value



    # [noreturn]
    def inputs_statement(self, name):
        value = self.console.get_input()
        name.value = value



    ##### HELPER FUNCTIONS #####

    def evaluate_all_identifiers(self, statement):
        for variable in self.current_method.local_stack:
            self.__evaluate_args(statement, variable)
            
        self.__evaluate_args(statement, self.current_method.args)
        self.__evaluate_args(statement, self.fields)
        # self.__values(statement)


    
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
                if token == self.console.NULL_DEF:
                    tokens[i] = Type.NULL


    def __validate_method(self, method, method_args):
        if method is None:
            return self.console.error(errno.NAME_ERROR)
        if len(method.args) != len(method_args):
            return self.console.error(errno.TYPE_ERROR)


    def valid_boolean(self, statement):
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
        self.evaluate_all_identifiers(condition)
        if isinstance(condition, list):
            return self.operator.parse_operator(condition)
        else:
            evaluated_condition = self.fields.get(condition)
            if evaluated_condition is not None:
                return evaluated_condition == self.console.TRUE_DEF or evaluated_condition != self.console.FALSE_DEF
            else:
                return condition == self.console.TRUE_DEF or condition != self.console.FALSE_DEF


    def __evaluate_variables(self, variables):
        for i, variable in enumerate(variables):
            if isinstance(variable, Variable):
                variables[i] = variable.value                

    def __return_value(self, return_value = None):
        if return_value is None:
            return_value = self.console.NOTHING_DEF
        vtype = self.current_method.type()

        print(f'')

        if vtype == Type.INT:
            if return_value == self.console.NOTHING_DEF:
                return_value = 0
            if typeof(return_value) != Type.INT:
                return self.console.error(errno.TYPE_ERROR)            
        elif vtype == Type.BOOL:
            if return_value == self.console.NOTHING_DEF:
                return_value = False
            if typeof(return_value) != Type.BOOL:
                return self.console.error(errno.TYPE_ERROR)
        elif vtype == Type.STRING:
            if return_value == self.console.NOTHING_DEF:
                return_value = ""
            if typeof(return_value) != Type.STRING:
                return self.console.error(errno.TYPE_ERROR)
        elif isinstance(vtype, Object):
            if return_value == self.console.NOTHING_DEF:
                return_value = self.console.NULL_DEF
            if typeof(return_value) != Type.OBJECT:
                return self.console.error(errno.TYPE_ERROR)
        return return_value

    def __push_local_variables(self, variables):
        scope = { }
        for variable in variables:
            vtype = variable[0]
            name = variable[1]
            value = variable[2]
            self.__validate_type(Type(vtype), value)
            scope[name] = Variable(name, Type(vtype), value)

        self.current_method.local_stack.append(scope)

        


    def __validate_type(self, var_type, value):
        if value == Type.NULL:
            return
        if isinstance(value, Object):
            return
        if value == self.console.TRUE_DEF or value == self.console.FALSE_DEF:
            if var_type != Type.BOOL:
                return self.console.error(errno.TYPE_ERROR)
        if typeof(value) == Type.INT:
            if var_type != Type.INT:
                return self.console.error(errno.TYPE_ERROR)
        if typeof(value) == Type.STRING:
            if var_type != Type.STRING:
                return self.console.error(errno.TYPE_ERROR)

    def type(self):
        return self.vtype
