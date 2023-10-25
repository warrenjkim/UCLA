#include "controller.h"
#include "alu.h"
#include "util.h"


namespace CTRL {
    bool RTYPE(CPU *cpu, instruction *instr) {
        std::bitset<32> rs1(cpu->get_register(instr->rs1.to_ulong()));
        std::bitset<32> rs2(cpu->get_register(instr->rs2.to_ulong()));

        // std::cout << "rs1: " << rs1 << " (" << rs1.to_ulong() << ")" << std::endl;
        // std::cout << "rs2: " << rs2 << " (" << rs2.to_ulong() << ")" << std::endl;

        std::bitset<32> (*operation)(const std::bitset<32> &, const std::bitset<32> &) = nullptr;

        switch(instr->operation) {
            case ADD:
                operation = ALU::ADD;
                break;
            case SUB:
                operation = ALU::SUB;
                break;
            case XOR:
                operation = ALU::XOR;
                break;
            case SRA:
                operation = ALU::SRA;
                break;
            default:
                return false;
        }

        std::bitset<32> result(operation(rs1, rs2));

        cpu->set_register(instr->rd.to_ulong(), result);
        // std::cout << "result: " << util::to_decimal(result) << std::endl << std::endl;

        return cpu->get_register(instr->rd.to_ulong()) == result;
    }



    bool ITYPE(CPU *cpu, instruction *instr) {
        std::bitset<32> rs1(cpu->get_register(instr->rs1.to_ulong()));
        std::bitset<32> imm(util::parse_immediate(instr));

        // std::cout << "rs1: " << rs1 << " (" << util::to_decimal(rs1) << ")" << std::endl;
        // std::cout << "imm: " << imm << " (" << util::to_decimal(imm) << ")" << std::endl;

        std::bitset<32> (*operation)(const std::bitset<32> &, const std::bitset<32> &) = nullptr;

        switch(instr->operation) {
            case ADDI:
                operation = ALU::ADDI;
                break;
            case ANDI:
                operation = ALU::ANDI;
                break;
            default:
                return false;
        }

        std::bitset<32> result = operation(rs1, imm);
        // std::cout << "result: " << result.to_ulong() << std::endl << std::endl;
        cpu->set_register(instr->rd.to_ulong(), result);
        return cpu->get_register(instr->rd.to_ulong()) == result;
    }


    bool SW(CPU *cpu, instruction *instr) {
        // std::cout << "instruction: SW" << std::endl;
        std::bitset<32> base(cpu->get_register(instr->rs1.to_ulong()));
        std::bitset<32> offset(util::parse_immediate(instr));
        std::bitset<32> write_data(cpu->get_register(instr->rs2.to_ulong()));

        // std::cout << "base: " << base << " (" << base.to_ulong() << ")" << std::endl;
        // std::cout << "offset: " << offset << " (" << util::to_decimal(offset) << ")" << std::endl;
        // std::cout << "write data: " << write_data << " (" << util::to_decimal(write_data) << ")" << std::endl;

        size_t addr = util::to_decimal(ALU::ADD(base, offset));
        cpu->set_dmemory(addr, write_data);

        // std::cout << "written to dmemory[" << addr << "]: " << write_data << " (" << util::to_decimal(write_data) << ")" << std::endl << std::endl;
        return cpu->get_dmemory(addr) == write_data;
    }


    bool LW(CPU *cpu, instruction *instr) {
        // std::cout << "instruction: LW" << std::endl;
        std::bitset<32> base(cpu->get_register(instr->rs1.to_ulong()));
        std::bitset<32> offset(util::parse_immediate(instr));

        // std::cout << "base: " << base << " (" << base.to_ulong() << ")" << std::endl;
        // std::cout << "offset: " << offset << " (" << util::to_decimal(offset) << ")" << std::endl;

        size_t addr = util::to_decimal(ALU::ADD(base, offset));
        cpu->set_register(instr->rd.to_ulong(), cpu->get_dmemory(addr));
        // std::cout << "result: " <<  cpu->get_register(instr->rd.to_ulong()) <<
            // " (" << util::to_decimal(cpu->get_register(instr->rd.to_ulong())) << ")"
            // << std::endl << std::endl;

        return cpu->get_register(instr->rd.to_ulong()) == cpu->get_dmemory(addr);
    }


    bool BLT(CPU *cpu, instruction *instr) {
        // std::cout << "instruction: BLT" << std::endl;
        std::bitset<32> rs1(cpu->get_register(instr->rs1.to_ulong()));
        std::bitset<32> rs2(cpu->get_register(instr->rs2.to_ulong()));
        std::bitset<32> offset(util::parse_immediate(instr));

        // std::cout << "rs1: " << rs1 << " (" << rs1.to_ulong() << ")" << std::endl;
        // std::cout << "rs2: " << rs2 << " (" << rs2.to_ulong() << ")" << std::endl;
        // std::cout << "offset: " << (offset) << " (" << util::to_decimal(offset) << ")" << std::endl << std::endl;;

        unsigned long old_PC = cpu->readPC();
        if (util::to_decimal(rs1) < util::to_decimal(rs2)) 
            cpu->addPC(-4 + util::to_decimal(offset.to_ulong()));

        return cpu->readPC() != old_PC;
    }


    bool JALR(CPU *cpu, instruction *instr) {
        // std::cout << "instruction: JALR" << std::endl;
        std::bitset<32> rs1(cpu->get_register(instr->rs1.to_ulong()));
        std::bitset<32> offset(util::parse_immediate(instr));

        // std::cout << "rs1: " << rs1 << " (" << rs1.to_ulong() << ")" << std::endl;
        // std::cout << "rd: " << rd << " (" << rd.to_ulong() << ")" << std::endl;
        // std::cout << "offset: " << (offset) << " (" << util::to_decimal(offset) << ")" << std::endl << std::endl;;

        cpu->set_register(instr->rd.to_ulong(), std::bitset<32>(cpu->readPC()));

        unsigned long old_PC = cpu->readPC();
        cpu->setPC(util::to_decimal(ALU::ADD(rs1, offset)));

        return (cpu->get_register(instr->rd.to_ulong()) == std::bitset<32>(cpu->readPC())) &&
               (cpu->readPC() == old_PC);
    }
}
