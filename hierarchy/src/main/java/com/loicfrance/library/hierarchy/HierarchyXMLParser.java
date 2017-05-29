package com.loicfrance.library.hierarchy;

import android.content.Context;
import android.util.Xml;


import com.loicfrance.library.utils.LogD;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Loic France on 19/10/2015 from developer tutorial at
 * http://developer.android.com/training/basics/network-ops/xml.html
 */
public class HierarchyXMLParser<T> {

    private static String namespace = null;

    public Node<T> parse(Context context, int xmlResId, ObjectFiller<T> filler) throws XmlPullParserException, IOException {
        XmlPullParser parser = context.getResources().getXml(xmlResId);
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        return parse(parser, filler);
    }

    public Node<T> parse(InputStream in, ObjectFiller<T> filler) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            return parse(parser, filler);
        } finally {
            in.close();
        }
    }

    private Node<T> parse(XmlPullParser parser, ObjectFiller<T> filler) throws XmlPullParserException, IOException {
        parser.nextTag();
        return readNode(parser, filler);
    }

    private Node<T> readNode(XmlPullParser parser, ObjectFiller<T> filler) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, namespace, "node");
        Node<T> currentNode = Node.root((T)null, (Node<T>[])null);
        String tag, value;
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            tag = parser.getAttributeName(i);
            value = parser.getAttributeValue(i);
            currentNode.obj = filler.fill(currentNode.obj, i, tag, value, parser);
        }
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("node")) {
                currentNode.addChild(readNode(parser, filler));
            } else skip(parser);
        }
        return currentNode;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }

    }

    public Node<T> parse_v2(InputStream in, ObjectFiller<T> filler) throws XmlPullParserException, IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        StringBuilder total = new StringBuilder();
        String line;
        int lineNb = 0;
        while ((line = r.readLine()) != null) {
            lineNb++;
            LogD.d("HierarchyXMLParser", "line " + lineNb + ": " + line);
            total.append(line);
        }
        LogD.d("HierarchyXMLParser", "parsing xml file : \n\n" + total + "\n\n");
        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        parser.setInput(in, null);
        int event = parser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
            if (event == XmlPullParser.START_TAG) {
                return readNode(parser, filler);
            }
            event = parser.next();
        }
        return null;
    }

    public Node<T> parse_v2(Context context, int xmlResId, ObjectFiller<T> filler) throws XmlPullParserException, IOException {
        XmlPullParser parser = context.getResources().getXml(xmlResId);
        int event = parser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
            if (event == XmlPullParser.START_TAG) {
                return readNode(parser, filler);
            }
            event = parser.next();
        }
        return null;
    }

    public interface ObjectFiller<T> {
        T fill(T t, int attrIndex, String attrName, String attrText, XmlPullParser parser);
    }

}
