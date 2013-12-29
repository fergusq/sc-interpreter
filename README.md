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
printm ( out -- ) writes to heap
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
lt ( a b -- a<b )
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
	0 eq not   ; check if null terminator
	prints if  ; jump to :prints if not null terminator
	0 popr     ; otherwise return 0

...

"Hello World!" 10 0 prints 12 pushp pop
```

## Preprocessor

SC Interpreter includes a macro preprocessor caller "sc-pp". A derivate of this preprocessor is available for external download
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
#define : #define
#define . PRINT_INT

: **. * * .

2 3 4 **.

#define DEBUG
...
#ifdef DEBUG
... ; print debug data
#endif
```
