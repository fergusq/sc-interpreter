; Hello World in SC Language

main goto

:prints nop
    dup printc ; print char
    0 eq not   ; check if null terminator
    prints if  ; jump to :prints if not null terminator
    0 popr     ; otherwise return 0

:main nop

"Hello World!" 10 0 prints 14 pushp pop
