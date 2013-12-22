sc-interpreter
=====

SC Interpreter

## Building

**These instructions are for Linux**

To build sc-interpreter, you need phl environment. Using installation script is the recommended way.

### Installing PHL environment

#### Using installation script

```
~ $ mkdir phl
~ $ cd phl
~/phl $ wget -nv -O installphl.sh http://www.kaivos.org/doc/phl/installphl.sh.all
~/phl $ bash installphl.sh
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

Manually: TODO

## About SC Language

### Functions

```
nop ( -- )
pop ( a -- )
dup ( a -- a a )
dupt ( .. index_a a -- a .. ) moves a number to a position in the stack
overf ( a .. index_a -- a .. a ) brings a number from a position of the stack
swap ( a b -- b a )
	
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
and ( a b -- a&b )
or ( a b -- a|b )
xor ( a b -- a^b )
not ( a -- !a )
	
io
print ( out -- ) print a number
printc ( out -- ) print a character
read ( -- in ) read a number
readc ( -- in ) read a character
]
```

### Comments

Comment character is ;. Newline terminates comment.

```
not_comment ; comment
not_comment
```

### Labels

Labels are prefixed with :. You must add a nop immediately after a label.

```
:label nop
...
label goto
```

### Example

Print-function:
```
:printf nop
	dup printc
	0 eq not
	printf if
	0 popr

:main nop

"Hello World!" 10 0 printf 12 pushp
```
