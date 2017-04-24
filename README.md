# CSC 531 Project 4: Compiling StockLang
CSC 521, SP 2017  
Alexander Ing  
Nolan Thompson

## Description
The _StockLang_ compiler compiles files written for the _StockLang_ programming language into a _Java_ intermediate representation.  Follow the instructions below to download, build, and compile a sample _StockLang_ program file.

## Instructions
1. Clone the project git.  The project contains the necessary _.java_ files, and a sample program written in _StockLang_ called `TestInput.txt`.
2. Run Antlr on the grammar file to generate the lexer, parser, and visitor java files.  
  * `antlr4 -visitor StockLang.g4`
3. Compile the parser, lexer, visitor, IR generator, and main class _.java_ files.
  * `javac *Lang*.java` _(compiles the ANTLR generated .java files)_
  * `javac IR*.java` _(compiles the .java files for generation of the StockLang intermediate representation)_
  * `javac SL*.java` _(compiles the main class and StockLang visitor extension files)_
4. Use your new _StockLang_ compiler to compile the _StockLang_ program file `TestInput.txt` and output the java translation of your program to the program file `output.java`.
  * `java SLMain TestInput.txt output`
5. Compile and run the compiled intermediate java representation of the _StockLang_ program file.
  * `javac output.java`
  * `java output`
