#ifndef CPU_H
#define CPU_H

#include <bitset>
#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <string>

#include "macros.h"
#include "enums.h"


/* @brief instruction struct mimics a CPU instruction.
 * Contains:
 * rs1/2: source register 1/2,         bitset<5>.
 * rd:    destination register,        bitset<5>.
 * opcode:                             bitset<7>.
 * funct3:                             bitset<3>.
 * funct7:                             bitset<7>.
 * immediate: may (not) be used,       bitset<32>.
 * operation: specific alu op,         Operation.
 * operation: alu op to send to ALU    OperationType.
 */
struct instruction {
    std::bitset<32> instr;              // instruction
    instruction(std::bitset<32> fetch); // constructor

    std::bitset<REGISTER_WIDTH> rs1;
    std::bitset<REGISTER_WIDTH> rs2;
    std::bitset<REGISTER_WIDTH> rd;

    std::bitset<OPCODE_WIDTH> opcode;
    std::bitset<FUNCT3_WIDTH> funct3;
    std::bitset<FUNCT7_WIDTH> funct7;

    std::bitset<32> immediate;

    OpType::OperationType operation_type;
    OpType::Operation operation;
};


/* @brief Controller struct mimics the Controller of a CPU.
 * Control bits for:
 * reg_write: write to register.
 * alu_src: r-type or i-type instruction.
 * branch: branch or not.
 * mem_read: read from dmemory.
 * mem_write: write to dmemory.
 * mem_to_reg: memory to register value.
 * jump: jump or not.
 *
 * @note All values initially set to 0.
 */
struct Controller {
    bool reg_write;
    bool alu_src;
    bool branch;
    bool mem_read;
    bool mem_write;
    bool mem_to_reg;
    bool jump;
    OpType::OperationType alu_op;

    Controller() {
        reg_write = 0;
        alu_src = 0;
        branch = 0;
        mem_read = 0;
        mem_write = 0;
        mem_to_reg = 0;
        jump = 0;
        alu_op = OpType::NONE;
    }
};


/* @brief CPU class mimics the CPU.
 * Contains:
 * PC mainpulation (get, set, add).
 * fetch/decode instructions.
 * register manipulation (get, set).
 * dmemory manipulation (get, set).
 */
class CPU {
    private:
        int dmemory[4096]; // data memory byte addressable in little endian fashion;
        unsigned long PC;  // pc
        std::bitset<32> register_file[32]; 

    public:
        struct Controller controller;

    public:
        CPU();

        // PC manipulation
        unsigned long readPC();
        void addPC(unsigned long offset);
        void setPC(unsigned long offset);

        // fetch/decode
        std::bitset<32> Fetch(std::bitset<8> *instmem);
        bool decode(instruction *curr);

        // register manipulation
        std::bitset<32> get_register(const size_t &reg_num);
        void set_register(const size_t &reg_num, const std::bitset<32> &result);

        // dmemory manipulation
        std::bitset<32> get_dmemory(const size_t &addr);
        void set_dmemory(const size_t &addr, const std::bitset<32> &data);
};

#endif
