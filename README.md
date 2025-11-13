# nand2tetris

 Nand to Tetris

使用 **C++17** 与 **CMake** 构建

在nand2TetrisCpp目录下，每一个项目都有自己的README文件。

---

## Project 6: The Assembler 汇编器

实验目标：开发一个汇编器，把Hack汇编语言翻译成Hack二进制语言。

### 代码设计思路

1. **两遍解析器 (Two-pass Assembler)**

   * **第一遍**：扫描 `(LABEL)` 指令，记录标签符号对应的 ROM 地址。
   * **第二遍**：将每条 `A` / `C` 指令翻译成 16 位二进制代码。
2. **符号表管理**

   * 预定义符号：`SP, LCL, ARG, THIS, THAT, R0–R15, SCREEN, KBD`。
   * 变量符号：自动从 RAM 地址 16 开始分配。
3. **C 指令解析**

   * `dest=comp;jump` 三段式拆解，使用查表法翻译 comp/jump 字段。

---

## Project 7–8: VM Translator

Project 7: Virtual Machine I - Stack Arithmetic 虚拟机I
Project 8: Virtual Machine II - Program Control 虚拟机II

**虚拟机翻译器：VM → Hack ASM**

将 VM 字节码翻译为 Hack 汇编指令，实现堆栈运算、流程控制与函数调用。

1. **指令分类**

   * **算术逻辑命令**：`add`, `sub`, `neg`, `eq`, `gt`, `lt`, `and`, `or`, `not`
   * **内存访问命令**：`push/pop segment index`
   * **程序控制命令**：`label`, `goto`, `if-goto`
   * **函数调用约定**：`function`, `call`, `return`
2. **模块化结构**

   * 每条命令类型对应独立的翻译函数。
   * `CodeWriter` 负责输出 `.asm`。
   * 当输入目录时，自动生成带引导代码 (`Sys.init`) 的单一输出。

在 `CPUEmulator` 中运行 `.asm` 或 `.tst` 脚本。

### 推荐测试顺序

1. **StackArithmetic** → 算术逻辑测试
2. **MemoryAccess** → 内存段映射
3. **ProgramFlow** → 程序控制
4. **FunctionCalls** → 函数调用（需要保证运行的第一个函数是Sys.init）

---

## Project 10–11: Jack Compiler

Project 10: Compiler I - Syntax Analysis 编译器I
Project 11: Compiler II - Code Generation 编译器II

编写一个 Jack 语言编译器，将高级语言翻译为 VM 字节码。
支持完整的类结构、方法调用、控制语句与表达式。

| 模块                  | 功能描述                           |
| ------------------- | ------------------------------ |
| `JackTokenizer`     | 词法分析：分离关键字、符号、标识符、字符串、整数常量。    |
| `CompilationEngine` | 语法分析与代码生成：递归下降法构建语法树并输出 `.vm`。 |
| `SymbolTable`       | 管理变量作用域与索引号（class/subroutine）。 |
| `VMWriter`          | 将 VM 指令写入输出文件。                 |
| `XmlPrinter`        | 输出语法树 XML 文件。              |

编译器由 `JackCompiler.cpp` 驱动，支持：

* 单文件或目录编译；
* `--xml` 选项输出 XML 调试结果（`Foo.xml`, `FooT.xml`）。
