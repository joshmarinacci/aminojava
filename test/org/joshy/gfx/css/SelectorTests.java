package org.joshy.gfx.css;

import org.joshy.gfx.Core;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.control.Scrollbar;
import org.joshy.gfx.util.u;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.parboiled.Node;
import org.parboiled.Parboiled;
import org.parboiled.ReportingParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static junit.framework.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Nov 11, 2010
 * Time: 5:24:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class SelectorTests {
    private CSSRuleSet set;

    @Before
    public void setUp() throws Exception {
        Core.setTesting(true);
        Core.init();
        URI uri = MainCSSTest.class.getResource("test2.css").toURI();
        InputStream css = MainCSSTest.class.getResourceAsStream("test2.css");
        ParsingResult<?> result = parseCSS(css);
        set = new CSSRuleSet();
        condense(result.parseTreeRoot,set,uri);
    }

    @Test
    public void selectorOrderTests() {
        Button button = new Button();
        button.setId("testButton");
        CSSMatcher matcher = new CSSMatcher(button);

        assertTrue(set.findIntegerValue(matcher,"prop1")==1);
        assertTrue(set.findIntegerValue(matcher,"prop2")==12);
        assertTrue(set.findIntegerValue(matcher,"prop3")==23);
        matcher.pseudo = "hover";
        assertTrue(set.findIntegerValue(matcher,"prop4")==4);

        Scrollbar sb = new Scrollbar(false);
        matcher = new CSSMatcher(sb);
        assertTrue(set.findIntegerValue(matcher,"width")==100);
        //sb = new Scrollbar(true);
        //matcher = new CSSMatcher(sb);
        matcher.pseudo = "vertical";
        assertTrue(set.findIntegerValue(matcher,"width")==20);

        matcher = new CSSMatcher(button);
        matcher.pseudo = "hover";
        matcher.pseudoElement = "micro";
        assertTrue(set.findIntegerValue(matcher,"prop5")==8);

        matcher = new CSSMatcher(sb);
        matcher.pseudo = "vertical";
        matcher.pseudoElement = "track";
        u.p("prop 6 = " + set.findIntegerValue(matcher,"prop6"));
        assertTrue(set.findIntegerValue(matcher,"prop6")==23);
        matcher.pseudoElement = "thumb";
        u.p("prop 6 = " + set.findIntegerValue(matcher,"prop6"));
        assertTrue(set.findIntegerValue(matcher,"prop6")==22);

    }

    
    
    /* -------------- support -------------- */
    private static void condense(Node<?> node, CSSRuleSet set, URI uri) {
        if(node == null) return;
        if("CSSRule".equals(node.getLabel())) {
            CSSRule rule = (CSSRule) node.getValue();
            rule.setBaseURI(uri);
            set.append(rule);
        }
        for(Node<?> n : node.getChildren()) {
            condense(n,set,uri);
        }
    }

    private static ParsingResult<?> parseCSS(InputStream css) throws IOException {
        String cssString = toString(css);
        CSSParser parser = Parboiled.createParser(CSSParser.class);
        //System.out.println("string = " + cssString);
        ParsingResult<?> result = ReportingParseRunner.run(parser.RuleSet(), cssString);
        String parseTreePrintOut = ParseTreeUtils.printNodeTree(result);
        //System.out.println(parseTreePrintOut);
        //u.p("other value = " + result.parseTreeRoot.getLabel());
        return result;
    }

    private static String toString(InputStream css) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buff = new byte[256];
        while(true) {
            int n = css.read(buff);
            if(n < 0) break;
            out.write(buff,0,n);
        }
        css.close();
        out.close();
        return new String(out.toByteArray());
    }

    @After
    public void tearDown() throws Exception {
    }
}
