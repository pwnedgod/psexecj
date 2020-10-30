package com.inspw.psexecj;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Getter
@Setter
public class BinaryExecutableExecutor implements Executor {

    /**
     * The executable file to use.
     */
    private File exeFile;

    /**
     * The timeout in milliseconds when executing the executable.
     * This timeout is measured at the start of an execution of the PsExec program.
     * It is mainly used to kill PsExec when stuck in an unresponsive state.
     * Set to 0 to disable execution timeout.
     */
    private int executeTimeout;

    /**
     * Suppress the display of the license dialog.
     * This is specific to the PsExec.exe implementation.
     */
    private boolean acceptEula = true;

    /**
     * Do not display the startup banner and copyright message.
     * This is specific to the PsExec.exe implementation.
     */
    private boolean noBanner = true;

    /**
     * Create a command executor instance.
     *
     * @param exeFile        the executable file to use
     * @param executeTimeout the timeout in milliseconds
     */
    public BinaryExecutableExecutor(File exeFile, int executeTimeout) {
        this.exeFile = exeFile;
        this.executeTimeout = executeTimeout;
    }

    /**
     * Create a command executor instance with no timeout.
     *
     * @param exeFile the executable file to use.
     */
    public BinaryExecutableExecutor(File exeFile) {
        this(exeFile, 0);
    }

    /**
     * Craft the given command into a string. The crafted string will be in a form of a shell command.
     *
     * @param command the command to craft with
     * @return the crafted string of the command
     */
    private String[] craft(Command command) {
        List<String> tokens = new ArrayList<>();
        tokens.add(getExeFile().getAbsolutePath());

        if (isAcceptEula()) {
            tokens.add("-accepteula");
        }

        if (isNoBanner()) {
            tokens.add("-nobanner");
        }

        if (!command.computers().isEmpty() && command.computerListFile() == null) {
            tokens.add(String.format("\\\\%s", String.join(",", command.computers())));
        }

        if (command.computerListFile() != null) {
            tokens.add(String.format("@\"%s\"", command.computerListFile()));
        }

        if (command.username() != null) {
            tokens.add("-u");
            tokens.add(command.username());

            if (command.password() != null) {
                tokens.add("-p");
                tokens.add(command.password());
            }
        }

        if (command.timeout() > 0) {
            tokens.add("-n");
            tokens.add(Integer.toString(command.timeout()));
        }

        if (command.serviceName() != null) {
            tokens.add("-r");
            tokens.add(command.serviceName());
        }

        if (command.runElevated()) {
            tokens.add("-h");
        }

        if (command.runLimited()) {
            tokens.add("-l");
        }

        if (command.asSystem() && !command.doNotLoadProfile()) {
            tokens.add("-s");
        }

        if (command.doNotLoadProfile()) {
            tokens.add("-e");
        }

        if (command.logonUI()) {
            tokens.add("-x");
        }

        if (command.session() != Command.SESSION_NONE) {
            tokens.add("-i");

            if (command.session() != Command.SESSION_ANY) {
                tokens.add(Integer.toString(command.session()));
            }
        }

        if (command.copy()) {
            tokens.add("-c");

            switch (command.copyOverride()) {
                case Command.COPY_OVERRIDE_ALWAYS:
                    tokens.add("-f");
                    break;
                case Command.COPY_OVERRIDE_NEWER:
                    tokens.add("-v");
                    break;
            }
        }

        if (command.workingDirectory() != null) {
            tokens.add("-w");
            tokens.add(command.workingDirectory());
        }

        if (command.detach()) {
            tokens.add("-d");
        }

        switch (command.priority()) {
            case Command.PRIORITY_BACKGROUND:
                tokens.add("-background");
                break;
            case Command.PRIORITY_LOW:
                tokens.add("-low");
                break;
            case Command.PRIORITY_BELOW_NORMAL:
                tokens.add("-belownormal");
                break;
            case Command.PRIORITY_ABOVE_NORMAL:
                tokens.add("-abovenormal");
                break;
            case Command.PRIORITY_HIGH:
                tokens.add("-high");
                break;
            case Command.PRIORITY_REALTIME:
                tokens.add("-realtime");
                break;
        }

        if (command.processors() != null) {
            tokens.add("-a");
            tokens.add(Arrays.stream(command.processors())
                    .mapToObj(Integer::toString)
                    .collect(Collectors.joining(","))
            );
        }

        if (command.cmd() == null) {
            throw new NullPointerException();
        }

        tokens.add(command.cmd());

        if (command.arguments() != null) {
            Collections.addAll(tokens, command.arguments());
        }

        String[] cmdarray = new String[tokens.size()];
        tokens.toArray(cmdarray);

        return cmdarray;
    }

    @Override
    public int execute(Command command) throws IOException {
        Runtime rt = Runtime.getRuntime();

        try {
            String[] cmdarray = craft(command);
            Process proc = rt.exec(cmdarray);

            if (getExecuteTimeout() <= 0) {
                proc.waitFor();
            } else {
                if (!proc.waitFor(getExecuteTimeout(), TimeUnit.MILLISECONDS)) {
                    proc.destroyForcibly();
                    // Assume error if timeout was reached.
                    return -1;
                }
            }

            return proc.exitValue();
        } catch (InterruptedException e) {
            // Assume error if timeout was interrupted.
            return -1;
        }
    }

}
