#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>

#include <limits.h>
#include <stdbool.h>

#include "output.h"

bool writebytes(unsigned long long x, long long nbytes, size_t n) {
  do {
    if(putchar (x) < 0)
      return false;
    
    x >>= CHAR_BIT;
    nbytes -= n;
  } while(0 < nbytes);

  return true;
}

bool write_wrapper(unsigned long long x, long long nbytes, size_t n) {
  char *buff;
  size_t bytes_to_write;
  ssize_t bytes_written;
  
  do {
    bytes_to_write = (size_t) nbytes < n ? (size_t) nbytes : n;
    buff = (char*) malloc(bytes_to_write); /* malloc here */
    
    if(!buff) {
      fprintf(stderr, "ERROR: Referencing null-pointer\n");
      exit(1);
    }
    
    for(size_t i = 0; i < bytes_to_write; i++) {
      buff[i] = x;
      x >>= CHAR_BIT;
    }

    bytes_written = write(STDOUT_FILENO, buff, bytes_to_write);
    
    if(bytes_written < 0 || (size_t) bytes_written != bytes_to_write) {
      free(buff); /* free here */
      return false;
    }

    nbytes -= n;
    free(buff); /* free here */
  } while(0 < nbytes);

  return true;
}
