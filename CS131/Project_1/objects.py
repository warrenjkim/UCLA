class Method:
    def __init__(self, name = "", args = []):
        self.name = name
        self.args = args
        self.statements = []
        self.variables = { }
        
    def add_statements(self, statements):
        self.statements = statements

    def add_variable(self, name, value):
        self.variables[name] = value


class Field:
    def __init__(self, name = "", value = None):
        self.name = name
        self.value = value
        # self.type = bool if value == base.TRUE_DEF or value == base.FALSE_DEF else type(eval(value))

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
