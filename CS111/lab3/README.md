## UID: 305600694
(IMPORTANT: Only replace the above numbers with your true UID, do not modify spacing and newlines, otherwise your tarfile might not be created correctly)

# Hash Hash Hash

This lab implements two thread-safe hash table implementations using mutex locks.

## Building

To build, in the lab3 directory, do

``make``

## Running

Below are two examples of the completed program using `-t 8 -s 50000`. Here, the base completes in just over 
## First Implementation

Describe your first implementation strategy here (the one with a single mutex).
Argue why your strategy is correct.

### Performance

Run the tester such that the base hash table completes in 1-2 seconds.
Report the relative speedup (or slow down) with a low number of threads and a
high number of threads. Note that the amount of work (`-t` times `-s`) should
remain constant. Explain any differences between the two.

## Second Implementation

Describe your second implementation strategy here (the one with a multiple
mutexes). Argue why your strategy is correct.

### Performance

Run the tester such that the base hash table completes in 1-2 seconds.
Report the relative speedup with number of threads equal to the number of
physical cores on your machine (at least 4). Note again that the amount of work
(`-t` times `-s`) should remain constant. Report the speedup relative to the
base hash table implementation. Explain the difference between this
implementation and your first, which respect why you get a performance increase.

## Cleaning up

Explain briefly how to clean up all binary files.
