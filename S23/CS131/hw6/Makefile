# Optimization level.  Change this -O2 to -Og or -O0 or whatever.
OPTIMIZE = -O0

# The C compiler and its options.
CC = g++
CFLAGS = $(OPTIMIZE) -g3 -Wall -Wextra -fanalyzer \
  -march=native -mtune=native

default: kontainer

kontainer: kontainer.hpp main.cpp
	$(CC) kontainer.hpp main.cpp -o $@
