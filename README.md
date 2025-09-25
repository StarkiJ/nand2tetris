# nand2tetris

 Nand to Tetris

---

## Project 6: The Assembler 汇编器

实验目标：开发一个汇编器，把Hack汇编语言翻译成Hack二进制语言。

测试方式：
1. 设置projects\6\HackAssembler\src\main\java\HackAssembler.java中的目标文件，分别将Add.asm, Max.asm, Rect.asm, Pong.asm翻译成二进制文件（*.hack）。
2. 打开tools\CPUEmulator.bat，点击Load Program按钮，将翻译的二进制文件导入CPU模拟器中。
3. 运行程序，验证其功能正确性与系统稳定性。

提示：
1. 可以将CPU模拟器的Animate项设置为No animate，以此加快程序运行速度。
2. 你也可以选择将汇编器打包成可执行文件，以参数的形式选择目标文件。

---

## Project 7: Virtual Machine I - Stack Arithmetic 虚拟机I

实验目标：开发一个虚拟机翻译器，把虚拟机字节码中的算术逻辑命令和push/pop命令翻译成Hack汇编语言。

测试方式：
1. 设置projects\7\VMTranslator\src\main\java\com\starki\VMTranslator.java中的目标文件，将虚拟机字节码文件（*.vm）翻译成汇编文件（*.asm）。
2. 打开tools\CPUEmulator.bat，点击Load Program按钮，将翻译的汇编文件导入CPU模拟器中。
3. 点击Load Script按钮，将对应的测试文件（*.tst）导入。
4. 运行程序，查看下方测试结果信息。

提示：
1. 可以将CPU模拟器的Animate项设置为No animate，以此加快程序运行速度。
2. 你也可以选择将虚拟机翻译器打包成可执行文件，以参数的形式选择目标文件。
3. 建议先通过StackArithmetic中的测试，再进行MemoryAccess中的测试。

---

## Project 8: Virtual Machine II - Program Control 虚拟机II

实验目标：完善上一个实验中的虚拟机翻译器。

测试方式：
1. 设置projects\8\VMTranslator\src\main\java\com\starki\VMTranslator.java中的目标文件，将虚拟机字节码文件（*.vm）翻译成汇编文件（*.asm）。
2. 打开tools\CPUEmulator.bat，点击Load Program按钮，将翻译的汇编文件导入CPU模拟器中。
3. 点击Load Script按钮，将对应的测试文件（*.tst）导入。
4. 运行程序，查看下方测试结果信息。

提示：
1. 可以将CPU模拟器的Animate项设置为No animate，以此加快程序运行速度。
2. 你也可以选择将虚拟机翻译器打包成可执行文件，以参数的形式选择目标文件。
3. 建议先通过ProgramFlow中的测试，再进行FunctionCalls中的测试。
4. 对于有多个虚拟机字节码文件的测试项，你需要把将它们翻译成一个汇编文件，并保证运行的第一个函数是Sys.init。

---

## Project 10: Compiler I - Syntax Analysis 编译器I

实验目标：开发一个编译器，实现Jack语言的语法分析，输出XML格式的分析结果。

测试方式：
1. 设置projects\10\JackCompiler\src\main\java\JackAnalyzer.java中的目标文件，读取Jack语言文件（*.jack），输出语法分析结果（*.xml）。
2. 将语法分析结果与参考文件进行对比。

提示：
1. 你也可以选择将编译器打包成可执行文件，以参数的形式选择目标文件。

---

## Project 11: Compiler II - Code Generation 编译器II

实验目标：完善上一个实验中的编译器，将Jack语言翻译成虚拟机字节码。

测试方式：
1. 设置projects\11\JackCompiler\src\main\java\JackCompiler.java中的目标文件，将Jack语言文件（*.jack）翻译成虚拟机字节码文件(*.vm)。
2. 打开tools\VMEmulator.bat，点击Load Program按钮，将翻译的虚拟机字节码文件导入VM模拟器中。
3. 运行程序，验证其功能正确性与系统稳定性。

提示：
1. 可以将CPU模拟器的Animate项设置为No animate，以此加快程序运行速度。
2. 你也可以选择将编译器打包成可执行文件，以参数的形式选择目标文件。
3. 当程序存在多个虚拟机字节码文件时，需要将导入目标设置为整个文件夹。

---

## Project 12: The Operating System 操作系统

实验目标：实现一个操作系统，以支持各项基本功能。

测试方式：
1. 执行 JackCompiler.bat [目标文件]，将操作系统和测试项目的Jack语言文件翻译成虚拟机字节码文件。
2. 打开tools\VMEmulator.bat，点击Load Program按钮，将翻译的虚拟机字节码文件导入VM模拟器中。
3. 运行程序，验证其功能正确性与系统稳定性。

提示：
1. 可以将CPU模拟器的Animate项设置为No animate，以此加快程序运行速度。
2. 当程序存在多个虚拟机字节码文件时，需要将导入目标设置为整个文件夹。
3. 在初次测试时，可以使用tools\OS中的操作系统文件，仅将测试项对应的文件替换成自己写的，以控制变量。
4. 你也可以使用自己的编译器来替代JackCompiler.bat。
