package com.inspw.psexecj;

import org.junit.Test;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class BinaryExecutableExecutorTest {

    /**
     * Invoke {@link BinaryExecutableExecutor#craft(Command)} with the specified command. For testing purposes.
     * @param executor the {@link BinaryExecutableExecutor} instance
     * @param command the command to craft with
     * @return the crafted command
     */
    private static String invokeCraft(BinaryExecutableExecutor executor, Command command) {
        try {
            Method craftMethod = BinaryExecutableExecutor.class
                    .getDeclaredMethod("craft", Command.class);
            craftMethod.setAccessible(true);
            return (String) craftMethod.invoke(executor, command);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    /**
     * Get a path to a dummy exe file. Does not have to exist.
     * @return the dummy file instance
     */
    private static File dummyExeFile() {
        return new File("C:\\PsExec.exe");
    }

    /**
     * Get a dummy instance of {@link BinaryExecutableExecutor}.
     * @param exeFile the exe file to use
     * @return the executor instance
     */
    private static BinaryExecutableExecutor dummyInstance(File exeFile) {
        return new BinaryExecutableExecutor(exeFile);
    }

    /**
     * Assert equality between the given command's crafted string and
     * the given string, concatenated as an argument.
     * @param command The command to craft with
     * @param psExecArguments the string, concatenated as an argument, to check against
     */
    private static void assertCraftEquals(Command command, String psExecArguments) {
        File exeFile = dummyExeFile();
        BinaryExecutableExecutor executor = dummyInstance(exeFile);

        assertEquals(
                invokeCraft(executor, command),
                "\"" + exeFile.getAbsolutePath() + "\" " + psExecArguments
        );
    }

    @Test
    public void craft_MatchesResult_GivenCmd() {
        Command command = Command.prepare()
                .cmd("ping")
                .build();

        assertCraftEquals(command, "\"ping\"");
    }

    @Test
    public void craft_MatchesResult_GivenCmdAndComputers() {
        Command command = Command.prepare()
                .cmd("ping")
                .computer("10.22.101.101")
                .computer("10.22.101.102")
                .computer("10.22.101.103")
                .build();

        assertCraftEquals(command, "\\\\10.22.101.101,10.22.101.102,10.22.101.103 \"ping\"");
    }

    @Test
    public void craft_MatchesResult_GivenCmdUsernameAndPassword() {
        Command command = Command.prepare()
                .cmd("ping")
                .username("root")
                .password("Testing123")
                .build();

        assertCraftEquals(command, "-u \"root\" -p \"Testing123\" \"ping\"");
    }

    @Test
    public void craft_MatchesResultWithoutPassword_GivenCmdAndPassword() {
        Command command = Command.prepare()
                .cmd("ping")
                .password("Testing123")
                .build();

        assertCraftEquals(command, "\"ping\"");
    }

    @Test
    public void craft_MatchesResult_GivenCmdAndTimeout() {
        Command command = Command.prepare()
                .cmd("ping")
                .timeout(60)
                .build();

        assertCraftEquals(command, "-n 60 \"ping\"");
    }

    @Test
    public void craft_MatchesResult_GivenCmdAndServiceName() {
        Command command = Command.prepare()
                .cmd("ping")
                .serviceName("DummyService")
                .build();

        assertCraftEquals(command, "-r \"DummyService\" \"ping\"");
    }

    @Test
    public void craft_MatchesResult_GivenCmdAndRunElevated() {
        Command command = Command.prepare()
                .cmd("ping")
                .runElevated(true)
                .build();

        assertCraftEquals(command, "-h \"ping\"");
    }

    @Test
    public void craft_MatchesResult_GivenCmdAndRunLimited() {
        Command command = Command.prepare()
                .cmd("ping")
                .runLimited(true)
                .build();

        assertCraftEquals(command, "-l \"ping\"");
    }

    @Test
    public void craft_MatchesResult_GivenCmdAndAsSystem() {
        Command command = Command.prepare()
                .cmd("ping")
                .asSystem(true)
                .build();

        assertCraftEquals(command, "-s \"ping\"");
    }

    @Test
    public void craft_MatchesResult_GivenCmdAndDoNotLoadProfile() {
        Command command = Command.prepare()
                .cmd("ping")
                .doNotLoadProfile(true)
                .build();

        assertCraftEquals(command, "-e \"ping\"");
    }

    @Test
    public void craft_MatchesResultWithoutAsSystem_GivenCmdAsSystemAndDoNotLoadProfile() {
        Command command = Command.prepare()
                .cmd("ping")
                .asSystem(true)
                .doNotLoadProfile(true)
                .build();

        assertCraftEquals(command, "-e \"ping\"");
    }

    @Test
    public void craft_MatchesResult_GivenCmdCopy() {
        Command command = Command.prepare()
                .cmd("ping")
                .copy(true)
                .build();

        assertCraftEquals(command, "-c \"ping\"");
    }

    @Test
    public void craft_MatchesResult_GivenCmdCopyAndCopyOverrideAlways() {
        Command command = Command.prepare()
                .cmd("ping")
                .copy(true)
                .copyOverride(Command.COPY_OVERRIDE_ALWAYS)
                .build();

        assertCraftEquals(command, "-c -f \"ping\"");
    }

    @Test
    public void craft_MatchesResult_GivenCmdCopyAndCopyOverrideNewer() {
        Command command = Command.prepare()
                .cmd("ping")
                .copy(true)
                .copyOverride(Command.COPY_OVERRIDE_NEWER)
                .build();

        assertCraftEquals(command, "-c -v \"ping\"");
    }

    @Test
    public void craft_MatchesResult_GivenCmdAndDetach() {
        Command command = Command.prepare()
                .cmd("ping")
                .detach(true)
                .build();

        assertCraftEquals(command, "-d \"ping\"");
    }

    @Test
    public void craft_MatchesResult_GivenCmdAndWorkingDirectory() {
        Command command = Command.prepare()
                .cmd("ping")
                .workingDirectory("C:\\Windows\\SysWOW64")
                .build();

        assertCraftEquals(command, "-w \"C:\\Windows\\SysWOW64\" \"ping\"");
    }

    @Test
    public void craft_MatchesResult_GivenCmdAndBackgroundPriority() {
        Command command = Command.prepare()
                .cmd("ping")
                .priority(Command.PRIORITY_BACKGROUND)
                .build();

        assertCraftEquals(command, "-background \"ping\"");
    }

    @Test
    public void craft_MatchesResult_GivenCmdAndProcessors() {
        Command command = Command.prepare()
                .cmd("ping")
                .processors(new int[]{1, 2})
                .build();

        assertCraftEquals(command, "-a 1,2 \"ping\"");
    }

    @Test
    public void craft_MatchesResult_GivenCmdAndArguments() {
        Command command = Command.prepare()
                .cmd("ping")
                .arguments("-n 10 localhost")
                .build();

        assertCraftEquals(command, "\"ping\" -n 10 localhost");
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

        assertCraftEquals(command, "\\\\10.22.101.101 -u \"root\" -p \"Testing\" -i 1 -d \"ping\" -n 15 localhost");
    }

}
