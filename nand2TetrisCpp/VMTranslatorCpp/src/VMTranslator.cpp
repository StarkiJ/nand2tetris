#include "CodeWriter.h"
#include "Parser.h"
#include <algorithm>
#include <filesystem>
#include <iostream>
#include <vector>

using namespace std;

static bool isVM(const std::filesystem::path &p) {
  return p.has_extension() &&
         (p.extension() == ".vm" || p.extension() == ".VM");
}

int main(int argc, char **argv) {
  if (argc != 2) {
    cerr << "Usage: vmtranslator <input .vm file or directory>\n";
    return 1;
  }
  std::filesystem::path in(argv[1]);

  if (!std::filesystem::exists(in)) {
    cerr << "Input path does not exist: " << in << "\n";
    return 1;
  }

  vector<std::filesystem::path> files;
  std::filesystem::path out_path;

  if (std::filesystem::is_directory(in)) {
    for (auto &entry : std::filesystem::directory_iterator(in)) {
      if (entry.is_regular_file() && isVM(entry.path())) {
        files.push_back(entry.path());
      }
    }
    sort(files.begin(), files.end());
    if (files.empty()) {
      cerr << "No .vm files found in directory: " << in << "\n";
      return 1;
    }
    out_path = in / (in.filename().string() + ".asm");
  } else {
    if (!isVM(in)) {
      cerr << "Input file must have .vm extension\n";
      return 1;
    }
    files.push_back(in);
    out_path = in.parent_path() / (in.stem().string() + ".asm");
  }

  try {
    CodeWriter writer(out_path.string());
    /*todo*/ // 目录模式是否需要 bootstrap?

    for (const auto &f : files) {
      Parser parser(f.string());
      writer.setFileName(parser.moduleName());

      while (parser.hasMoreCommands()) {
        auto type = parser.commandType();
        if (type == "C_ARITHMETIC") {
          writer.writeArithmetic(parser.arg1());
        } else {
          /*todo*/ // 其余分派：push/pop/label/goto/if/function/call/return
        }
        parser.advance();
      }
    }
    writer.close();
    cout << "Wrote: " << out_path << "\n";
  } catch (const std::exception &e) {
    cerr << "Error: " << e.what() << "\n";
    return 2;
  }
  return 0;
}
