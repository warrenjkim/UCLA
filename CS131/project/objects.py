from intbase import ErrorType as errno
from operators import Operators
from copy import copy, deepcopy
from brewintypes import Type, typeof, type_to_enum
from variable import Variable, evaluate, stringify



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

    def __init__(self, name, console):
        self.console = console                                # interpreter console
        self.name = name
        self.vtype = self.name                                # current class vtype  maybe Object
        
        self.current_class = console.classes.get(name)        # current class object
        self.fields = deepcopy(self.current_class.fields)     # dictionary of fields        

        self.methods = self.current_class.methods             # dictionary of methods
        self.current_method = None                            # current method object
        self.operator = Operators(self)                       # operator object

        self.parent = None         # parent class (None by default)
        self.m_stack = [self.current_class.methods]


        
    def run(self, method_name, method_args = []):
        self.current_method = self.methods.get(method_name)      # define the current method
        self.validate_method(self, self.current_method.name, method_args) # validate current method
        self.current_method.bind_args(method_args)               # bind the current method arguments
        self.operator.set_object(self)                           # tbh idk why i need to do this but i do
        statements = deepcopy(self.current_method.statements)    # deep copy statements
        self.evaluate_all_identifiers(statements)


        for statement in statements:                             # run each statement         
            return_value = self.run_statement(statement)         # check for a return value
            if return_value == Type.VOID:                        # if there is no return value, return VOID
                return None
            if return_value is not None:                         # terminate the loop if we return a value
                return return_value

        return stringify(evaluate(self.default_return()))



    def run_statement(self, statement):
        # print(f'name: {self.current_method.type()}')
