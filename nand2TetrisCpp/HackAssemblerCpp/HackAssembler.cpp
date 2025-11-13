#include <bits/stdc++.h>
#include <filesystem>
using namespace std;
namespace fs = std::filesystem;

// Instruction kinds
enum class InstrType { A_INSTR, C_INSTR, L_INSTR, UNKNOWN };

// -----------------------------
// SymbolTable
// -----------------------------
class SymbolTable {
public:
  /**
   * 构造函数：创建空的符号表
   * 仅预置 SP，其余留空待完成
   */
  SymbolTable() {
    // Predefined symbols
    // 预置符号表
    table_["SP"] = 0;
    /*todo */
  }

  /**
   * 判断 symbol 是否在符号表中
   */
  bool contains(const string &sym) const { /*todo */ return false; }

  /**
   * 返回符号表中 symbol 关联的地址
   * 
   */
  int get(const string &sym) const { /*todo */ return 0; }

  /**
   * 将 (symbol, address) 配对加入符号表
   * 
   */
  void add(const string &sym, int addr) { /*todo */ (void)sym; (void)addr; }

private:
  unordered_map<string, int> table_;
};

// -----------------------------
// Code: mnemonics -> bit strings
// -----------------------------
class Code {
public:
  /**
   * 返回 dest 助记符对应的二进制码 (3位)
   * 
   */
  static string dest(const string &d) {
    // order: A D M
    /*todo */
    return "000";
  }

  /**
   * 返回 jump 助记符对应的二进制码 (3位)
   * 
   */
  static string jump(const string &j) {
    if (j.empty()) return "000";
    if (j == "JGT") return "001";
    /*todo */
    throw runtime_error("Invalid jump: " + j);
  }

  /**
   * 返回 comp 助记符对应的二进制码 (7位)
   * 
   */
  static string comp(const string &c) {
    // returns 7 bits: a c1 c2 c3 c4 c5 c6
    static const unordered_map<string, string> mp = {
        // a=0 set
        {"0", "0101010"},
        {"1", "0111111"},
        /* todo */
    };
    auto it = mp.find(c);
    if (it == mp.end())
      throw runtime_error("Invalid comp: " + c);
    return it->second;
  }
};

// -----------------------------
// Utilities
// -----------------------------
static inline void ltrim(string &s) {
  s.erase(s.begin(), find_if(s.begin(), s.end(),
                             [](unsigned char ch) { return !isspace(ch); }));
}
static inline void rtrim(string &s) {
  s.erase(find_if(s.rbegin(), s.rend(),
                  [](unsigned char ch) { return !isspace(ch); })
              .base(),
          s.end());
}
static inline string trim(string s) {
  ltrim(s);
  rtrim(s);
  return s;
}

static inline bool is_number(const string &s) {
  if (s.empty())
    return false;
  for (char c : s)
    if (!isdigit((unsigned char)c))
      return false;
  return true;
}

// Remove comments (//...) and handle block comments /* ... */
static vector<string> preprocess_lines(istream &in) {
  vector<string> out;
  bool in_block = false;
  string line;
  while (getline(in, line)) {
    string cur;
    for (size_t i = 0; i < line.size(); ++i) {
      if (!in_block && i + 1 < line.size() && line[i] == '/' &&
          line[i + 1] == '*') {
        in_block = true;
        ++i;
        continue;
      }
      if (in_block) {
        if (i + 1 < line.size() && line[i] == '*' && line[i + 1] == '/') {
          in_block = false;
          ++i;
        }
        continue;
      }
      if (!in_block && i + 1 < line.size() && line[i] == '/' &&
          line[i + 1] == '/') {
        break; // rest of line is a comment
      }
      cur.push_back(line[i]);
    }
    cur = trim(cur);
    if (!cur.empty())
      out.push_back(cur);
  }
  return out;
}

// -----------------------------
// Parser: exposes the nand2tetris Parser API
// -----------------------------
class Parser {
public:
  explicit Parser(const vector<string> &lines) : lines_(lines), i_(-1) {}

  bool hasMoreCommands() const { return (i_ + 1) < (int)lines_.size(); }

  void advance() {
    if (hasMoreCommands()) {
      ++i_;
      parse_current();
    }
  }

  InstrType instructionType() const { return type_; }

  string symbol() const { return symbol_; }
  string dest() const { return dest_; }
  string comp() const { return comp_; }
  string jump() const { return jump_; }

private:
  void parse_current() {
    const string &s = lines_[i_];
    // L-instruction: (XXX)
    if (!s.empty() && s.front() == '(' && s.back() == ')') {
      type_ = InstrType::L_INSTR;
      symbol_ = s.substr(1, s.size() - 2);
      dest_.clear();
      comp_.clear();
      jump_.clear();
      return;
    }
    // A-instruction: @value|symbol
    if (!s.empty() && s.front() == '@') {
      type_ = InstrType::A_INSTR;
      symbol_ = s.substr(1);
      dest_.clear();
      comp_.clear();
      jump_.clear();
      return;
    }
    // C-instruction: dest=comp;jump (any parts optional)
    type_ = InstrType::C_INSTR;
    symbol_.clear();
    string left, right; // left may be dest=comp or comp; right may be jump
    size_t semi = s.find(';');
    if (semi != string::npos) {
      right = s.substr(semi + 1);
      left = s.substr(0, semi);
    } else {
      left = s;
    }
    size_t eq = left.find('=');
    if (eq != string::npos) {
      dest_ = left.substr(0, eq);
      comp_ = left.substr(eq + 1);
    } else {
      dest_.clear();
      comp_ = left;
    }
    dest_ = trim(dest_);
    comp_ = trim(comp_);
    jump_ = trim(right);
  }

