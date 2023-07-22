## UID: 305600694
(IMPORTANT: Only replace the above numbers with your true UID, do not modify spacing and newlines, otherwise your tarfile might not be created correctly)

## Pipe Up

A program that recreates the pipe (|) operator in the shell.

## Building

To build the program, in the lab1 directory, do :
``
make
``
or in the lab1 directory, do:
``
gcc pipe.c -o pipe
``

## Running

Show an example run of your program, using at least two additional arguments, and what to expect
Example 1:
``
./pipe ls cat
``
should return:
``
Makefile
README.md
pipe
pipe.c
test_lab1.py
``

Example 2:
``
./pipe ls cat wc
``
should return:
``
       5       5      44
``


## Cleaning up

To remove the program, in the lab1 directory, do:
``
make clean
``
or in the lab1 directory, do:
``
rm ./pipe
``
