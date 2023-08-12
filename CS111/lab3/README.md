## UID: 305600694
(IMPORTANT: Only replace the above numbers with your true UID, do not modify spacing and newlines, otherwise your tarfile might not be created correctly)

# Hash Hash Hash

This lab implements two thread-safe hash table implementations using mutex locks.

## Building

To build, in the lab3 directory, do

``make``

## Running

Below are two examples of the completed program using `-t 8 -s 50000`. Here, the base completes in just over one second.

bash-4.4$ ./hash-table-tester -t 8 -s 50000
Generation: 69,002 usec
Hash table base: 1,021,491 usec
  - 0 missing
Hash table v1: 1,339,195 usec
  - 0 missing
Hash table v2: 347,323 usec
  - 0 missing


bash-4.4$ ./hash-table-tester -t 8 -s 50000
Generation: 69,833 usec
Hash table base: 1,004,705 usec
  - 0 missing
Hash table v1: 1,294,166 usec
  - 0 missing
Hash table v2: 331,716 usec
  - 0 missing


## First Implementation
Here, we have a lock for the entire hash table via a `mutex` in the `hash_table_v1` struct. Inside `hash_table_v1_add_entry`, we lock upon entering the function. There are two cases. If we modify an entry, we unlock and return. If we add a new entry, we add the new entry, unlock, and return. Either way, we unlock after doing any modifications. This is correct since it guarantees that only one thread can modify the hash table at any given time. Here, the critical section is when we modify the hash table. Thus, we provide a mutex to ensure that only one thread has exclusive access to the critical section at any given time.

### Performance
Here, we run the program using -t 8 -s 50000:

bash-4.4$ ./hash-table-tester -t 8 -s 50000
Generation: 69,002 usec
Hash table base: 1,021,491 usec
  - 0 missing
Hash table v1: 1,339,195 usec
  - 0 missing
Hash table v2: 347,323 usec
  - 0 missing


bash-4.4$ ./hash-table-tester -t 8 -s 50000
Generation: 69,833 usec
Hash table base: 1,004,705 usec
  - 0 missing
Hash table v1: 1,294,166 usec
  - 0 missing
Hash table v2: 331,716 usec
  - 0 missing

In both cases, v1 seems to slow down by about 0.3 seconds from the base implementation. This is because v1 ensures that the program runs serially; i.e. only one thread can access the hash table at a time. Furthermore, we have a slowdown due to the overhead that comes with implementing locks. This is why v1 is slower than base.

## Second Implementation

Here, we use a mutex for each entry in the hash table as opposed to the entire hash table. This ensures that only one thread can access a given entry at any given time. Therefore, we allow for concurrent access into the hash table, but ensure mutual exlusion when updating or adding values to the hash table which guarantees correctness.

### Performance
Here, we run the program using -t 8 -s 50000:

bash-4.4$ ./hash-table-tester -t 8 -s 50000
Generation: 69,002 usec
Hash table base: 1,021,491 usec
  - 0 missing
Hash table v1: 1,339,195 usec
  - 0 missing
Hash table v2: 347,323 usec
  - 0 missing


bash-4.4$ ./hash-table-tester -t 8 -s 50000
Generation: 69,833 usec
Hash table base: 1,004,705 usec
  - 0 missing
Hash table v1: 1,294,166 usec
  - 0 missing
Hash table v2: 331,716 usec
  - 0 missing

In both cases, v2 is about 0.7 seconds faster than the base implementation. This is because the v2 implementation allows for concurrency. Thus, multiple threads can operate on different parts of the hash table at the same time, increasing performance. This explains the performance increase.

## Cleaning up

To clean up, in the lab3 directory, do
``make clean``
