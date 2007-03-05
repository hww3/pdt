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

import java_cup.runtime.*;

%%

%class Scanner
%implements JFlex.sym

%unicode
%pack

%line
%column

%cup

%{
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

  /* keywords */
  "array"                        { return symbol(ARRAY); }
  "break"                        { return symbol(BREAK); }
  "case"                         { return symbol(CASE); }
  "catch"                        { return symbol(CATCH); }
  "class"                        { return symbol(CLASS); }
  "constant"                     { return symbol(CONSTANT); }
  "continue"                     { return symbol(CONTINUE); }
  "do"                           { return symbol(DO); }
  "else"                         { return symbol(ELSE); }
  "final"                        { return symbol(FINAL); }
  "float"                        { return symbol(FLOAT); }
  "for"                          { return symbol(FOR); }
  "function"                     { return symbol(FUNCTION); }
  "default"                      { return symbol(DEFAULT); }
  "foreach"                      { return symbol(FOREACH); }
  "import"                       { return symbol(IMPORT); }
  "inherit"                      { return symbol(INHERIT); }
  "int"                          { return symbol(INT); }
  "goto"                         { return symbol(GOTO); }
  "if"                           { return symbol(IF); }
  "mapping"                      { return symbol(MAPPING); }
  "mixed"                        { return symbol(MIXED); }
  "multiset"                     { return symbol(MULTISET); }
  "object"                       { return symbol(OBJECT); }
  "optional"                     { return symbol(OPTIONAL); }
  "public"                       { return symbol(PUBLIC); }
  "switch"                       { return symbol(SWITCH); }
  "string"                       { return symbol(STRING); }
  "private"                      { return symbol(PRIVATE); }
  "protected"                    { return symbol(PROTECTED); }
  "return"                       { return symbol(RETURN); }
  "void"                         { return symbol(VOID); }
  "static"                       { return symbol(STATIC); }
  "while"                        { return symbol(WHILE); }
  "this"                         { return symbol(THIS); }
  "throw"                        { return symbol(THROW); }
  "variant"                        { return symbol(VARIANT); }
  "nomask"                        { return symbol(NOMASK); }  
  "lambda"                        { return symbol(LAMBDA); }  
  "gauge"                        { return symbol(GAUGE); }  
  "sscanf"                        { return symbol(SSCANF); }    
  /* boolean literals */
/*  "true"                         { return symbol(BOOLEAN_LITERAL, new Boolean(true)); }
  "false"                        { return symbol(BOOLEAN_LITERAL, new Boolean(false)); }
  */
  /* null literal */
/*  "null"                         { return symbol(NULL_LITERAL); }
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
  "!"                            { return symbol(NOT); }
  "~"                            { return symbol(COMP); }
  "?"                            { return symbol(QUESTION); }
  ":"                            { return symbol(COLON); }
  "=="                           { return symbol(EQEQ); }
  "::"                           { return symbol(COLONCOLON); }
  "<="                           { return symbol(LTEQ); }
  ".."                           { return symbol(DOTDOT); }
  "..."                          { return symbol(DOTDOTDOT); }
  ">="                           { return symbol(GTEQ); }
  "->"                           { return symbol(INDIRECT); }
  "!="                           { return symbol(NOTEQ); }
  "&&"                           { return symbol(ANDAND); }
  "||"                           { return symbol(OROR); }
  "++"                           { return symbol(PLUSPLUS); }
  "--"                           { return symbol(MINUSMINUS); }
  "+"                            { return symbol(PLUS); }
  "-"                            { return symbol(MINUS); }
  "*"                            { return symbol(MULT); }
  "/"                            { return symbol(DIV); }
  "/"                            { return symbol(DIV); }
  "@"                            { return symbol(AT); }
  "|"                            { return symbol(OR); }
  "^"                            { return symbol(XOR); }
  "({"                           { return symbol(LPLC); }
  "})"                           { return symbol(RCRP); }
  "(["                           { return symbol(LPLB); }
  "])"                           { return symbol(RBRP); }
  "(<"                           { return symbol(RPLT); }
  ">)"                           { return symbol(GTLP); }
  "%"                            { return symbol(MOD); }
  "<<"                           { return symbol(LSHIFT); }
  ">>"                           { return symbol(RSHIFT); }
  ">>>"                          { return symbol(URSHIFT); }
  "+="                           { return symbol(PLUSEQ); }
  "-="                           { return symbol(MINUSEQ); }
  "*="                           { return symbol(MULTEQ); }
  "/="                           { return symbol(DIVEQ); }
  "&="                           { return symbol(ANDEQ); }
  "|="                           { return symbol(OREQ); }
  "^="                           { return symbol(XOREQ); }
  "%="                           { return symbol(MODEQ); }
  "<<="                          { return symbol(LSHIFTEQ); }
  ">>="                          { return symbol(RSHIFTEQ); }
  
  
  /* string literal */
  \"                             { yybegin(STRING); string.setLength(0); }

  /* character literal */
  \'                             { yybegin(CHARLITERAL); }

  /* numeric literals */

  {DecIntegerLiteral}            { return symbol(INTEGER_LITERAL, new Integer(yytext())); }
  {DecLongLiteral}               { return symbol(INTEGER_LITERAL, new Long(yytext().substring(0,yylength()-1))); }
  
  {HexIntegerLiteral}            { return symbol(INTEGER_LITERAL, new Integer((int) parseLong(yytext().substring(2),16))); }
  {HexLongLiteral}               { return symbol(INTEGER_LITERAL, new Long(parseLong(yytext().substring(2,yylength()-1),16))); }
 
  {OctIntegerLiteral}            { return symbol(INTEGER_LITERAL, new Integer((int) parseLong(yytext(),8))); }  
  {OctLongLiteral}               { return symbol(INTEGER_LITERAL, new Long(parseLong(yytext().substring(0,yylength()-1),8))); }
  
  {FloatLiteral}                 { return symbol(FLOATING_POINT_LITERAL, new Float(yytext().substring(0,yylength()-1))); }
  {DoubleLiteral}                { return symbol(FLOATING_POINT_LITERAL, new Double(yytext())); }
  {DoubleLiteral}[dD]            { return symbol(FLOATING_POINT_LITERAL, new Double(yytext().substring(0,yylength()-1))); }
  
  /* comments */
  {Comment}                      { /* ignore */ }

  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }

  /* identifiers */ 
  {Identifier}                   { return symbol(IDENTIFIER, yytext()); }  
}

