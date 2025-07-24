.data
result_msg: .asciiz "The result is: "

.text
main:
  li $t0, 10
  li $t1, 20
  add $t2, $t0, $t1     # t2 = 10 + 20 = 30
  sub $t3, $t2, $t0     # t3 = 30 - 10 = 20

  li $v0, 4             # print string
  la $a0, result_msg
  syscall

  li $v0, 1             # print int
  move $a0, $t3
  syscall

  li $v0, 10            # exit
  syscall
