sc-interpreter
=====

SC Interpreter

## Building

**These instructions are for Linux**

To build sc-interpreter, you need phl environment. Using installation script is the recommended way.

### Downloading sources

Cloning git-repository:

```
$ git clone https://github.com/fergusq/sc-interpreter.git
```

### Installing PHL environment

#### Using installation script

```
$ cd sc-interpreter
sc-interpreter $ wget -nv -O installphl.sh http://www.kaivos.org/doc/phl/installphl.sh.all
sc-interpreter $ bash installphl.sh
```

#### Manual download

Look more information from <http://www.kaivos.org/doc/phl/phl.html>

### Install libraries

PHL implementation uses Boehm-Demers-Weiser as garbage collector. <http://www.hpl.hp.com/personal/Hans_Boehm/gc/>

In Debian systems (Ubuntu, Mint, etc) bdwgc is usually in package libgc.

```
sudo apt-get install libgc
```

### Compiling

Using script:

```
PHLC_FLAGS="--path src" tools/compile.sh src/main.phl sc
```

To compile preprocessor only: (for debug purposes)

```
PHLC_FLAGS="--path src" tools/compile.sh src/pp_main.phl sc-pp
```

Manually: TODO

## About SC Language

### Functions

```
nop ( -- )

stack manipulation
pop ( a -- )
dup ( a -- a a )
dupt ( .. index_a a -- a .. ) moves a number to a position in the stack
overf ( a .. index_a -- a .. a ) brings a number from a position of the stack
swap ( a b -- b a )

stack frame manipulation
pushp ( .. p addr psize -- .. p )( new stack: -- p ) jump to addr, create new stack frame
popr ( .. r -- ) (old stack: .. -- .. r ) jump back, exit stack frame
	
memory manipulation
printm ( out addr -- ) writes to heap
readm ( addr -- in ) reads from heap
	
control
goto ( to -- ) jumps to a locations
if ( cond to -- ) conditional jump
	
math
add ( a b -- a+b )
mul ( a b -- a*b )
sub ( a b -- a-b )
div ( a b -- a/b )
	
logic
eq ( a b -- a=b )
gt ( a b -- a>b )
lt ( a b -- a&lt;b )
and ( a b -- a&&b ) 0 = false
or ( a b -- a||b ) 0 = false
xor ( a b -- a^b ) 0 = false
not ( a -- !a ) 0 = false
	
io
print ( out -- ) print a number
printc ( out -- ) print a character
read ( -- in ) read a number
readc ( -- in ) read a character
```

### Comments

Comment character is `;`. Newline terminates comment.

```
not_comment ; comment
not_comment
```

### Labels

Labels are prefixed with `:`. You must add a nop immediately after a label.

```
:label nop
...
label goto ; unconditional jump
...
cond label if ; conditional 
```

### Stack

The stack is the main data structure of the language.

#### Pushing numbers

Every number in the source code is pushed to the stack.

```
1 2 3 ; pushes three numbers to the stack
```

#### Manipulating stack

Basic stack manipulation commands are `pop`, `dup` and `swap`. Pop pops the top value of the stack. Dup duplicates the top value. Swap changes places of the two topmost values.

Commands `dupt` (duplicate to) and `overf` (over from) are used for deep stack manipulation. Dupt replaces a value in the stack with the topmost value. It also pops the topmost value.
Overf copies and pushes a value from the stack.

```
1 2 3   ; stack: [1 2 3
dup     ; stack: [1 2 3 3
1 dupt  ; stack: [1 3 3
0 overf ; stack: [1 3 3 1
swap    ; stack: [1 3 1 3
pop     ; stack: [1 3 1
```

##### Tip: Variables

Use `dupt` and `overf` to read and write local variables.

```
0 ; var 1 = 0
1 ; var 2 = 1
0 ; var 3 = 0

0 overf ; read var 1
1 overf ; read var 2
add     ; var1 + var2
2 dupt  ; write var 3
```

### Memory

The memory (also called the heap) is a big data structure that contains many *slots* that can store one number.

#### Reading and writing

Memory can be manipulated using commands `readm` and `printm`.

```
; DATA ADDRESS printm
; ADDRESS readm

2 0 printm ; store 2 to slot 0
4 1 printm ; store 4 to slot 1
8 2 printm ; store 8 to slot 2

0 readm ; read slot 0
2 readm ; read slot 2
mul     ; 2+8=10
1 readm ; read slot 1
add     ; 10+4=14
```

### Strings

Preprocessor replaces strings with character codes.

```
"HELLO"0
; Equivalent to:
72 69 76 76 79 0
```

### Functions

#### Defining a function

```
:NAME nop
	; code
	VALUE popr
```

Arguments are pushed to the stack.

`popr` is like return-keyword in C.

All labels are global, were they inside a function or not. It is recommended to prefix labels with their functions name.

Interpreter does not skip functions but interprets them like any other code segments. You should use goto to jump over functions in code.

```
; jump over functions
main goto

:function1 nop ... popr
:function2 nop ... popr

:main nop
	; execution continues here
	...
```

Example, adds 2 to the argument:
```
:add2 nop
	2 add popr
```

#### Calling functions

Syntax:
```
ARGUMENTS FUNCTION_NAME ARGUMENT_COUNT pushp
```
Examples:
```
3 add2 1 pushp ; calls example function above with argument 3

"Text..."0 processText 8 pushp ; remember all characters are separate arguments + null terminator

1 2 3 4 print_data 4 pushp pop ; pop return value
```

### Example

Print-function:
```
:prints nop
	dup printc ; print char
	prints if  ; jump to :prints if not null terminator ( 0 = terminator and false)
	0 popr     ; otherwise return 0

...

"Hello World!" 10 0 prints 14 pushp pop
```

## Preprocessor

SC Interpreter includes a macro preprocessor called "sc-pp". A derivate of this preprocessor is available for external download
here: <https://github.com/fergusq/sc-pp>.

Included preprocessor can handle basic directives like `#define` and `#ifdef`. `#include` is not currently supported.

```
#define PRINT_INT print 10 printc
2 2 add PRINT_INT

#define SQUARE dup mul
2 SQUARE  ; 2*2=4
4 SQUARE  ; 4*4=16
mul       ; 4*8=64
PRINT_INT

#define + add
#define - sub
#define * mul
#define / div
#define def #define
#define . PRINT_INT

def **. * * .

2 3 4 **.

#define DEBUG
...
#ifdef DEBUG
... ; print debug data
#endif
```

Special built-in macro `__COUNTER` returns a new string every time it is called. It can be used for automatic name generation.

*Note!* Macro arguments are always evaluated lazy. For eager evaluation, surround macro call with `#<` and `#>`.

```
#define fun(A, B) A A B B

fun(__COUNTER, __COUNTER) ;=> __COUNTER __COUNTER __COUNTER __COUNTER => (1) (2) (3) (4)

#< fun(__COUNTER, __COUNTER) #> ;=> fun((1), (2)) => (1) (1) (2) (2)
```

The other built-in, `__LEN` returns the number of tokens in the argument. It can be used eg. for calculating string lengths.

```
:_prints nop
	dup printc _prints if
	0 popr

#define prints(str) str _prints __LEN(str) pushp pop

prints("hello world" 10 0) ;=> "hello world" 10 0 _prints 13 pushp pop
```

File `examples/preprocessor_example.sc_souruce` contais many examples of both *__COUNTER* and *__LEN*. Note that the file is only
showing features of the preprocessor, not being the recommended style to write code. Macros should not be used everywhere in the code.
