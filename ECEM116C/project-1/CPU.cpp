#include "CPU.h"

instruction::instruction(std::bitset<32> fetch) {
  // std::cout << "fetch: " << fetch << std::endl;
  instr = fetch;
  // std::cout << instr << std::endl;
}


CPU::CPU() {
  PC = 0;                        // set PC to 0
  for (int i = 0; i < 4096; i++) // copy instrMEM
  {
    dmemory[i] = (0);
  }
}

std::bitset<32> CPU::Fetch(std::bitset<8> *instmem) {
  std::bitset<32> instr = ((((instmem[PC + 3].to_ulong()) << 24)) +
                      ((instmem[PC + 2].to_ulong()) << 16) +
                      ((instmem[PC + 1].to_ulong()) << 8) +
                      (instmem[PC + 0].to_ulong())); // get 32 bit instruction
  PC += 4;                                           // increment PC
  return instr;
}

bool CPU::Decode(instruction *curr) {
  std::bitset<FUNCT7_WIDTH> funct7(curr->instr.to_string().substr(0, 7));
  std::bitset<REGISTER_WIDTH> rs2(curr->instr.to_string().substr(7, 5));
  std::bitset<REGISTER_WIDTH> rs1(curr->instr.to_string().substr(12, 5));
  std::bitset<FUNCT3_WIDTH> funct3(curr->instr.to_string().substr(17, 3));
  std::bitset<REGISTER_WIDTH> rd(curr->instr.to_string().substr(20, 5));
  std::bitset<OPCODE_WIDTH> opcode(curr->instr.to_string().substr(25, 7));

  curr->funct7 = funct7;
  curr->rs2 = rs2;
  curr->rs1 = rs1;
  curr->funct3 = funct3;
  curr->rd = rd;
  curr->opcode = opcode;
  curr->operation = util::get_instruction(curr);
  curr->operation_type = util::get_instruction_type(curr);

  std::cout << "instruction: " << curr->instr << std::endl;
  // std::cout << "funct7: " << curr->funct7 << std::endl;
  // std::cout << "rs2: " << curr->rs2 << std::endl;
  // std::cout << "rs1: " << curr->rs1 << std::endl;
  // std::cout << "funct3: " << curr->funct3 << std::endl;
  // std::cout << "rd: " << curr->rd << std::endl;
  // std::cout << "opcode: " << curr->opcode << std::endl;
  // std::cout << "instruction type: " << curr->operation << std::endl;

  return true;
}

unsigned long CPU::readPC() { return this->PC; }

void CPU::addPC(unsigned long offset) { this->PC += offset; }

void CPU::setPC(unsigned long offset) { this->PC = offset; }

std::bitset<32> CPU::get_register(const size_t &reg_num) {
  return this->register_file[reg_num];
}

void CPU::set_register(const size_t &reg_num, const std::bitset<32> &result) {
  this->register_file[reg_num] = result;
}

std::bitset<32> CPU::get_dmemory(const size_t &addr) {
  return std::bitset<32>(this->dmemory[addr]);
}

void CPU::set_dmemory(const size_t &addr, const std::bitset<32> &data) {
  this->dmemory[addr] = util::bitset_to_signed_long(data);
}







bool util::execute_instruction(CPU *cpu, instruction *instr) {
  switch(instr->operation_type) {
  case RTYPE:
    return rtype::RTYPE(cpu, instr);
  case ITYPE:
    return itype::ITYPE(cpu, instr);
  case JTYPE:
    return jtype::JALR(cpu, instr);
  case BTYPE:
    return btype::BLT(cpu, instr);
  case LOAD_TYPE:
    return load::LW(cpu, instr);
  case STORE_TYPE:
    return store::SW(cpu, instr);
  default:
    return true;
  }
  return true;
}






// Add other functions here ...

OperationType util::get_instruction_type(instruction *instr) {
  if (instr->opcode == R_TYPE_OPCODE)
    return RTYPE;
  else if (instr->opcode == I_TYPE_OPCODE)
    return ITYPE;
  else if (instr->opcode == B_TYPE_OPCODE)
    return BTYPE;
  else if (instr->opcode == J_TYPE_OPCODE)
    return JTYPE;
  else if (instr->opcode == LOAD_OPCODE)
    return LOAD_TYPE;
  else if (instr->opcode == STORE_OPCODE)
    return STORE_TYPE;
  return NONE;
}

Operation util::get_instruction(instruction *instr) {
  // r-type
  if (instr->opcode == R_TYPE_OPCODE) {
    if (instr->funct3 == ADD_FUNCT3 && instr->funct7 == ADD_FUNCT7)
      return ADD;
    else if (instr->funct3 == SUB_FUNCT3 && instr->funct7 == SUB_FUNCT7)
      return SUB;
    else if (instr->funct3 == XOR_FUNCT3 && instr->funct7 == XOR_FUNCT7)
      return XOR;
    else if (instr->funct3 == SRA_FUNCT3 && instr->funct7 == SRA_FUNCT7)
      return SRA;
  }

  // i-type
  else if (instr->opcode == I_TYPE_OPCODE) {
    if (instr->funct3 == ADDI_FUNCT3)
      return ADDI;
    else if (instr->funct3 == ANDI_FUNCT3)
      return ANDI;
  }
  // b-type
  else if (instr->opcode == B_TYPE_OPCODE) {
    if (instr->funct3 == BLT_FUNCT3)
      return BLT;
  }
  // j-type
  else if (instr->opcode == J_TYPE_OPCODE)
    return JALR;

  else if (instr->opcode == LOAD_OPCODE) {
    if (instr->funct3 == LW_FUNCT3)
      return LW;
  }

  else if (instr->opcode == STORE_OPCODE) {
    if (instr->funct3 == SW_FUNCT3)
      return SW;
  }

  return DONE;
}










