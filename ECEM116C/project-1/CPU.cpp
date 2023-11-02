#include "CPU.h"
#include "util.h"

instruction::instruction(std::bitset<32> fetch) {
    instr = fetch;
}





CPU::CPU() {
    PC = 0; 
    for (int i = 0; i < 4096; i++)
        dmemory[i] = (0);
}


// fetch instruction
std::bitset<32> CPU::Fetch(std::bitset<8> *instmem) {
    std::bitset<32> instr = ((((instmem[PC + 3].to_ulong()) << 24)) +
            ((instmem[PC + 2].to_ulong()) << 16) +
            ((instmem[PC + 1].to_ulong()) << 8) +
            (instmem[PC + 0].to_ulong())); // get 32 bit instruction
    PC += 4;                                           // increment PC
    return instr;
}


// decode the instruction
bool CPU::decode(instruction *curr) {
    // parse the instruction into respective bitsets
    std::bitset<32> instr = curr->instr;
    curr->funct7 = std::bitset<FUNCT7_WIDTH>((instr.to_ulong() & FUNCT7_MASK) >> 25);
    curr->rs2    = std::bitset<REGISTER_WIDTH>((instr.to_ulong() & RS2_MASK) >> 20);
    curr->rs1    = std::bitset<REGISTER_WIDTH>((instr.to_ulong() & RS1_MASK) >> 15);
    curr->funct3 = std::bitset<FUNCT3_WIDTH>((instr.to_ulong() & FUNCT3_MASK) >> 12);
    curr->rd     = std::bitset<REGISTER_WIDTH>((instr.to_ulong() & RD_MASK) >> 7);
    curr->opcode = std::bitset<OPCODE_WIDTH>((instr.to_ulong() & OPCODE_MASK));

    // get the instruction type (alu op)
    this->controller.alu_op = CTRL::alu_op(curr);
    curr->operation_type = this->controller.alu_op;
    curr->operation = CTRL::alu_control(this->controller.alu_op, curr);

    CTRL::set_controller(&(this->controller));


    curr->immediate = util::immediate_generator(curr);

    return curr->opcode.to_ulong();
}


// PC manipulation
unsigned long CPU::readPC() { 
    return this->PC; 
}


void CPU::addPC(unsigned long offset) { 
    if (this->controller.branch)
        this->PC += offset; 
}


void CPU::setPC(unsigned long offset) { 
    if (this->controller.jump)
        this->PC = offset; 
}


// register manipulation
std::bitset<32> CPU::get_register(const size_t &reg_num) {
    return this->register_file[reg_num];
}


void CPU::set_register(const size_t &reg_num, const std::bitset<32> &result) {
    if (this->controller.reg_write)
        this->register_file[reg_num] = result;
}


// dmemory manipulation
std::bitset<32> CPU::get_dmemory(const size_t &addr) {
    if (this->controller.mem_read && this->controller.mem_to_reg)
        return std::bitset<32>(this->dmemory[addr]);
    return std::bitset<32>(0);
}


void CPU::set_dmemory(const size_t &addr, const std::bitset<32> &data) {
    if (this->controller.mem_write)
        this->dmemory[addr] = util::to_decimal(data);
}

