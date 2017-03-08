# COMP
A tool able to read a list of regular expressions (a possibility is to use the PCRE (http://www.pcre.org/) format) and to build correspondent automata (e-NFA, NFA, DFA, etc.). The tool should be able to transform automata, minimize the DFA, and to generate Java and/or C code to implement the automata.

##Doubts
1. Do we read a list of regular expressions and make an automata by joining them together, or do we only read a single regular expression?
2. Transforming the automata means changing from DFA to NFA, and so on, right?
3. Is there a way to, with only one program, generate both Java and C code?
