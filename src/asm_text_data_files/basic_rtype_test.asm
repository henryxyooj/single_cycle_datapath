.data

.text
.globl main
  main:
    li $t0, 10
    li $t1, 5
    li $t2, 3
    li $t3, 1
    li $t4, 30
    
    add $t5, $t4, $t3
    sub $t6, $t4, $t0
    and $t7, $t1, $t4
    or $t8, $t2, $t1
    slt $t9, $t3, $t0