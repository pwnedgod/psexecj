package com.inspw.psexecj;

import org.junit.Test;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertArrayEquals;

public class BinaryExecutableExecutorTest {

    /**
     * Invoke {@link BinaryExecutableExecutor#craft(Command)} with the specified command. For testing purposes.
     *
     * @param executor the {@link BinaryExecutableExecutor} instance
     * @param command  the command to craft with
     * @return the crafted command
     */
    private static String[] invokeCraft(BinaryExecutableExecutor executor, Command command) {
        try {
            Method craftMethod = BinaryExecutableExecutor.class
                    .getDeclaredMethod("craft", Command.class);
            craftMethod.setAccessible(true);
            return (String[]) craftMethod.invoke(executor, command);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("There was a problem invoking craft", e);
        }
    }

    /**
     * Get a path to a dummy exe file. Does not have to exist.
     *
     * @return the dummy file instance
     */
    private static File dummyExeFile() {
        return new File("C:\\PSTools\\PsExec.exe");
    }

    /**
     * Get a dummy instance of {@link BinaryExecutableExecutor}.
     *
     * @param exeFile the exe file to use
     * @return the executor instance
     */
    private static BinaryExecutableExecutor dummyExecutor(File exeFile) {
        return new BinaryExecutableExecutor(exeFile);
    }

    /**
     * Assert equality between the given command's crafted string and
     * the given string, concatenated as an argument.
     *
     * @param expectedArgs  the string, concatenated as an argument, to check against
     * @param actualCommand The command to craft with
     */
    private static void assertCraftEquals(String[] expectedArgs, Command actualCommand) {
        File exeFile = dummyExeFile();
        BinaryExecutableExecutor executor = dummyExecutor(exeFile);

        String[] cmdarray = new String[expectedArgs.length + 3];
        cmdarray[0] = exeFile.getAbsolutePath();
        cmdarray[1] = "-accepteula";
        cmdarray[2] = "-nobanner";

        System.arraycopy(expectedArgs, 0, cmdarray, 3, expectedArgs.length);

        assertArrayEquals(cmdarray, invokeCraft(executor, actualCommand));
    }

    @Test
    public void craft_MatchesResult_GivenCmd() {
        Command command = Command.prepare()
                .cmd("dir")
                .build();

        assertCraftEquals(new String[]{"dir"}, command);
    }

    @Test
    public void craft_MatchesResult_GivenCmdAndComputers() {
        Command command = Command.prepare()
                .cmd("dir")
                .computer("10.22.101.101")
                .computer("10.22.101.102")
                .computer("10.22.101.103")
                .build();

        assertCraftEquals(new String[]{"\\\\10.22.101.101,10.22.101.102,10.22.101.103", "dir"}, command);
    }

    @Test
    public void craft_MatchesResult_GivenCmdUsernameAndPassword() {
        Command command = Command.prepare()
                .cmd("dir")
                .username("root")
                .password("Testing123")
                .build();

        assertCraftEquals(new String[]{"-u", "root", "-p", "Testing123", "dir"}, command);
    }

    @Test
    public void craft_MatchesResultWithoutPassword_GivenCmdAndPassword() {
        Command command = Command.prepare()
                .cmd("dir")
                .password("Testing123")
                .build();

        assertCraftEquals(new String[]{"dir"}, command);
    }

    @Test
    public void craft_MatchesResult_GivenCmdAndTimeout() {
        Command command = Command.prepare()
                .cmd("dir")
                .timeout(60)
                .build();

        assertCraftEquals(new String[]{"-n", "60", "dir"}, command);
    }

    @Test
    public void craft_MatchesResult_GivenCmdAndServiceName() {
        Command command = Command.prepare()
                .cmd("dir")
                .serviceName("DummyService")
                .build();

        assertCraftEquals(new String[]{"-r", "DummyService", "dir"}, command);
    }

    @Test
    public void craft_MatchesResult_GivenCmdAndRunElevated() {
        Command command = Command.prepare()
                .cmd("dir")
                .runElevated(true)
                .build();

        assertCraftEquals(new String[]{"-h", "dir"}, command);
    }

