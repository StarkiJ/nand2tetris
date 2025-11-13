cmake_minimum_required(VERSION 3.15)

project(HackAssembler
  VERSION 1.0.0
  LANGUAGES CXX
)

# C++17 严格模式
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

# 默认构建类型为 Release（单配置生成器如 Ninja/Makefile）
if(NOT CMAKE_BUILD_TYPE AND NOT CMAKE_CONFIGURATION_TYPES)
  set(CMAKE_BUILD_TYPE Release CACHE STRING "Build type" FORCE)
  set_property(CACHE CMAKE_BUILD_TYPE PROPERTY STRINGS "Debug" "Release" "RelWithDebInfo" "MinSizeRel")
endif()

# 生成 compile_commands.json，方便 IDE/静态分析
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)

add_executable(HackAssembler
  HackAssembler.cpp
)

# 警告等级
if(MSVC)
  target_compile_options(HackAssembler PRIVATE /W4 /permissive- /Zc:__cplusplus)
else()
  target_compile_options(HackAssembler PRIVATE -Wall -Wextra -Wpedantic)
endif()
