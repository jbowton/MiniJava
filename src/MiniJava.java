import Scanner.*;
import Parser.*;
import AST.*;
import AST.Visitor.*;
import java_cup.runtime.Symbol;
import java_cup.runtime.ComplexSymbolFactory;
import java.util.*;
import java.io.*;

public class MiniJava {
  public static void main(String[] args) {
    if(args.length == 0) {
      System.err.println("No arguments used");
      System.exit(1);
    } else if(args.length > 2) {
      System.err.println("Too many arguments used");
      System.exit(1);
    }
    String fileName = args[args.length - 1];
    File myFile = new File(fileName);

    if (args.length == 1){
      CodeGen(myFile);
    }

    if(args[0].equals("-S")) { // -S means to use the scanner
      PrintScan(myFile);
    } else if(args[0].equals("-A")) {
      PrintAST(myFile);
    } else if(args[0].equals("-P")) {
      PrintPretty(myFile);
    } else if(args[0].equals("-T")) {
      Semantics(myFile);
    }
  }

  public static void PrintScan(File myFile){
    try {
      int exit_status = 0;
      // create a scanner on the input file
      ComplexSymbolFactory sf = new ComplexSymbolFactory();
      Reader in = new BufferedReader(new FileReader(myFile));
      scanner s = new scanner(in, sf);
      Symbol t = s.next_token();
      while (t.sym != sym.EOF) {
          if(t.sym == sym.error){
            exit_status = 1; // Set exit status to 1 when we find an unexpected character
          }
          // print each token that we scan
          System.out.print(s.symbolToString(t) + " ");
          t = s.next_token();
      }
      System.out.println();
      System.exit(exit_status);
    } catch (Exception e) {
      // yuck: some kind of error in the compiler implementation
      // that we're not expecting (a bug!)
      System.err.println("Unexpected internal compiler error: " +
                  e.toString());
      // print out a stack dump
      e.printStackTrace();
      System.exit(1);
    }
  }

  public static void PrintAST(File myFile) {
    try {
      // create a scanner on the input file
      ComplexSymbolFactory sf = new ComplexSymbolFactory();
      Reader in = new BufferedReader(new FileReader(myFile));
      scanner s = new scanner(in, sf);
      parser p = new parser(s, sf);
      Symbol root;
      // replace p.parse() with p.debug_parse() in the next line to see
      // a trace of parser shift/reduce actions during parsing
      root = p.parse();
      // We know the following unchecked cast is safe because of the
      // declarations in the CUP input file giving the type of the
      // root node, so we suppress warnings for the next assignment.
      @SuppressWarnings("unchecked")
      Program program = (Program)root.value;
      program.accept(new AbstractTreeVisitor());
      System.exit(0);
    } catch (Exception e) {
      // yuck: some kind of error in the compiler implementation
      // that we're not expecting (a bug!)
      System.err.println("Unexpected internal compiler error: " +
                         e.toString());
      // print out a stack dump
      e.printStackTrace();
      System.exit(1);
    }
  }

  public static void PrintPretty(File myFile) {
    try {
      // create a scanner on the input file
      ComplexSymbolFactory sf = new ComplexSymbolFactory();
      Reader in = new BufferedReader(new FileReader(myFile));
      scanner s = new scanner(in, sf);
      parser p = new parser(s, sf);
      Symbol root;
      // replace p.parse() with p.debug_parse() in the next line to see
      // a trace of parser shift/reduce actions during parsing
      root = p.parse();
      // We know the following unchecked cast is safe because of the
      // declarations in the CUP input file giving the type of the
      // root node, so we suppress warnings for the next assignment.
      @SuppressWarnings("unchecked")
      Program program = (Program)root.value;
      program.accept(new PrettyPrintVisitor());
      System.exit(0);
    } catch (Exception e) {
      // yuck: some kind of error in the compiler implementation
      // that we're not expecting (a bug!)
      System.err.println("Unexpected internal compiler error: " +
                         e.toString());
      // print out a stack dump
      e.printStackTrace();
      System.exit(1);
    }
  }

