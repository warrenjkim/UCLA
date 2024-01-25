from intbase import ErrorType as errno
from operators import Operators
from copy import copy, deepcopy
from brewintypes import Type, typeof, type_to_enum
from variable import Variable, evaluate, stringify
from method import Method
from brewinclass import Class
from re import sub


class Object:
    UNARY_OPERATOR = ['!']
    BINARY_OPERATORS = ['+', '-', '*', '%', '/', '<', '<=', '==', '!=', '>', '>=', '&', '|']
    OPERATORS = BINARY_OPERATORS + UNARY_OPERATOR

    def __init__(self, name, console):
        self.console        = console                              # interpreter console
        self.templates      = console.templates                    # templates
        self.name           = name
        self.vtype          = self.name                            # current class vtype  maybe Object
        
        self.current_class  = console.classes.get(name)            # current class object
        self.fields         = deepcopy(self.current_class.fields)  # dictionary of fields

        self.methods        = self.current_class.methods           # dictionary of methods
        self.current_method = None                                 # current method object
        self.operator       = Operators(self)                      # operator object

        self.child          = None                                 # child class
        self.parent         = None                                 # parent class
        self.build_hierarchy()                                     # build object hierarchy

        self.exception_stack = []


    def __deepcopy__(self, memo = {}):
        return self


        
    def run(self, method_name, method_args = []):
        self.add_templates(self.fields)
        for arg in method_args:
            if isinstance(arg, Variable):
                if arg.name == "THROW":
                    return arg

        self.current_method = self.methods.get(method_name)               # define the current method
        self.validate_method(self, self.current_method.name, method_args) # validate current method
        self.current_method.bind_args(method_args)                        # bind the current method arguments
        statements = deepcopy(self.current_method.statements)             # deep copy statements
        self.evaluate_all_identifiers(statements)

        for statement in statements:                                      # run each statement         
            return_value = self.run_statement(statement)                  # check for a return value
            if return_value is not None:
                if return_value.name == "RETURN":
                    return_value.name = "RUN"
 #                   print(f'exiting {method_name}')
                    return return_value
                elif return_value.name == "THROW":
#                    print(f'exiting {method_name}')
                    return return_value
                
        return self.default_return()



    def run_statement(self, statement):
        # print(f'name: {self.current_method.name}')
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
                return self.print_statement(statement[i + 1:])
            elif token == self.console.SET_DEF:                  # (set) [noreturn]
                return self.set_statement(statement[i + 1:])                
            elif token == self.console.INPUT_INT_DEF:            # (inputi) [noreturn]
                self.inputi_statement(statement[i + 1])  
            elif token == self.console.INPUT_STRING_DEF:         # (inputs) [noreturn]
                self.inputs_statement(statement[i + 1])
            elif token == self.console.LET_DEF:                  # (let) return evaluated statement(s)
                return self.let_statement(statement[i + 1:])     
            elif token in self.OPERATORS:                        # (operator) return evaluated expression
                return self.operator.parse_operator(statement)


            elif token == self.console.THROW_DEF:
                return self.throw_statement(statement[i + 1])
            elif token == self.console.TRY_DEF:
                return self.try_catch_statement(statement[i + 1:])

    #WIP : TRY AND THROW
    def throw_statement(self, statement):
        if isinstance(statement, Variable):
            if statement.name == "THROW":
                return statement
        if isinstance(statement, list):                               # there are nested calls
            return self.throw_statement(self.run_statement(statement))  # evaluate statement(s)
        else:
            return self.evaluate_return(statement, "THROW")                      # call return helper

    def try_catch_statement(self, statement):
#        print(f'TRY {self.current_method.name}')
        self.evaluate_all_identifiers(statement)
        if len(statement) < 2:
            return self.run_statement(statement[0])
        
        try_statement = statement[0]
        catch_statement = statement[1]

        current_method = self.current_method
        return_value = self.run_statement(try_statement)
        self.current_method = current_method

        if return_value is not None:
            if return_value.name == "RETURN":
