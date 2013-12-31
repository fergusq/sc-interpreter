; Preprocessor example in SC Language

; function + goto
#define RETURN(x) x popr
#define CALL_0(fun) fun 0 pushp
#define CALL_1(fun, arg) arg fun 1 pushp

#define GOTO(to) to goto
#define GOIF(cond, to) cond to if
#define LOOP(start, end, while, body) :start nop GOIF(while not, end) body GOTO(start) :end nop

#define MAIN :main nop
#define TOMAIN GOTO(main)

; variables
#define NEW(val) val ; crazy?
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
#define PRINT(num) num print 10 printc
#define PRINTV(var) PRINT(VAR(var))

; for fun
#define _(operand1, operator, operand2) operand1 operand2 operator

TOMAIN

:fib nop
	;var0   var1   var2   var3
	NEW(0) NEW(1) NEW(0) NEW(0)
	LOOP(loop_start, loop_end, _(VAR(3), <, 10),
		SET(2, VAR(0)VAR(1)+)
		MOV(0, 1)
		MOV(1, 2)
		INC(3)
		PRINTV(0)
	)
	RETURN(0)

MAIN
	CALL_0(fib)
