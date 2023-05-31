from variable import Variable
from method import Method

class Template:
    def __init__(self, name, types):
        self.name = name
        self.types = types

    
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
