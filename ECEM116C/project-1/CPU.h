#include <bitset>
#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <string>


#define REGISTER_WIDTH 5

#define FUNCT3_WIDTH 3
#define FUNCT7_WIDTH 7

#define OPCODE_WIDTH 7

#define I_TYPE_IMM_WIDTH 12
#define B_TYPE_IMM_WIDTH 12
#define J_TYPE_IMM_WIDTH 20


#define R_TYPE_OPCODE 0b0110011
#define I_TYPE_OPCODE 0b0010011
#define B_TYPE_OPCODE 0b1100011
#define J_TYPE_OPCODE 0b1100111
#define LOAD_OPCODE   0b0000011
#define STORE_OPCODE  0b0100011

#define ADD_FUNCT3 0b000
#define ADD_FUNCT7 0b0000000

#define SUB_FUNCT3 0b000
#define SUB_FUNCT7 0b0100000

#define XOR_FUNCT3 0b100
#define XOR_FUNCT7 0b0000000

#define SRA_FUNCT3 0b101
#define SRA_FUNCT7 0b0100000

#define ADDI_FUNCT3 0b000
#define ANDI_FUNCT3 0b111

#define BLT_FUNCT3 0b100

#define LW_FUNCT3 0b010

#define SW_FUNCT3 0b010

enum OperationType {
  RTYPE,
  ITYPE,
  BTYPE,
  JTYPE,
  LOAD_TYPE,
  STORE_TYPE,
  NONE
};


enum Operation {
  ADD,
  SUB,
  XOR,
  SRA,
  ADDI,
  ANDI,
  BLT,
  JALR,
  LW,
  SW,
  DONE
};


class instruction {
public:
  std::bitset<32> instr;              // instruction
  instruction(std::bitset<32> fetch); // constructor

public:
  // metadata
  std::bitset<REGISTER_WIDTH> rs1;
  std::bitset<REGISTER_WIDTH> rs2;
  std::bitset<REGISTER_WIDTH> rd;
  std::bitset<OPCODE_WIDTH> opcode;
  std::bitset<FUNCT3_WIDTH> funct3;
  std::bitset<FUNCT7_WIDTH> funct7;
  std::bitset<32> immediate;
  Operation operation;
  OperationType operation_type;
};



class CPU {
private:
  int dmemory[4096]; // data memory byte addressable in little endian fashion;
  unsigned long PC;  // pc
  std::bitset<32> register_file[32];

public:
  CPU();
  unsigned long readPC();
  void addPC(unsigned long offset);
  void setPC(unsigned long offset);
  std::bitset<32> Fetch(std::bitset<8> *instmem);
  bool Decode(instruction *instr);

  std::bitset<32> get_register(const size_t &reg_num);
  void set_register(const size_t &reg_num, const std::bitset<32> &result);

  std::bitset<32> get_dmemory(const size_t &addr);
  void set_dmemory(const size_t &addr, const std::bitset<32> &data);
};

namespace util {
  template <size_t FromSize, size_t ToSize>
  std::bitset<ToSize> sign_extend(const std::bitset<FromSize>& original) {
    std::bitset<ToSize> extended;

    for (size_t i = 0; i < FromSize; i++)
      extended[i] = original[i];

    bool sign = original[FromSize - 1];
    for (size_t i = FromSize; i < ToSize; i++)
      extended[i] = sign;

    return extended;
  }

  std::bitset<32> parse_immediate(instruction *instr);
  Operation get_instruction(instruction *instr);
  OperationType get_instruction_type(instruction *instr);
  bool execute_instruction(CPU *cpu, instruction *instr);
  long int bitset_to_signed_long(const std::bitset<32> &bitset);
}

namespace rtype {
  bool RTYPE(CPU *cpu, instruction *instr);
  std::bitset<32> ADD(const std::bitset<32> &rs1, const std::bitset<32> &rs2);
  std::bitset<32> SUB(const std::bitset<32> &rs1, const std::bitset<32> &rs2);
  std::bitset<32> XOR(const std::bitset<32> &rs1, const std::bitset<32> &rs2);
  std::bitset<32> SRA(const std::bitset<32> &rs1, const std::bitset<32> &rs2);
}

namespace itype {
  bool ITYPE(CPU *cpu, instruction *instr);
  std::bitset<32> ADDI(const std::bitset<32> &rs1, const std::bitset<32> &imm);
  std::bitset<32> ANDI(const std::bitset<32> &rs1, const std::bitset<32> &imm);
}

namespace btype {
  bool BLT(CPU *cpu, instruction *instr);
}

namespace jtype {
  bool JALR(CPU *cpu, instruction *instr);
}

namespace store {
  bool SW(CPU *cpu, instruction *instr);
}

namespace load {
  bool LW(CPU *cpu, instruction *instr);
}

// add other functions and objects here
