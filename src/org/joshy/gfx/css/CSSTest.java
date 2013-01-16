package org.joshy.gfx.css;

import org.parboiled.Node;
import org.parboiled.Parboiled;
import org.parboiled.ReportingParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Jul 28, 2010
 * Time: 2:32:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class CSSTest {

    public static void main(String ... args) throws IOException {
        File file = new File("test1.css");
        p("file = " + file.getAbsolutePath());
        ParsingResult<?> result = parseCSS(new FileInputStream("test1.css"));
        CSSRuleSet set = new CSSRuleSet();
        printIt(result.parseTreeRoot,set);

        p("button color = " + set.findStringValue("button","color"));
        p("label color = "  + set.findStringValue("label","color"));
        p("button border = " + set.findIntegerValue("button","border"));
    }

    private static ParsingResult<?> parseCSS(InputStream css) throws IOException {
        String cssString = toString(css);
        CSSParser parser = Parboiled.createParser(CSSParser.class);
        System.out.println("string = " + cssString);
        ParsingResult<?> result = ReportingParseRunner.run(parser.RuleSet(), cssString);
        String parseTreePrintOut = ParseTreeUtils.printNodeTree(result);
        System.out.println(parseTreePrintOut);
        p("other value = " + result.parseTreeRoot.getLabel());
        return result;
    }

    private static void printIt(Node<?> node, CSSRuleSet set) {
        if("CSSRule".equals(node.getLabel())) {
            p("rule: " + node.getValue());
            CSSRule rule = (CSSRule) node.getValue();
            set.append(rule);
            p("rule = " + rule.matchers);
            for(CSSProperty prop : rule.getProperties()) {
                p("print " + prop.name + " = " + prop.value);
            }
        }
        if("PropertyRule".equals(node.getLabel())) {
            //p("rule: " + node.getValue());
        }

//        if("PropertyName".equals(node.getLabel())) {
//            p("name = " + node.getValue());
//        }
//        if("PropertyValue".equals(node.getLabel())) {
//            p("value = " + node.getValue());
//        }
        for(Node<?> n : node.getChildren()) {
            printIt(n,set);
        }
    }

    public static void p(String s) {
        System.out.println(s);
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
}
