package org.joshy.gfx.event;

import org.joshy.gfx.node.Node;
import org.joshy.gfx.util.OSUtil;

import java.util.HashSet;
import java.util.Set;

public class KeyEvent extends Event {
    private boolean shiftPressed;
    private boolean controlPressed;
    private boolean altPressed;
    private boolean commandPressed;
    private boolean accepted = false;
    private char keyChar;

    public boolean isAccepted() {
        return accepted;
    }

    public void accept() {
        this.accepted = true;
    }

    public boolean isSystemPressed() {
        if(OSUtil.isMac()) {
            return isCommandPressed();
        }
        return isControlPressed();
    }

    public boolean isSystemShortcut() {
        if(isCommandPressed() && OSUtil.isMac()) return true;
        if(isControlPressed() && !OSUtil.isMac()) return true;
        return false;
    }


    public static enum KeyCode {
        //letters
        KEY_A('a'),KEY_B('b'),KEY_C('c'),KEY_D('d'),KEY_E('e'),KEY_F('f'),KEY_G('g'),
        KEY_H('h'),KEY_I('i'),KEY_J('j'),KEY_K('k'),KEY_L('l'),KEY_M('m'),KEY_N('n'),
        KEY_O('o'),KEY_P('p'),KEY_Q('q'),KEY_R('r'),KEY_S('s'),KEY_T('t'),KEY_U('u'),
        KEY_V('v'),KEY_W('w'),KEY_X('x'),KEY_Y('y'),KEY_Z('z'),

        // top row / numbers
        KEY_BACKQUOTE('`'),
        KEY_1('1'),KEY_2('2'),KEY_3('3'),KEY_4('4'),KEY_5('5'),KEY_6('6'),KEY_7('7'),KEY_8('8'),KEY_9('9'),KEY_0('0'),
        KEY_MINUS('-'), KEY_EQUALS('='),

        //punctuation and spaces
        KEY_SPACE(' '), KEY_SLASH('/'), KEY_PERIOD('.'), KEY_COMMA(','), KEY_QUOTE('\''), KEY_SEMICOLON(';'),
        KEY_BRACKETLEFT('['), KEY_BRACKETRIGHT(']'), KEY_BACKSLASH('\\'),

        //modifier keys
        KEY_SHIFT(-1), KEY_CONTROL(-1), KEY_ALT(-1), KEY_META(-1),

        //editing & navigation keys
        KEY_BACKSPACE(-1), KEY_DELETE(-1), KEY_TAB(-1), KEY_ENTER(-1),
        KEY_DOWN_ARROW(-1), KEY_UP_ARROW(-1), KEY_LEFT_ARROW(-1), KEY_RIGHT_ARROW(-1),
        KEY_ESCAPE(-1),

        KEY_UNKNOWN(-1);

        KeyCode(int ch) {
            this.ch = ch;
        }

        int ch;
        public char getChar() {
            return (char)ch;
        }
    }
    private KeyCode keyCode;
    private static Set<KeyCode> textKeys;

    static {
        textKeys = new HashSet<KeyCode>();
        for(KeyCode code : KeyCode.values()) {
            if(code.ch >= 'a' && code.ch <= 'z') {
                textKeys.add(code);
            }
        }

        textKeys.add(KeyCode.KEY_BACKQUOTE);
        textKeys.add(KeyCode.KEY_1);
        textKeys.add(KeyCode.KEY_2);
        textKeys.add(KeyCode.KEY_3);
        textKeys.add(KeyCode.KEY_4);
        textKeys.add(KeyCode.KEY_5);
        textKeys.add(KeyCode.KEY_6);
        textKeys.add(KeyCode.KEY_7);
        textKeys.add(KeyCode.KEY_8);
        textKeys.add(KeyCode.KEY_9);
        textKeys.add(KeyCode.KEY_0);
        textKeys.add(KeyCode.KEY_MINUS);
        textKeys.add(KeyCode.KEY_EQUALS);

        textKeys.add(KeyCode.KEY_SPACE);
        textKeys.add(KeyCode.KEY_COMMA);
        textKeys.add(KeyCode.KEY_PERIOD);
        textKeys.add(KeyCode.KEY_SLASH);
        textKeys.add(KeyCode.KEY_SEMICOLON);
        textKeys.add(KeyCode.KEY_QUOTE);
        textKeys.add(KeyCode.KEY_BRACKETLEFT);
        textKeys.add(KeyCode.KEY_BRACKETRIGHT);
        textKeys.add(KeyCode.KEY_BACKSLASH);
    }
    