bool rtype::RTYPE(CPU *cpu, instruction *instr) {
  std::bitset<32> rs1(cpu->get_register(instr->rs1.to_ulong()));
  std::bitset<32> rs2(cpu->get_register(instr->rs2.to_ulong()));

  std::cout << "rs1: " << rs1 << " (" << rs1.to_ulong() << ")" << std::endl;
  std::cout << "rs2: " << rs2 << " (" << rs2.to_ulong() << ")" << std::endl;

  std::bitset<32> (*operation)(const std::bitset<32> &, const std::bitset<32> &) = nullptr;

  switch(instr->operation) {
  case ::ADD:
    operation = rtype::ADD;
    break;
  case ::SUB:
    operation = rtype::SUB;
    break;
  case ::XOR:
    operation = rtype::XOR;
    break;
  case ::SRA:
    operation = rtype::SRA;
    break;
  default:
    return false;
  }

  std::bitset<32> result(operation(rs1, rs2));

  cpu->set_register(instr->rd.to_ulong(), result);
  std::cout << "result: " << util::bitset_to_signed_long(result) << std::endl << std::endl;

  return true;
}





std::bitset<32> rtype::ADD(const std::bitset<32> &rs1, const std::bitset<32> &rs2) {
  std::cout << "instruction: ADD" << std::endl;
  return std::bitset<32>(util::bitset_to_signed_long(rs1) + util::bitset_to_signed_long(rs2));
}


std::bitset<32> rtype::SUB(const std::bitset<32> &rs1, const std::bitset<32> &rs2) {
  std::cout << "instruction: SUB" << std::endl;
  return std::bitset<32>(util::bitset_to_signed_long(rs1) - util::bitset_to_signed_long(rs2));
}


std::bitset<32> rtype::XOR(const std::bitset<32> &rs1, const std::bitset<32> &rs2) {
  std::cout << "instruction: XOR" << std::endl;
  return rs1 ^ rs2;
}


std::bitset<32> rtype::SRA(const std::bitset<32> &rs1, const std::bitset<32> &rs2) {
  std::cout << "instruction: SRA" << std::endl;
  return std::bitset<32>(util::bitset_to_signed_long(rs1) >> util::bitset_to_signed_long(rs2));
}
















bool itype::ITYPE(CPU *cpu, instruction *instr) {
  std::bitset<32> rs1(cpu->get_register(instr->rs1.to_ulong()));
  std::bitset<32> imm(util::parse_immediate(instr));

  std::cout << "rs1: " << rs1 << " (" << util::bitset_to_signed_long(rs1) << ")" << std::endl;
  std::cout << "imm: " << imm << " (" << util::bitset_to_signed_long(imm) << ")" << std::endl;

  std::bitset<32> (*operation)(const std::bitset<32> &, const std::bitset<32> &) = nullptr;

  switch(instr->operation) {
  case ::ADDI:
    operation = itype::ADDI;
    break;
  case ::ANDI:
    operation = itype::ANDI;
    break;
  default:
    return false;
  }

  std::bitset<32> result = operation(rs1, imm);
  std::cout << "result: " << result.to_ulong() << std::endl << std::endl;
  cpu->set_register(instr->rd.to_ulong(), result);
  return true;
}





std::bitset<32> itype::ADDI(const std::bitset<32> &rs1, const std::bitset<32> &imm) {
  std::cout << "instruction: ADDI" << std::endl;
  return rtype::ADD(rs1, imm);
}





std::bitset<32> itype::ANDI(
                const std::bitset<32> &rs1,
                const std::bitset<32> &imm) {
  
  std::cout << "instruction: ANDI" << std::endl;

  return rs1 & imm;
}





bool store::SW(CPU *cpu, instruction *instr) {
  std::cout << "instruction: SW" << std::endl;
  std::bitset<32> base(cpu->get_register(instr->rs1.to_ulong()));
  std::bitset<32> offset(util::parse_immediate(instr));
  std::bitset<32> write_data(cpu->get_register(instr->rs2.to_ulong()));

  std::cout << "base: " << base << " (" << base.to_ulong() << ")" << std::endl;
  std::cout << "offset: " << offset << " (" << util::bitset_to_signed_long(offset) << ")" << std::endl;
  std::cout << "write data: " << write_data << " (" << util::bitset_to_signed_long(write_data) << ")" << std::endl;

  size_t addr = util::bitset_to_signed_long(rtype::ADD(base, offset));
  cpu->set_dmemory(addr, write_data);

  std::cout << "written to dmemory[" << addr << "]: " << write_data << " (" << util::bitset_to_signed_long(write_data) << ")" << std::endl << std::endl;
  return cpu->get_dmemory(addr) == write_data;
}


