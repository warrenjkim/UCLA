import os
import sys
import zlib

# commit node class to keep track of commits in a
# data structure.
# inputs: commit_hash: str, branches: []
# contains a set of parents and children (sets to
# keep uniqueness of nodes)
class commit_node:
    def __init__(self, commit_hash, branches = []):

        # :type commit_hash: str
        # :type branches:    []

        self.commit_hash = commit_hash
        self.parents = set()
        self.children = set()
        self.branches = branches
        

# checks to see if a .git directory exists and changes our working directory
# to it if it does, erroring otherwise.
# [[noreturn]]
def find_git_directory():
    # while not in root directory
    while os.getcwd() != '/':
        # check if path exists
        if os.path.exists('./.git'):
            # change to .git directory and return
            os.chdir('./.git')
            return
        else:
            # go up to parent directory
            os.chdir('./..')

    # if .git exists in '/'
    if os.path.exists('./.git'):
        # change to .git directory and return
        os.chdir('./.git')
        return
    # no git directory
    else:
        sys.stderr.write('Not inside a Git repository\n')
        sys.exit(1)


# traverses through .git/refs/head and make a dictionary with
# [k = hash, v = name]
# returns a dictionary of branches
def get_hash_branches():
    # empty dictionary [k = hash, v = name]
    hash_branches = {}
    # branch names are located in ./refs/head
    os.chdir('./refs/heads')
    
    # walk through directory tree topdown
    for root, dirs, files in os.walk('.', topdown = True):
        # for every item in dirs and files
        for item in files:
            # path branch_name of the item
            # strip the './' from branch_name
            branch_name = (root + '/' + item)[2:]
            # if it's a file, read and strip the first line
            if os.path.isfile(branch_name):
                with open(branch_name, 'r') as file:
                    # get hash of branch
                    hash = file.readline().strip()
                    # if it's unique, add it to the dict
                    if hash not in hash_branches.keys():
                        hash_branches[hash] = [branch_name]
                    # otherwise, append to current hash and
                    else:
                        hash_branches[hash].append(branch_name)
    # redirect to ./git                    
    os.chdir('../..')
    
    return hash_branches



# gets the parents of a specific commit
def get_parent_hashes(hash):
    decompressed = None
    # go to ./hash[:2]
    os.chdir(hash[:2])

    # open ./hash[2:] as compressed
    with open(hash[2:], 'rb') as compressed:
        # decompress file
        decompressed = str(zlib.decompress(compressed.read()), 'UTF-8')

    # redirect to ./objects
    os.chdir('..')
    
    # since there can be multiple parent hashes,
    # store in a list
    hashes = []
    # Split lines by '\n'
    for line in decompressed.split('\n'):
        # if it's a parent, take only the hash
        if 'parent' in line:
            hashes.append(line.split('parent ').pop())

    return hashes


# makes the commit graph (DAG) using the commit_node data structure
def get_commit_dag(hash_branches):
    # empty dag
    dag = {}
    root_commits = set()

    # go to ./objects
    os.chdir('./objects')

    for hash in hash_branches:
        # add node to dag
        if hash in dag.keys():
            dag[hash].branches = hash_branches[hash]

        # not visited, create a new node and 'recurse'
        # using a local stack
        else:
            # create a new commit_node
            dag[hash] = commit_node(hash, hash_branches[hash])
            # artificial stack
            stack = []
            stack.append(dag[hash])

            # iterative dfs
            while stack:
                # pop node from back of stack (LIFO)
                node = stack.pop()
                # get parent hashes
                parent_hashes = get_parent_hashes(node.commit_hash)

                # if there no parents, node has indegree 0
                if len(parent_hashes) == 0:
                    root_commits.add(node.commit_hash)

                for parent_hash in parent_hashes:
                    # create a new commit_node if necessary
                    if parent_hash not in dag.keys():
                        dag[parent_hash] = commit_node(parent_hash)

                    # connect to parent
                    node.parents.add(dag[parent_hash])
                    # connect to child
                    dag[parent_hash].children.add(node)
                    # 'recurse'
                    stack.append(dag[parent_hash])

    # redirect to ./objects
    os.chdir('..')
    
    # sort the root_commits
    sorted_sources = sorted(list(root_commits))
    # return the dag and the root_commits (as a sorted list)
    return dag, sorted_sources

# returns the topological sort
def topological_sort(dag, sources):
    # topological ordering
    sorted = []
    # visited set
    visited = set()

    # topological sort
    while sources:
        # get node from top of sources (LIFO)
        node = sources[-1]
        # add to visited
        visited.add(node)

        # next node, if there is one
        next_node = ''
        # for each child
        for child in dag[node].children:
            # if child hasn't been visited, assign
            # to next_node and break
            if child.commit_hash not in visited:
                next_node = child.commit_hash
                break

        # if there's a next node, append to
        # sources and do not pop
        if next_node:
            sources.append(next_node)
        # if there isn't a next node, append
        # node to sorted 
        else:
            sorted.append(node)
            sources.pop()

    # if the length of the sorted list is not the number of commits, error
    # and exit
    if len(sorted) != len(visited):
        sys.stderr.write('graph has a cycle')
        sys.exit(1)

    return sorted


# formats output to assignment specifications
def print_sorted_order(top_sorted, dag):
    # for each node in the topologically sorted list
    for i, top_node in enumerate(top_sorted):
        # current node
        node = dag[top_node]

        # if leaf node
        if len(node.branches) == 0:
            print(top_node)
        # not leaf node
        else:
            print(top_node, end = ' ')
            # sort branches (make it deterministic)
            branches = sorted(node.branches)
            # unpack branches
            print(*branches)
            
        # if not parent
        if i < (len(top_sorted) - 1):
            # next top_sorted node (dag key)
            next_top_node = top_sorted[i + 1]
            # next node in dag
            next_node = dag[next_top_node]
            # list of parent hashes
            parent_hashes = []
            # list of child hashes
            child_hashes = []

            # append parent hashes
            for parent in node.parents:
                parent_hashes.append(parent.commit_hash)

            # append child hashes
            for child in next_node.children:
                child_hashes.append(child.commit_hash)

            # if the next node isn't in parent hashes
            if next_top_node not in parent_hashes:
                # unpack parent and child hashes
                print(*parent_hashes, end = '=\n\n=')
                print(*child_hashes)

                
# Keep the function signature,
# but replace its body with your implementation.
#
# Note that this is the driver function.
# Please write a well-structured implemention by creating other functions outside of this one,
# each of which has a designated purpose.
#
# As a good programming practice,
# please do not use any script-level variables that are modifiable.
# This is because those variables live on forever once the script is imported,
# and the changes to them will persist across different invocations of the imported functions.
def topo_order_commits():
    # find and go to git directory
    find_git_directory()
    # get the dictionary of hash: branch_names
    hash_branches = get_hash_branches()
    # create the dag and the sorted root_commits
    dag, root_commits = get_commit_dag(hash_branches)
    # topologically sort the dag
    top_sorted = topological_sort(dag, root_commits)
    # print as per assignment specifications
    print_sorted_order(top_sorted, dag)

    
if __name__ == '__main__':
    topo_order_commits()
