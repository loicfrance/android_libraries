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