bool load::LW(CPU *cpu, instruction *instr) {
  std::cout << "instruction: LW" << std::endl;
  std::bitset<32> base(cpu->get_register(instr->rs1.to_ulong()));
  std::bitset<32> offset(util::parse_immediate(instr));

  std::cout << "base: " << base << " (" << base.to_ulong() << ")" << std::endl;
  std::cout << "offset: " << offset << " (" << util::bitset_to_signed_long(offset) << ")" << std::endl;

  size_t addr = util::bitset_to_signed_long(rtype::ADD(base, offset));
  cpu->set_register(instr->rd.to_ulong(), cpu->get_dmemory(addr));
  std::cout << "result: " <<  cpu->get_register(instr->rd.to_ulong()) <<
    " (" << util::bitset_to_signed_long(cpu->get_register(instr->rd.to_ulong())) << ")"
            << std::endl << std::endl;

  return true;
}


bool btype::BLT(CPU *cpu, instruction *instr) {
  std::cout << "instruction: BLT" << std::endl;
  std::bitset<32> rs1(cpu->get_register(instr->rs1.to_ulong()));
  std::bitset<32> rs2(cpu->get_register(instr->rs2.to_ulong()));
  std::bitset<32> offset(util::parse_immediate(instr));

  std::cout << "rs1: " << rs1 << " (" << rs1.to_ulong() << ")" << std::endl;
  std::cout << "rs2: " << rs2 << " (" << rs2.to_ulong() << ")" << std::endl;
  std::cout << "offset: " << (offset) << " (" << util::bitset_to_signed_long(offset) << ")" << std::endl << std::endl;;
  
  if (util::bitset_to_signed_long(rs1) < util::bitset_to_signed_long(rs2)) 
    cpu->addPC(-4 + util::bitset_to_signed_long(offset.to_ulong()));
  else
    return true;
    
  return true;
}


bool jtype::JALR(CPU *cpu, instruction *instr) {
  std::cout << "instruction: JALR" << std::endl;
  std::bitset<32> rs1(cpu->get_register(instr->rs1.to_ulong()));
  std::bitset<32> rd(cpu->get_register(instr->rd.to_ulong()));
  std::bitset<32> offset(util::parse_immediate(instr));

  std::cout << "rs1: " << rs1 << " (" << rs1.to_ulong() << ")" << std::endl;
  std::cout << "rd: " << rd << " (" << rd.to_ulong() << ")" << std::endl;
  std::cout << "offset: " << (offset) << " (" << util::bitset_to_signed_long(offset) << ")" << std::endl << std::endl;;
  
  cpu->set_register(instr->rd.to_ulong(), std::bitset<32>(cpu->readPC()));
  cpu->setPC(util::bitset_to_signed_long(rtype::ADD(rs1, offset)));
    
  return true;

}



















std::bitset<32> util::parse_immediate(instruction *instr) {
  if (instr->opcode == I_TYPE_OPCODE || instr->opcode == J_TYPE_OPCODE || instr->opcode == LOAD_OPCODE) {
    std::bitset<I_TYPE_IMM_WIDTH> immediate(instr->instr.to_string().substr(0, 12));
    return util::sign_extend<I_TYPE_IMM_WIDTH, 32>(immediate);
  }
  else if (instr->opcode == STORE_OPCODE) {
    std::string instr_string = instr->instr.to_string();
    std::string imm_string = instr_string.substr(0, 7) + instr_string.substr(20, 5);
    std::bitset<I_TYPE_IMM_WIDTH> immediate(imm_string);
    return util::sign_extend<I_TYPE_IMM_WIDTH, 32>(immediate);
  }
  else if (instr->opcode == B_TYPE_OPCODE) {
    std::string instr_string = instr->instr.to_string();
    std::string imm_string = instr_string.substr(0, 1) +
      instr_string.substr(24, 1) +
      instr_string.substr(1, 5) +
      instr_string.substr(20, 4) +
      "0";
    std::bitset<B_TYPE_IMM_WIDTH> immediate(imm_string);

    return util::sign_extend<B_TYPE_IMM_WIDTH, 32>(immediate);
  }
  // else if (instr->opcode == J_TYPE_OPCODE) {
  //   std::string instr_string = instr->instr.to_string();
  //   std::string imm_string = instr_string.substr(0, 1) +
  //     instr_string.substr(11, 8) +
  //     instr_string.substr(10, 1) +
  //     instr_string.substr(1, 9) +
  //     "0";

  //   std::bitset<J_TYPE_IMM_WIDTH> immediate(imm_string);

  //   return util::sign_extend<J_TYPE_IMM_WIDTH, 32>(immediate);
  // }


  return 0;
}






long int util::bitset_to_signed_long(const std::bitset<32> &bitset) {
  if (bitset.test(31))
    return -((~bitset).to_ulong() + 1);
  else
    return static_cast<long>(bitset.to_ulong());
}

