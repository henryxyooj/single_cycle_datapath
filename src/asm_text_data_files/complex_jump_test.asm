.data
msg_equal:      .asciiz "Equal branch taken\n"
msg_notequal:   .asciiz "Not equal branch taken\n"
msg_jump:       .asciiz "Jump taken\n"
msg_jal:        .asciiz "JAL taken, now returning...\n"
msg_returned:   .asciiz "Returned from JAL target\n"

.text
.globl main
main:
    # Set up values
    li $t0, 5
    li $t1, 5
    li $t2, 10

    # Test BEQ (should take branch)
    beq $t0, $t1, equal_branch
    j skip_equal

equal_branch:
    li $v0, 4
    la $a0, msg_equal
    syscall

skip_equal:
    # Test BNE (should take branch)
    bne $t0, $t2, notequal_branch
    j skip_notequal

notequal_branch:
    li $v0, 4
    la $a0, msg_notequal
    syscall

skip_notequal:
    # Test JUMP
    j jump_target

    # This should be skipped
    li $v0, 4
    la $a0, msg_notequal
    syscall

jump_target:
    li $v0, 4
    la $a0, msg_jump
    syscall

    # Test JAL and JR
    jal link_target

    li $v0, 4
    la $a0, msg_returned
    syscall

    # Exit program
    li $v0, 10
    syscall

link_target:
    li $v0, 4
    la $a0, msg_jal
    syscall

    jr $ra  # Return to caller
