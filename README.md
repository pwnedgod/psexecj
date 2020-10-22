# PsExecJ
Simple library for crafting PsExec commands.

# How To Use
Create an instance of `BinaryExecutableExecutor` with the file path to the PsExec.exe to use.
```
File exeFile = new File("PsExec64.exe");
Executor executor = new BinaryExecutableExecutor(exeFile);
```
Build your PsExec command with the `Command` class builder.
```
Command command = Command.prepare()
        .cmd("net")
        .computer("192.168.0.50")
        .username("Administrator")
        .password("MyAdministratorPassword")
        .session(Command.SESSION_ANY)
        .arguments("user \"SomeOtherUser\" \"NewPassword\"")
        .build();
```
Pass in your command to the executor instance.
```
int exitCode = executor.execute(command);
```

# Future Plans
* Read output of executed commands.
* Create a low level implementation that does not require a .exe.