    @Test
    public void craft_MatchesResult_GivenCmdAndRunLimited() {
        Command command = Command.prepare()
                .cmd("dir")
                .runLimited(true)
                .build();

        assertCraftEquals(new String[]{"-l", "dir"}, command);
    }

    @Test
    public void craft_MatchesResult_GivenCmdAndAsSystem() {
        Command command = Command.prepare()
                .cmd("dir")
                .asSystem(true)
                .build();

        assertCraftEquals(new String[]{"-s", "dir"}, command);
    }

    @Test
    public void craft_MatchesResult_GivenCmdAndDoNotLoadProfile() {
        Command command = Command.prepare()
                .cmd("dir")
                .doNotLoadProfile(true)
                .build();

        assertCraftEquals(new String[]{"-e", "dir"}, command);
    }

    @Test
    public void craft_MatchesResultWithoutAsSystem_GivenCmdAsSystemAndDoNotLoadProfile() {
        Command command = Command.prepare()
                .cmd("dir")
                .asSystem(true)
                .doNotLoadProfile(true)
                .build();

        assertCraftEquals(new String[]{"-e", "dir"}, command);
    }

    @Test
    public void craft_MatchesResult_GivenCmdCopy() {
        Command command = Command.prepare()
                .cmd("dir")
                .copy(true)
                .build();

        assertCraftEquals(new String[]{"-c", "dir"}, command);
    }

    @Test
    public void craft_MatchesResult_GivenCmdCopyAndCopyOverrideAlways() {
        Command command = Command.prepare()
                .cmd("dir")
                .copy(true)
                .copyOverride(Command.COPY_OVERRIDE_ALWAYS)
                .build();

        assertCraftEquals(new String[]{"-c", "-f", "dir"}, command);
    }

    @Test
    public void craft_MatchesResult_GivenCmdCopyAndCopyOverrideNewer() {
        Command command = Command.prepare()
                .cmd("dir")
                .copy(true)
                .copyOverride(Command.COPY_OVERRIDE_NEWER)
                .build();

        assertCraftEquals(new String[]{"-c", "-v", "dir"}, command);
    }

    @Test
    public void craft_MatchesResult_GivenCmdAndDetach() {
        Command command = Command.prepare()
                .cmd("dir")
                .detach(true)
                .build();

        assertCraftEquals(new String[]{"-d", "dir"}, command);
    }

    @Test
    public void craft_MatchesResult_GivenCmdAndWorkingDirectory() {
        Command command = Command.prepare()
                .cmd("dir")
                .workingDirectory("C:\\Windows\\SysWOW64")
                .build();

        assertCraftEquals(new String[]{"-w", "C:\\Windows\\SysWOW64", "dir"}, command);
    }

    @Test
    public void craft_MatchesResult_GivenCmdAndBackgroundPriority() {
        Command command = Command.prepare()
                .cmd("dir")
                .priority(Command.PRIORITY_BACKGROUND)
                .build();

        assertCraftEquals(new String[]{"-background", "dir"}, command);
    }

    @Test
    public void craft_MatchesResult_GivenCmdAndProcessors() {
        Command command = Command.prepare()
                .cmd("dir")
                .processors(new int[]{1, 2})
                .build();

        assertCraftEquals(new String[]{"-a", "1,2", "dir"}, command);
    }

    @Test
    public void craft_MatchesResult_GivenCmdAndArguments() {
        Command command = Command.prepare()
                .cmd("ping")
                .arguments("-n 10 localhost")
                .build();

        assertCraftEquals(new String[]{"ping", "-n", "10", "localhost"}, command);
    }

    @Test
    public void craft_MatchesResult_GivenCmdComputerUsernamePasswordSessionDetachAndArguments() {
        Command command = Command.prepare()
                .cmd("ping")
                .computer("10.22.101.101")
                .username("root")
                .password("Testing")
                .session(1)
                .detach(true)
                .arguments("-n 15 localhost")
                .build();

        assertCraftEquals(new String[]{
                "\\\\10.22.101.101",
                "-u", "root",
                "-p", "Testing",
                "-i", "1",
                "-d",
                "ping", "-n", "15", "localhost"
        }, command);
    }

}
