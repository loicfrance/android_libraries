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
import android.support.annotation.NonNull;

import com.loicfrance.library.parsers.ParserObjectFiller;
import com.loicfrance.library.parsers.XmlResParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * An xml parser improved to parse the file in a {@link Node} hierarchy.
 * Created by Loic France on 27/10/2015.
 */
public class HierarchyParser<T> implements ParserObjectFiller<Node<T>> {
    public static String NODE_NAME = "node";
    private NodeObjectFiller<T> filler;
    private Context context;
    private XmlResParser parser;

    public HierarchyParser(Context context) {
        this.context = context;
        parser = new XmlResParser(context);
    }

    public Node<T> parse(int xmlResId, NodeObjectFiller<T> filler) throws IOException, XmlPullParserException {
        this.filler = filler;
        return parser.parse(xmlResId, this);
    }

    @Override
    @NonNull
    public Node<T> getRoot() {
        return new Node<>(null);
    }

    @Override
    @NonNull
    public Node<T> getObject(String tagName) {
        return new Node<>(null);
    }

    @Override
    public void newAttrib(Node<T> obj, String attrName, String attrValue) {
        obj.obj = filler.fill(context, obj.obj, attrName, attrValue);
    }

    @Override
    public void newAttrib(Node<T> obj, String attrName, int attrResId) {
        obj.obj = filler.fill(context, obj.obj, attrName, attrResId);
    }

    @Override
    public void addChild(Node<T> parent, Node<T> child) { parent.addChild(child); }

    @Override
    public void text(Node<T> obj, String text) {
    }

    @Override
    public void objectClose(Node<T> obj) {
    }

    @Override
    public boolean skip(String tagName) { return !tagName.equals(NODE_NAME); }

    public interface NodeObjectFiller<T> {
        T fill(Context context, T obj, String attrName, String attrValue);

        T fill(Context context, T obj, String attrName, int attrResId);
    }
}
