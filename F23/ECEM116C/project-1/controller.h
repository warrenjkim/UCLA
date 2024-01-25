#ifndef CTRL_H
#define CTRL_H

#include "CPU.h"


namespace CTRL {
    // gets the ALU control
    OpType::Operation alu_control(OpType::OperationType alu_op, instruction *instr);

    // gets the ALUOp
    OpType::OperationType alu_op(instruction *instr);

    // sets the controller
    void set_controller(struct Controller *controller);

    // r-type
    bool RTYPE(CPU *cpu, instruction *instr);

    // i-type
    bool ITYPE(CPU *cpu, instruction *instr);

    // b-type
    bool BLT(CPU *cpu, instruction *instr);

    // j-type
    bool JALR(CPU *cpu, instruction *instr);

    // store word
    bool SW(CPU *cpu, instruction *instr);

    // load word
    bool LW(CPU *cpu, instruction *instr);
}

#endif