#                print(f'RETURN {return_value, self.current_method.name}')
                return return_value
            if return_value.name == "THROW":
                self.validate_type(Type.STRING, evaluate(return_value.value))
                exception = Variable('exception', Type.STRING, return_value.value)
                if len(self.exception_stack) > 0:
                    self.exception_stack[0]['exception'] = exception
                else:
                    self.exception_stack.insert(0, { 'exception': exception })

                self.reset_exceptions(catch_statement)
                self.evaluate_exceptions(catch_statement)
                
 #               print(f'CATCH {self.current_method.name}')
                return_value = self.run_statement(catch_statement)
  #              print(f'RETURN(THROW) {return_value, self.current_method.name}')

                if len(self.exception_stack) != 0:
                    self.exception_stack.pop(0)
        return return_value

    def evaluate_exceptions(self, statement):
        for variable in self.exception_stack:         # evaluate local stack first (in -> out)
            self.evaluate_args(statement, variable)              # evaluate arguments

    def reset_exceptions(self, tokens):
        for i, token in enumerate(tokens):                    # for each item
            if isinstance(token, Variable):
                if token.name == self.console.EXCEPTION_VARIABLE_DEF:
                    tokens[i] = self.console.EXCEPTION_VARIABLE_DEF
            if isinstance(token, list):                       # nested calls -> recurse
                self.reset_exceptions(token)


    # RETURN STATEMENT
    def return_statement(self, statement):
        if isinstance(statement[1], Variable):
            if statement[1].name == "THROW":
                return statement[1]
        if len(statement) == 1:                                            # there is no return value
            return self.evaluate_return(Type.VOID)
        elif isinstance(statement[1], list):                               # there are nested calls
            return_value = self.run_statement(statement[1])
            if isinstance(return_value, Variable):
                if return_value.name == "THROW":
                    return return_value
            return self.evaluate_return(return_value)  # evaluate statement(s)
        else:
            return self.evaluate_return(statement[1])                      # call return helper



    # CALL STATEMENT
    def call_statement(self, statement):
        #print(f'call: {statement}')
        who = statement[0]                              # caller
        method_name = statement[1]                      # method name
        if isinstance(method_name, Variable):           # potential method name shadowing
            method_name = method_name.name
        method_args = statement[2:]                     # method arguments
        owner = None                                    # calling object
        
        if isinstance(who, list):                       # nested call
            who = self.run_statement(who)

        # determine who the owner is
        if who == self.console.ME_DEF:                  # child has priority over self
            owner = self if self.child is None else self.child
        elif who == self.console.SUPER_DEF:             # super class
            owner = self.parent
            owner.child = self                          # current object is a child of super
        elif isinstance(who, Object):                   # object defined
            owner = who
        elif isinstance(who, Variable):                 # variable as argument
            owner = who.value
            if owner == Type.NULL:                      # null dereference
                self.console.error(errno.FAULT_ERROR)

        if owner is None:                               # unknown identifier
            self.console.error(errno.NAME_ERROR)

        evaluated_method_args = []                      # evaluate the method arguments
        
        self.validate_method(owner, method_name, method_args)

        caller = owner                                  # current caller
        owner = self.determine_polymorphic_function(owner, method_name, method_args)

        if owner.child is None:                         # assign owner.child to caller if there's no defined child
            owner.child = caller


        for arg in method_args:                         # for each argument, evaluate each argument if nested
            if isinstance(arg, list):                   # argument is nested
                arg = owner.run_statement(arg)
            elif isinstance(arg, Variable):             # argument is a variable
                arg = arg.value
            elif arg == self.console.ME_DEF:            # argument is me
                arg = self
            else:
                arg = stringify(evaluate(arg))          # argument is a constant

            evaluated_method_args.append(arg)

        return owner.run(method_name, evaluated_method_args)  # return the return value of the method



    # IF STATEMENT
    def if_statement(self, statement):
        condition = statement[0]                             # if condition
        true_statement = statement[1]                        # statement to run if true

        throw = self.validate_boolean(condition)

        if throw is not None:
            return throw
 
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

        throw = self.validate_boolean(condition)
        if throw is not None:
            return throw

        while self.evaluate_condition(condition):               # run the while loop
            return_value = self.run_statement(true_statement)   # check for a return value
            if return_value is not None:
                if return_value.name in ["RETURN", "THROW"]:
                    return return_value



    # BEGIN STATEMENT
    def begin_statement(self, statement):
        for nested_statement in statement:                       # run each statement in the begin block
            return_value = self.run_statement(nested_statement)  # check for a return value
            if return_value is not None:
                if return_value.name in ["RETURN", "THROW"]:
                    return return_value



    # LET STATEMENT
    def let_statement(self, statement):
        self.push_local_variables(statement[0])                  # push scope onto stack
        self.evaluate_all_identifiers(statement)                 # evaluate all identifiers
        return_value = self.begin_statement(statement[1:])       # run begin statement
        if len(self.current_method.local_stack) != 0:            # pop scope from stack
            self.current_method.local_stack.pop(0)

        return return_value                                      # return value at the end of the let block


    
    # NEW STATEMENT
    def new_statement(self, class_name):
        if self.console.TYPE_CONCAT_CHAR in class_name:
            split         = class_name.split(self.console.TYPE_CONCAT_CHAR)  # split class name and type
            template_name = split[0]
            vtype      = split[1:]
            if self.console.templates.get(template_name) is None:
                return self.console.error(errno.TYPE_ERROR)

            self.generate_concrete_class(class_name, vtype)
        elif self.console.classes.get(class_name) is None:         # fetch class
            return self.console.error(errno.TYPE_ERROR)          # invalid identifier
        return Object(class_name, self.console)                  # new object



    # [noreturn]
    def print_statement(self, args):
        to_print = ""                                      # to_string
        for arg in args:                                   # for each argument
            if isinstance(arg, list):                      # nested call
                value = self.run_statement(arg)  # -> variable
                if isinstance(value, Variable):
                    if value.name == "THROW":
                        return value
                    
                value = evaluate(value)
                    
                if isinstance(value, bool):                # -> brewin bool
                    value = self.console.TRUE_DEF if value else self.console.FALSE_DEF
                elif value == Type.NULL:                   # -> None
                    value = 'None'
            elif isinstance(arg, Variable):                # variable
                if arg.name == "THROW":
                    return arg
                if arg.vtype == Type.BOOL:                 # -> brewin bool
                    value = self.console.TRUE_DEF if evaluate(arg.value) else self.console.FALSE_DEF
                elif arg.value == Type.NULL:               # -> None
                    value = 'None'
                else:                                      # -> primitive
                    value = evaluate(arg.value)
            else:
                value = evaluate(arg)                      # primitive
                if isinstance(value, errno):               # unknown identifier
                    return self.console.error(value)
                elif value == Type.NULL:                   # -> None
                    value = 'None'
                elif isinstance(value, bool):              # -> brewin bool
                    value = self.console.TRUE_DEF if value else self.console.FALSE_DEF

            to_print += str(value)                         # add to to_string

        
        self.console.output(to_print)                      # print the value(s) to the console


        
    def set_statement(self, statement):
        variable = statement[0]                                     # variable object (name, vtype, value)
        value    = statement[1]                                     # value to be assigned

        if not isinstance(variable, Variable):                      # invalid identifier
            return self.console.error(errno.NAME_ERROR)

        if value == Type.NULL and isinstance(variable.vtype, Type): # invalid null assignment
            return self.console.error(errno.TYPE_ERROR)

        if isinstance(value, list):                                 # nested call
            value = self.run_statement(value)                       # evaluate statement(s)
            self.validate_classes(variable, value)                  # type check

        if isinstance(value, Variable):                             # variable to variable assignment
            if value.name == "THROW":
                return value
            if isinstance(value.value, Object):                     # check object definition
                self.validate_type(variable.type(), value.value)
            else:
                self.validate_type(variable.type(), evaluate(value))

            variable.value = value.value                            # assign value and return
            return

        if isinstance(value, Object):                               # object
            self.validate_type(variable.type(), value)              # type check
            variable.value = value                                  # assign value and return
            return
        else:                                                       # primitive
            value = evaluate(value)                                 # evaluate primitive for type checking
            self.validate_type(variable.type(), value)              # type check
            variable.value = stringify(value)                       # assign value and return
            return


    # [noreturn]
    def inputi_statement(self, variable):
        value = self.console.get_input()                      # get input
        self.validate_type(variable.type(), evaluate(value))  # type check
        variable.value = value                                # assign value



    # [noreturn]
    def inputs_statement(self, variable):
        value = self.console.get_input()            # get input
        self.validate_type(variable.type(), value)  # type check
        variable.value = stringify(value)           # assign value



    ##### HELPER FUNCTIONS #####

    def evaluate_all_identifiers(self, statement):
        self.replace_null(statement)                             # replace 'null' with Type.NULL
        for variable in self.current_method.local_stack:         # evaluate local stack first (in -> out)
            self.evaluate_args(statement, variable)              # evaluate arguments
            
        self.evaluate_args(statement, self.current_method.args)  # evaluate method arguments next
        self.evaluate_args(statement, self.fields)               # evaluate field arguments last


    
    def evaluate_args(self, statement, args):
        for key in args:                                           # for each key
            self.replace_arg_with_argv(statement, key, args[key])  # replace key with value


    def replace_arg_with_argv(self, tokens, arg, argv):
        for i, token in enumerate(tokens):                    # for each item
            if isinstance(token, list):                       # nested calls -> recurse
                self.replace_arg_with_argv(token, arg, argv)              
            else:
                if token == arg:                              # replace key with value
                    tokens[i] = argv



    def replace_null(self, tokens):
        for i, token in enumerate(tokens):          # for each item
            if isinstance(token, list):             # nested calls -> recurse
                self.replace_null(token)                
            else:
                if token == self.console.NULL_DEF:  # if item is 'null', replace with Type.NULL
                    tokens[i] = Type.NULL


    def validate_method(self, owner, method_name, method_args = []):
        if owner is None:                                # invalid method
            return self.console.error(errno.NAME_ERROR)

        if owner.methods.get(method_name) is None:       # no instance of method -> recurse up
            return self.validate_method(owner.parent, method_name, method_args)
        
        method = owner.methods.get(method_name)          # get method
        
        if len(method.args) != len(method_args):         # if arguments don't match -> recurse up
            return self.validate_method(owner.parent, method_name, method_args)

        if not self.valid_args(method, method_args):     # if arguments are not valid -> recurse up
            return self.validate_method(owner.parent, method_name, method_args)
            

    def valid_args(self, method, method_args):
        i = 0
        for key, arg in method.args.items():  # for each key, validate type
            try:
                self.validate_type(arg.type(), evaluate(method_args[i]))
                i += 1
            except:
                return False                  # catch error -> invalid

        return True

    def validate_boolean(self, statement):
        if isinstance(statement, Variable):
            if statement.vtype != Type.BOOL:                       # check if variable is a bool
                return self.console.error(errno.TYPE_ERROR)
            
        if isinstance(statement, list):
            condition = self.evaluate_condition(statement)
            if isinstance(condition, Variable):
                return condition
            if not isinstance(condition, bool):  # check if statement is a bool
                return self.console.error(errno.TYPE_ERROR)
        else:
            if not isinstance(evaluate(statement), bool):
                return self.console.error(errno.TYPE_ERROR)


            
    def valid_boolean(self, statement):
        if isinstance(statement, Variable):
            return statement.vtype == Type.BOOL                       # check if variable is a bool
        if isinstance(statement, list):
            if isinstance(self.evaluate_condition(statement), bool):  # check if statement is a bool
                return True
            else:
                return False
        else:
            return isinstance(evaluate(statement), bool)

    
    def evaluate_condition(self, condition):
        if isinstance(condition, list):
            evaluated_condition = self.run_statement(condition)
            if isinstance(evaluated_condition, Variable):
                if evaluated_condition.name == "THROW":
                    return evaluated_condition

            else:
                return evaluate(evaluated_condition)
        else:
            return evaluate(condition)

    def default_return(self):
        method_type = self.current_method.type()

        if method_type == Type.INT:     # method expects and int
            return_value = "0"
        if method_type == Type.STRING:  # method expects a string
            return_value = "''"
        if method_type == Type.BOOL:    # method expects a bool
            return_value = "False"
        if method_type == Type.VOID:    # method expects None
            return_value = "None"
        if not isinstance(method_type, Type): # method expects an object
            return_value = "None"
            
        return Variable("DEFAULT", method_type, return_value)


    
    def evaluate_return(self, return_value, return_type = "RETURN"):
