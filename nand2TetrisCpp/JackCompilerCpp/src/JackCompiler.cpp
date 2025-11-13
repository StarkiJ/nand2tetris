#include "CompilationEngine.hpp"
#include "JackTokenizer.hpp"
#include <filesystem>
#include <iostream>
#include <string>
#include <vector>

namespace fs = std::filesystem;

static void compile_one(const fs::path &jack, bool xml_debug) {
  std::string out_base = jack.string();
  out_base = out_base.substr(0, out_base.find_last_of('.')); // strip .jack
  JackTokenizer tokenizer(jack.string());
  tokenizer.enableTokensXml(xml_debug);
  // prime first token inside compileClass itself
  CompilationEngine engine(tokenizer, out_base, xml_debug);
  engine.compileClass();
  if (xml_debug)
    tokenizer.dumpTokensXml(out_base);
  std::cout << "Compiled: " << jack << std::endl;
}

int main(int argc, char **argv) {
  bool xml_debug = false;
  std::vector<std::string> args;
  for (int i = 1; i < argc; i++) {
    std::string a = argv[i];
    if (a == "--xml" || a == "-x")
      xml_debug = true;
    else
      args.push_back(a);
  }
  if (args.size() != 1) {
    std::cerr << "Usage: JackCompiler [--xml] <file.jack | directory>\n";
    return 1;
  }
  fs::path input(args[0]);
  std::vector<fs::path> files;
  if (fs::is_regular_file(input)) {
    if (input.extension() == ".jack")
      files.push_back(input);
    else {
      std::cerr << "Not a .jack file: " << input << "\n";
      return 1;
    }
  } else if (fs::is_directory(input)) {
    for (auto &p : fs::directory_iterator(input)) {
      if (p.is_regular_file() && p.path().extension() == ".jack")
        files.push_back(p.path());
    }
    if (files.empty()) {
      std::cerr << "Directory has no .jack files: " << input << "\n";
      return 1;
    }
  } else {
    std::cerr << "Path not found: " << input << "\n";
    return 1;
  }

  for (auto &f : files) {
    try {
      compile_one(f, xml_debug);
    } catch (const std::exception &e) {
      std::cerr << "Failed to compile " << f << ": " << e.what() << "\n";
      return 2;
    }
  }
  return 0;
}
