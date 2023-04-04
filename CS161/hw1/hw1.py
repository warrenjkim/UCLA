# The PAD function calculates the Nth number in the Padovan sequence
# @param N: int
# @return The Nth Padovan number: int
def PAD(N: int) -> int:
    # base case: if the number is 0, 1, 2, the Padovan number is 1
    if N <= 2:
        return 1

    # recursively call PAD() to calculate the (N - 2)nd and (N - 3)rd Padovan numbers to get the Nth Padovan number
    return PAD(N - 2) + PAD(N - 3)


# The SUMS function calculates the number of additions required required to calculate the Nth number in the Padovan sequence
# @param N: int
# @return The number of additions required to calculate the Nth padovan number: int
def SUMS(N: int) -> int:
    # base case: if the number is 0, 1, 2, the number of additions required to calculate the 0, 1, 2nd Padovan number is 0
    if N <= 2:
        return 0

    # recursively call SUMS to calculate the number of additions required to calculate the (N - 1)st and (N - 2)nd Padovan numbers
    return SUMS(N - 2) + SUMS(N - 3) + 1


# The ANON function replaces all of the nodes in a tree (with tuple implementation) with a '?'
# @param TREE: any
# @return An anonymized tuple tree if input is a tuple: tuple
# @return A '?' if input is not a tuple: str
def ANON(TREE: any) -> str | tuple:
    # base case: if TREE is not a tuple, return the string '?' because then TREE is not a tree
    if type(TREE) is not tuple:
        return '?'

    # create an empty tree
    anon_tree = ()

    # traverse through the tree
    for node in TREE:
	# if the node is a subtree, recurse
        if type(node) is tuple:
	    # create a subtree, calling ANON() recursively
            subtree = (ANON(node),)

	# node is a leaf node
        else:
            subtree = ('?',)
	    
        # add the subtree to the result
        anon_tree += subtree
            
    return anon_tree
