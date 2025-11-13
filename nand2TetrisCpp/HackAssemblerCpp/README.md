# HackAssembler (C++)

A C++ implementation of the Nand2Tetris **Hack ASM → machine code** assembler with CMake.

## Build

```bash
mkdir -p build && cd build
cmake ..
cmake --build . -j
```

This produces the `HackAssembler` executable in `build`.

## Usage

Assemble a single `.asm` file:

```bash
./HackAssembler path/to/Prog.asm
# => writes path/to/Prog.hack
```

Assemble a directory of `.asm` files (non-recursive):

```bash
./HackAssembler path/to/AsmDir
# => writes .hack next to each .asm (e.g., Add.asm -> Add.hack)
```

## Features

* **Two-pass assembler** (Project 6 compliant):

  * **Pass 1**: resolves `(LABEL)` symbols to ROM addresses.
  * **Pass 2**: translates A/C instructions to 16-bit Hack machine code.
* **Predefined symbols**: `SP, LCL, ARG, THIS, THAT, R0..R15, SCREEN, KBD`.
* **Variables**: first seen `@Symbol` assigned from RAM address **16** upward.
* **C-instruction encoding**: `111 a c1..c6 d1..d3 j1..j3` (N2T truth table).
* **Comments & whitespace**:

  * `// line comments`
  * `/* block comments */` (supported)
  * leading/trailing whitespace ignored.
* **Output**: text `.hack` file with one 16-bit binary line per instruction.
* **Quality of life**:

  * Case-insensitive `.asm` extension detection.
  * Directory mode for batch assembling.

---

## Design & Architecture（设计结构思路）

### 为什么是“两遍”？

* Hack 汇编有**前向引用**（例如使用尚未出现的 `(LABEL)` 或变量名）。
* 第一遍只统计**“可执行指令的 ROM 地址”**并记录 **label→address**；第二遍在符号表完整的前提下安全地进行编码。
* 两遍时间复杂度均为 O(N)，实现清晰、鲁棒且与 N2T 课程一致。

### 模块划分与职责

```
+--------------------+      +------------------+      +-----------------+
|  preprocess_lines  |      |      Parser      |      |      Code       |
| 去注释/裁剪空白     | ---> | 逐条识别指令类型    | ---> | 助记符→比特串映射   |
+--------------------+      | A / C / L         |      | comp/dest/jump  |
                             +------------------+      +-----------------+
                                     |
                                     v
                            +--------------------+
                            |   SymbolTable      |
                            | 预置符号/标签/变量分配 |
                            +--------------------+
```

* **`preprocess_lines(istream&) -> vector<string>`**
  统一处理 `//` 行注释与 `/* ... */` 块注释，并去掉空白行，产出“**干净指令流**”。
* **`Parser`**
  提供 `instructionType()/symbol()/dest()/comp()/jump()` 等方法。
  解析规则：

  * `@xxx` → A 指令；
  * `(XXX)` → L 指令（不产出机器码）；
  * 其余 → C 指令（`dest=comp;jump` 形式，各部分可空）。
* **`SymbolTable`**
  管理三类符号：

  1. **预置符号**（`SP/LCL/ARG/THIS/THAT/R0..R15/SCREEN/KBD`）；
  2. **(LABEL)**：Pass 1 记录对应 ROM 地址；
  3. **变量**：Pass 2 首次见到 `@Symbol` 即从 **16** 起分配。
* **`Code`**
  纯静态映射：`comp/dest/jump` 助记符 → 指定位宽的二进制字符串。`comp` 返回 7 位（含 a 位），`dest/jump` 各 3 位。

---

## File/Code Structure（代码结构要点）

* `SymbolTable`

  * `contains/get/add` 三个基础接口。
  * 预置符号在构造函数中注册。
  * 变量分配不在 `SymbolTable` 内部做，而由第二遍的驱动逻辑在**首次遇到未知 symbol 时**按序分配并 `add` 回表。

* `Parser`

  * 维护 `lines_` 与当前索引 `i_`；
  * `advance()` 推进并解析当前行；
  * C 指令通过查找 `';'` 与 `'='` 来分割 `left/right` 与 `dest/comp`。

* `Code`

  * `comp()`：使用一张静态 `unordered_map<string, string>`（包含 a 位）按 **N2T Truth Table** 映射；
  * `dest()/jump()`：或用表驱动或位拼装（本实现推荐表驱动，便于教学与单元测试）。

* `assemble_one_file()`

  * 读取、预处理；
  * **Pass 1**：扫一遍将 `(LABEL)` 记入符号表，**仅“可执行”指令**使 ROM 地址自增；
  * **Pass 2**：A 指令处理常量 vs 符号；C 指令经 `Code` 完成编码；写入 `.hack`。
  * 使用异常报告错误（如非法助记符、A 常量越界、无法创建/打开文件等）。

## Assignment Mode

> 为了课堂练习，我们对 C++ 与 Java 版本保持**相同功能块的挖空**。只需补齐这些点即可让汇编器完整工作。

**本仓库的 C++ 留空点：**

* `SymbolTable`

  * 预置符号除 `SP` 外**留空**（学生补：`LCL/ARG/THIS/THAT/R0..R15/SCREEN/KBD`）。
  * `contains(const string&)` / `get(const string&)` / `add(const string&, int)` **留空**。
* `Code`

  * `dest(const string&)` **留空**（返回 3 位）。
  * `jump(const string&)` 仅示例性实现 `"" -> 000`, `"JGT" -> 001`，其余 **留空**。
  * `comp(const string&)` 仅给出 `"0"`、`"1"` 两项，其余 **留空**（按 N2T 表补齐）。

> **注意**：这些留空处已放置**占位返回值**以保证可编译，但功能不完整；学生需要根据 README 的“编码参考”补齐以通过测试。

## Implementation Notes

* 使用 `std::filesystem` 进行路径与枚举；`has_asm_extension()` 统一小写比较。
* `trim/ltrim/rtrim` 助手确保解析前无多余空白。
* `preprocess_lines()` 在字符级处理块注释，跨行安全。
* A 常量编码由 `to15(int)` 负责；越界直接抛异常。
* 输出 `.hack` 路径规则：若输入后缀为 `.asm/.ASM`，替换为 `.hack`；否则追加 `.hack`。