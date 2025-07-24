.data
msg_equal: .asciiz "BEQ: Branch taken\n"

.text
.globl main
main:
    li $t0, 42
    li $t1, 42

    beq $t0, $t1, branch_taken
    j end

branch_taken:
    li $v0, 4
    la $a0, msg_equal
    syscall

end:
    li $v0, 10
    syscall