<STRING> {
  \"                             { yybegin(YYINITIAL); return symbol(STRING_LITERAL, string.toString()); }
  
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
  {SingleCharacter}\'            { yybegin(YYINITIAL); return symbol(CHARACTER_LITERAL, new Character(yytext().charAt(0))); }
  
  /* escape sequences */
  "\\b"\'                        { yybegin(YYINITIAL); return symbol(CHARACTER_LITERAL, new Character('\b'));}
  "\\t"\'                        { yybegin(YYINITIAL); return symbol(CHARACTER_LITERAL, new Character('\t'));}
  "\\n"\'                        { yybegin(YYINITIAL); return symbol(CHARACTER_LITERAL, new Character('\t'));}
  "\\f"\'                        { yybegin(YYINITIAL); return symbol(CHARACTER_LITERAL, new Character('\f'));}
  "\\r"\'                        { yybegin(YYINITIAL); return symbol(CHARACTER_LITERAL, new Character('\r'));}
  "\\\""\'                       { yybegin(YYINITIAL); return symbol(CHARACTER_LITERAL, new Character('\"'));}
  "\\'"\'                        { yybegin(YYINITIAL); return symbol(CHARACTER_LITERAL, new Character('\''));}
  "\\\\"\'                       { yybegin(YYINITIAL); return symbol(CHARACTER_LITERAL, new Character('\\')); }
  \\[0-3]?{OctDigit}?{OctDigit}\' { yybegin(YYINITIAL); 
			                              int val = Integer.parseInt(yytext().substring(1,yylength()-1),8);
			                            return symbol(CHARACTER_LITERAL, new Character((char)val)); }
  
  /* error cases */
  \\.                            { throw new RuntimeException("Illegal escape sequence \""+yytext()+"\""); }
  {LineTerminator}               { throw new RuntimeException("Unterminated character literal at end of line"); }
}

/* error fallback */
.|\n                             { throw new RuntimeException("Illegal character \""+yytext()+"\" at line "+yyline+", column "+yycolumn); }
