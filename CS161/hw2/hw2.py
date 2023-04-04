##############
# Homework 2 #
##############

##############
# Question 1 #
##############

# BFS implements a breadth-first search of a given fringe.
# BFS takes in a single argument, FRINGE, which is a tuple representation of a tree
def BFS(FRINGE):
    order = ()
    queue = []

    # add leaf nodes to the order
    for node in FRINGE:
        # add subtree to queue
        if type(node) is tuple:
            queue.append(node)
            continue
        order += (node,)

    # while every node hasn't been visited
    while len(queue) > 0:
        for node in queue[0]:
            # if the element is a subtree, add to order
            if type(node) is tuple:
                queue.append(node)
                continue
            order += (node,)
        queue.pop(0)

    return order



##############
# Question 2 #
##############


# These functions implement a depth-first solver for the homer-baby-dog-poison
# problem. In this implementation, a state is represented by a single tuple
# (homer, baby, dog, poison), where each variable is True if the respective entity is
# on the west side of the river, and False if it is on the east side.
# Thus, the initial state for this problem is (False False False False) (everybody
# is on the east side) and the goal state is (True True True True).

# The main entry point for this solver is the function DFS, which is called
# with (a) the state to search from and (b) the path to this state. It returns
# the complete path from the initial state to the goal state: this path is a
# list of intermediate problem states. The first element of the path is the
# initial state and the last element is the goal state. Each intermediate state
# is the state that results from applying the appropriate operator to the
# preceding state. If there is no solution, DFS returns [].
# To call DFS to solve the original problem, one would call
# DFS((False, False, False, False), [])
# However, it should be possible to call DFS with a different initial
# state or with an initial path.

# First, we define the helper functions of DFS.

# FINAL-STATE takes a single argument S, the current state, and returns True if it
# is the goal state (True, True, True, True) and False otherwise.
def FINAL_STATE(S):
    return S == (True, True, True, True)


# NEXT-STATE returns the state that results from applying an operator to the
# current state. It takes three arguments: the current state (S), and which entity
# to move (A, equal to "h" for homer only, "b" for homer with baby, "d" for homer
# with dog, and "p" for homer with poison).
# It returns a list containing the state that results from that move.
# If applying this operator results in an invalid state (because the dog and baby,
# or poisoin and baby are left unsupervised on one side of the river), or when the
# action is impossible (homer is not on the same side as the entity) it returns None.
# NOTE that next-state returns a list containing the successor state (which is
# itself a tuple)# the return should look something like [(False, False, True, True)].
def NEXT_STATE(S, A):

    if (S[1] == S[2] or S[1] == S[3]) and (S[0] != S[1]):
        return None

    next_state = ()

    # just homer
    if A == 'h':
        next_state = (not S[0], S[1], S[2], S[3])
        
    # homer and baby
    elif A == 'b' and (S[0] == S[1]):
        next_state = (not S[0], not S[1], S[2], S[3])

    # homer and dog
    elif A == 'd' and (S[0] == S[2]):
        next_state = (not S[0], S[1], not S[2], S[3])

    # homer and poison
    elif A == 'p' and (S[0] == S[3]):
        next_state = (not S[0], S[1], S[2], not S[3])

    # if it's an invalid state, don't return
    if not next_state or ((next_state[1] == next_state[2] or next_state[1] == next_state[3]) and (next_state[0] != next_state[1])):
        return None

    return [next_state]


# SUCC-FN returns all of the possible legal successor states to the current
# state. It takes a single argument (s), which encodes the current state, and
# returns a list of each state that can be reached by applying legal operators
# to the current state.
def SUCC_FN(S):
    possible_successors = []

    # for each option, get the successors
    for option in ['h', 'b', 'd', 'p']:
        successor = NEXT_STATE(S, option)

        # if the successor is a valid state, add it to the possible successors
        if successor:
            possible_successors.extend(successor)

    return possible_successors


# ON-PATH checks whether the current state is on the stack of states visited by
# this depth-first search. It takes two arguments: the current state (S) and the
# stack of states visited by DFS (STATES). It returns True if s is a member of
# states and False otherwise.
def ON_PATH(S, STATES):
    return S in STATES


# MULT-DFS is a helper function for DFS. It takes two arguments: a list of
# states from the initial state to the current state (PATH), and the legal
# successor states to the last, current state in the PATH (STATES). PATH is a
# first-in first-out list of states
# that is, the first element is the initial
# state for the current search and the last element is the most recent state
# explored. MULT-DFS does a depth-first search on each element of STATES in
# turn. If any of those searches reaches the final state, MULT-DFS returns the
# complete path from the initial state to the goal state. Otherwise, it returns
# [].
def MULT_DFS(STATES, PATH):
    for state in STATES:
        
        # temp path
        temp = PATH.copy()
        temp.append(state)

        # if it's the final state, return temp
        if FINAL_STATE(state):
            return temp

        # if not visited
        if not ON_PATH(state, PATH):
            stack =  MULT_DFS(SUCC_FN(state), temp)
            if stack and FINAL_STATE(stack[-1]):
                return stack


    return []

# DFS does a depth first search from a given state to the goal state. It
# takes two arguments: a state (S) and the path from the initial state to S
# (PATH). If S is the initial state in our search, PATH is set to False. DFS
# performs a depth-first search starting at the given state. It returns the path
# from the initial state to the goal state, if any, or False otherwise. DFS is
# responsible for checking if S is already the goal state, as well as for
# ensuring that the depth-first search does not revisit a node already on the
# search path.
def DFS(S, PATH):
    PATH.append(S)
    if FINAL_STATE(S):
        return PATH
    return MULT_DFS(SUCC_FN(S), PATH)
