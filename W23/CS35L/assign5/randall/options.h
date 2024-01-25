#ifndef OPTIONS_H
#define OPTIONS_H

#include <stdbool.h>

/* function pointers for initialize, rand64, finalize, and write_function */

/* depending on the implementation (hardware/software), initialize, rand64, and finalize 
   will point to the hardware or software version of the function */
typedef void (*initialize_t)();
typedef unsigned long long (*rand64_t)();
typedef void (*finalize_t)();
typedef bool (*write_t)(unsigned long long, long long, size_t);

extern initialize_t initialize;
extern rand64_t rand64;
extern finalize_t finalize;
extern write_t write_function;

extern long long nbytes;
extern size_t n;

/* command-line options processing */
void parse_options(int, char**);

/* helper function that parses the input */
void parse_input(char*);

/* helper function that parses the output */
void parse_output(char*);

/* helper function for error-checking nbytes */
void assign_nbytes(char*, char*);

#endif
