" Vim syntax file
" Language: SOM - Simple Object Machine
" Maintainer: Stefan Marr
" Latest Revision: 26 April 2014

"if exists("b:current_syntax")
"  finish
"endif

" For Debugging:
map <F8> :echo "hi<" . synIDattr(synID(line("."),col("."),1),"name") . '> trans<' . synIDattr(synID(line("."),col("."),0),"name") . "> lo<" . synIDattr(synIDtrans(synID(line("."),col("."),1)),"name") . ">"<CR>


syn sync fromstart

" set what the identifier characters are for SOM
setlocal isident=a-z,A-Z,48-57,_

syn match somNumber '\d\+'     " contained display
syn match somNumber '[-]\d\+' " contained display

syn match somNumber '\d\+\.\d+'    " contained display
syn match somNumber '[-]\d\+\.\d+' " contained display

syn region somString start="'" end="'" contained


syn keyword somTodo contained TODO FIXME XXX NOTE 
syn region  somComment start=/"/ end=/"/ contains=somTodo 

syn keyword somPrimitive primitive
syn keyword somSelf      self super
syn keyword somNil       nil
syn keyword somBoolean   true false
syn keyword somSystem    system

"syn region somClassBlock  start="(" end=")" fold transparent contains=somFieldDef,somMethodDef,somClassSideSwitch,somComment
"syn region somMethodBlock start="(" end=")" fold transparent contains=somTermBlock,somBlockBlock
"syn region somTermBlock   start="(" end=")" fold transparent contains=somTermBlock,somBlockBlock
"syn region somBlockBlock  start="\[" end="\]" fold transparent contains=somTermBlock,somBlockBlock

"syn match somIdentifier /[A-Z][a-z,A-Z,0-9,_]/ contained display

"syn region somClass start=/[A-Z][a-z,A-Z,0-9,_]*\s*=([A-Z][a-z,A-Z,0-9,_]*)?\s(/ms=e+1 end=/)/me=s-1 transparent fold contains=somComment,somMethodBlock


" Grammar:

syn match  somClassName      /\I\i*/ nextgroup=somClassEquals skipwhite
syn match  somClassEquals    /=/     contained nextgroup=somSuperClassName skipwhite
syn match  somSuperClassName /\I\i*/ contained nextgroup=somClassDecl skipwhite skipnl
syn region somClassDecl matchgroup=somClassPar start=/(/ end=/)/ contained fold transparent contains=somComment,somFieldDecl,somClassSideSwitch,somMethodDeclOp,somMethodDeclUnary
",somMethodDeclKey

syn region somFieldDecl matchgroup=somFieldDeclDelim start="|" end="|" fold transparent contains=somFieldName
syn match somFieldName /\I\i*/ contained display
syn match somClassSideSwitch /-----*/ contained

"operators: ~&|*/\+=><,@%
"syn match somMethodDeclOp "\~&|\*/\\+=\>\<,@%" contained nextgroup=somMethodArgument
syn match somMethodDeclOp "+" contained nextgroup=somMethodArgument

syn match somMethodDeclUnary /\I\i*/ contained nextgroup=somMethodEquals
syn match somMethodArgument /\I\i*/  contained nextgroup=somMethodEquals

syn match somMethodEquals /=/ nextgroup=somMethodBody contained skipwhite skipnl
"syn region somMethodBody matchgroup=somMethodBodyPar contained start="(" end=")"


let b:current_syntax = "som"

hi def link somClassName      Identifier
hi def link somSuperClassName Identifier
hi def link somClassEquals    Operator
hi def link somMethodEquals   Operator
hi def link somMethodDeclOp   Operator
hi def link somMethodDeclUnary Keyword
hi def link somMethodArgument Identifier
hi def link somClassPar       Delimiter
hi def link somMethodBodyPar  Delimiter
hi def link somFieldDeclDelim Delimiter
hi def link somFieldName      Identifier
hi def link somClassSideSwitch Delimiter


hi def link somTodo       Todo
hi def link somComment    Comment
hi def link somNumber     Number
hi def link somString     String
hi def link somPrimitive  Keyword
hi def link somSelf       Keyword
hi def link somBoolean    Boolean
hi def link somNil        Keyword
hi def link somSystem     Constant
"hi def link somClass      Type
"hi def link somIdentifier Identifier
