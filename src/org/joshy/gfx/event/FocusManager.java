package org.joshy.gfx.event;

import java.awt.event.InputMethodEvent;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.Parent;
import org.joshy.gfx.node.control.Focusable;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 26, 2010
 * Time: 9:56:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class FocusManager {
    private Focusable focusedNode;
    private IMETarget ime_target;

    public Node findFocusedNode(Parent parent) {
        if(focusedNode != null) return (Node) focusedNode;
        for(Node node : parent.children()) {
            if(node instanceof Focusable) {
                focusedNode = (Focusable) node;
                return (Node) focusedNode;
            }
            if(node instanceof Parent) {
                findFocusedNode((Parent)node);
            }
        }
        return null;
    }

    public void setFocusedNode(Focusable focusableNode) {
//        u.p("switching focused node to " + focusableNode);
        EventBus.getSystem().publish(new FocusEvent(FocusEvent.Lost, focusedNode));
        focusedNode = focusableNode;
        EventBus.getSystem().publish(new FocusEvent(FocusEvent.Gained, focusedNode));
        if(focusedNode instanceof IMETarget) {
            ime_target = (IMETarget) focusedNode;
        } else {
            ime_target = null;
        }
    }

    public void setIMETarget(IMETarget imeTarget) {
        this.ime_target = imeTarget;
    }


    public Focusable getFocusedNode() {
        return focusedNode;
    }


    public void gotoPrevFocusableNode() {
        if(focusedNode != null) {
            Node root = ((Node) focusedNode).getParent().getStage().getContent();
            List<Node> children = getAllChildren(root);
            Focusable prev = null;
            for(Node n : children) {
                if(n == focusedNode) {
                    setFocusedNode(prev);
                    return;
                }
                if(n instanceof  Focusable) {
                    prev = (Focusable) n;
                }
            }
        }
    }

    private List<Node> getAllChildren(Node root) {
        ArrayList<Node> list = new ArrayList<Node>();
        getAllChildren(root,list);
        return list;
    }

    private void getAllChildren(Node root, ArrayList<Node> list) {
        list.add(root);
        if(root instanceof Parent) {
            Parent parent = (Parent) root;
            for(Node child : parent.children()) {
                getAllChildren(child,list);
            }
        }
    }


    //search through the adjacent siblings for focusable nodes
    public void gotoNextFocusableNode() {
        if(focusedNode != null) {
            Node root = ((Node) focusedNode).getParent().getStage().getContent();
            List<Node> children = getAllChildren(root);
            boolean found = false;
            for(Node n : children) {
                if(found && n instanceof Focusable) {
                    setFocusedNode((Focusable) n);
                    return;
                }
                if(n == focusedNode) {
                    found = true;
                }
            }
        }
    }

    public IMETarget getIMETarget() {
        return ime_target;
    }

    public static interface IMETarget {
        public void setComposingText(InputMethodEvent inputMethodEvent);
        public void appendCommittedText(InputMethodEvent inputMethodEvent);
        public String getCommittedText();
        public final AttributedCharacterIterator.Attribute[] IM_ATTRIBUTES = { TextAttribute.INPUT_METHOD_HIGHLIGHT };
    }

}
