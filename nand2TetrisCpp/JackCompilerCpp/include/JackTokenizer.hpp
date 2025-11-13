#pragma once
#include <queue>
#include <string>
#include <unordered_set>

// JackTokenizer: splits Jack source into tokens and provides lookahead.
// API mirrors the nand2tetris project.
// Optional: record tokens for tokens XML dump.
class JackTokenizer {
public:
  struct RecToken {
    std::string type;
    std::string value;
  };

public:
  explicit JackTokenizer(const std::string &file_path);

  // Whether there are more characters (prior to queueing) or queued tokens.
  bool hasMoreTokens();

  // Fill internal queue with the next token from the source.
  // (Useable to pre-tokenize or for debugging.)
  void advancePre();

  // Consume one token into `currentToken_` from the queue.
  void advance();

  // Peek the next token without consuming (nullptr if none).
  const std::string *peekNextToken() const;

  // Access the current raw token string (after advance())
  const std::string &getToken() const { return currentToken_; }

  // Token classification and typed accessors (valid after advance()).
  std::string tokenType() const; // "KEYWORD" | "SYMBOL" | "STRING_CONST" |
                                 // "INT_CONST" | "IDENTIFIER"
  std::string keyword() const;
  std::string
  symbol() const; // returns encoded &lt; &gt; &amp; for XML convenience
  std::string identifier() const;
  int intVal() const;
  std::string stringVal() const; // without the surrounding quotes

  // Enable/disable tokens XML recording; call before parsing begins.
  void enableTokensXml(bool on) {
    record_tokens_ = on;
    tokens_record_.clear();
  }
  // Dump tokens.xml (filename is <out_base>T.xml)
  void dumpTokensXml(const std::string &out_base) const;

private:
  // Read next line from file if needed.
  bool ensureLine();

  std::string file_path_;
  std::string current_line_;
  size_t cursor_ = 0;
  std::string currentToken_;
  std::queue<std::string> q_;

  enum State { NORMAL = 0, IN_QUOTE = 1, IN_COMMENT = 2 };
  State state_ = NORMAL;

  bool record_tokens_ = false;
  std::vector<RecToken> tokens_record_;

  // Static tables
  static const std::unordered_set<std::string> &keywords();
  static const std::unordered_set<std::string> &symbols();
};
