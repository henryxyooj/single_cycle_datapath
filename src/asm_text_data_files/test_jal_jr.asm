.data
msg_jal: .asciiz "Entered JAL target\n"
msg_returned: .asciiz "Returned from JAL\n"

.text
.globl main
main:
    jal function

    li $v0, 4
    la $a0, msg_returned
    syscall

    li $v0, 10
    syscall

function:
    li $v0, 4
    la $a0, msg_jal
    syscall

    jr $ra
