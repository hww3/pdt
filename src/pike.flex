/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (C) 1998,99 Gerwin Klein <kleing@informatik.tu-muenchen.de>.  *
 * All rights reserved.                                                    *
 *                                                                         *
 * This program is free software; you can redistribute it and/or modify    *
 * it under the terms of the GNU General Public License. See the file      *
 * COPYRIGHT for more information.                                         *
 *                                                                         *
 * This program is distributed in the hope that it will be useful,         *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of          *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the           *
 * GNU General Public License for more details.                            *
 *                                                                         *
 * You should have received a copy of the GNU General Public License along *
 * with this program; if not, write to the Free Software Foundation, Inc., *
 * 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA                 *
 *                                                                         *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


/* Java 1.2 language lexer specification */

/* Use together with unicode.flex for Unicode preprocesssing */
/* and java12.cup for a Java 1.2 parser                      */

/* Note, that this lexer specification is not tuned for speed.
   It is in fact quite slow on integer and floating point literals, 
   because the input is read twice and the methods used to parse
   the numbers are not very fast. 
   For a real world application (e.g. a Java compiler) this can 
   and should be optimized */

package org.gotpike.pdt.parser;
import java_cup.runtime.*;


%%

%class Scanner
%extends sym

%unicode
%pack

%line
%column

%cup

%{

  public void yyerror(String message)
  { 
    report_error(message, null);
  }
  
  public void report_error(String message, Object info) {
    StringBuffer m = new StringBuffer("Error ");

    if (info instanceof java_cup.runtime.Symbol) 
      m.append( "(" +info.toString()+")" );
     
    m.append(" : "+message);
   
    System.out.println(m);
  }
   
  public int getYycolumn()
  {
    return yycolumn;
  }

  public int getYyline()
  {
    return yyline;
  }

  StringBuffer string = new StringBuffer();
  
  private Symbol symbol(int type) {
    return new PikeSymbol(type, yyline+1, yycolumn+1);
  }

  private Symbol symbol(int type, Object value) {
    return new PikeSymbol(type, yyline+1, yycolumn+1, value);
  }

  /* assumes correct representation of a long value for 
     specified radix in String s */
  private long parseLong(String s, int radix) {
    int  max = s.length();
    long result = 0;
    long digit;

    for (int i = 0; i < max; i++) {
      digit  = Character.digit(zzBuffer[i],radix);
      result*= radix;
      result+= digit;
    }

    return result;
  }
%}

/* main character classes */
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]

WhiteSpace = {LineTerminator} | [ \t\f]

/* comments */
Comment = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}

TraditionalComment = "/*" [^*] {CommentContent} \*+ "/"
EndOfLineComment = "//" {InputCharacter}* {LineTerminator}
DocumentationComment = "/*!" {CommentContent} \*+ "/"

CommentContent = ( [^*] | \*+[^!/] )*


/* identifiers */
Identifier = [:jletter:][:jletterdigit:]*

/* integer literals */
DecIntegerLiteral = 0 | [1-9][0-9]*
DecLongLiteral    = {DecIntegerLiteral} [lL]

HexIntegerLiteral = 0 [xX] 0* {HexDigit} {1,8}
HexLongLiteral    = 0 [xX] 0* {HexDigit} {1,16} [lL]
HexDigit          = [0-9a-fA-F]

OctIntegerLiteral = 0+ [1-3]? {OctDigit} {1,15}
OctLongLiteral    = 0+ 1? {OctDigit} {1,21} [lL]
OctDigit          = [0-7]
    
/* floating point literals */        
FloatLiteral  = ({FLit1}|{FLit2}|{FLit3}|{FLit4}) [fF]
DoubleLiteral = {FLit1}|{FLit2}|{FLit3}|{FLit4}

FLit1 = [0-9]+ \. [0-9]* {Exponent}?
FLit2 = \. [0-9]+ {Exponent}?
FLit3 = [0-9]+ {Exponent}
FLit4 = [0-9]+ {Exponent}?

Exponent = [eE] [+\-]? [0-9]+

