; Preprocessor example in SC Language

; function + goto
#define FUN(name) :name nop
#define RETURN(x) x popr
#define CALL_0(fun) fun 0 pushp
#define CALL_1(fun, arg) arg fun 1 pushp

#define GOTO(to) to goto
#define GOIF(cond, to) cond to if
#define _LOOP(start, end, while, body) :start nop GOIF(while not, end) body GOTO(start) :end nop
#define LOOP(while, body) #< _LOOP(__COUNTER, __COUNTER, while, body) #> ; #< #> used to evaluate macro arguments eagerly

#define MAIN :main nop
#define TOMAIN GOTO(main)

; variables
#define PAR(name, index) #define name index
#define NEW(name, index, val) val #define name index
#define VAR(var) var overf
#define SET(var, val) val var dupt
#define MOV(var1, var2) var2 overf var1 dupt
#define INC(var) var overf 1 add var dupt

; operator macros
#define + add
#define - sub
#define * mul
#define / div
#define = eq
#define /= neq
#define < lt
#define > gt

; additional commands
#define le gt not
#define <= gt not
#define ge lt not
#define >= lt not

; io
#define PRINT(num) num print
#define PRINTC(chr) chr printc
#define PRINTV(var) PRINT(VAR(var))

; for fun
#define _(operand1, operator, operand2) operand1 operand2 operator

TOMAIN

#define prints(str) str __prints __LEN(str) pushp
FUN(__prints)
    GOIF(dup printc, __prints) ; jump to :__prints if not null terminator
    RETURN(0)

#define fib(p1) CALL_1(__fib, p1);
FUN(__fib)
	; params
	PAR(p1,0)
	; vars
	NEW(a,1, 0)
	NEW(b,2, 1)
	NEW(c,3, 0)
	NEW(i,4, 0)
	LOOP(_(VAR(i), <, VAR(p1)),
		SET(c, VAR(a)VAR(b)+)
		MOV(a, b)
		MOV(b, c)
		INC(i)
		PRINTV(i)
		prints(": "0)
		PRINTV(a)
		PRINTC(10)
	)
	RETURN(0)

MAIN
	prints("Fibonacci sequence: " 10 0)
	PRINT(fib(10)) PRINTC(10)
