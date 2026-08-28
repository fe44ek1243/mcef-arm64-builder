package com.mcef.arm64.wrapper;

/**
 * Abstract interface for browser implementations
 * Provides unified API for both MCEF (desktop) and WebView (Android)
 */
public abstract class WebViewBrowser {
    protected String url;
    protected String browserId;
    protected int width = 1024;
    protected int height = 768;
    
    public WebViewBrowser(String url) {
        this.url = url;
    }
    
    public void setId(String id) {
        this.browserId = id;
    }
    
    public String getId() {
        return browserId;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    // Abstract methods
    public abstract void navigate(String url) throws Exception;
    public abstract String getHTML() throws Exception;
    public abstract void setHTML(String html) throws Exception;
    public abstract void executeJS(String code) throws Exception;
    public abstract Object executeJSWithReturn(String code) throws Exception;
    public abstract void click(int x, int y) throws Exception;
    public abstract void scroll(int dx, int dy) throws Exception;
    public abstract void type(String text) throws Exception;
    public abstract void keyDown(int keyCode) throws Exception;
    public abstract void keyUp(int keyCode) throws Exception;
    public abstract void close() throws Exception;
}
