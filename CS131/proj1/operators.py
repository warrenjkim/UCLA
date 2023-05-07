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
