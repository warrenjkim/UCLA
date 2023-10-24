CC = g++
CFLAGS = -Wall -c
OBJS = CPU.o cpusim.o
TARGET = cpusim

# linker flags
LDFLAGS = -Wall


all: $(TARGET)

$(TARGET): $(OBJS)
	$(CC) $(LDFLAGS) -o $(TARGET) $(OBJS)

CPU.o: CPU.cpp CPU.h
	$(CC) $(CFLAGS) CPU.cpp

cpusim.o: cpusim.cpp CPU.h
	$(CC) $(CFLAGS) cpusim.cpp

# Clean target to remove object files and executable
clean:
	rm -f $(OBJS) $(TARGET) .*~ *~
