# nand2tetris

 Nand to Tetris

## Project 4: Machine Language Programming

LOC: 112 = 86 + 36

**Background**:
Every hardware platform is designed to execute commands in a certain machine language, expressed using agreed-upon binary codes. Writing programs directly in binary code is a possible, yet unnecessary. Instead, we can write such programs using a low-level symbolic language, called assembly, and have them translated into binary code by a program called assembler. In this project you will write some low-level assembly programs, and will be forever thankful for high-level languages like Java and Python. (Actually, assembly programming can be highly rewarding, allowing direct and complete control of the underlying machine.)

**Objective**:
To get a taste of low-level programming in machine language, and to get acquainted with the Hack computer platform. In the process of working on this project, you will become familiar with the assembly process - translating from symbolic language to machine-language - and you will appreciate visually how native binary code executes on the target hardware platform. These lessons will be learned in the context of writing and testing two low-level programs, as follows.

---

## Project 7: Virtual Machine I - Stack Arithmetic

LOC: 420 = 302 + 78 + 40

**Background**:
Java (or C#) compilers generate code written in an intermediate language called bytecode (or IL). This code is designed to run on a virtual machine architecture like the JVM (or CLR). One way to implement such VM programs is to translate them further into lower-level programs written in the machine language of some concrete (rather than virtual) host computer. In projects 7 and 8 we build such a VM translator, designed to translate programs written in the VM language into programs written in the Hack assembly language. The VM language, abstraction, and translation process are described in chapters 7 and 8 of the book. For the purpose of this project, chapter 8 can be ignored.

**Objective**:
Build a basic VM translator, focusing on the implementation of the VM language's stack arithmetic and memory accesscommands. In Project 8, this basic translator will be extended into a full-scale VM translator.

---

## Project 8: Virtual Machine II - Program Control

LOC: 599 = 418 + 86 + 95

**Background**:
We continue building the VM translator - a program that translates programs written in the VM language into programs written in the Hack machine language. This is a respectable chunk of engineering, so we are doing it in two stages. Welcome to stage II.

**Objective**:
Extend the basic VM translator built in project 7 into a full-scale VM translator. In particular, in project 7 we focused on handling the stack arithmetic and memory access commands of the VM language. We now turn to handle the VM language's branching and function calling commands.

---

## Project 9: High-Level Programming

LOC: 337 = 10 + 83 + 149 + 95

**Background**:
This project introduces Jack - a simple, Java-like, object-based programming language. Before building a Jack compiler in projects 10-11, it makes sense to become familiar with the language itself. That's the main purpose of this project. And... there is also the thrill of writing a little computer game in a cool little language.

**Objective**:
Adopt or invent an application idea like a simple computer game or some other interactive program. Examples include Tetris, Snake, Hangman, Space Invaders, Sokoban, Pong, etc. Your job is to implement this application in Jack. You don't have to create a complete application. For example, you can create a basic version, or part of, some simple game or cool interaction.

---

## Project 10: Compiler I - Syntax Analysis

LOC: 748 = 446 + 72 + 230

**Background**:
The Jack compiler, like those of Java and C#, is two-tiered: the compiler's front-end translates from the high-level language to an intermediate VM language; the compiler's back-end translates further from the VM language to the native code of the host platform. In projects 7-8 we've built the compiler's back-end (the VM translator); we now turn to building the compiler's front-end. This development will span two projects: syntax analysis (this project), and code generation (next project). Welcome to syntax analysis.

**Objective**:
In this project we build a syntax analyzer that parses Jack programs according to the Jack grammar, producing an XML file that renders the program's structure using marked-up text. In the next project, the logic that generates the XML output will be morphed into logic that generates VM code.

---

## Project 11: Compiler II - Code Generation

LOC: 1038 = 615 + 55 + 234 + 81 + 53

**Background**:
In this project we complete the construction of the Jack Compiler that we started building in the previous project.

---

## Project 12: The Operating System

LOC: 1164 = 23 + 92 + 197 + 117 + 265 + 303 + 120 + 47

**Background**:
An Operating System (OS) is a collection of software services, designed to close gaps between the computer's hardware and application software. For example, if you use a high-level langauge to write a program that prompts the user to enter some data using the keyboard, the code generated by the compiler will include (among other things) calls to OS routines that handle keyboard inputs. Therefore, the OS can also be viewed as an extension of a high-level programming language. This is a rather simplistic view of operating systems, but it will suffice for our purpose.

**Objective**:
Implement the Jack OS specified in Chapter 12. In the process of building this OS, you will implement a modular collection of some beautiful algorithms in applied computer science. Thus, you will also be exposed, hands-on, to some cool computer science stuff.