#        print(f'running:{statement}')
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
    # replace that part with begin
    def let_statement(self, statement):
        self.push_local_variables(statement[0])           # push scope onto stack
        self.evaluate_all_identifiers(statement)          # evaluate all identifiers
        for statement in statement[1:]:                   # run each statement
            return_value = self.run_statement(statement)  # check for a return value
            if return_value is not None:
                self.current_method.local_stack.pop(0)    # pop scope from stack
                return return_value                       # return

        if len(self.current_method.local_stack) != 0:
            self.current_method.local_stack.pop(0)            # pop scope from stack



    # RETURN STATEMENT
    def return_statement(self, statement):
        if len(statement) == 1:                                         # there is no return value
            return self.return_value(Type.VOID)
        elif isinstance(statement[1], list):                            # there are nested calls
            return self.return_value(self.run_statement(statement[1]))  # evaluate statement(s)
        else:
            return self.return_value(statement[1])                      # call return helper


    # CALL STATEMENT
    def call_statement(self, statement):
        who = statement[0]                              # caller
        method_name = statement[1]                      # method name
        method_args = statement[2:]                     # method arguments
        owner = None                                    # calling object
        
        if isinstance(who, list):                       # nested call
            who = self.run_statement(who)

        # determine who the owner is
        if who == self.console.ME_DEF:                  # me (main)
            owner = self
        elif isinstance(who, Object):                   # object itself
            owner = who
        elif isinstance(who, Variable):
            owner = who.value
            if owner == Type.NULL:
                self.console.error(errno.FAULT_ERROR)
        
        if owner is None:                               # unknown identifier
            self.console.error(errno.NAME_ERROR)

        evaluated_method_args = []                      # evaluate the method arguments

        self.validate_method(owner, method_name, method_args)
        owner = self.determine_polymorphic_function(owner, method_name, method_args)

        for arg in method_args:                         # for each argument, evaluate each argument if nested
            if isinstance(arg, list):                   # argument is nested
                arg = owner.run_statement(arg)
            elif isinstance(arg, Variable):             # argument is a variable
                arg = arg.value
            elif arg == self.console.ME_DEF:            # argument is a constant
                arg = self

            evaluated_method_args.append(arg)

        print(f'evaluated: {evaluated_method_args}')
        return owner.run(method_name, evaluated_method_args)  # return the return value of the method



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



    def build_hierarchy(self):
        parent_class = self.current_class.super_class

        if parent_class is None:
            return None

        self.parent = Object(parent_class, self.console)
        self.parent.build_hierarchy()


    
    # NEW STATEMENT
    def new_statement(self, class_name):
        if self.console.classes.get(class_name) is None:  # fetch class
            return self.console.error(errno.TYPE_ERROR)          # invalid identifier

        obj = Object(class_name, self.console)
        obj.build_hierarchy()

        return obj

    

    # [noreturn]
    def print_statement(self, args):
        to_print = ""
        for arg in args:
            if isinstance(arg, list):
                value = evaluate(self.run_statement(arg))
                if isinstance(value, bool):
                    value = self.console.TRUE_DEF if value else self.console.FALSE_DEF
                to_print += str(value)
            elif isinstance(arg, Variable):
                if arg.vtype == Type.BOOL:
                    to_print += self.console.TRUE_DEF if evaluate(arg.value) else self.console.FALSE_DEF
                    continue
                elif arg.value == Type.NULL:
                    to_print += 'None'
                    continue
                to_print += str(evaluate(arg.value))
            else:
                value = evaluate(arg)

                if isinstance(value, errno):
                    return self.console.error(value)
                
                if value == Type.NULL:
                    to_print += 'None'
                    continue
                if isinstance(value, bool):
                    value = self.console.TRUE_DEF if value else self.console.FALSE_DEF
                to_print += str(value)
                
        self.console.output(to_print)              # print the value(s) to the console


        
    def set_statement(self, statement):
        variable = statement[0]  # variable object (name, vtype, value)
        value    = statement[1]  # value to be assigned

        #print(f'set called: {variable, value}')

        if not isinstance(variable, Variable):           # invalid identifier
            return self.console.error(errno.NAME_ERROR)

        # variable to variable assignment
        if isinstance(value, Variable):
            if isinstance(value.value, Object):
                self.validate_type(variable.type(), value.value)
            else:
                self.validate_type(variable.type(), evaluate(value))

            variable.value = value.value
            return

        # can't assign null to primitives
        if value == Type.NULL and isinstance(variable.vtype, Type):
            return self.console.error(errno.TYPE_ERROR)
        
        if isinstance(value, list):
            value = self.run_statement(value)

        if isinstance(value, Object): # check this
            variable.value = value
            return
        else:
            value = evaluate(value)

        self.validate_type(variable.type(), value)

        variable.value = stringify(value)


    # [noreturn]
    def inputi_statement(self, name):
        value = self.console.get_input()
        self.validate_type(name.type(), evaluate(value))
        name.value = value



    # [noreturn]
    def inputs_statement(self, name):
        value = self.console.get_input()
        self.validate_type(name.type(), value)
        name.value = stringify(value)



    ##### HELPER FUNCTIONS #####

    def evaluate_all_identifiers(self, statement):
        self.replace_null(statement)
        for variable in self.current_method.local_stack:
            self.evaluate_args(statement, variable)
            
        self.evaluate_args(statement, self.current_method.args)
        self.evaluate_args(statement, self.fields)


    
    def evaluate_args(self, statement, args):
        for key in args:
            self.replace_arg_with_argv(statement, key, args[key])


    def replace_null(self, tokens):
        for i, token in enumerate(tokens):
            if isinstance(token, list):
                self.replace_null(token)                
            else:
                if token == self.console.NULL_DEF:
                    tokens[i] = Type.NULL

    def replace_arg_with_argv(self, tokens, arg, argv):
        for i, token in enumerate(tokens):
            if isinstance(token, list):
                self.replace_arg_with_argv(token, arg, argv)                
            else:
                if token == arg:
                    tokens[i] = argv


    def validate_method(self, owner, method_name, method_args = []):
        if owner is None:
            return self.console.error(errno.NAME_ERROR)

        if owner.methods.get(method_name) is None:
            return self.validate_method(owner.parent, method_name, method_args)
        
        method = owner.methods.get(method_name)
        
        if len(method.args) != len(method_args):
            return self.validate_method(owner.parent, method_name, method_args)

        if not self.valid_args(method, method_args):
            return self.validate_method(owner.parent, method_name, method_args)
            

    def valid_args(self, method, method_args):
        i = 0
        for key, arg in method.args.items():
            try:
                self.validate_type(arg.type(), evaluate(method_args[i]))
                i += 1
            except:
                return False

        return True
            

    def valid_boolean(self, statement):
        if isinstance(statement, list):
            if isinstance(self.evaluate_condition(statement), bool):
                return True
            else:
                return False
        else:
            return statement == self.console.TRUE_DEF or statement == self.console.FALSE_DEF        

    
    def evaluate_condition(self, condition):
        if isinstance(condition, list):
            return evaluate(self.operator.parse_operator(condition))
        else:
            return condition == self.console.TRUE_DEF or condition != self.console.FALSE_DEF


    def evaluate_variables(self, variables):
        for i, variable in enumerate(variables):
            if isinstance(variable, Variable):
                variables[i] = variable.value                


    def default_return(self):
        method_type = self.current_method.type()

        if method_type == Type.INT:     # method expects and int
            return "0"
        if method_type == Type.STRING:  # method expects a string
            return "''"
        if method_type == Type.BOOL:    # method expects a bool
            return "False"
        if method_type == Type.OBJECT:  # method expects an object
            return "None"
        if method_type == Type.VOID:    # method expects None
            return "None"
            
        

    def return_value(self, return_value):
        vtype = self.current_method.type()                   # method return type

        if return_value == self.console.ME_DEF:              # if return me, return self
            self.validate_type(vtype, self)
            return self
            
        if return_value == Type.VOID:                        # if return type is void, return default value
            return self.default_return()

        return_value = evaluate(return_value)
        
        if isinstance(return_value, errno):
            return self.console.error(return_value)
        
        self.validate_type(vtype, return_value)
        
        return stringify(return_value)                       # stringify the return value


    def push_local_variables(self, variables):
        scope = { }

        variable_names = [variable[1] for variable in variables]
        if len(set(variable_names)) != len(variable_names):
            return self.console.error(errno.NAME_ERROR)
        
        for variable in variables:
            vtype = variable[0]
            name = variable[1]
            value = evaluate(variable[2])
            self.validate_type(type_to_enum(vtype), value)
            scope[name] = Variable(name, type_to_enum(vtype), stringify(value))

        self.current_method.local_stack.insert(0, scope)


    def validate_type(self, var_type, value):
        if var_type == Type.VOID:
            if value is not None:
                return self.console.error(errno.TYPE_ERROR)
            
        if value == Type.NULL:
            return
        if isinstance(value, Object):
            if var_type != value.vtype:
                if value.parent is None:
                    return self.console.error(errno.TYPE_ERROR)
                else:
                    return self.validate_type(var_type, value.parent)

        if typeof(value) == Type.BOOL:
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


    def determine_polymorphic_function(self, owner, method_name, method_args):
        if owner is None:
            return self.console.error(errno.NAME_ERROR)

        if owner.methods.get(method_name) is None:
            return self.determine_polymorphic_function(owner.parent, method_name, method_args)
        
        method = owner.methods.get(method_name)
        if len(method.args) != len(method_args):
            return self.determine_polymorphic_function(owner.parent, method_name, method_args)

        return owner

    
# 200/205
# 79/90 gc
