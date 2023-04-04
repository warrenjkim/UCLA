#ifndef RAND64_HW_H
#define RAND64_HW_H

#include <cpuid.h>
#include <stdbool.h>

/* Hardware implementation.  */

/* Description of the current CPU.  */
struct cpuid { unsigned eax, ebx, ecx, edx; };

/* Return information about the CPU.  See <http://wiki.osdev.org/CPUID>.  */
struct cpuid cpuid (unsigned int, unsigned int);

/* Return true if the CPU supports the RDRAND instruction.  */
bool rdrand_supported (void);

/* Initialize the hardware rand64 implementation.  */
void hardware_rand64_init (void);

/* Return a random value, using hardware operations.  */
unsigned long long hardware_rand64 (void);

/* Finalize the hardware rand64 implementation.  */
void hardware_rand64_fini (void);

/* wrapper function for the GNU C mrand48_r function */
unsigned long long mrand48_r_wrapper();

#endif
