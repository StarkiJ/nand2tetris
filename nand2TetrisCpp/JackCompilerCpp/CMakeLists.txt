cmake_minimum_required(VERSION 3.16)
project(JackCompilerCPP LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)

add_library(jacklib
    src/JackTokenizer.cpp
    src/SymbolTable.cpp
    src/VMWriter.cpp
    src/CompilationEngine.cpp
    src/XmlPrinter.cpp
)

target_include_directories(jacklib PUBLIC include)

add_executable(JackCompiler src/JackCompiler.cpp)
target_link_libraries(JackCompiler PRIVATE jacklib)

# On MSVC, use UTF-8 source encoding and secure warnings off
if(MSVC)
  target_compile_options(jacklib PRIVATE /utf-8 /D_CRT_SECURE_NO_WARNINGS)
  target_compile_options(JackCompiler PRIVATE /utf-8 /D_CRT_SECURE_NO_WARNINGS)
else()
  target_compile_options(jacklib PRIVATE -Wall -Wextra -Wno-unused-parameter)
  target_compile_options(JackCompiler PRIVATE -Wall -Wextra -Wno-unused-parameter)
endif()
