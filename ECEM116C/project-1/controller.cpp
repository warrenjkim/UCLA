#include "controller.h"
#include "alu.h"
#include "util.h"


namespace CTRL {
    // determine the correct opcode type (part of the controller)
    OpType::OperationType alu_op(instruction *instr) {
        if (instr->opcode == R_TYPE_OPCODE)
            return OpType::RTYPE;
        else if (instr->opcode == I_TYPE_OPCODE)
            return OpType::ITYPE;
        else if (instr->opcode == B_TYPE_OPCODE)
            return OpType::BTYPE;
        else if (instr->opcode == J_TYPE_OPCODE)
            return OpType::JTYPE;
        else if (instr->opcode == LOAD_OPCODE)
            return OpType::LOAD_TYPE;
        else if (instr->opcode == STORE_OPCODE)
            return OpType::STORE_TYPE;
        return OpType::NONE;
    }


    // specifies the operation to be executed
    OpType::Operation alu_control(OpType::OperationType alu_op, instruction *instr) {
        switch (alu_op) {
            case OpType::RTYPE:
                if (instr->funct3 == ADD_FUNCT3 && instr->funct7 == ADD_FUNCT7)
                    return OpType::ADD;
                else if (instr->funct3 == SUB_FUNCT3 && instr->funct7 == SUB_FUNCT7)
                    return OpType::SUB;
                else if (instr->funct3 == XOR_FUNCT3 && instr->funct7 == XOR_FUNCT7)
                    return OpType::XOR;
                else if (instr->funct3 == SRA_FUNCT3 && instr->funct7 == SRA_FUNCT7)
                    return OpType::SRA;
            case OpType::ITYPE:
                if (instr->funct3 == ADDI_FUNCT3)
                    return OpType::ADDI;
                else if (instr->funct3 == ANDI_FUNCT3)
                    return OpType::ANDI;
            case OpType::BTYPE:
                if (instr->funct3 == BLT_FUNCT3)
                    return OpType::BLT;
            case OpType::JTYPE:
                return OpType::JALR;
            case OpType::LOAD_TYPE:
                if (instr->funct3 == LW_FUNCT3)
                    return OpType::LW;
            case OpType::STORE_TYPE:
                if (instr->funct3 == SW_FUNCT3)
                    return OpType::SW;
            default:
                return OpType::DONE;
        }

        return OpType::DONE;
    }
    

    // set the CPU controller
    void set_controller(struct Controller *controller) {
        switch (controller->alu_op) {
            case OpType::RTYPE:
                controller->reg_write = 1;
            case OpType::ITYPE:
                controller->reg_write = 1;
                controller->alu_src = 1;
            case OpType::BTYPE:
                controller->branch = 1;
            case OpType::JTYPE:
                controller->jump = 1;
            case OpType::STORE_TYPE:
                controller->alu_src = 1;
                controller->mem_write = 1;
            case OpType::LOAD_TYPE:
                controller->mem_read = 1;
                controller->mem_to_reg = 1;
            default:
                return;
        }
    }


    // prepare the r-type instruction for the ALU
    bool RTYPE(CPU *cpu, instruction *instr) {
        std::bitset<32> rs1(cpu->get_register(instr->rs1.to_ulong()));
        std::bitset<32> rs2(cpu->get_register(instr->rs2.to_ulong()));

        std::bitset<32> (*operation)(const std::bitset<32> &, const std::bitset<32> &) = nullptr;

        // set the function pointer to the correct ALU operation
        switch(instr->operation) {
            case OpType::ADD:
                operation = ALU::ADD;
                break;
            case OpType::SUB:
                operation = ALU::SUB;
                break;
            case OpType::XOR:
                operation = ALU::XOR;
                break;
            case OpType::SRA:
                operation = ALU::SRA;
                break;
            default:
                return false;
        }

        // write to register
        std::bitset<32> result(operation(rs1, rs2));
        cpu->set_register(instr->rd.to_ulong(), result);

        // ensure operation executed correctly
        return cpu->get_register(instr->rd.to_ulong()) == result;
    }



    // prepare the i-type instruction for the ALU
    bool ITYPE(CPU *cpu, instruction *instr) {
        std::bitset<32> rs1(cpu->get_register(instr->rs1.to_ulong()));
        std::bitset<32> imm(instr->immediate);

        std::bitset<32> (*operation)(const std::bitset<32> &, const std::bitset<32> &) = nullptr;

        // set the function pointer to the correct ALU operation
        switch(instr->operation) {
            case OpType::ADDI:
                operation = ALU::ADDI;
                break;
            case OpType::ANDI:
                operation = ALU::ANDI;
                break;
            default:
                return false;
        }

        // write to register
        std::bitset<32> result = operation(rs1, imm);
        cpu->set_register(instr->rd.to_ulong(), result);

        // ensure operation executed correctly
        return cpu->get_register(instr->rd.to_ulong()) == result;
    }


    // store word instruction. 
    // NOT in ALU because we know the instruction ahead of time.
    bool SW(CPU *cpu, instruction *instr) {
        std::bitset<32> base(cpu->get_register(instr->rs1.to_ulong()));
        std::bitset<32> offset(instr->immediate);
        std::bitset<32> write_data(cpu->get_register(instr->rs2.to_ulong()));

        // write to dmemory
        size_t addr = util::to_decimal(ALU::ADD(base, offset));
        cpu->set_dmemory(addr, write_data);
        
        // ensure operation executed correctly
        return cpu->get_dmemory(addr) == write_data;
    }


    // load word instruction. 
    // NOT in ALU because we know the instruction ahead of time.
    bool LW(CPU *cpu, instruction *instr) {
        std::bitset<32> base(cpu->get_register(instr->rs1.to_ulong()));
        std::bitset<32> offset(instr->immediate);

        // write to register
        size_t addr = util::to_decimal(ALU::ADD(base, offset));
        cpu->set_register(instr->rd.to_ulong(), cpu->get_dmemory(addr));
        
        // ensure operation executed correctly
        return cpu->get_register(instr->rd.to_ulong()) == cpu->get_dmemory(addr);
    }


    // branch less than instruction. 
    // NOT in ALU because we know the instruction ahead of time.
    bool BLT(CPU *cpu, instruction *instr) {
        std::bitset<32> rs1(cpu->get_register(instr->rs1.to_ulong()));
        std::bitset<32> rs2(cpu->get_register(instr->rs2.to_ulong()));
        std::bitset<32> offset(instr->immediate << 1); // bitshift left by one
        
        // branch if <
        unsigned long old_PC = cpu->readPC();
        if (util::to_decimal(rs1) < util::to_decimal(rs2))  {
            cpu->addPC(-4 + util::to_decimal(offset.to_ulong()));
            return cpu->readPC() != old_PC;
        }

        // ensure operation executed correctly
        return cpu->readPC() == old_PC;
    }


    // jump and link instruction. 
    // NOT in ALU because we know the instruction ahead of time.
    bool JALR(CPU *cpu, instruction *instr) {
        std::bitset<32> rs1(cpu->get_register(instr->rs1.to_ulong()));
        std::bitset<32> offset(instr->immediate);


        // set the PC before jumping
        unsigned long old_PC = cpu->readPC();
        cpu->set_register(instr->rd.to_ulong(), std::bitset<32>(old_PC));

        // jump
        cpu->setPC(util::to_decimal(ALU::ADD(rs1, offset)));

        // ensure operation executed correctly
        return (cpu->get_register(instr->rd.to_ulong()) == std::bitset<32>(old_PC));
    }
}
