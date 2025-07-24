.text
.globl main

main:
  addi $t0, $zero, 0
  jal subroutine
  addi $t0, $t0, 1
  
 end:
   j end
  
subroutine:
  addi $t0, $t0, 5
  jr $ra
