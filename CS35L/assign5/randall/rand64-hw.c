#include <immintrin.h>
#include <cpuid.h>
#include <time.h>

#include "rand64-hw.h"

/* Hardware implementation.  */

/* Return information about the CPU.  See <http://wiki.osdev.org/CPUID>.  */
struct cpuid cpuid (unsigned int leaf, unsigned int subleaf) {
  struct cpuid result;

  asm ("cpuid"
       : "=a" (result.eax), "=b" (result.ebx),
	 "=c" (result.ecx), "=d" (result.edx)
       : "a" (leaf), "c" (subleaf));
  
  return result;
}

/* Return true if the CPU supports the RDRAND instruction.  */
bool rdrand_supported (void) {
  struct cpuid extended = cpuid (1, 0);
  
  return (extended.ecx & bit_RDRND) != 0;
}

/* Initialize the hardware rand64 implementation.  */
void hardware_rand64_init (void) { }

/* Return a random value, using hardware operations.  */
unsigned long long hardware_rand64 (void) {
  unsigned long long int x;

  /* Work around GCC bug 107565
     <https://gcc.gnu.org/bugzilla/show_bug.cgi?id=107565>.  */
  x = 0;

  while (! _rdrand64_step (&x))
    continue;
  
  return x;
}

/* Finalize the hardware rand64 implementation.  */
void hardware_rand64_fini (void) { }

/* wrapper for mrand48_r */
unsigned long long  mrand48_r_wrapper() {
  struct drand48_data buffer;
  unsigned long long result;
  unsigned int seed_init = (unsigned int) time(NULL);
  
  srand48_r((long int) seed_init, &buffer);
  mrand48_r(&buffer, (long int*) &result);

  return result;
}
