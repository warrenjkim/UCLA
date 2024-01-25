/* Generate N bytes of random output.  */

/* When generating output this program uses the x86-64 RDRAND
   instruction if available to generate random numbers, falling back
   on /dev/random and stdio otherwise.

   This program is not portable.  Compile it with gcc -mrdrnd for a
   x86-64 machine.

   Copyright 2015, 2017, 2020 Paul Eggert

   This program is free software: you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation, either version 3 of the
   License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.  */

#include <errno.h>

#include "rand64-hw.h"
#include "rand64-sw.h"
#include "options.h"
#include "output.h"


/* Main program, which outputs N bytes of random data.  */
int main (int argc, char **argv) {
  parse_options(argc, argv);

  if(!initialize)
    return 1;
  
  if(nbytes == 0) {
    finalize();
    return 0;
  }
  
  initialize();

  int output_errno = 0;
  unsigned long long x = rand64();
  
  if(!write_function(x, nbytes, n))
    output_errno = errno;
  
  if (fclose (stdout) != 0)
    output_errno = errno;

  if (output_errno) {
    errno = output_errno;
    perror ("output");
  }

  finalize();

  return !!output_errno;
}