    public static final EventType KeyPressed = new EventType("KeyPressed");
    public static final EventType KeyReleased = new EventType("KeyReleased");
    public static final EventType KeyTyped = new EventType("KeyTyped");
    public static final EventType KeyAll = new EventType("KeyAll") {
        @Override
        public boolean matches(EventType type) {
            if(type == KeyPressed) return true;
            if(type == KeyReleased) return true;
            if(type == KeyTyped) return true;
            return super.matches(type);
        }
    };

    public KeyEvent(EventType type,
                    KeyCode keyCode,
                    Node node,
                    char keyChar,
                    boolean shiftPressed,
                    boolean controlPressed,
                    boolean altPressed,
                    boolean commandPressed) {
        super(type);
        this.keyChar = keyChar;
        this.keyCode = keyCode;
        this.source = node;
        this.shiftPressed = shiftPressed;
        this.controlPressed = controlPressed;
        this.altPressed = altPressed;
        this.commandPressed = commandPressed;
    }

    public KeyCode getKeyCode() {
        return keyCode;
    }

    public char getKeyChar() {
        return keyChar;
    }


    public boolean isShiftPressed() {
        return shiftPressed;
    }

    public boolean isControlPressed() {
        return controlPressed;
    }

    public boolean isAltPressed() {
        return altPressed;
    }

    public boolean isCommandPressed() {
        return commandPressed;
    }


    @Override
    public String toString() {
        return type + " code="+keyCode;
    }