  vector<string> lines_;
  int i_;
  InstrType type_{InstrType::UNKNOWN};
  string symbol_, dest_, comp_, jump_;
};

// -----------------------------
// Assembler core (two passes)
// -----------------------------
static string to15(int x) {
  if (x < 0 || x > 32767)
    throw runtime_error("A-instruction constant out of range: " + to_string(x));
  string b(15, '0');
  for (int i = 14; i >= 0; --i) {
    if (x & 1)
      b[i] = '1';
    x >>= 1;
  }
  return b;
}

static void assemble_one_file(const string &asm_path, const string &hack_path) {
  ifstream fin(asm_path);
  if (!fin)
    throw runtime_error("无法打开输入文件: " + asm_path);

  // Preprocess: strip comments/whitespace, keep non-empty lines
  vector<string> raw = preprocess_lines(fin);
  fin.close();

  // First pass: build symbol table from labels
  SymbolTable sym;
  int rom_addr = 0;
  for (const string &line : raw) {
    if (!line.empty() && line.front() == '(' && line.back() == ')') {
      string label = line.substr(1, line.size() - 2);
      if (!sym.contains(label))
        sym.add(label, rom_addr);
    } else {
      ++rom_addr; // only real instructions increase ROM
    }
  }

  // Second pass: translate to machine code
  ofstream fout(hack_path);
  if (!fout)
    throw runtime_error("无法创建输出文件: " + hack_path);

  int next_var_addr = 16;
  Parser parser(raw);
  while (parser.hasMoreCommands()) {
    parser.advance();
    switch (parser.instructionType()) {
    case InstrType::A_INSTR: {
      string symb = parser.symbol();
      int val = 0;
      if (is_number(symb)) {
        val = stoi(symb);
      } else {
        if (!sym.contains(symb)) {
          sym.add(symb, next_var_addr++);
        }
        val = sym.get(symb);
      }
      fout << '0' << to15(val) << '\n';
      break;
    }
    case InstrType::C_INSTR: {
      string comp_bits = Code::comp(parser.comp());
      string dest_bits = Code::dest(parser.dest());
      string jump_bits = Code::jump(parser.jump());
      fout << "111" << comp_bits << dest_bits << jump_bits << '\n';
      break;
    }
    case InstrType::L_INSTR:
      // no output for (LABEL)
      break;
    default:
      throw runtime_error("未知指令类型");
    }
  }
  fout.close();
}

static string default_out_path(const string &in_path) {
  // replace .asm with .hack; if no .asm, just append .hack
  size_t pos = in_path.rfind('.');
  if (pos != string::npos) {
    string ext = in_path.substr(pos);
    if (ext == ".asm" || ext == ".ASM") {
      return in_path.substr(0, pos) + ".hack";
    }
  }
  return in_path + ".hack";
}

static bool has_asm_extension(const fs::path &p) {
  if (!p.has_extension())
    return false;
  string ext = p.extension().string();
  for (auto &ch : ext)
    ch = static_cast<char>(tolower(static_cast<unsigned char>(ch)));
  return ext == ".asm";
}

int main(int argc, char **argv) {
  ios::sync_with_stdio(false);
  cin.tie(nullptr);

  if (argc != 2) {
    cerr << "用法: " << argv[0] << " <input.asm | directory>\n";
    return 1;
  }

  fs::path in_path = argv[1];

  try {
    if (fs::is_directory(in_path)) {
      size_t count = 0, ok = 0, fail = 0;
      for (const auto &de : fs::directory_iterator(in_path)) {
        if (!de.is_regular_file())
          continue;
        const fs::path &p = de.path();
        if (!has_asm_extension(p))
          continue;
        ++count;
        string in_file = p.string();
        string out_file = default_out_path(in_file);
        try {
          assemble_one_file(in_file, out_file);
          cout << "Assemble 成功 -> " << out_file << "\n";
          ++ok;
        } catch (const exception &e) {
          cerr << "错误: " << e.what() << " (in " << in_file << ")\n";
          ++fail;
        }
      }
      if (count == 0) {
        cout << "目录中未找到 .asm 文件: " << in_path.string() << "\n";
      } else {
        cout << "完成: 处理 " << count << " 个 .asm，成功 " << ok << "，失败 "
             << fail << "\n";
      }
    } else if (fs::is_regular_file(in_path)) {
      string out_path = default_out_path(in_path.string());
      assemble_one_file(in_path.string(), out_path);
      cout << "Assemble 成功 -> " << out_path << "\n";
    } else {
      cerr << "错误: 输入既不是文件也不是目录: " << in_path.string() << "\n";
      return 1;
    }
  } catch (const exception &e) {
    cerr << "错误: " << e.what() << "\n";
    return 2;
  }

  return 0;
}
