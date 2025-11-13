cmake_minimum_required(VERSION 3.12)
project(vmtranslator LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

add_executable(VMTranslator
    src/VMTranslator.cpp
    src/Parser.cpp
    src/CodeWriter.cpp
)

# For MSVC, enable modern conformance and secure warnings off
if(MSVC)
  target_compile_options(vmtranslator PRIVATE /permissive- /W4 /D_CRT_SECURE_NO_WARNINGS)
else()
  target_compile_options(vmtranslator PRIVATE -Wall -Wextra -Wpedantic)
endif()

