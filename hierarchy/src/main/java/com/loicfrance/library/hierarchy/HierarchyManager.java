/*
 * Copyright 2018 RichardFrance
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom
 * the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.loicfrance.library.hierarchy;

import android.content.Context;

import com.loicfrance.library.utils.LogD;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Loic France on 21/10/2015.
 */
public class HierarchyManager<T> {
    protected Node<T> root;
    protected Node<T> current;

//__________________________________________________________________________________________________Constructors
//################################################################################################## 3

    public HierarchyManager(T rootObject) {
        root = Node.root(rootObject, null);
        current = root;
    }

    public HierarchyManager() {
        root = Node.root(null, null);
        current = root;
    }

    public HierarchyManager(Context context, int xmlRes, HierarchyParser.NodeObjectFiller filler) throws IOException, XmlPullParserException {
//        root = (new HierarchyXMLParser<T>().parse(context, xmlRes, filler));
        root = (new HierarchyParser<T>(context)).parse(xmlRes, filler);
        root = root.getChildrenNodes().get(0);
        root.setRoot();
//        root = (new HierarchyXMLParser<T>().parse_v2(context.getResources().openRawResource(xmlRes), filler));
        current = root;
    }

    public HierarchyManager(Context context, int xmlRes, HierarchyXMLParser.ObjectFiller filler) throws IOException, XmlPullParserException {
//        root = (new HierarchyXMLParser<T>().parse_v2(context.getResources().openRawResource(xmlRes), filler));
        if (root == null)
            root = (new HierarchyXMLParser<T>()).parse_v2(context, xmlRes, filler);
        if (root == null)
            LogD.e("HierarchyManager", "error parsing xml resource : no hierarchy found");
        current = root;
    }
//__________________________________________________________________________________________________Functions
//##################################################################################################

//__________________________________________________________________________________________________current node functions
//-------------------------------------------------------------------------------------------------- 7

    public T getObj() {
        return current.obj;
    }

    public Node<T> getNode() {
        return current;
    }

    public List<T> getPath() {
        List<Node<T>> nodePath = getNodePath();
        List<T> result = new ArrayList<>();
        for (Node<T> n : nodePath) {
            result.add(n.obj);
        }
        nodePath.clear();
        return result;
    }

    public List<Node<T>> getNodePath() {
        List<Node<T>> result = new ArrayList<>();
        root.getPathTo(current, result);
        return result;
    }

    public int getIndexInParent() {
        return current.getIndexInParent();
    }

    public boolean isRoot() {
        return current.isRoot();
    }

    public boolean isLeaf() {
        return current.isLeaf();
    }

//__________________________________________________________________________________________________children nodes functions
//-------------------------------------------------------------------------------------------------- 6

    public int getChildCount() {
        return current.getChildCount();
    }

    public T getChildObj(int index) {
        if (index >= getChildCount()) return null;
        return current.children.get(index).obj;
    }

    public void addChild(T obj) {
        Node.leaf(current, obj);
    }

    public boolean removeChild(T obj) {
        Node<T> n = current.getNode(obj, true);
        if (n == null) return false;
        current.removeChild(n);
        return true;
    }

    public T removeChild(int index) {
        if (index >= getChildCount()) return null;
        return current.children.remove(index).obj;
    }

    public List<T> getChildren() {
        List<Node<T>> list = current.getChildrenNodes();
        List<T> result = new ArrayList<>();
        for (Node<T> n : list) {
            result.add(n.obj);
        }
        return result;
    }

    public boolean isChildParent(int childIndex) {
        return !current.getChildrenNodes().get(childIndex).isLeaf();
    }

//__________________________________________________________________________________________________movement in hierarchy functions
//-------------------------------------------------------------------------------------------------- 4

    public boolean goToChildAt(int index) {
        if (index >= getChildCount()) return false;
        current = current.children.get(index);
        return true;
    }

    public boolean goToParent() {
        if (isRoot()) return false;
        current = current.parent;
        return true;
    }

    public void goToRoot() {
        current = root;
    }

    public boolean removeAbsolute(T obj) {
        Node<T> n = root.getNode(obj);
        if (n == null) return false;
        Node<T> parent = n.parent;
        parent.removeChild(n);
        return true;
    }

//__________________________________________________________________________________________________global hierarchy functions
//-------------------------------------------------------------------------------------------------- 4

    public T getRootObj() {
        return root.obj;
    }

    public Node<T> getRootNode() {
        return root;
    }

    public boolean checkNoLoop() {
        return !current.hasChild(current, false);
    }
}
