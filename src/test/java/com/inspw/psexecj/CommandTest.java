package com.inspw.psexecj;

import org.junit.Test;

public class CommandTest {

    @Test(expected = NullPointerException.class)
    public void build_ThrowsException_IfCmdIsNeverSet() {
        Command.prepare().build();
    }

}