#        print(return_value, self.current_method.name)
        if return_type == "THROW":
            if isinstance(return_value, Variable):
                return return_value
            return Variable(return_type, Type.STRING, return_value)
        
        vtype = self.current_method.type()                   # method return type
            
        if return_value == self.console.ME_DEF:              # if return me, return self
            self.validate_type(vtype, self)
            return Variable(return_type, vtype, self)
            
        if return_value == Type.VOID:                        # if return type is void, return default value
            return_value = self.default_return()

        return_value = evaluate(return_value)
        
        if isinstance(return_value, errno):
            return self.console.error(return_value)

        self.validate_type(vtype, return_value)
        
        return Variable(return_type, vtype, stringify(return_value))  # stringify the return value



    def push_local_variables(self, variables):
        types = {}
        for variable in variables:
            if len(variable) == 2:
                types[variable[0]] = Variable(variable[1], variable[0], self.console.default_value(variable[0]))
            else:
                types[variable[0]] = Variable(variable[1], variable[0], variable[2])

        self.add_templates(types)
        
        scope = { }

        variable_names = [variable[1] for variable in variables]
        if len(set(variable_names)) != len(variable_names):
            return self.console.error(errno.NAME_ERROR)
        
        for variable in variables:
            vtype = variable[0]
            name = variable[1]
            if len(variable) == 3:
                value = evaluate(variable[2])
            else:
                value = evaluate(self.console.default_value(vtype))
                
            self.validate_type(type_to_enum(vtype), value)
            scope[name] = Variable(name, type_to_enum(vtype), stringify(value))
            
        self.current_method.local_stack.insert(0, scope)



    def validate_type(self, var_type, value):
        if isinstance(var_type, str):
            if self.console.TYPE_CONCAT_CHAR in var_type:
                template_name = var_type.split(self.console.TYPE_CONCAT_CHAR)[0]
                if self.console.classes.get(var_type) is None:
                    if self.templates.get(template_name) is None:
                        return self.console.error(errno.TYPE_ERROR)
                        
        if var_type == Type.VOID:
            if value is not None:
                return self.console.error(errno.TYPE_ERROR)
            
        if value == Type.NULL:
            if var_type == Type.NULL:
                return
            if self.console.classes.get(var_type) is None:
                return self.console.error(errno.TYPE_ERROR)
            
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


    def validate_classes(self, lhs, rhs):
        lhs_type = None
        rhs_type = None
        if isinstance(lhs, Variable):
            if not isinstance(lhs.vtype, Type):
                lhs_type = lhs.vtype
        if isinstance(rhs, Variable):
            if not isinstance(rhs.vtype, Type):
                rhs_type = rhs.vtype

        if isinstance(lhs, Object):
            lhs_type = lhs.vtype
        if isinstance(rhs, Object):
            rhs_type = rhs.vtype

        if lhs_type is None or rhs_type is None:
            return

        if not self.same_classes(lhs_type, rhs_type):
            return self.console.error(errno.TYPE_ERROR)
    

    def same_classes(self, lhs_type, rhs_type):
        lhs_class = self.console.classes.get(lhs_type)
        rhs_class = self.console.classes.get(rhs_type)
        
        if self.console.TYPE_CONCAT_CHAR in lhs_type or self.console.TYPE_CONCAT_CHAR in rhs_type:
            return lhs_type == rhs_type
        
        if lhs_class is None:
            return self.console.error(errno.TYPE_ERROR)
        if rhs_class.super_class is None:
            return lhs_class.name == rhs_class.name
        else:
            if lhs_class.name != rhs_class.name:
                if lhs_class.super_class is None:
                    return self.same_classes(lhs_type, rhs_class.super_class)
                
        return True

    def build_hierarchy(self):
        parent_class = self.current_class.super_class

        if parent_class is None:
            return None

        self.parent = Object(parent_class, self.console)
        self.parent.build_hierarchy()



    def generate_concrete_class(self, name, types):
        template_name = name.split(self.console.TYPE_CONCAT_CHAR)[0]
        class_name = name

        if self.templates.get(template_name) is None:
            return self.console.error(errno.TYPE_ERROR)
        class_obj = Class(class_name)
        template = deepcopy(self.templates[template_name])
        self.replace_template_types(template, types)
        class_obj.methods = template.methods
        class_obj.fields = template.fields

        for _, field in class_obj.fields.items():
            if field.value == Type.NULL:
                field.value = self.console.default_value(field.vtype)

            if not isinstance(field.vtype, Type):
                if self.console.classes.get(field.vtype) is None:
                    if field.vtype != class_name:
                        return self.console.error(errno.TYPE_ERROR)


            if field.vtype != class_name:
                self.validate_type(field.vtype, evaluate(field.value))
            
        self.console.classes[class_name] = class_obj


    # replace fields first
    # replace methods
    def replace_template_types(self, template, concrete_types):
        if len(template.types) != len(concrete_types):
            return self.console.error(errno.TYPE_ERROR)
        for i, vtype in enumerate(template.types):       # each type in the template
            self.replace_template_objects(template.fields, vtype, concrete_types[i])
            self.replace_template_objects(template.methods, vtype, concrete_types[i])
            self.replace_template_methods(template.methods, vtype, concrete_types[i])


    def replace_template_methods(self, methods, template_type, concrete_type):
        for _, method in methods.items():
            self.replace_template_objects(method.args, template_type, concrete_type)
            self.replace_template_tokens(method.statements, template_type, concrete_type)


            
    def replace_template_objects(self, objects, template_type, concrete_type):
        for _, obj in objects.items():
            if isinstance(obj.vtype, str):
                if self.console.TYPE_CONCAT_CHAR in obj.vtype:
                    obj.vtype = type_to_enum(sub('@' + template_type + r'$', '@' + concrete_type, obj.vtype))
                    obj.vtype = type_to_enum(sub('@' + template_type + '@', '@' + concrete_type + '@', obj.vtype))
                else:
                    obj.vtype = type_to_enum(sub(r'^' + template_type + r'$', concrete_type, obj.vtype))



    def replace_template_tokens(self, tokens, template_type, concrete_type):
        for i, token in enumerate(tokens):
            if isinstance(token, list):
                self.replace_template_tokens(token, template_type, concrete_type)
            elif isinstance(token, Variable):
                self.replace_template_objects(token, template_type, concrete_type)

            elif isinstance(token, str):
                if self.console.TYPE_CONCAT_CHAR in token:
                    tokens[i] = type_to_enum(sub('@' + template_type + r'$', '@' + concrete_type, tokens[i]))
                    tokens[i] = type_to_enum(sub('@' + template_type + '@', '@' + concrete_type + '@', tokens[i]))
                else:
                    tokens[i] = type_to_enum(sub(r'^' + template_type + r'$', concrete_type, tokens[i]))

                    

    def add_templates(self, variables):
        for _, field in variables.items():
            if isinstance(field, str):
                continue
            if isinstance(field.vtype, str):
                if self.console.TYPE_CONCAT_CHAR in field.vtype:
                    split = field.vtype.split(self.console.TYPE_CONCAT_CHAR)
                    template_name = split[0]
                    types = split[1:]

                    if self.templates.get(template_name) is None:
                        return self.console.error(errno.NAME_ERROR)

                    for t in types:
                        if not isinstance(type_to_enum(t), Type):
                            if self.console.classes.get(t) is None:
                                return self.console.error(errno.TYPE_ERROR)
                    if self.console.classes.get(field.vtype) is None:
                        self.new_statement(field.vtype)
