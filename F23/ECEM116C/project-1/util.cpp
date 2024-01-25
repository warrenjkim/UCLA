#include "util.h"

namespace util {
    bool execute_instruction(CPU *cpu, instruction *instr) {
        // check the instruction type and execute the corresponding 
        switch(cpu->controller.alu_op) {
            case OpType::RTYPE:
                return CTRL::RTYPE(cpu, instr);
            case OpType::ITYPE:
                return CTRL::ITYPE(cpu, instr);
            case OpType::JTYPE:
                return CTRL::JALR(cpu, instr);
            case OpType::BTYPE:
                return CTRL::BLT(cpu, instr);
            case OpType::LOAD_TYPE:
                return CTRL::LW(cpu, instr);
            case OpType::STORE_TYPE:
                return CTRL::SW(cpu, instr);
            case OpType::NONE:
                return true;
            default:
                return false;
        }

        return false;
    }


    // generates the immediate based on the type of instruction
    std::bitset<32> immediate_generator(instruction *instr) {
        // if the instruction is i-type, j-type, or lw
        if (
                instr->operation_type == OpType::ITYPE || 
                instr->operation_type == OpType::JTYPE || 
                instr->operation_type == OpType::LOAD_TYPE
           ) {

            std::bitset<I_TYPE_IMM_WIDTH> immediate((instr->instr.to_ulong() & I_TYPE_MASK) >> 20);
            return sign_extend<I_TYPE_IMM_WIDTH, 32>(immediate);
        }
        // if the instruction is sw
        else if (instr->operation_type == OpType::STORE_TYPE) {
            std::string instr_string = instr->instr.to_string();
            std::string imm_string = instr_string.substr(0, 7) + instr_string.substr(20, 5);
            std::bitset<I_TYPE_IMM_WIDTH> immediate(imm_string);
            return sign_extend<I_TYPE_IMM_WIDTH, 32>(immediate);
        }
        // if the instruction is blt
        else if (instr->operation_type == OpType::BTYPE) {
            std::string instr_string = instr->instr.to_string();
            std::string imm_string = 
                instr_string.substr(0, 1) +
                instr_string.substr(24, 1) +
                instr_string.substr(1, 6) +
                instr_string.substr(20, 4);
            std::bitset<B_TYPE_IMM_WIDTH> immediate(imm_string);

            // return the 32-bit sign extended version
            return sign_extend<B_TYPE_IMM_WIDTH, 32>(immediate);
        }

        return std::bitset<32>(0);
    }


    // 2's complement
    long int to_decimal(const std::bitset<32> &bitset) {
        if (bitset.test(31))
            return -((~bitset).to_ulong() + 1);
        else
            return static_cast<long>(bitset.to_ulong());
    }


}


