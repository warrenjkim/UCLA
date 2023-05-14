class Variable:
    def __init__(self, name, vtype, value):
        self.name = name
        self.vtype = vtype
        self.value = value

    def __repr__(self):
        return str(self.__dict__)
    
    def __str__(self):
        return str(self.__dict__)

    def set_value(self, value):
        self.value = value

    def type(self):
        return self.vtype
