package org.joshy.gfx.test.scripting;

import org.joshy.gfx.util.u;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.InputStreamReader;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Aug 4, 2010
 * Time: 11:25:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleScriptTest {
    public static void main(String ... args) throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByExtension("js");
        u.p("engine = " + engine);
        u.p("object = " + engine.eval("4+5"));
        u.p("js = " + engine.eval(new InputStreamReader(SimpleScriptTest.class.getResourceAsStream("test1.js"))));
    }
}