  public static void Semantics(File myFile) {
    try {
      // create a scanner on the input file
      ComplexSymbolFactory sf = new ComplexSymbolFactory();
      Reader in = new BufferedReader(new FileReader(myFile));
      scanner s = new scanner(in, sf);
      parser p = new parser(s, sf);
      Symbol root;
      // replace p.parse() with p.debug_parse() in the next line to see
      // a trace of parser shift/reduce actions during parsing
      root = p.parse();
      // We know the following unchecked cast is safe because of the
      // declarations in the CUP input file giving the type of the
      // root node, so we suppress warnings for the next assignment.
      @SuppressWarnings("unchecked")
      Program program = (Program)root.value;

      GlobalTableVisitor gtv = new GlobalTableVisitor();
      program.accept(gtv);

      LocalTablesVisitor ltv = new LocalTablesVisitor(gtv.getGlobalTable());
      program.accept(ltv);

      //Print Symbol Table
      for (int i = 0; i < ltv.symbolTable.size(); i++) {
        System.out.println(ltv.symbolTable.get(i));
      }

      TypeCheckVisitor tcv = new TypeCheckVisitor(gtv.getGlobalTable());
      program.accept(tcv);
      for (int i = 0; i < gtv.errors.size(); i++) {
        System.out.println(gtv.errors.get(i));
      }
      for (int i = 0; i < ltv.errors.size(); i++) {
        System.out.println(ltv.errors.get(i));
      }

      if (tcv.error() || gtv.errors.size() > 0 || ltv.errors.size() > 0) {
        System.exit(1);
      } else {
        System.exit(0);
      }
    } catch (Exception e) {
      // yuck: some kind of error in the compiler implementation
      // that we're not expecting (a bug!)
      System.err.println("Unexpected internal compiler error: " +
                         e.toString());
      // print out a stack dump
      e.printStackTrace();
      System.exit(1);
    }
  }

  public static void CodeGen(File myFile) {
    try {
      // create a scanner on the input file
      ComplexSymbolFactory sf = new ComplexSymbolFactory();
      Reader in = new BufferedReader(new FileReader(myFile));
      scanner s = new scanner(in, sf);
      parser p = new parser(s, sf);
      Symbol root;
      // replace p.parse() with p.debug_parse() in the next line to see
      // a trace of parser shift/reduce actions during parsing
      root = p.parse();
      // We know the following unchecked cast is safe because of the
      // declarations in the CUP input file giving the type of the
      // root node, so we suppress warnings for the next assignment.
      @SuppressWarnings("unchecked")
      Program program = (Program)root.value;


      GlobalTableVisitor gtv = new GlobalTableVisitor();
      program.accept(gtv);

      LocalTablesVisitor ltv = new LocalTablesVisitor(gtv.getGlobalTable());
      program.accept(ltv);

      TypeCheckVisitor tcv = new TypeCheckVisitor(gtv.getGlobalTable());
      program.accept(tcv);

      for (int i = 0; i < gtv.errors.size(); i++) {
        System.out.println(gtv.errors.get(i));
      }
      for (int i = 0; i < ltv.errors.size(); i++) {
        System.out.println(ltv.errors.get(i));
      }

      if (tcv.error() || gtv.errors.size() > 0 || ltv.errors.size() > 0) {
        System.exit(1);
      }

      program.accept(new PreCodeGenVisitor(gtv.getGlobalTable()));
      program.accept(new CodeGenVisitor(gtv.getGlobalTable()));
      
      System.exit(0);
    } catch (Exception e) {
      // yuck: some kind of error in the compiler implementation
      // that we're not expecting (a bug!)
      System.err.println("Unexpected internal compiler error: " +
                         e.toString());
      // print out a stack dump
      e.printStackTrace();
      System.exit(1);
    }
  }
}