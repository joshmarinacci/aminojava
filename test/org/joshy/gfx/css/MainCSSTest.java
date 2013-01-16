package org.joshy.gfx.css;

import org.joshy.gfx.Core;
import org.joshy.gfx.css.values.BaseValue;
import org.joshy.gfx.css.values.ShadowValue;
import org.joshy.gfx.css.values.StringListValue;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.stage.Stage;
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
import java.net.URISyntaxException;

import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Jul 28, 2010
 * Time: 9:37:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainCSSTest {
    private CSSRuleSet set;

    @Before
    public void setUp() throws Exception {
        Core.setTesting(true);
        Core.init();
        URI uri = MainCSSTest.class.getResource("test1.css").toURI();
        InputStream css = MainCSSTest.class.getResourceAsStream("test1.css");
        ParsingResult<?> result = parseCSS(css);
        set = new CSSRuleSet();
        condense(result.parseTreeRoot,set,uri);
    }

    @Test
    public void basicTests() throws IOException, URISyntaxException {

        //basic matching
        assertTrue(set.findStringValue("button","color").equals("ffff00aa"));
        //fallback to *
        assertTrue(set.findStringValue("label","color").equals("ffff00aa"));
        //parsing a pixel value
        assertTrue(set.findIntegerValue("button","border")==3);

        //get the url
        CSSMatcher matcher = new CSSMatcher("button");
        u.p("uri = " + set.findURIValue(matcher,"icon").getFullURI().toString());
        assertTrue(set.findURIValue(matcher,"icon").getFullURI().toString().endsWith("png"));
        //get the icon position:
        assertTrue("left".equals(set.findStringValue("button","icon-position")));

        //get a url with a - in it
        matcher = new CSSMatcher("button2");
        u.p(" uri = " + set.findURIValue(matcher,"icon"));

        //background-color: transparent
        assertTrue("transparent".equals(set.findStringValue("transptest","background-color")));
        assertTrue(!"transparent".equals(set.findStringValue("transptest","color")));
        //background-color: red turns into #ff0000

        //fonts
        BaseValue fontList = set.findValue(matcher, "font-family");
        assertTrue(fontList instanceof StringListValue);
        u.p(((StringListValue)fontList).getList());
        assertTrue(((StringListValue)fontList).getList().contains("serif"));
        assertTrue("normal".equals(set.findStringValue("label","font-weight")));
        assertTrue("bold".equals(set.findStringValue("button","font-weight")));
        assertTrue(set.findIntegerValue(matcher,"font-size") == 12);



        //tests for id and class matching
        CSSMatcher idMatcher = new CSSMatcher();
        idMatcher.id = "idmatch1";
        //test an ID search
        assertTrue(set.findIntegerValue(idMatcher,"dummy-prop-name") == 87);

        //match by class
        CSSMatcher classMatcher = new CSSMatcher();
        classMatcher.classes.add("classmatch1");
        assertTrue(set.findIntegerValue(classMatcher,"dummy-prop-name") == 88);

        //match by element against a rule with multiple elements
        CSSMatcher multiElementMatcher = new CSSMatcher();
        multiElementMatcher.element = "e1";
        assertTrue(set.findIntegerValue(multiElementMatcher,"border")==3);
        multiElementMatcher.element = "e2";
        assertTrue(set.findIntegerValue(multiElementMatcher,"border")==3);


        idMatcher = new CSSMatcher();
        idMatcher.id = "hex_test";
        u.p("color value = " + Integer.toHexString(set.findColorValue(idMatcher,"background-color")));
        assertTrue(set.findColorValue(idMatcher,"background-color") == 0xffff00ff);

    }

    @Test
    public void advancedClassTests() {

        Button button = new Button();
        button.getCSSClasses().add("class1");
        CSSMatcher matcher = new CSSMatcher(button);
        assertTrue(set.findIntegerValue(matcher,"dummy-prop")==1);

        button.getCSSClasses().add("class2");
        assertTrue(set.findIntegerValue(new CSSMatcher(button),"dummy-prop")==3);
        assertTrue(set.findIntegerValue(new CSSMatcher(button),"dummy-prop2")==10);

        CSSMatcher pseudoTest = new CSSMatcher(button);
        pseudoTest.pseudo = "hover";
        assertTrue(set.findIntegerValue(pseudoTest,"dummy-prop3")==25);
        
    }

    @Test
    public void marginTests() {
        CSSMatcher m = new CSSMatcher();
        m.id = "margin_test_1";
        assertTrue(set.findIntegerValue(m,"margin-top")==1);
        assertTrue(set.findIntegerValue(m,"margin-right")==3);
        assertTrue(set.findIntegerValue(m,"margin-bottom")==5);
        assertTrue(set.findIntegerValue(m,"margin-left")==7);

        m.id = "margin_test_2";
        assertTrue(set.findIntegerValue(m,"margin-top")==1);
        assertTrue(set.findIntegerValue(m,"margin-right")==3);
        assertTrue(set.findIntegerValue(m,"margin-bottom")==5);
        assertTrue(set.findIntegerValue(m,"margin-left")==7);

        m.id = "margin_test_3";
        assertTrue(set.findIntegerValue(m,"margin-top")==9);
        assertTrue(set.findIntegerValue(m,"margin-right")==9);
        assertTrue(set.findIntegerValue(m,"margin-bottom")==9);
        assertTrue(set.findIntegerValue(m,"margin-left")==9);

        m.id = "margin_test_4";
        assertTrue(set.findIntegerValue(m,"margin-top")==10);
        assertTrue(set.findIntegerValue(m,"margin-right")==11);
        assertTrue(set.findIntegerValue(m,"margin-bottom")==10);
        assertTrue(set.findIntegerValue(m,"margin-left")==11);

        m.id = "margin_test_5";
        assertTrue(set.findIntegerValue(m,"margin-top")==10);
        assertTrue(set.findIntegerValue(m,"margin-right")==11);
        assertTrue(set.findIntegerValue(m,"margin-bottom")==12);
        assertTrue(set.findIntegerValue(m,"margin-left")==11);
    }

    @Test
    public void paddingTests() {
        CSSMatcher m = new CSSMatcher();
        m.id = "padding_test_1";
        assertTrue(set.findIntegerValue(m,"padding-top")==1);
        assertTrue(set.findIntegerValue(m,"padding-right")==3);
        assertTrue(set.findIntegerValue(m,"padding-bottom")==5);
        assertTrue(set.findIntegerValue(m,"padding-left")==7);

        m.id = "padding_test_2";
        assertTrue(set.findIntegerValue(m,"padding-top")==1);
        assertTrue(set.findIntegerValue(m,"padding-right")==3);
        assertTrue(set.findIntegerValue(m,"padding-bottom")==5);
        assertTrue(set.findIntegerValue(m,"padding-left")==7);

        m.id = "padding_test_3";
        assertTrue(set.findIntegerValue(m,"padding-top")==9);
        assertTrue(set.findIntegerValue(m,"padding-right")==9);
        assertTrue(set.findIntegerValue(m,"padding-bottom")==9);
        assertTrue(set.findIntegerValue(m,"padding-left")==9);

        m.id = "padding_test_4";
        assertTrue(set.findIntegerValue(m,"padding-top")==10);
        assertTrue(set.findIntegerValue(m,"padding-right")==11);
        assertTrue(set.findIntegerValue(m,"padding-bottom")==10);
        assertTrue(set.findIntegerValue(m,"padding-left")==11);

        m.id = "padding_test_5";
        assertTrue(set.findIntegerValue(m,"padding-top")==10);
        assertTrue(set.findIntegerValue(m,"padding-right")==11);
        assertTrue(set.findIntegerValue(m,"padding-bottom")==12);
        assertTrue(set.findIntegerValue(m,"padding-left")==11);

        m.id = "padding_test_6";
        assertTrue(set.findIntegerValue(m,"padding-top")==6);
        assertTrue(set.findIntegerValue(m,"padding-right")==5);
        assertTrue(set.findIntegerValue(m,"padding-bottom")==5);
        assertTrue(set.findIntegerValue(m,"padding-left")==5);
    }

    @Test
    public void borderTests() {
        CSSMatcher m = new CSSMatcher();
        m.id = "border_test_1";
        assertTrue(set.findIntegerValue(m,"border-top-width")==1);
        assertTrue(set.findIntegerValue(m,"border-right-width")==3);
        assertTrue(set.findIntegerValue(m,"border-bottom-width")==5);
        assertTrue(set.findIntegerValue(m,"border-left-width")==7);

        m.id = "border_test_2";
        assertTrue(set.findIntegerValue(m,"border-top-width")==1);
        assertTrue(set.findIntegerValue(m,"border-right-width")==3);
        assertTrue(set.findIntegerValue(m,"border-bottom-width")==5);
        assertTrue(set.findIntegerValue(m,"border-left-width")==7);

        m.id = "border_test_3";
        assertTrue(set.findIntegerValue(m,"border-top-width")==9);
        assertTrue(set.findIntegerValue(m,"border-right-width")==9);
        assertTrue(set.findIntegerValue(m,"border-bottom-width")==9);
        assertTrue(set.findIntegerValue(m,"border-left-width")==9);

        m.id = "border_test_4";
        assertTrue(set.findIntegerValue(m,"border-top-width")==10);
        assertTrue(set.findIntegerValue(m,"border-right-width")==11);
        assertTrue(set.findIntegerValue(m,"border-bottom-width")==10);
        assertTrue(set.findIntegerValue(m,"border-left-width")==11);

        m.id = "border_test_5";
        assertTrue(set.findIntegerValue(m,"border-top-width")==10);
        assertTrue(set.findIntegerValue(m,"border-right-width")==11);
        assertTrue(set.findIntegerValue(m,"border-bottom-width")==12);
        assertTrue(set.findIntegerValue(m,"border-left-width")==11);

        m.id = "border_test_6";
        m.pseudoElement = "thumb";
        assertTrue(set.findIntegerValue(m,"border-top-width")==10);
        assertTrue(set.findIntegerValue(m,"border-right-width")==11);
        assertTrue(set.findIntegerValue(m,"border-bottom-width")==12);
        assertTrue(set.findIntegerValue(m,"border-left-width")==11);
    }


    @Test
    public void borderRadius() {
        CSSMatcher m = new CSSMatcher();
        m.id = "border_radius_1";
        assertTrue(set.findIntegerValue(m,"border-top-left-radius")==1);
        assertTrue(set.findIntegerValue(m,"border-top-right-radius")==2);
        assertTrue(set.findIntegerValue(m,"border-bottom-right-radius")==3);
        assertTrue(set.findIntegerValue(m,"border-bottom-left-radius")==4);
        m.id = "border_radius_2";
        assertTrue(set.findIntegerValue(m,"border-top-left-radius")==1);
        assertTrue(set.findIntegerValue(m,"border-top-right-radius")==1);
        assertTrue(set.findIntegerValue(m,"border-bottom-right-radius")==1);
        assertTrue(set.findIntegerValue(m,"border-bottom-left-radius")==1);
        m.id = "border_radius_3";
        assertTrue(set.findIntegerValue(m,"border-top-left-radius")==1);
        assertTrue(set.findIntegerValue(m,"border-top-right-radius")==2);
        assertTrue(set.findIntegerValue(m,"border-bottom-right-radius")==1);
        assertTrue(set.findIntegerValue(m,"border-bottom-left-radius")==2);
        m.id = "border_radius_4";
        assertTrue(set.findIntegerValue(m,"border-top-left-radius")==1);
        assertTrue(set.findIntegerValue(m,"border-top-right-radius")==2);
        assertTrue(set.findIntegerValue(m,"border-bottom-right-radius")==3);
        assertTrue(set.findIntegerValue(m,"border-bottom-left-radius")==2);
        m.id = "border_radius_5";
        assertTrue(set.findIntegerValue(m,"border-top-left-radius")==1);
        assertTrue(set.findIntegerValue(m,"border-top-right-radius")==2);
        assertTrue(set.findIntegerValue(m,"border-bottom-right-radius")==3);
        assertTrue(set.findIntegerValue(m,"border-bottom-left-radius")==4);
        m.id = "border_radius_6";
        m.pseudoElement = "thumb";
        assertTrue(set.findIntegerValue(m,"border-top-left-radius")==1);
    }

    @Test
    public void shadow() {
        //shadow
        BaseValue shadow = set.findValue(new CSSMatcher("shadowtest"), "text-shadow");
        assertTrue(shadow instanceof ShadowValue);
        ShadowValue sv = (ShadowValue)shadow;
        assertTrue(sv.getXoffset()==3);
        assertTrue(sv.getYoffset()==2);
        assertTrue(sv.getBlurRadius()==4);
        
        CSSMatcher m = new CSSMatcher();
        m.id = "box_shadow_1";
        assertTrue(set.findValue(m,"box-shadow") instanceof ShadowValue);
        m.id = "text_shadow_1";
        assertTrue(set.findValue(m,"text-shadow") instanceof ShadowValue);
        
    }

    
    @Test
    public void constantTests() {
        CSSMatcher matcher = new CSSMatcher();
        matcher.id = "constant_color_1";
        //test for red
        assertTrue(set.findColorValue(matcher,"prop1")== 0x00ff0000);
        assertTrue(set.findColorValue(matcher,"prop2")== 0x0000ff00);
        assertTrue(set.findColorValue(matcher,"prop3")== 0x000000ff);
    }

    @Test
    public void advancedSelectorTests() {
        Button button = new Button();
        CSSMatcher matcher = new CSSMatcher(button);
        Control panel = new Panel().add(button).setId("idtest1");
        assertTrue(set.findIntegerValue(matcher,"dummy-prop")==78);
        Control panel2 = new Panel().add(panel).setId("idtest2");
        assertTrue(set.findIntegerValue(matcher,"dummy-prop2")==165);


        Stage stage = Stage.createStage();
        stage.setId("window1");
        stage.setContent(button);
        assertTrue(set.findIntegerValue(matcher,"dummy-prop3")==199);
    }

    @Test
    public void colorTests() {
        CSSMatcher matcher = new CSSMatcher();
        matcher.id = "color_rgba_1";
        assertTrue(set.findColorValue(matcher,"prop1")==0xffffffff);
        assertTrue(set.findColorValue(matcher,"prop2")==0xffffffff);
        assertTrue(set.findColorValue(matcher,"prop3")==0xffff8000);
        assertTrue(set.findColorValue(matcher,"prop4")==0x80008000);
        assertTrue(set.findColorValue(matcher,"prop5")==0xFF00FF00);
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