/* string and character literals */
StringCharacter = [^\r\n\"\\]
SingleCharacter = [^\r\n\'\\]

%state STRING, CHARLITERAL

%%

<YYINITIAL> {

  /* misc */
  "(<"							{ return symbol(TOK_MULTISET_START); }
  ">)"							{ return symbol(TOK_MULTISET_END); }


  /* keywords */
  "array"                        { return symbol(TOK_ARRAY_ID); }
  "break"                        { return symbol(TOK_BREAK); }
  "case"                         { return symbol(TOK_CASE); }
  "catch"                        { return symbol(TOK_CATCH); }
  "class"                        { return symbol(TOK_CLASS); }
  "constant"                     { return symbol(TOK_CONSTANT); }
  "continue"                     { return symbol(TOK_CONTINUE); }
  "do"                           { return symbol(TOK_DO); }
  "else"                         { return symbol(TOK_ELSE); }
  "final"                        { return symbol(TOK_FINAL_ID); }
  "float"                        { return symbol(TOK_FLOAT); }
  "for"                          { return symbol(TOK_FOR); }
  "function"                     { return symbol(TOK_FUNCTION_ID); }
  "default"                      { return symbol(TOK_DEFAULT); }
  "foreach"                      { return symbol(TOK_FOREACH); }
  "import"                       { return symbol(TOK_IMPORT); }
  "inherit"                      { return symbol(TOK_INHERIT); }
  "int"                          { return symbol(TOK_INT_ID); }
 "if"                           { return symbol(TOK_IF); }
  "mapping"                      { return symbol(TOK_MAPPING_ID); }
  "mixed"                        { return symbol(TOK_MIXED_ID); }
  "multiset"                     { return symbol(TOK_MULTISET_ID); }
  "object"                       { return symbol(TOK_OBJECT_ID); }
  "optional"                     { return symbol(TOK_OPTIONAL); }
  "public"                       { return symbol(TOK_PUBLIC); }
  "switch"                       { return symbol(TOK_SWITCH); }
  "string"                       { return symbol(TOK_STRING_ID); }
  "private"                      { return symbol(TOK_PRIVATE); }
  "protected"                    { return symbol(TOK_PROTECTED); }
  "return"                       { return symbol(TOK_RETURN); }
  "void"                         { return symbol(TOK_VOID_ID); }
  "static"                       { return symbol(TOK_STATIC); }
  "while"                        { return symbol(TOK_WHILE); }
   "variant"                        { return symbol(TOK_VARIANT); }
  "nomask"                        { return symbol(TOK_NO_MASK); }  
  "lambda"                        { return symbol(TOK_LAMBDA); }  
  "gauge"                        { return symbol(TOK_GAUGE); }  
  "sscanf"                        { return symbol(TOK_SSCANF); }    
/*
  "goto"                         { return symbol(TOK_GOTO); }
  "this"                         { return symbol(TOK_THIS); }
  "throw"                        { return symbol(TOK_THROW); }
*/
  /* separators */
  "("                            { return symbol(LPAREN); }
  ")"                            { return symbol(RPAREN); }
  "{"                            { return symbol(LBRACE); }
  "}"                            { return symbol(RBRACE); }
  "["                            { return symbol(LBRACK); }
  "]"                            { return symbol(RBRACK); }
  ";"                            { return symbol(SEMICOLON); }
  ","                            { return symbol(COMMA); }
  "."                            { return symbol(DOT); }
  
  /* operators */
  "="                            { return symbol(EQ); }
  ">"                            { return symbol(GT); }
  "<"                            { return symbol(LT); }
  "!"                            { return symbol(TOK_NOT); }
  "~"                            { return symbol(COMP); }
  "?"                            { return symbol(QUESTION); }
  ":"                            { return symbol(COLON); }
  "=="                           { return symbol(TOK_EQ); }
  "::"                           { return symbol(TOK_COLON_COLON); }
  "<="                           { return symbol(TOK_LE); }
  ".."                           { return symbol(TOK_DOT_DOT); }
  "..."                          { return symbol(TOK_DOT_DOT_DOT); }
  ">="                           { return symbol(TOK_GE); }
  "->"                           { return symbol(TOK_ARROW); }
  "!="                           { return symbol(TOK_NE); }
  "&&"                           { return symbol(TOK_LAND); }
  "||"                           { return symbol(TOK_LOR); }
  "++"                           { return symbol(TOK_INC); }
  "--"                           { return symbol(TOK_DEC); }
  "+"                            { return symbol(PLUS); }
  "-"                            { return symbol(MINUS); }
  "*"                            { return symbol(MULT); }
  "/"                            { return symbol(DIV); }
  "@"                            { return symbol(AT); }
  "|"                            { return symbol(OR); }
  "^"                            { return symbol(XOR); }
  "%"                            { return symbol(MOD); }
  "<<"                           { return symbol(TOK_LSH); }
  ">>"                           { return symbol(TOK_RSH); }
  "+="                           { return symbol(TOK_ADD_EQ); }
  "-="                           { return symbol(TOK_SUB_EQ); }
  "*="                           { return symbol(TOK_MULT_EQ); }
  "/="                           { return symbol(TOK_DIV_EQ); }
  "&="                           { return symbol(TOK_AND_EQ); }
  "|="                           { return symbol(TOK_OR_EQ); }
  "^="                           { return symbol(TOK_XOR_EQ); }
  "%="                           { return symbol(TOK_MOD_EQ); }
  "<<="                          { return symbol(TOK_LSH_EQ); }
  ">>="                          { return symbol(TOK_RSH_EQ); }
  
  
  /* string literal */
  \"                             { yybegin(STRING); string.setLength(0); }

  /* character literal */
  \'                             { yybegin(CHARLITERAL); }

  /* numeric literals */

  {DecIntegerLiteral}            { return symbol(TOK_NUMBER, new Integer(yytext())); }
  {DecLongLiteral}               { return symbol(TOK_NUMBER, new Long(yytext().substring(0,yylength()-1))); }
  
  {HexIntegerLiteral}            { return symbol(TOK_NUMBER, new Integer((int) parseLong(yytext().substring(2),16))); }
  {HexLongLiteral}               { return symbol(TOK_NUMBER, new Long(parseLong(yytext().substring(2,yylength()-1),16))); }
 
  {OctIntegerLiteral}            { return symbol(TOK_NUMBER, new Integer((int) parseLong(yytext(),8))); }  
  {OctLongLiteral}               { return symbol(TOK_NUMBER, new Long(parseLong(yytext().substring(0,yylength()-1),8))); }
  
  {FloatLiteral}                 { return symbol(TOK_FLOAT, new Float(yytext().substring(0,yylength()-1))); }
  {DoubleLiteral}                { return symbol(TOK_FLOAT, new Double(yytext())); }
  {DoubleLiteral}[dD]            { return symbol(TOK_FLOAT, new Double(yytext().substring(0,yylength()-1))); }
  
  /* comments */
  {Comment}                      { /* ignore */ }

  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }

  /* identifiers */ 
  {Identifier}                   { return symbol(TOK_IDENTIFIER, yytext()); }  
}

<STRING> {
  \"                             { yybegin(YYINITIAL); return symbol(TOK_STRING, string.toString()); }
  
  {StringCharacter}+             { string.append( yytext() ); }
  
  /* escape sequences */
  "\\b"                          { string.append( '\b' ); }
  "\\t"                          { string.append( '\t' ); }
  "\\n"                          { string.append( '\n' ); }
  "\\f"                          { string.append( '\f' ); }
  "\\r"                          { string.append( '\r' ); }
  "\\\""                         { string.append( '\"' ); }
  "\\'"                          { string.append( '\'' ); }
  "\\\\"                         { string.append( '\\' ); }
  \\[0-3]?{OctDigit}?{OctDigit}  { char val = (char) Integer.parseInt(yytext().substring(1),8);
                        				   string.append( val ); }
  
  /* error cases */
  \\.                            { throw new RuntimeException("Illegal escape sequence \""+yytext()+"\""); }
  {LineTerminator}               { throw new RuntimeException("Unterminated string at end of line"); }
}

<CHARLITERAL> {
  {SingleCharacter}\'            { yybegin(YYINITIAL); return symbol(TOK_NUMBER, new Character(yytext().charAt(0))); }
  
  /* escape sequences */
  "\\b"\'                        { yybegin(YYINITIAL); return symbol(TOK_NUMBER, new Character('\b'));}
  "\\t"\'                        { yybegin(YYINITIAL); return symbol(TOK_NUMBER, new Character('\t'));}
  "\\n"\'                        { yybegin(YYINITIAL); return symbol(TOK_NUMBER, new Character('\t'));}
  "\\f"\'                        { yybegin(YYINITIAL); return symbol(TOK_NUMBER, new Character('\f'));}
  "\\r"\'                        { yybegin(YYINITIAL); return symbol(TOK_NUMBER, new Character('\r'));}
  "\\\""\'                       { yybegin(YYINITIAL); return symbol(TOK_NUMBER, new Character('\"'));}
  "\\'"\'                        { yybegin(YYINITIAL); return symbol(TOK_NUMBER, new Character('\''));}
  "\\\\"\'                       { yybegin(YYINITIAL); return symbol(TOK_NUMBER, new Character('\\')); }
  \\[0-3]?{OctDigit}?{OctDigit}\' { yybegin(YYINITIAL); 
			                              int val = Integer.parseInt(yytext().substring(1,yylength()-1),8);
			                            return symbol(TOK_NUMBER, new Character((char)val)); }
  
  /* error cases */
  \\.                            { throw new RuntimeException("Illegal escape sequence \""+yytext()+"\""); }
  {LineTerminator}               { throw new RuntimeException("Unterminated character literal at end of line"); }
}

/* error fallback */
.|\n                             { throw new RuntimeException("Illegal character \""+yytext()+"\" at line "+yyline+", column "+yycolumn); }