    public static KeyCode getKeyCodeFromChar(char ch) {
//        u.p("ch = " + ch);
        if(ch >= 'a') {
            ch-=32;
        }
//        u.p("ch = " + ch);
        switch(ch) {
            case 'A' : return KeyCode.KEY_A;
            case 'B' : return KeyCode.KEY_B;
            case 'C' : return KeyCode.KEY_C;
            case 'D' : return KeyCode.KEY_D;
            case 'E' : return KeyCode.KEY_E;
            case 'F' : return KeyCode.KEY_F;
            case 'G' : return KeyCode.KEY_G;
            case 'H' : return KeyCode.KEY_H;
            case 'I' : return KeyCode.KEY_I;
            case 'J' : return KeyCode.KEY_J;
            case 'K' : return KeyCode.KEY_K;
            case 'L' : return KeyCode.KEY_L;
            case 'M' : return KeyCode.KEY_M;
            case 'N' : return KeyCode.KEY_N;
            case 'O' : return KeyCode.KEY_O;
            case 'P' : return KeyCode.KEY_P;
            case 'Q' : return KeyCode.KEY_Q;
            case 'R' : return KeyCode.KEY_R;
            case 'S' : return KeyCode.KEY_S;
            case 'T' : return KeyCode.KEY_T;
            case 'U' : return KeyCode.KEY_U;
            case 'V' : return KeyCode.KEY_V;
            case 'W' : return KeyCode.KEY_W;
            case 'X' : return KeyCode.KEY_X;
            case 'Y' : return KeyCode.KEY_Y;
            case 'Z' : return KeyCode.KEY_Z;
        }
        return null;
    }
    public static KeyCode getKeyCodeFromAWT(int awt) {
//        u.p("converting awt " + awt + " to ");
        switch(awt) {
            case 'A' : return KeyCode.KEY_A;
            case 'B' : return KeyCode.KEY_B;
            case 'C' : return KeyCode.KEY_C;
            case 'D' : return KeyCode.KEY_D;
            case 'E' : return KeyCode.KEY_E;
            case 'F' : return KeyCode.KEY_F;
            case 'G' : return KeyCode.KEY_G;
            case 'H' : return KeyCode.KEY_H;
            case 'I' : return KeyCode.KEY_I;
            case 'J' : return KeyCode.KEY_J;
            case 'K' : return KeyCode.KEY_K;
            case 'L' : return KeyCode.KEY_L;
            case 'M' : return KeyCode.KEY_M;
            case 'N' : return KeyCode.KEY_N;
            case 'O' : return KeyCode.KEY_O;
            case 'P' : return KeyCode.KEY_P;
            case 'Q' : return KeyCode.KEY_Q;
            case 'R' : return KeyCode.KEY_R;
            case 'S' : return KeyCode.KEY_S;
            case 'T' : return KeyCode.KEY_T;
            case 'U' : return KeyCode.KEY_U;
            case 'V' : return KeyCode.KEY_V;
            case 'W' : return KeyCode.KEY_W;
            case 'X' : return KeyCode.KEY_X;
            case 'Y' : return KeyCode.KEY_Y;
            case 'Z' : return KeyCode.KEY_Z;

            case java.awt.event.KeyEvent.VK_BACK_QUOTE : return KeyCode.KEY_BACKQUOTE;
            case java.awt.event.KeyEvent.VK_1 : return KeyCode.KEY_1;
            case java.awt.event.KeyEvent.VK_2 : return KeyCode.KEY_2;
            case java.awt.event.KeyEvent.VK_3 : return KeyCode.KEY_3;
            case java.awt.event.KeyEvent.VK_4 : return KeyCode.KEY_4;
            case java.awt.event.KeyEvent.VK_5 : return KeyCode.KEY_5;
            case java.awt.event.KeyEvent.VK_6 : return KeyCode.KEY_6;
            case java.awt.event.KeyEvent.VK_7 : return KeyCode.KEY_7;
            case java.awt.event.KeyEvent.VK_8 : return KeyCode.KEY_8;
            case java.awt.event.KeyEvent.VK_9 : return KeyCode.KEY_9;
            case java.awt.event.KeyEvent.VK_0 : return KeyCode.KEY_0;
            case java.awt.event.KeyEvent.VK_MINUS : return KeyCode.KEY_MINUS;
            case java.awt.event.KeyEvent.VK_EQUALS : return KeyCode.KEY_EQUALS;

            case java.awt.event.KeyEvent.VK_SHIFT : return KeyCode.KEY_SHIFT;
            case java.awt.event.KeyEvent.VK_CONTROL : return KeyCode.KEY_CONTROL;
            case java.awt.event.KeyEvent.VK_ALT : return KeyCode.KEY_ALT;
            case java.awt.event.KeyEvent.VK_META : return KeyCode.KEY_META;
            case java.awt.event.KeyEvent.VK_BACK_SPACE : return KeyCode.KEY_BACKSPACE;
            case java.awt.event.KeyEvent.VK_DELETE : return KeyCode.KEY_DELETE;
            case java.awt.event.KeyEvent.VK_TAB : return KeyCode.KEY_TAB;
            case java.awt.event.KeyEvent.VK_DOWN : return KeyCode.KEY_DOWN_ARROW;
            case java.awt.event.KeyEvent.VK_UP : return KeyCode.KEY_UP_ARROW;
            case java.awt.event.KeyEvent.VK_LEFT : return KeyCode.KEY_LEFT_ARROW;
            case java.awt.event.KeyEvent.VK_RIGHT : return KeyCode.KEY_RIGHT_ARROW;
            case java.awt.event.KeyEvent.VK_SPACE : return KeyCode.KEY_SPACE;
            case java.awt.event.KeyEvent.VK_ENTER : return KeyCode.KEY_ENTER;
            case java.awt.event.KeyEvent.VK_ESCAPE : return KeyCode.KEY_ESCAPE;

            case java.awt.event.KeyEvent.VK_SLASH : return KeyCode.KEY_SLASH;
            case java.awt.event.KeyEvent.VK_PERIOD : return KeyCode.KEY_PERIOD;
            case java.awt.event.KeyEvent.VK_COMMA : return KeyCode.KEY_COMMA;
            case java.awt.event.KeyEvent.VK_QUOTE : return KeyCode.KEY_QUOTE;
            case java.awt.event.KeyEvent.VK_SEMICOLON : return KeyCode.KEY_SEMICOLON;
            case java.awt.event.KeyEvent.VK_OPEN_BRACKET : return KeyCode.KEY_BRACKETLEFT;
            case java.awt.event.KeyEvent.VK_CLOSE_BRACKET : return KeyCode.KEY_BRACKETRIGHT;
            case java.awt.event.KeyEvent.VK_BACK_SLASH : return KeyCode.KEY_BACKSLASH;

        }
        return KeyCode.KEY_UNKNOWN;
    }
    
    public boolean isTextKey() {
        if(textKeys.contains(getKeyCode())) {
            return true;
        }
        return false;
    }

    
    public String getGeneratedText() {
        if(getType() == KeyTyped) {
            return ""+keyChar;
        }
        if(isTextKey()) {
            char ch = keyCode.getChar();
//            u.p("char code = " + ch);
            if(isShiftPressed() && ch >= 'a' && ch <= 'z') {
//                u.p("a shifted letter");
                ch = (char) (ch-32);
            }
            if(isShiftPressed()) {
                switch(ch) {
                    case ',': return "<";
                    case '.': return ">";
                    case '/': return "?";
                    case ';': return ":";
                    case '\'': return "\"";
                    case '[': return "{";
                    case ']': return "}";
                    case '\\': return "|";

                    case '`' : return "~";
                    case '0': return ")";
                    case '1': return "!";
                    case '2': return "@";
                    case '3': return "#";
                    case '4': return "$";
                    case '5': return "%";
                    case '6': return "^";
                    case '7': return "&";
                    case '8': return "*";
                    case '9': return "(";
                    case '-': return "_";
                    case '=': return "+";
                }

            }
            return ""+ch;
        }
        return "";
    }

}
