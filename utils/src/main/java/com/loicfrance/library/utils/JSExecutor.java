package com.loicfrance.library.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import java.util.ArrayList;

/**
 * Created by Loic France on 18/02/2018.<br/><br/>
 *
 * An easy-to-use class that allows an app to create a javascript execution context using an invisible
 * WebView, and dialog with it using JavascriptInterface annotated methods and javascript code
 * lines. It can be used to create apps that allows the user to create scripts that will interact
 * with the application.<br/><br/>
 *
 * Remember that executing javascript can be dangerous for the user if the script is meant to steal
 * data, for example.<br/><br/>
 *
 * Example : <br/><br/>
 * <pre> {@code
 *class JsAlert {
 *     @JavascriptInterface
 *     public void alert(String msg) {
 *         Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
 *     }
 * }
 * JSExecutor jsExec = new JSExecutor(context);
 * jsExec.addInterface(new JsAlert(), "display");
 * jsExec.reset();
 * jsExec.eval("display.alert('Hello World!');"); // will display "Hello World!" in a toast
 *
 * jsExec.eval("function sayHello(name) {display.alert(`Hello ${name}!`);}");
 * jsExec.eval("sayHello('John Doe');");  // will display "Hello John Doe!" in a toast
 *
 * }
 * </pre>
 */

public class JSExecutor {

    private final ArrayList<String> interfaces;
    private WebView webView;

    /**
     * creates an invisible {@link WebView} to use it as a JavaScript execution context.
     * @param context
     */
    public JSExecutor(@NonNull Context context) {

        this.webView = new WebView(context);
        this.interfaces = new ArrayList<>();
    }

    /**
     * completely reset the javascript context. This method needs to be called at the beginning,
     * after adding the interfaces and before running a script.
     */
    public void reset() {
        webView.loadData("", "text/html", null);
    }

    /**
     * evaluate the specified script (in JavaScript language) inside the WebView context.
     * this function must be called only once all the interfaces have been added and the reset
     * method called, otherwise they won't be available in the script
     * @param script The JavaScript code to be executed
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void eval(String script) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.webView.evaluateJavascript(script, null);
        } else {
            this.webView.loadUrl("javascript:" + script);
        }
        webView.getSettings().setJavaScriptEnabled(true);
    }
    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void eval(String script, ValueCallback<String> onResult) {
        this.webView.evaluateJavascript(script, onResult);
        webView.getSettings().setJavaScriptEnabled(true);
    }

    /**
     * Adds javascript methods pointing to java methods. This is the easiest way to let javascript
     * communicate with the java application. To make a java method available in javascript, it has
     * to be annotated with {@link JavascriptInterface}.<br/>
     *
     * This is a one-way communication. To call javascript
     * methods, the java application needs to call the {@link #eval(String)} method with the call
     * to the javascript method in the paramater.<br/>
     *
     * for more details about the
     * {@code object} parameter, consult the {@link WebView#addJavascriptInterface(Object, String)}
     * documentation
     */
    @SuppressLint({"JavascriptInterface", "AddJavascriptInterface"})
    @RequiresApi(17)
    public void addInterface(Object object, String name) {
        this.webView.addJavascriptInterface(object, name);
        this.interfaces.add(name);
    }

    /**
     * Removes the specified interface from the javascript context.
     * @param name The name of the interface to remove
     */
    public void removeInterface(String name) {
        this.webView.removeJavascriptInterface(name);
    }

    /**
     * Removes all previously added interfaces.
     */
    public void clearInterfaces() {
        for (String name : this.interfaces) {
            this.removeInterface(name);
        }
        this.interfaces.clear();
    }

    /**
     * @return the {@link WebView} used as a javascript execution context. You can use this method
     * to make the WebView visible, or call methods that are not available using the
     * {@link JSExecutor} class.
     */
    public WebView getView() {
        return this.webView;
    }

    /**
     * Sets the {@link WebView} to be used as a JavaScript execution context. By default,
     * an invisible one is created. But using this method, the app can specify the one to use
     * @param view The {@link WebView} to use as a JavaScript execution context.
     */
    public void setView(@NonNull WebView view) {
        this.webView = view;
    }
}
