package com.inspw.psexecj;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.File;
import java.util.List;

@Builder(builderMethodName = "prepare")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true)
@Getter
public class Command {

    /**
     * If used, PsExec will select any available desktop session.
     */
    public static final int SESSION_ANY = -1;

    /**
     * If used, PsExec will instead run on console session.
     */
    public static final int SESSION_NONE = Integer.MIN_VALUE;

    /**
     * Never override existing files.
     */
    public static final int COPY_OVERRIDE_NEVER = 0;

    /**
     * Always override existing files.
     */
    public static final int COPY_OVERRIDE_ALWAYS = 1;

    /**
     * Override files if it is a newer version.
     */
    public static final int COPY_OVERRIDE_NEWER = 2;

    /*
     * Priority levels.
     */
    public static final int PRIORITY_BACKGROUND = -3;
    public static final int PRIORITY_LOW = -2;
    public static final int PRIORITY_BELOW_NORMAL = -1;
    public static final int PRIORITY_NORMAL = 0;
    public static final int PRIORITY_ABOVE_NORMAL = 1;
    public static final int PRIORITY_HIGH = 2;
    public static final int PRIORITY_REALTIME = 3;

    /**
     * Direct PsExec to run the application on the remote computer or computers specified.
     * If you omit this, PsExec runs the application on the local system.
     * If a wildcard is given, PsExec runs the command on all computers in
     * the current domain.
     */
    @Singular
    @NonNull
    private final List<String> computers;

    /**
     * PsExec will execute the command on each of the computers listed in the file.
     */
    @Builder.Default
    private final File computerListFile = null;

    /**
     * The optional user name for login to remote computer.
     */
    @Builder.Default
    private final String username = null;

    /**
     * The optional password for user name.
     */
    @Builder.Default
    private final String password = null;

    /**
     * The timeout in seconds connecting to remote computers.
     */
    @Builder.Default
    private final int timeout = 0;

    /**
     * The the name of the remote service to create or interact with.
     */
    @Builder.Default
    private final String serviceName = null;

    /**
     * If the target system is Vista or higher, has the process
     * run with the account's elevated token, if available.
     */
    @Builder.Default
    private final boolean runElevated = false;

    /**
     * Run process as limited user (strips the Administrators group
     * and allows only privileges assigned to the Users group).
     */
    @Builder.Default
    private final boolean runLimited = false;

    /**
     * Run the remote process in the System account.
     */
    @Builder.Default
    private final boolean asSystem = false;

    /**
     * Does not load the specified account's profile.
     */
    @Builder.Default
    private final boolean doNotLoadProfile = false;

    /**
     * Display the UI on the Winlogon secure desktop (local system only).
     */
    @Builder.Default
    private final boolean logonUI = false;

    /**
     * Run the program so that it interacts with the desktop of the
     * specified session on the remote system. If no session is
     * specified, the process runs in the console session.
     */
    @Builder.Default
    private final int session = SESSION_NONE;

    /**
     * Copy the specified program to the remote system for execution.
     * If you disable this option, the application
     * must be in the system path on the remote system.
     */
    @Builder.Default
    private final boolean copy = false;

    /**
     * The copy overriding behaviour when copy is enabled.
     */
    @Builder.Default
    private final int copyOverride = COPY_OVERRIDE_NEVER;

    /**
     * The working directory of the process (relative to remote computer).
     */
    @Builder.Default
    private final String workingDirectory = null;

    /**
     * The priority for the process to be run at.
     */
    @Builder.Default
    private final int priority = PRIORITY_NORMAL;

    /**
     * Don't wait for process to terminate (non-interactive).
     */
    @Builder.Default
    private final boolean detach = false;

    /**
     * Processors on which the application can run with,
     * where 1 is the lowest numbered CPU.
     */
    @Builder.Default
    private final int[] processors = null;

    /**
     * The command to execute on the specified computer.
     */
    @NonNull
    private final String cmd;

    /**
     * The arguments to pass when executing the command.
     */
    @Builder.Default
    private final String arguments = null;

}
