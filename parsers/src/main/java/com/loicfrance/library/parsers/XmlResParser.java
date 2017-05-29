package com.loicfrance.library.parsers;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import com.loicfrance.library.utils.LogD;

/**
 * a class to parse resources xml files.
 * Created by Loic France on 27/10/2015.
 */
public class XmlResParser {
    private static final String LOG_TAG = "XML_PARSER";
    private Resources resources;
    private XmlResourceParser parser;
    public XmlResParser(Context context) {
        this.resources = context.getResources();
    }
    public <T> T parse(int xmlResId, ParserObjectFiller<T> filler) throws IOException, XmlPullParserException {
        LogD.d(LOG_TAG, "parsing resource " + xmlResId);
        T root = filler.getRoot();
        parser = resources.getXml(xmlResId);
        parser.next();
        int eventType = parser.getEventType();
        while(eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_DOCUMENT) {
                LogD.d(LOG_TAG, "--- Start Xml ---");
            }
            else if(eventType == XmlPullParser.START_TAG) {
                String tagName = parser.getName();
                if(filler.skip(tagName)) skip();
                else filler.addChild(root, parseObject(tagName, filler));
            }
        }
        return root;
    }
    public <T> T parseObject(String name, ParserObjectFiller<T> filler) throws IOException, XmlPullParserException {
        LogD.d(LOG_TAG, "parsing object tagged " + name);
        T obj = filler.getObject(name);
        if(parser.isEmptyElementTag()) return obj;
        for(int i=0; i< parser.getAttributeCount(); i++) {
            String attrName = parser.getAttributeName(i);
            String attrValue = parser.getAttributeValue(i);
            LogD.d(LOG_TAG, "attribute " + attrName + ": " + attrValue);
            int attrResId = parser.getAttributeResourceValue(i, -1);
            if(attrResId != -1) {
                LogD.d(LOG_TAG, "corresponding resource id: " + attrResId);
                filler.newAttrib(obj, attrName, attrResId);
            }
            else filler.newAttrib(obj, attrName, parser.getAttributeValue(i));
        }
        int eventType = parser.next();
        while(eventType != XmlPullParser.END_TAG && eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.TEXT) {
                String text = parser.getText();
                LogD.d(LOG_TAG, "text : " + text);
                filler.text(obj, text);
            }
            else if(eventType == XmlPullParser.START_TAG) {
                LogD.d(LOG_TAG, "found child");
                filler.addChild(obj, parseObject(parser.getName(), filler));
            }
        }
        LogD.d(LOG_TAG, "object end");
        filler.objectClose(obj);
        return obj;
    }

    private void skip() throws IOException, XmlPullParserException {
        if(parser.isEmptyElementTag())  {
            parser.next();
            return;
        }
        int eventType = parser.next();
        int level = 1;
        while(level > 0 && eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_TAG) {
                level++;
            }
            else if(eventType == XmlPullParser.END_TAG) {
                level--;
            }
            eventType = parser.next();
        }
    }
}
