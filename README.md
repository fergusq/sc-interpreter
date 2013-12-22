sc-interpreter
=====

SC Interpreter

## Building

**These instructions are for Linux**

To build sc-pp, you need phl environment. Using installation script is the recommended way.

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

TODO

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
