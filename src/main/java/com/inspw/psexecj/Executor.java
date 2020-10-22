package com.inspw.psexecj;

import java.io.IOException;

public interface Executor {

    /**
     * Execute the given PsExec command with its argument.
     * @param command the command to execute
     * @return the exit code of the program
     * @throws IOException if an I/O error occurs
     */
    int execute(Command command) throws IOException;

}
