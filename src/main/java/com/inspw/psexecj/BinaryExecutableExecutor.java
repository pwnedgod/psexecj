package com.inspw.psexecj;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.Formatter;
import java.util.concurrent.TimeUnit;

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
     * Create a command executor instance.
     * @param exeFile the executable file to use
     * @param executeTimeout the timeout in milliseconds
     */
    public BinaryExecutableExecutor(File exeFile, int executeTimeout) {
        this.exeFile = exeFile;
        this.executeTimeout = executeTimeout;
    }

    /**
     * Create a command executor instance with no timeout.
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
    private String craft(Command command) {
        Formatter formatter = new Formatter();
        formatter.format("\"%s\"", getExeFile().getAbsolutePath());

        if (!command.computers().isEmpty() && command.computerListFile() == null) {
            formatter.format(" \\\\%s", String.join(",", command.computers()));
        }

        if (command.computerListFile() != null) {
            formatter.format(" @\"%s\"", command.computerListFile());
        }

        if (command.username() != null) {
            formatter.format(" -u \"%s\"", command.username());

            if (command.password() != null) {
                formatter.format(" -p \"%s\"", command.password());
            }
        }

        if (command.timeout() > 0) {
            formatter.format(" -n %d", command.timeout());
        }

        if (command.serviceName() != null) {
            formatter.format(" -r \"%s\"", command.serviceName());
        }

        if (command.runElevated()) {
            formatter.format(" -h");
        }

        if (command.runLimited()) {
            formatter.format(" -l");
        }

        if (command.asSystem() && !command.doNotLoadProfile()) {
            formatter.format(" -s");
        }

        if (command.doNotLoadProfile()) {
            formatter.format(" -e");
        }

        if (command.logonUI()) {
            formatter.format(" -x");
        }

        if (command.session() != Command.SESSION_NONE) {
            formatter.format(" -i");

            if (command.session() != Command.SESSION_ANY) {
                formatter.format(" %d", command.session());
            }
        }

        if (command.copy()) {
            formatter.format(" -c");

            switch (command.copyOverride()) {
                case Command.COPY_OVERRIDE_ALWAYS:
                    formatter.format(" -f");
                    break;
                case Command.COPY_OVERRIDE_NEWER:
                    formatter.format(" -v");
                    break;
            }
        }

        if (command.workingDirectory() != null) {
            formatter.format(" -w \"%s\"", command.workingDirectory());
        }

        if (command.detach()) {
            formatter.format(" -d");
        }

        switch (command.priority()) {
            case Command.PRIORITY_BACKGROUND:
                formatter.format(" -background");
                break;
            case Command.PRIORITY_LOW:
                formatter.format(" -low");
                break;
            case Command.PRIORITY_BELOW_NORMAL:
                formatter.format(" -belownormal");
                break;
            case Command.PRIORITY_ABOVE_NORMAL:
                formatter.format(" -abovenormal");
                break;
            case Command.PRIORITY_HIGH:
                formatter.format(" -high");
                break;
            case Command.PRIORITY_REALTIME:
                formatter.format(" -realtime");
                break;
        }

        if (command.processors() != null) {
            String[] processors = new String[command.processors().length];

            for (int i = 0; i < command.processors().length; i++) {
                processors[i] = Integer.toString(command.processors()[i]);
            }

            formatter.format(" -a %s", String.join(",", processors));
        }

        formatter.format(" \"%s\"", command.cmd() == null ? "" : command.cmd());

        if (command.arguments() != null) {
            formatter.format(" %s", command.arguments());
        }

        return formatter.toString();
    }

    @Override
    public int execute(Command command) throws IOException {
        String craftedCommand = craft(command);

        Runtime rt = Runtime.getRuntime();
        try {
            Process proc = rt.exec(craftedCommand);

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
