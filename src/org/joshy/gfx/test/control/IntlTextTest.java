package org.joshy.gfx.test.control;

import org.joshy.gfx.util.u;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.im.InputMethodRequests;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.CharacterIterator;

public class IntlTextTest {
    public static void main(String ... args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new IntlTextTest();
            }
        });
    }

    public IntlTextTest() {
        JFrame frame = new JFrame();
        MyJComponent comp = new MyJComponent();
        comp.requestFocus();
        frame.add(comp);
        frame.pack();
        frame.setSize(640,480);
        frame.setVisible(true);

    }

    private static class MyJComponent extends JComponent {
        private InputMethodRequests imr;
        private InputMethodListener iml;
        private AttributedString composingText;
        private StringBuffer committedText;
        private TextHitInfo caret;

        private MyJComponent() {
            committedText = new StringBuffer();
            this.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent keyEvent) {
                    u.p("key typed: " + keyEvent);
                    char keyChar = keyEvent.getKeyChar();
                    if(keyChar == '\b') {
                        int len = committedText.length();
                        if(len > 0) {
                            committedText.setLength(len-1);
                            repaint();
                        }
                    } else {
                        committedText.append(keyChar);
                        repaint();
                    }
                }

                @Override
                public void keyPressed(KeyEvent keyEvent) {
                    //u.p("key pressed: " + keyEvent);
                }

                @Override
                public void keyReleased(KeyEvent keyEvent) {
                    //u.p("key released: " + keyEvent);
                }
            });
            this.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent focusEvent) {
                }

                @Override
                public void focusLost(FocusEvent focusEvent) {
                }
            });

            this.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent mouseEvent) {
                }

                @Override
                public void mousePressed(MouseEvent mouseEvent) {
                    u.p("mouse pressed: " + mouseEvent);
                    requestFocus();
                }

                @Override
                public void mouseReleased(MouseEvent mouseEvent) {
                }

                @Override
                public void mouseEntered(MouseEvent mouseEvent) {
                }

                @Override
                public void mouseExited(MouseEvent mouseEvent) {
                }
            });
            iml = new InputMethodListener(){
                @Override
                public void inputMethodTextChanged(InputMethodEvent inputMethodEvent) {
                    u.p("input method text changed: " + inputMethodEvent);
                    u.p("caret = " + inputMethodEvent.getCaret());
                    u.p("count = " + inputMethodEvent.getCommittedCharacterCount());
                    u.p("text = " + inputMethodEvent.getText());
                    setComposingText(inputMethodEvent);
                    appendCommittedText(inputMethodEvent);

                    //dump(inputMethodEvent.getText());
                    inputMethodEvent.consume();
                    caret = inputMethodEvent.getCaret();
                }

                @Override
                public void caretPositionChanged(InputMethodEvent inputMethodEvent) {
                    u.p("caret position changed: " + inputMethodEvent);
                    caret = inputMethodEvent.getCaret();
                    inputMethodEvent.consume();
                    repaint();
                }
            };
            addInputMethodListener(iml);
            imr = new InputMethodRequests(){
                @Override
                public Rectangle getTextLocation(TextHitInfo textHitInfo) {
                    u.p("get text location called: " + textHitInfo);
                    return new Rectangle(60,60,100,100);
                }

                @Override
                public TextHitInfo getLocationOffset(int i, int i1) {
                    u.p("get location offset called");
                    return null;
                }

                @Override
                public int getInsertPositionOffset() {
                    u.p("get insert position offset called");
                    return 0;
                }

                @Override
                public AttributedCharacterIterator getCommittedText(int i, int i1, AttributedCharacterIterator.Attribute[] attributes) {
                    u.p("get committed text called: " + i + " " + i1 + " " + attributes);
                    return new AttributedString(committedText.toString()).getIterator();
                }

                @Override
                public int getCommittedTextLength() {
                    u.p("get committed text length called");
                    return committedText.length();
                }

                @Override
                public AttributedCharacterIterator cancelLatestCommittedText(AttributedCharacterIterator.Attribute[] attributes) {
                    u.p("cancel latest committed text called " + attributes);
                    return null;  //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                public AttributedCharacterIterator getSelectedText(AttributedCharacterIterator.Attribute[] attributes) {
                    u.p("get selected text caleld: " + attributes);
                    return (new AttributedString("")).getIterator();
                }
            };
            enableInputMethods(true);
        }

        private static final AttributedCharacterIterator.Attribute[] IM_ATTRIBUTES = { TextAttribute.INPUT_METHOD_HIGHLIGHT };

        private void appendCommittedText(InputMethodEvent inputMethodEvent) {
            int count = inputMethodEvent.getCommittedCharacterCount();
            //u.p("appending committed text : " + count + " chars");
            AttributedCharacterIterator text = inputMethodEvent.getText();
            char c = text.first();
            while(count > 0) {
                committedText.append(c);
                c = text.next();
                count--;
            }
            u.p("committed text now = " + committedText.toString());
        }

        private void setComposingText(InputMethodEvent ime) {
            AttributedCharacterIterator text = ime.getText();
            if(text.getEndIndex() - (text.getBeginIndex()+ime.getCommittedCharacterCount()) > 0) {
                u.p("there is some composing text right now: ");
                composingText = new AttributedString(
                        text,
                        text.getBeginIndex()+ime.getCommittedCharacterCount(),
                        text.getEndIndex(),
                        IM_ATTRIBUTES
                );
            }
            repaint();
        }

        private void dump(AttributedCharacterIterator text) {
            u.p("dummping attr text: " + text);
            StringBuffer sb = new StringBuffer();
            for(char c = text.first(); c != CharacterIterator.DONE; c = text.next()) {
                u.p(" char = " + c + " " + Integer.toHexString((int)c));
                sb.append(c);
            }
            u.p("result = " + sb.toString());
        }

        @Override
        public InputMethodRequests getInputMethodRequests() {
            return imr;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            graphics = graphics.create();
            graphics.setColor(Color.RED);
            graphics.fillRect(0, 0, getWidth(), getHeight());
            graphics.setColor(Color.BLACK);
            graphics.setFont(graphics.getFont().deriveFont(25f));

            //draw the regular text
            int len = committedText.length();
            graphics.drawString(committedText.toString(),20,30);

            graphics.drawLine(15,32,500,32);

            if(composingText != null) {
                int i=0;
                graphics.setColor(Color.GRAY);
                graphics.fillRoundRect(40,40,300,200,30,30);
                graphics.setColor(Color.WHITE);
                graphics.setFont(graphics.getFont().deriveFont(60f));
                AttributedCharacterIterator it = composingText.getIterator();
                for(char c = it.first(); c != CharacterIterator.DONE; c=it.next()) {
                    graphics.drawString(""+c,60+i*60,120);
                    i++;
                }
                //graphics.drawString(composingText,20,20);
            }
            graphics.dispose();
        }

    }

}
