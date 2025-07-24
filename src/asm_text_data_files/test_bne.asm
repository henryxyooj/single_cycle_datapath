.data
msg_notequal: .asciiz "BNE: Branch taken\n"

.text
.globl main
main:
    li $t0, 42
    li $t1, 13

    bne $t0, $t1, branch_taken
    j end

branch_taken:
    li $v0, 4
    la $a0, msg_notequal
    syscall

end:
    li $v0, 10
    syscall
