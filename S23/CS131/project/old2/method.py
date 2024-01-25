class Method:
    def __init__(self, name, vtype, args = {}, statements = []):
        self.name = name
        self.vtype = vtype
        self.args = args
        self.local_stack = []
        self.statements = [statements]
        
    def bind_args(self, argv):
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
