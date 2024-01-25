#ifndef UTIL_H
#define UTIL_H

#include <bitset>
#include <string>

#include "CPU.h"
#include "controller.h"
#include "enums.h"
#include "macros.h"


namespace util {
    // sign extends an immediate from 12/20 bits to 32 bits.
    template <size_t from_size, size_t to_size>
        std::bitset<to_size> sign_extend(const std::bitset<from_size>& original) {
            std::bitset<to_size> extended;

            for (size_t i = 0; i < from_size; i++)
                extended[i] = original[i];

            bool sign = original[from_size - 1];
            for (size_t i = from_size; i < to_size; i++)
                extended[i] = sign;

            return extended;
        }

    std::bitset<32> immediate_generator(instruction *instr);
    
    bool execute_instruction(CPU *cpu, instruction *instr);

    long int to_decimal(const std::bitset<32> &bitset);
}

#endif
