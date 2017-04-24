# CSC 531 Project 4: Compiling StockLang
CSC 521, SP 2017  
Alexander Ing  
Nolan Thompson

## Instructions
1. Clone the project git.
2. Run Antlr on the grammar file to generate the lexer, parser, and visitor java files.  
  * `antlr4 -visitor StockLang.g4`
3. Compile the parser, lexer, visitor, IR generator, and main class files.
  * `javac *Lang*.java`
  * `javac IR*.java`
  * `javac SL*.java`
4. Run your new __StockLang__ compiler on the provided input file `TestInput.txt` and output the java translation of your program to the program file `output.java`.
  * `java SLMain TestInput.txt output`
5. Compile and run the translated code.
  * `javac output.java`
  * `java output`
