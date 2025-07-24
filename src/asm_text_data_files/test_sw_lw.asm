.data
  mydata: .word 0         # Reserve a word in memory at label 'mydata'

.text
main:
  li $t0, 0x12345678      # $t0 = 0x12345678

  la $t1, mydata          # $t1 = address of mydata
  sw $t0, 0($t1)          # Store $t0 into memory

  li $t0, 0               # Clear $t0

  lw $t0, 0($t1)          # Load back into $t0

  li $v0, 1               # syscall code: print_int
  add $a0, $t0, $zero     # $a0 = $t0 (no pseudoinstruction)
  syscall

  li $v0, 10              # syscall code: exit
  syscall
