MiniJava Compiler
MiniJava is a subset of the Java programming language designed for educational purposes, particularly for teaching compiler design. This MiniJava compiler processes MiniJava source code by lexically analyzing, parsing, performing semantic checks, and finally generating target code.

Features of MiniJava:
Class definitions with fields and methods
Basic data types: int, boolean, and int[]
Statements like assignments, conditionals, loops, and method calls
Arithmetic operations (addition, subtraction, multiplication, etc.)
Boolean expressions

Project Overview
The MiniJava compiler performs the following steps:

Lexical Analysis: Tokenizes MiniJava code into a list of meaningful symbols.
Parsing: Parses tokens according to the MiniJava grammar, building an Abstract Syntax Tree (AST).
Semantic Analysis: Checks for type consistency, variable declaration before use, and method correctness.
Code Generation: Converts AST to intermediate code, usually in assembly-like syntax for educational purposes or to Java bytecode.

MiniJava/
├── README.md
├── src/                    # Source code for the MiniJava compiler
├── SamplePrograms/         # Sample MiniJava programs
├── lib/                    # External libraries
└── test/                  # Test MiniJava programs
Grammar and Syntax Rules
For a complete list of supported grammar rules, please refer to the src/Parser/minijava.cup directory.

Sources: AST classes and SampleMiniJavaPrograms from the Appel /
Palsberg MiniJava project.  Some code and ideas borrowed from an earlier
UW version by Craig Chambers with modifications by Jonathan Beall and
Hal Perkins.  Updates to include more recent releases of JFlex and CUP
by Hal Perkins, Jan. 2017.  Updates to use recent JFlex ComplexSymbol
class by Nate Yazdani, April 2018.  Updates to improve testing support
by Aaron Johnston, Sep. 2019.  Update to JFlex 1.8.2 by Hal Perkins,
Oct. 2020.
