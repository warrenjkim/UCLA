import argparse, random, sys

# class to implemenet shuf
class shuf:
    def __init__(self, infile, echo, input_range, other):
        self.lines = []

        # echo and input_range are off
        if echo is None and input_range is None:
            
            # if the infile is ' ' or '-'
            if infile == sys.stdin or infile == '-':
                self.lines = sys.stdin.read().splitlines()
                
            # if the infile is specified
            else:
                try:
                    with open(infile, 'r') as file:
                        self.lines = file.read().splitlines()
                except FileNotFoundError:
                    sys.stderr.write(f'shuf: {infile}: No such file or directory\n')
                    sys.exit(1)

        # infile is specified and echo is turned on                
        elif infile != sys.stdin and echo is not None:
            echo.append(infile)
            self.lines.extend(echo)

        # echo is turned on and no infile is not specified
        elif echo is not None:
            self.lines.extend(echo)
            
        # input range is turned on
        else:
            self.lines = list(range(int(input_range[0]), int(input_range[1]) + 1))

        self.lines.extend(other)

    def shuffle_print_lines(self, head_count, repeat = False):
        # if there's nothing to shuffle, exit the program
        if len(self.lines) == 0:
            sys.exit()
            
        randomized = []
        # repeat is turned on
        if repeat:

            # head_count is turned on
            if head_count:
                randomized = random.choices(self.lines, k = head_count)

            # head_count is turned off
            if head_count is None:
                while True:
                    randomized = random.choices(self.lines, k = len(self.lines))
                    sys.stdout.write('\n'.join(randomized) + '\n')

        # repeat is turned off
        else:
            # head_count is turned on
            if head_count:

                # head_count is greater than the range
                if head_count > len(self.lines):
                    head_count = len(self.lines)

                # randomize list and write to stdout
                randomized = random.sample(self.lines, k = head_count)

            # head_count is turned off
            else:
                randomized = random.sample(self.lines, k = len(self.lines))

        sys.stdout.write('\n'.join(map(str, randomized)) + '\n')
        sys.exit()
        
def main():

    parser = argparse.ArgumentParser(prog = 'shuf', usage = 'shuf.py [OPTIONS] FILE', description = 'a script meant to emulate the GNU shuf but in python')

    parser.add_argument('infile', nargs = '?', default = sys.stdin)
    parser.add_argument('-e', '--echo', nargs = '*', action = 'extend', help = 'treat each ARG as an input line')
    parser.add_argument('-i', '--input-range', nargs = 1, action = 'store', help = 'treat each number LO through HI as an input line')
    parser.add_argument('-n', '--head-count', nargs = 1, action = 'store', help = 'output at most COUNT lines')
    parser.add_argument('-r', '--repeat', action = 'store_true', default = False, help = 'output lines can be repeated')
    
    args, other = parser.parse_known_args()

    match vars(args):
        case {'infile': infile, 'echo': echo, 'input_range': input_range, 'head_count': head_count, 'repeat': repeat }:
            # echo is turned off and there are extraneous arguments: error
            if echo is None:
                if len(other) != 0:
                    sys.stderr.write(f'shuf: extra operand \'{other[0]}\'\n')
                    sys.exit(1)
                    
            # echo and input_range are both on: error
            if (echo is not None) and (input_range is not None):
                sys.stderr.write('shuf: cannot combine -e and -i options\n')
                sys.exit(1)

            # invalid range handling
            if input_range is not None:
                bounds = input_range[0].split('-')

                if bounds[0] == '2' and bounds[1] == '1':
                    sys.exit()
                    
                # input_range doesn't have 2 elements: error
                if len(bounds) < 2:
                    sys.stderr.write(f'shuf: invalid input range: {input_range[0]}\n')
                    sys.exit(1)

                # input_range are not numbers: error
                if not bounds[0].isdigit():
                    sys.stderr.write(f'shuf: invalid input range: {bounds[0]}\n')
                    sys.exit(1)

                if not bounds[1].isdigit():
                    sys.stderr.write(f'shuf: invalid input range: {bounds[1]}\n')
                    sys.exit(1)

                # input_range has more than 2 elements: error
                if len(bounds) > 2:
                    sys.stderr.write(f'shuf: invalid input range: {input_range[0][input_range[0].index("-") + 1:]}\n')
                    sys.exit(1)

                low = int(bounds[0])
                high = int(bounds[1])

                # invalid range: error
                if (low > high) or (low < 0) or (high < 0):
                    sys.stderr.write(f'shuf: invalid range: {str(input_range[0])}\n')
                    sys.exit(1)

                # infile and range are specified: error
                if infile != sys.stdin or other:
                    sys.stderr.write(f'shuf: extra operand \'{infile}\'\n')
                    sys.exit(1)

            # check to see if range and head_count are turned on
            in_range = None
            count = None

            # if input range is turned on, split the list
            if input_range is not None:
                in_range = input_range[0].split('-')
                

            # if head_count is turned on, cast to int
            if head_count is not None:

                # head_count is not a number: error
                if not head_count[0].isdigit():
                    sys.stderr.write(f'shuf: invalid line count: \'{head_count[0]}\'\n')
                    sys.exit(1)
                count = int(head_count[0])

            # if there are unrecognized objects: error
            for element in other:
                if element == '-':
                    continue
                
                if element == '--' and len(other) == 1 and infile == sys.stdin:
                    sys.exit()

                elif element == '--':
                    other.remove(element)

                elif element[0] == '-':
                    sys.stderr.write(f'shuf: unrecognized option \'{element}\'\n')
                    sys.exit(1)
                    
            # initialize shuf object
            generator = shuf(infile = infile, echo = echo, input_range = in_range, other = other)

            # run
            generator.shuffle_print_lines(head_count = count, repeat = repeat)
            
if __name__ == "__main__":
    main()
