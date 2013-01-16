package org.joshy.gfx.css;

import org.joshy.gfx.css.values.BaseValue;
import org.joshy.gfx.css.values.ColorValue;
import org.joshy.gfx.css.values.IntegerPixelValue;
import org.joshy.gfx.css.values.URLValue;
import org.joshy.gfx.node.Parent;
import org.joshy.gfx.node.control.Control;

import java.util.ArrayList;
import java.util.List;

/**  Represents an entire set of CSS rules. Usually it maps to a single .css file.
 */
public class CSSRuleSet {
    private List<CSSRule> rules;// = new ArrayList<CSSRule>();
    private static final boolean DEBUG = false;
    private List<CSSMatcher> idMatchers;
    private List<CSSMatcher> classMatchers;
    private List<CSSMatcher> classWithPseudoMatchers;
    private List<CSSMatcher> classWithPseudoElementMatchers;
    private List<CSSMatcher> otherMatchers;
    private List<CSSRule> reverseRules;

    public CSSRuleSet() {
        rules = new ArrayList<CSSRule>();
        reverseRules = new ArrayList<CSSRule>();
        idMatchers = new ArrayList<CSSMatcher>();
        classMatchers = new ArrayList<CSSMatcher>();
        classWithPseudoMatchers = new ArrayList<CSSMatcher>();
        classWithPseudoElementMatchers = new ArrayList<CSSMatcher>();
        otherMatchers = new ArrayList<CSSMatcher>();
    }

    public void append(CSSRuleSet set) {
        for(CSSRule r : set.rules) {
            append(r);
        }
    }
    public void append(CSSRule rule) {
        rules.add(rule);
        reverseRules.add(0,rule);
        for(CSSMatcher m : rule.matchers) {
            if(m.id != null) {
                idMatchers.add(0,m);
            } else if(!m.classes.isEmpty()) {
                if(m.pseudoElement != null) {
                    classWithPseudoElementMatchers.add(0,m);
                } else if(m.pseudo != null) {
                    classWithPseudoMatchers.add(0,m);
                } else {
                    classMatchers.add(0,m);
                }
            } else {
                otherMatchers.add(0,m);
            }
        }
    }

    public String findStringValue(String elem, String propName) {
        return findMatchingRule(new CSSMatcher(elem),propName).value.asString();
    }
    public String findStringValue(CSSMatcher matcher, String propName) {
        CSSProperty property = findMatchingRule(matcher, propName);
        if(property == null) return null;
        return property.value.asString();
    }

    public CSSProperty findMatchingRule(List<CSSMatcher> matchers, CSSMatcher elem, String propName) {
        for(CSSMatcher matcher : matchers) {
            if(matches(matcher,elem)) {
                for(CSSProperty prop : matcher.rule.getProperties()) {
                    if(prop.name.equals(propName)) {
                        if(prop.value instanceof URLValue) {
                            URLValue uv = (URLValue) prop.value;
                            uv.baseURI = matcher.rule.getBaseURI();
                        }
                        return prop;
                    }
                }
            }
        }
        return null;
    }
    
    public CSSProperty findMatchingRule(CSSMatcher elem, String propName) {
        CSSProperty rule;

        //go through ID rules
        rule = findMatchingRule(idMatchers, elem, propName);
        if(rule != null) {
            return rule;
        }

        //go through class w/ pseudo element rules
        rule = findMatchingRule(classWithPseudoElementMatchers, elem, propName);
        if(rule != null) {
            return rule;
        }

        //go through class w/ pseudo rules
        rule = findMatchingRule(classWithPseudoMatchers, elem, propName);
        if(rule != null) {
            return rule;
        }

        //go through class rules
        rule = findMatchingRule(classMatchers, elem, propName);
        if(rule != null) return rule;

        //go through element rules
        rule = findMatchingRule(otherMatchers, elem, propName);
        if(rule != null) return rule;
        return null;
    }

    private boolean matches(CSSMatcher matcher, CSSMatcher elem) {

        //match pseudo class
        if(matcher.pseudo != null) {
            if(matchPseudo(matcher,elem)) {
                return true;
            }
            return false;
        }

        if(matcher.pseudoElement != null) {
            if(matchPseudoElement(matcher,elem)) {
                return true;
            } else {
                return false;
            }
        }

        if(matcher.id != null) {
            if(matcher.id.equals(elem.id)) {
                return true;
            }
        }

        if(matcher.element != null) {
            if(matcher.element.equals(elem.element) && matcher.pseudo == null) {
                return true;
            }
        }

        for(String c1 : matcher.classes) {
            for(String c2 : elem.classes) {
                if(c1.equals(c2)) {
                    if(matcher.parent != null) {
                        return checkParent(matcher.parent,elem);
                    } else {
                        return true;
                    }
                }
            }
        }

        if("*".equals(matcher.element)) {
            return true;
        }
        return false;
    }

    private boolean matchPseudoElement(CSSMatcher matcher, CSSMatcher elem) {
        if(!matcher.pseudoElement.equals(elem.pseudoElement)) return false;

        for(String c1 : matcher.classes) {
            if(!elem.classes.contains(c1)) {
                return false;
            }
        }

        if(matcher.element != null) {
            if(!matcher.element.equals(elem.element)) {
                return false;
            }
        }
        return true;
    }

    private boolean matchPseudo(CSSMatcher matcher, CSSMatcher elem) {
        if(!matcher.pseudo.equals(elem.pseudo)) {
            return false;
        }

        if(matcher.pseudoElement != null) {
            if(!matcher.pseudoElement.equals(elem.pseudoElement)) {
                return false;
            }
        }

        for(String c1 : matcher.classes) {
            if(!elem.classes.contains(c1)) {
                return false;
            }
        }

        if(matcher.element != null) {
            if(!matcher.element.equals(elem.element)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkParent(CSSMatcher matcher, CSSMatcher elem) {
        if(elem.control == null) return false;
        Parent parent = elem.control.getParent();
        if(parent == null) return false;
        if(!(parent instanceof Control)) return false;

        CSSMatcher pmatch = new CSSMatcher((Control) parent);
        if(matches(matcher, pmatch)) {
            return true;
        } else {
            return checkParent(matcher, pmatch);
        }
    }

    public int findIntegerValue(String elemName, String propName) {
        CSSProperty prop = findMatchingRule(new CSSMatcher(elemName), propName);
        if(prop == null) return 0;
        int v = ((IntegerPixelValue)prop.value).getValue();
        return v;
    }

    public int findColorValue(CSSMatcher matcher, String propName) {
        CSSProperty prop = findMatchingRule(matcher,propName);
        if(prop == null) {
            System.out.println("Couldn't find property " + propName + " for " + matcher);
            return 0;
        }
        return ((ColorValue)prop.value).getValue();
    }

    public BaseValue findValue(CSSMatcher matcher, String propName) {
        CSSProperty prop = findMatchingRule(matcher,propName);
        if(prop == null) return null;
        return prop.value;
    }

    public URLValue findURIValue(CSSMatcher matcher, String propName) {
        CSSProperty prop = findMatchingRule(matcher,propName);
        if(prop == null) return null;
        return ((URLValue)prop.value);
    }

    public int findIntegerValue(CSSMatcher matcher, String propName) {
        CSSProperty prop = findMatchingRule(matcher,propName);
        if(prop == null) return -1;
        return ((IntegerPixelValue)prop.value).getValue();
    }

    public int rulesCount() {
        return rules.size();
    }
}
