package org.joshy.gfx.util.localization;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SimpleTest {
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testLocalization() throws Exception {
        Localization.init(SimpleTest.class.getResource("translation.xml"),"en_US");
        //test that we can look up the correct value
        assertTrue("Wonder".equals(Localization.getString("menus.window")));
        //test that a default value is found
        assertTrue("defaultfoobar".equals(Localization.getString("menus.foobar")));
    }
}