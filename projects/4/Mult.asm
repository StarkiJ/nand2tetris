// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/4/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)
// The algorithm is based on repetitive addition.

//// Replace this comment with your code.

    // R2 = 0
    @R2
    M=0
    // i = 0
    @i
    M=0
(LOOP)
    // if(i >= R0) goto END
    @R0
    D=M
    @i
    D=M-D
    @END
    D;JGE
    // R2+=R1
    @R1
    D=M
    @R2
    M=D+M
    // i++
    @i
    M=M+1
    // goto LOOP
    @LOOP
    0;JMP
(END)
    @END
    0;JMP

