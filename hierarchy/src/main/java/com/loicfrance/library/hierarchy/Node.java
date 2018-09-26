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

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Loic France on 28/09/2015.
 */
public class Node<T> {
    public T obj;
    public Node<T> parent;
    public List<Node<T>> children;

//__________________________________________________________________________________________________Constructors
//################################################################################################## 4

    public Node(Node<T> parent, T obj, Node<T>[] children) {
        if (children != null) addChildren(children);
        this.obj = obj;
        setParentNode(parent);
    }

    public Node(Node<T> parent, T obj, Collection<Node<T>> children) {
        if (children != null) addChildren(children);
        this.obj = obj;
        setParentNode(parent);
    }

    public Node(T obj) {
        this.obj = obj;
    }

    public  static <E> Node<E> root(E obj, Node<E>[] children) {
        try {
            return new Node<>(null, obj, children);
        } catch (ClassCastException e) {
            Log.e("HierarchyNode", "children element do not have the same type as obj element: " +
                    "obj class: " + obj.getClass().getName() + ", children hierarchyNode class : " +
                    children[0].getClass().getName() + ".");
            throw e;
        }
    }

    public static <E> Node<E> leaf(Node<E> parent, E obj) {
        try {
            return new Node<>(parent, obj, (Node<E>[]) null);
        } catch (ClassCastException e) {
            Log.e("HierarchyNode", "children element do not have the same type as obj element: " +
                    "obj class: " + obj.getClass().getName() + ", parent hierarchyNode class : " +
                    parent.getClass().getName() + ".");
            throw e;
        }
    }


    //__________________________________________________________________________________________________children functions
//################################################################################################## 11
    public boolean hasChild(Node<T> node, boolean directChild) {
        if (isLeaf()) return false;
        if (directChild) return children != null && children.contains(node);
        boolean result = false;
        for (Node<T> child : children) {
            if (child.hasChild(node, true)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public void addChild(Node<T> child) {
        if (isLeaf()) children = new ArrayList<>();
        if (!children.contains(child)) {
            children.add(child);
            if (child.getParentNode() != this)
                child.setParentNode(this);
        }
    }

    @SafeVarargs
    public final void addChildren(Node<T>... children) {
        if (isLeaf()) this.children = new ArrayList<>();
        Collections.addAll(this.children, children);
        for (Node<T> child : children) {
            if (!equals(child.parent))
                child.setParentNode(this);
        }
    }

    public void addChildren(Collection<Node<T>> children) {
        if (isLeaf()) this.children = new ArrayList<>();
        this.children.addAll(children);
        for (Node<T> child : children) {
            if (!equals(child.parent))
                child.setParentNode(this);
        }
    }

    public void removeChildren(Collection<Node<T>> children) {
        if (isLeaf()) return;
        this.children.removeAll(children);
        for (Node<T> child : children) {
            if (equals(child.parent))
                child.setRoot();
        }
        if (this.children.isEmpty()) this.children = null;
    }

    @SafeVarargs
    public final void removeChildren(Node<T>... children) {
        if (isLeaf()) return;
        for (Node<T> child : children) {
            removeChild(child);
        }
        if (this.children.isEmpty()) this.children = null;
    }

    public void removeChild(Node<T> child) {
        if (children.remove(child)) {
            child.setRoot();
            if (this.children.isEmpty()) this.children = null;
        }
    }

    public List<Node<T>> getChildrenNodes() {
        return children;
    }

    public boolean isLeaf() {
        return children == null;
    }

    public void setLeaf() {
        removeChildren(children);
    }

    public int getAltitude() {
        if (isLeaf()) return 0;
        int childMax = 0;
        for (Node<T> child : children) {
            childMax = Math.max(child.getAltitude(), childMax);
        }
        return childMax + 1;
    }


//__________________________________________________________________________________________________parent functions
//################################################################################################## 6


    public Node<T> getParentNode() {
        return parent;
    }

    public void setParentNode(Node<T> parent) {
        this.parent = parent;
        if (parent != null && !parent.hasChild(this, true))
            parent.addChild(this);
    }

    public Node<T> getRootNode() {
        return isRoot() ? this : parent.getRootNode();
    }

    public boolean isRoot() {
        return parent == null;
    }

    public void setRoot() {
        setParentNode(null);
    }

    public int getDepth() {
        return isRoot() ? 0 : parent.getDepth() + 1;
    }


//__________________________________________________________________________________________________hierarchy functions
//################################################################################################## 4


    public void getPathTo(Node<T> node, @NonNull List<Node<T>> dest) {
        if (this.equals(node)) {
            dest.add(this);
            return;
        }
        if (hasChild(node, false)) {
            dest.add(this);
            for (Node<T> child : children) {
                if (hasChild(node, false)) {
                    child.getPathTo(node, dest);
                    break;
                }
            }
        }
    }

    public Node<T> getNode(T obj) {
        return getNode(obj, false);
    }

    public Node<T> getNode(T obj, boolean directChild) {
        if (this.obj.equals(obj)) return this;
        if (isLeaf()) return null;
        else if (!directChild) {
            Node<T> result = null;
            for (Node<T> child : children) {
                result = child.getNode(obj);
                if (result != null) break;
            }
        }
        return null;
    }

    public void getAbsolutePathTo(Node<T> node, @NonNull List<Node<T>> dest) {
        if (this.equals(node)) {
            getRootNode().getPathTo(this, dest);
        } else getRootNode().getAbsolutePathTo(node, dest);
    }

    public int getIndexInParent() {
        if (isRoot()) return -1;
        else return getParentNode().getChildrenNodes().indexOf(this);
    }

    public int getChildCount() {
        if (isLeaf()) return 0;
        else return children.size();
    }


//__________________________________________________________________________________________________@Overridden functions
//################################################################################################## 4


    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Node)) return false;
        Node hn = (Node) other;
        boolean result = (obj == hn.obj) ||
                (obj != null && obj.equals(hn.obj));
        if (result && isLeaf()) result = hn.isLeaf();
        if (result && isRoot()) result = hn.isRoot();
        return result && (isRoot() || parent == hn.parent) && (isLeaf() || children == hn.children);
    }


}
