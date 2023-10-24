CC = gcc
CFLAGS = -Wall -c
OBJS = CPU.o cpusim.o
TARGET = cpusim

# linker flags
LDFLAGS = -Wall


all: $(TARGET)

$(TARGET): $(OBJS)
	$(CC) $(LDFLAGS) -o $(TARGET) $(OBJS)

CPU.o: CPU.c CPU.h
	$(CC) $(CFLAGS) CPU.c

cpusim.o: cpusim.c CPU.h
	$(CC) $(CFLAGS) cpusim.c

# Clean target to remove object files and executable
clean:
	rm -f $(OBJS) $(TARGET) .*~ *~
