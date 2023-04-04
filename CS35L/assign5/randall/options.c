#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>

#include <errno.h>
#include <stdbool.h>
#include <string.h>

#include "rand64-hw.h"
#include "rand64-sw.h"
#include "output.h"
#include "options.h"

initialize_t initialize = NULL;
rand64_t rand64 = NULL;
finalize_t finalize = NULL;
write_t write_function = NULL;

long long nbytes = 0;
size_t n = 1;


void parse_options(int argc, char **argv) {
  int option;
  char *func_name = argv[0];
  char *n_bytes;
  int oflag = 0;
  int iflag = 0;
  
  /* while there is a valid option */
  while((option = getopt(argc, argv, "i:o:")) != -1) {
    switch(option) {
    case 'i':
      parse_input(optarg);
      iflag++;
      break;
    case 'o':
      parse_output(optarg);
      oflag++;
      break;
    case ':':
      fprintf(stderr, "option -%c requires an operand\n", optopt);
      exit(1);
    case '?':
      fprintf(stderr, "unrecognized option '-%c'\n", optopt);
      exit(1);
    }
  }

  /* if there are no command-line arguments:
     - rdrand (if available) is used by default
     - stdio is used by default */
  if(!iflag)
    parse_input("rdrand");
  if(!oflag)
    parse_output("stdio");

  /* verify that the correct number of args was passed in */
  if(argc - (2 * (iflag + oflag)) != 2) {
    fprintf (stderr, "%s: usage: %s NBYTES [OPTIONS] (-i INPUT, -o OUTPUT)\n", func_name, func_name);
    exit(1);
  }

  n_bytes = argv[optind];
    
  assign_nbytes(func_name, n_bytes);
}

void parse_input(char *in_str) {
  /* three cases:
     - rdrand: Check if rdrand is supported (error if not), and set function pointers
       to the respective hardware functions (default)
     - mrand48_r: Use the GNU C mrand48_r function (inside stdlib.h)
     - /F where F is a file: Use the file F in place of "/dev/random" and set function
       pointers to the respective software functions  */

  /* since there's only one case we'd use rand64-sw, we check it first */
  if(in_str[0] == '/') {
    urandstream = fopen(in_str, "r");

    if(!urandstream || !fread(&nbytes, sizeof(nbytes), 1, urandstream)) {
      fprintf(stderr, "input is not a valid file %s", in_str);
      exit(1);
    }

    initialize = software_rand64_init;
    rand64 = software_rand64;
    finalize = software_rand64_fini;
  }
  else {
    initialize = hardware_rand64_init;
    finalize = hardware_rand64_fini;
    
    /* we check which rand64 we're going to use */
    if(!strcmp(in_str, "rdrand")) {
      if(!rdrand_supported()) {
	fprintf(stderr, "the rdrand instruction is not supported by your processor\n");
	exit(1);
      }
      rand64 = hardware_rand64;
    }
    else if(!strcmp(in_str, "mrand48_r"))
      rand64 = mrand48_r_wrapper;
  }
}

void parse_output(char* out_str) {
  /* two cases:
     - stdout (default): use stdout
     - N (> 0): output N bytes at a time */
  if(!strcmp(out_str, "stdio"))
    write_function = writebytes;
  else {
    char* endptr;
    size_t size = (size_t) strtoll(out_str, &endptr, 10);
    
    if(*endptr != '\0') {
      fprintf(stderr, "ERROR: invalid input %s\n", out_str);
      exit(1);
    }
    
    n = size;
    write_function = write_wrapper;
  }
}

void assign_nbytes(char *func_name, char *bytes) {
  bool valid = 0;
  char *endptr;
  nbytes = strtoll(bytes, &endptr, 10);

  /* error checking */
  if(errno)
    perror(bytes);
  else
    valid = !*endptr && 0 <= nbytes;

  /* invalid input */
  if(!valid) {
    fprintf (stderr, "%s: usage: %s NBYTES [OPTIONS] (-i INPUT, -o OUTPUT)\n", func_name, func_name);   
    exit(1);
  }
}
