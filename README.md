# Reduced Instruction Set MIPS Simulator
___

## Introduction
In this programming assignment, you will create a Java program that will take MIPS machine code instructions and simulate the internal workings of a MIPS processor using a single-cycle datapath. The goal of this assignment is to help you understand the functional relationships between the various MIPS processor components, and how instructions are used to modify those relationships. To make sure that this assignment is manageable, but still provides the deep understanding, we will only worry about a reduced set of instructions:

+ add
+ addi
+ and
+ andi
+ beq
+ bne
+ j
+ lui
+ lw
+ or
+ ori
+ slt
+ sub
+ sw
+ syscall
___
## JAR file
The output of Milestone 2 is a JAR file. When run from the command line, the JAR file will take two arguments (passed in through the main method args array). The first argument will be a path to a .text file. This file will contain a sequence of MIPS instructions (from the list above) that are represented in ASCII hex, one instruction per line. The second argument is a path to a .data file. This file will contain a sequence of data represented in ASCII hex format, one word of data per line. Your program will read these files, and then simulate execution of the MIPS program. To accomplish this, you will need to maintain a representation of the internal MIPS CPU state, including registers and memory. You are only expected to simulate single-cycle version of the processor (no need to simulate multiple instructions executing in different stages).

The output will be how the program interfaces with the user. Your program will need to support the following MARS syscalls:

+ print string
+ print integer
+ read integer
+ exit

That is, if the following sequence of instructions were executed:

```
addiu $v0, $zero, 1
addiu $a0, $zero, 42
syscall
```

you would print the number 42 to the terminal (prints an integer because the number 1 was put in $v0, and prints 42 specifically because 42 was put in $a0). You should consult the MARS syscall documentation for how these syscalls should work.

Use the EvenOrOdd program as an initial test for your simulator, and compare its behavior to that of the MARS simulator
+ EvenOrOdd.asm
+ EvenOrOdd.text
+ EvenOrOdd.data
However, it would be a good idea for you to write other examples.
___

### Notes:
+ I am intentionally leaving the implementation details sparse.  I want you to think about how you can simulate a processor in Java, what the important components that you need to keep track of are, how they relate to each other, and the best way to program them.  If you are really stuck and don't know how to get started, come see me in office hours and we will get you on the right path.
+ Remember to correctly offset your addresses (the data segment will start at with 0x10010000 and the text segment will start at 0x00400000)
+ It might be helpful for you to implement a debug mode, where you can essentially add breakpoints. Essentially, when the program counter is a a specific value (or perhaps when a specific instruction is about to be executed), you print out the system state.
___

### Requirements:
+ Your program is a command line program that takes two arguments: a path to a .text file, and a path to a .data file
+ Your program will simulate the execution of the MIPS program contained in the .text and .data files.
+ Your program will correctly interface with the users when syscall instructions are executed
  - printing a string will print a string from the simulated memory (from the .data file) to the terminal. This means that you will need to be able to recreate a Java string from a sequence of bytes (up to the null terminator byte). $a0 will contain the address of the first character in the string.
  - printing an integer will print the value of $a0 as an integer to the terminal
  - reading an integer will read user input from the terminal, cast it to an integer, and place it in the simulated $v0 register
  - exiting will cleanly exit your simulator program (see next bullet point)
+  How the program exits should mimic the behavior of MARS. That is, when the exit syscall is executed, your program should print "-- program is finished running --" to the terminal, and then exit the program. If the simulator reaches the end of the program otherwise (if the next instruction equals 0x00000000), then your program should print "-- program finished running (dropped off bottom) --" and exit.
