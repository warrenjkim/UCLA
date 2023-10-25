#include "alu.h"

namespace ALU {
    std::bitset<32> ADD(const std::bitset<32> &rs1, const std::bitset<32> &rs2) {
        // std::cout << "instruction: ADD" << std::endl;
        return std::bitset<32>(util::to_decimal(rs1) + util::to_decimal(rs2));
    }


    std::bitset<32> SUB(const std::bitset<32> &rs1, const std::bitset<32> &rs2) {
        // std::cout << "instruction: SUB" << std::endl;
        return std::bitset<32>(util::to_decimal(rs1) - util::to_decimal(rs2));
    }


    std::bitset<32> XOR(const std::bitset<32> &rs1, const std::bitset<32> &rs2) {
        // std::cout << "instruction: XOR" << std::endl;
        return rs1 ^ rs2;
    }


    std::bitset<32> SRA(const std::bitset<32> &rs1, const std::bitset<32> &rs2) {
        // std::cout << "instruction: SRA" << std::endl;
        return std::bitset<32>(util::to_decimal(rs1) >> util::to_decimal(rs2));
    }


    std::bitset<32> ADDI(const std::bitset<32> &rs1, const std::bitset<32> &imm) {
        // std::cout << "instruction: ADDI" << std::endl;
        return ADD(rs1, imm);
    }


    std::bitset<32> ANDI(const std::bitset<32> &rs1, const std::bitset<32> &imm) {
        // std::cout << "instruction: ANDI" << std::endl;
        return rs1 & imm;
    }
}
