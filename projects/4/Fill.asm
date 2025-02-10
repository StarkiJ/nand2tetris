// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/4/Fill.asm

// Runs an infinite loop that listens to the keyboard input. 
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel. When no key is pressed, 
// the screen should be cleared.

//// Replace this comment with your code.

    // m = 256, n = 32
    @32
    D=A
    @m
    M=D
    @256
    D=A
    @n
    M=D
    // flag = 0
    @flag
    M=0
(LOOP0)
    // if(R[KBD] == 0) flag = 0, else flag = -1
    @KBD
    D=M
    @FLAG0
    D;JEQ
    // flag = -1
    @flag
    M=-1
    @FLAG1
    0;JMP
(FLAG0) //flag = 0
    @flag
    M=0
(FLAG1)
    // arr = SCREEN
    @SCREEN
    D=A
    @arr
    M=D-1
    // i = 0
    @i
    M=0
(LOOP1)
    // if(i >= m) goto END1
    @m
    D=M
    @i
    D=M-D
    @END1
    D;JGE
    // j = 0
    @j
    M=0
(LOOP2)
    // if(j >= n) goto END2
    @n
    D=M
    @j
    D=M-D
    @END2
    D;JGE
    // R[++arr] = flag
    @flag
    D=M
    @arr
    M=M+1
    A=M
    M=D
    // j++
    @j
    M=M+1
    // goto LOOP2
    @LOOP2
    0;JMP
(END2)
    // i++
    @i
    M=M+1
    // goto LOOP1
    @LOOP1
    0;JMP
(END1)
    @LOOP0
    0;JMP

