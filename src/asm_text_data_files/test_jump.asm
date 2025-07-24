.data
msg_jump: .asciiz "Jump succeeded\n"

.text
.globl main
main:
    j target

    # This line should not execute
    li $v0, 1
    li $a0, 999
    syscall

target:
    li $v0, 4
    la $a0, msg_jump
    syscall

    li $v0, 10
    syscall
