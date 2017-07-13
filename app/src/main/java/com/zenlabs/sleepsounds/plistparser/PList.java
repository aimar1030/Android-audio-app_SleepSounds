package com.zenlabs.sleepsounds.plistparser;

import java.util.Stack;

/**
 * Created by fedoro on 5/12/16.
 */
public class PList {

    private PListObject root;
    private boolean stackCtxInDict;
    private boolean stackCtxInArray;
    private int stackCtxNestedDepth;
    private Stack<PListObject> stack;

    public PList() {

        stackCtxInDict = false;
        stackCtxInArray = false;
        stackCtxNestedDepth = 0;
        stack = new Stack<PListObject>();
    }

    public PListObject getRootElement() {
        return root;
    }

    public void setRootElement(PListObject root) {
        this.root = root;
    }

    private void attachPListObjToParent(PListObject obj, String key) {
        if (stackCtxInArray) {

            attachPListObjToArrayParent(stack, obj);
        } else if (stackCtxInDict) {

            attachPListObjToDictParent(obj, key);
        } else if (stackCtxNestedDepth == 0) {

            setRootElement(obj);
        }
    }

    private void attachPListObjToDictParent(PListObject obj,
                                            String key) {

        Dict parent = (Dict) stack.pop();
        parent.putConfig(key, obj);
        stack.push(parent);
    }

    private void attachPListObjToArrayParent(Stack<PListObject> stack,
                                             PListObject obj) {

        Array parent = (Array) stack.pop();
        parent.add(obj);
        stack.push(parent);
    }

    public void stackObject(PListObject obj, String key)
            throws Exception {
        if (null == key && stackCtxInDict) {
            throw new Exception(
                    "PList objects with Dict parents require a key.");
        }
        if (stackCtxNestedDepth > 0 && !stackCtxInDict && !stackCtxInArray) {

            throw new Exception(
                    "PList elements that are not at the root should have an Array or Dict parent.");
        }
        switch (obj.getType()) {
            case DICT:
                attachPListObjToParent(obj, key);
                stack.push(obj);
                stackCtxInArray = false;
                stackCtxInDict = true;
                stackCtxNestedDepth++;
                break;
            case ARRAY:
                attachPListObjToParent(obj, key);
                stack.push(obj);
                stackCtxInArray = true;
                stackCtxInDict = false;
                stackCtxNestedDepth++;
                break;
            default:
                attachPListObjToParent(obj, key);
        }
    }

    public PListObject popStack() {
        if (stack.isEmpty()) {
            return null;
        }
        PListObject ret = stack.pop();
        stackCtxNestedDepth--;
        if (!stack.isEmpty()) {
            switch (stack.lastElement().getType()) {
                case DICT:
                    stackCtxInArray = false;
                    stackCtxInDict = true;
                    break;
                case ARRAY:
                    stackCtxInArray = true;
                    stackCtxInDict = false;
                    break;
                default:
                    break;
            }
        } else {
            stackCtxInArray = false;
            stackCtxInDict = false;
        }
        return ret;
    }

    public PListObject buildObject(String tag, String value)
            throws Exception {
        if (null == tag) {
            throw new Exception(
                    "Cannot add a child with a null tag to a PList.");
        }
        PListObject ret = null;
        if (tag.equalsIgnoreCase(Constants.TAG_INTEGER)) {
            ret = new Integer();
            ((Integer) ret).setValue(value);
        } else if (tag.equalsIgnoreCase(Constants.TAG_STRING)) {
            ret = new MyString();
            ((MyString) ret).setValue(value);
        } else if (tag.equalsIgnoreCase(Constants.TAG_REAL)) {
            ret = new Real();
            ((Real) ret).setValue(value);
        } else if (tag.equalsIgnoreCase(Constants.TAG_DATE)) {
            ret = new Date();
            ((Date) ret).setValue(value);
        } else if (tag.equalsIgnoreCase(Constants.TAG_BOOL_FALSE)) {
            ret = new False();
        } else if (tag.equalsIgnoreCase(Constants.TAG_BOOL_TRUE)) {
            ret = new True();
        } else if (tag.equalsIgnoreCase(Constants.TAG_DATA)) {
            ret = new Data();
            ((Data) ret).setValue(value.trim(), true);
        } else if (tag.equalsIgnoreCase(Constants.TAG_DICT)) {
            ret = new Dict();
        } else if (tag.equalsIgnoreCase(Constants.TAG_PLIST_ARRAY)) {
            ret = new Array();
        }
        return ret;
    }

    @Override
    public String toString() {
        if (null == root) {
            return null;
        }
        return root.toString();
    }

}
