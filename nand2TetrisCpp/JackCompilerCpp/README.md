# JackCompiler (C++)

A C++ implementation of the Nand2Tetris **Jack → VM** compiler with CMake.

This project corresponds to **Projects 10–11** of the *Nand2Tetris* course, converting high-level Jack source code (`.jack`) into intermediate Virtual Machine (`.vm`) code.

---

## Build

```bash
mkdir -p build && cd build
cmake ..
cmake --build . -j
```

This produces the `JackCompiler` executable in `build`.

## Usage

Compile a single `.jack` file:

```bash
./JackCompiler path/to/Foo.jack
# => writes path/to/Foo.vm
```

Compile a directory of `.jack` files:

```bash
./JackCompiler path/to/ProjectDir
# => writes VM files next to each .jack (e.g., ClassA.vm, ClassB.vm, …)
```

Optional XML debug outputs:

```bash
# Emit XML: FooT.xml (token list) and Foo.xml (parsed structure)
./JackCompiler --xml path/to/Foo.jack
```

---

## Overview

This compiler is built as a **modular pipeline**, mirroring the official Nand2Tetris toolchain:

```
 Jack (.jack)
     ↓
 JackTokenizer        → lexical analysis (tokens)
     ↓
 CompilationEngine     → recursive-descent parser / code generator
     ↓
 VMWriter              → emits VM code
     ↓
 SymbolTable           → manages variable scopes and indices
     ↓
 .vm output (for VM Emulator)
```

Each component corresponds closely to the textbook implementation, rewritten in idiomatic modern C++.

---

## Code Structure

```
.
├── CMakeLists.txt
├── include/
│   ├── JackTokenizer.hpp     # Tokenizer: lexical analysis
│   ├── CompilationEngine.hpp # Parser + Code generator
│   ├── SymbolTable.hpp       # Symbol scope management
│   ├── VMWriter.hpp          # VM output writer
│   ├── XmlPrinter.hpp        # (Optional) XML pretty-printer
│   └── JackCompiler.hpp      # CLI frontend / main driver
└── src/
    ├── JackTokenizer.cpp
    ├── CompilationEngine.cpp
    ├── SymbolTable.cpp
    ├── VMWriter.cpp
    ├── XmlPrinter.cpp
    └── JackCompiler.cpp
```

### **1. `JackTokenizer`**

Performs lexical analysis on `.jack` source files:

* Splits input into tokens (`keyword`, `symbol`, `identifier`, `int_const`, `string_const`)
* Handles comments and whitespace
* Provides `advance()`, `tokenType()`, `keyword()`, etc.

### **2. `SymbolTable`**

Implements the **variable scope system**:

* Two tables: *class scope* and *subroutine scope*
* Tracks identifiers’ `type`, `kind`, and `index`
* Exposes `define`, `varCount`, `kindOf`, `typeOf`, `indexOf`

### **3. `CompilationEngine`**

Core of the compiler — a **recursive-descent parser** and **VM code generator**.

* Implements the full Jack grammar
* Emits VM code using `VMWriter`
* Consults `SymbolTable` for variable translation
* Optionally writes XML structure for debugging

### **4. `VMWriter`**

Responsible for writing VM commands:

* `push/pop/label/goto/if-goto/call/function/return`
* Wraps a file stream for `.vm` output

### **5. `XmlPrinter`**

A small helper for the `--xml` debug mode.

* Emits nested tags to visualize parse tree and tokens.

### **6. `JackCompiler` (main)**

CLI entry point:

* Accepts file or directory paths
* Instantiates the above modules in order
* Handles XML flag and output management

## Assignments

| Project | Focus                                                           |
| ------- | --------------------------------------------------------------- |
| 10      | `JackTokenizer`, `CompilationEngine` (syntax analysis)          |
| 11      | `SymbolTable`, `VMWriter` (semantic analysis + code generation) |

Typical incremental schedule:

1. Implement tokenizer (output `T.xml`)
2. Implement parser XML output (output `<class>` structure)
3. Add symbol table and VM writer to generate executable VM code

## ⚙️ VM Code Conventions

VM mapping strictly follows Nand2Tetris rules:

| Jack Symbol Kind | VM Segment |
| ---------------- | ---------- |
| `static`         | `static`   |
| `field`          | `this`     |
| `var`            | `local`    |
| `arg`            | `argument` |

Special behavior:

* **Method**: set `pointer 0 = argument 0`
* **Constructor**: call `Memory.alloc(fieldCount)` and assign `pointer 0`
* **Operators**:

  * `*` → `call Math.multiply 2`
  * `/` → `call Math.divide 2`
* **String constants**:

  * Use `String.new(length)` + repeated `String.appendChar`

---

## 🧪 Example

```bash
./JackCompiler --xml projects/11/Square/SquareGame.jack
```

Outputs:

```
SquareGame.vm
SquareGame.xml
SquareGameT.xml
```

Use the VM Emulator to run the compiled program.
