grammar TextSearch;
options {
    output = AST;
    language = Java;
}
tokens {
    AND = '&';
    ANDX = '&&';
    OR = '|';
    LOOKUP = '~';
    PREFIXLOOKUP = '*';
    OPEN = '(';
    CLOSE = ')';
}
@lexer::header {// Copyright 2011 by TalkingTrends (Amsterdam, The Netherlands)
//
// Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
// in compliance with the License. You may obtain a copy of the License at
//
// http://opensahara.com/licenses/apache-2.0
//
// Unless required by applicable law or agreed to in writing, software distributed under the License
// is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
// or implied. See the License for the specific language governing permissions and limitations under
// the License.
//CHECKSTYLE:ANTLR
    package com.useekm.fulltext.antlr3;
}
@parser::header {// Copyright 2011 by TalkingTrends (Amsterdam, The Netherlands)
//
// Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
// in compliance with the License. You may obtain a copy of the License at
//
// http://opensahara.com/licenses/apache-2.0
//
// Unless required by applicable law or agreed to in writing, software distributed under the License
// is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
// or implied. See the License for the specific language governing permissions and limitations under
// the License.
//CHECKSTYLE:ANTLR
    package com.useekm.fulltext.antlr3;
    import com.useekm.fulltext.FulltextParseException;
}
@lexer::members {
    public static void suppresCompileWarnings() {
    	Stack<String> stack = new Stack<String>(); stack.empty();
		List<String> list = new ArrayList<String>(); list.size();
		ArrayList<String> arrayList = new ArrayList<String>(); arrayList.size();
    }
}
@parser::members {
    public void reportError(RecognitionException e) {
        throw new FulltextParseException();
    }
    public static void suppresCompileWarnings() {
    	Stack<String> stack = new Stack<String>(); stack.empty();
		List<String> list = new ArrayList<String>(); list.size();
		ArrayList<String> arrayList = new ArrayList<String>(); arrayList.size();
    }
}

searches: expr EOF!;
//left-to-right:
expr
  : (e1=exprPart->$e1) (
      (AND? e2=exprPart -> ^(ANDX $expr $e2))+
    | (OR e4=exprPart   -> ^(OR  $expr $e4))+
    | (()               -> $e1)
    )
  ;
//right-to-left tree would look like:
//expr
//  : e1=exprLhs (
//      AND? e2=expr -> ^(ANDX $e1 $e2)
//    | OR e4=expr   -> ^(OR $e1 $e4)
//    | ()           -> $e1
//    )
//  ;
exprPart
  : OPEN! expr CLOSE!
  | lookup;
lookup
  : WORD -> ^(LOOKUP WORD)
  | PREFIXWORD -> ^(PREFIXLOOKUP PREFIXWORD);
PREFIXWORD: LETTER+ '*';
WORD: LETTER+;
fragment LETTER: ~(' ' | '\t' | '\n' | '\r' | '!' | '&' | '|' | '+' | '*' | '(' | ')');
WS: (' '|'\t'|'\n'|'\r')+ { $channel = HIDDEN; };