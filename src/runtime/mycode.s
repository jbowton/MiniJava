    .text
    .globl  asm_main
asm_main:
    pushq   %rbp
    movq    %rsp,%rbp
    movq    $8,%rdi
    addq    $0,%rdi
    call    mjcalloc
    movq    %rax,%rdi
    call    LS$Start
    movq    %rax,%rdi
    call    put    # Call the Print method
    movq    %rbp,%rsp
    popq    %rbp
    ret
LS$Start:
    pushq   %rbp    # Prologue
    movq    %rsp,%rbp    # Prologue
    subq    $16,%rsp    # Make space for parameters
    movq    %rdi,-8(%rbp)    # Copy 'this' reference to memory
    subq    $8,%rsp    # Make space for local variables
    subq    $8,%rsp    # Make space for local variables
    subq    $8,%rsp    # Make space for local variables
    movq    $5,%rax
    movq    %rax,%rdi
    pushq    %rax
    imulq    $8,%rdi
    addq    $8,%rdi
    call    mjcalloc
    popq    %rbx
    movq    %rbx,(%rax)
    movq    %rax,-16(%rbp)    # Assign
    pushq    -16(%rbp)
    movq    $3,%rax
    pushq    %rax    # Save the index
    movq    $1234,%rax
    popq    %rbx
    addq    $1,%rbx
    imulq    $8,%rbx
    popq    %rcx
    movq    (%rcx),%rcx
    movq    %rax,(%rbx,%rcx)
    movq    $3,%rax
    movq    %rax,-24(%rbp)    # Assign
    movq    -24(%rbp),%rax    # Move identifier to %rax
    movq    %rbp,%rsp    # Epilogue
    popq    %rbp    # Epilogue
    ret
LS$get:
    pushq   %rbp    # Prologue
    movq    %rsp,%rbp    # Prologue
    subq    $16,%rsp    # Make space for parameters
    movq    %rdi,-8(%rbp)    # Copy 'this' reference to memory
    subq    $8,%rsp    # Make space for parameters
    movq    %rsi,-16(%rbp)    # Copy parameters to memory
    movq    $5,%rax
    movq    %rbp,%rsp    # Epilogue
    popq    %rbp    # Epilogue
    ret
