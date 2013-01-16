package org.joshy.gfx.util.xml;

import com.joshondesign.xml.Doc;
import com.joshondesign.xml.XMLParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.joshy.gfx.event.BackgroundTask;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.util.u;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: Apr 8, 2010
* Time: 5:12:51 PM
* To change this template use File | Settings | File Templates.
*/
public class XMLRequest extends BackgroundTask<String, Doc> {
    private Throwable error;

    public enum METHOD { GET, POST };

    private URL url;
    private Map<String,String> parameters;
    private String username;
    private String password;
    private Callback<Doc> callback;
    private Callback<Throwable> errorCallback;
    private METHOD method = METHOD.GET;
    private boolean multiPart = false;
    private boolean useUserPass = false;
    private File file;
    private boolean done = false;

    public XMLRequest() {
        parameters = new HashMap<String,String>();
    }

    public XMLRequest onComplete(Callback<Doc> callback) {
        this.callback = callback;
        return this;
    }

    public XMLRequest onError(Callback<Throwable> callback) {
        this.errorCallback = callback;
        return this;
    }

    public XMLRequest setURL(String url) throws MalformedURLException {
        this.url = new URL(url);
        return this;
    }

    public XMLRequest setUserPass(String username, String password) {
        this.username = username;
        this.password = password;
        useUserPass = true;
        return this;
    }

    public XMLRequest setMethod(METHOD method) {
        this.method = method;
        return this;
    }

    public XMLRequest setParameter(String name, String value) {
        parameters.put(name,value);
        return this;
    }

    public XMLRequest setMultiPart(boolean b) {
        multiPart = b;
        return this;
    }

    public XMLRequest setFile(File file) {
        this.file = file;
        return this;
    }


    @Override
    protected Doc onWork(String data) {
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            //disable expect-continue because it breaks twitter and some proxies;
            httpclient.getParams().setBooleanParameter("http.protocol.expect-continue",Boolean.FALSE);
            if(useUserPass) {
                httpclient.getCredentialsProvider().setCredentials(
                        new AuthScope(url.getHost(),url.getPort()),
                        new UsernamePasswordCredentials(username,password)
                );
            }

            HttpRequestBase request = null;
            if(method == METHOD.GET) {
                StringBuffer query = new StringBuffer();
                for(String key : parameters.keySet()) {
                    query.append(key+"="+parameters.get(key)+"&");
                }
                URI origUri = url.toURI();
                URI finalUri = new URI(
                        origUri.getScheme()
                        ,origUri.getUserInfo()
                        ,origUri.getHost()
                        ,origUri.getPort()
                        ,origUri.getPath()
                        ,query.toString()
                        ,origUri.getFragment());
                request = new HttpGet(finalUri);
            }
            if(method == METHOD.POST) {
                HttpPost postrequest = new HttpPost(url.toURI());
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                for(String key : parameters.keySet()) {
                    nameValuePairs.add(new BasicNameValuePair(key,parameters.get(key)));
                }
                if(multiPart) {
                    MultipartEntity entity = new MultipartEntity();
                    for(String key : parameters.keySet()) {
                        entity.addPart(key, new StringBody(parameters.get(key), Charset.forName("UTF-8")));
                    }
                    if(file != null) {
                        entity.addPart("media",new FileBody(file));
                    }
                    postrequest.setEntity(entity);
                } else {
                    postrequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                }
                request = postrequest;
            }

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            Doc doc = XMLParser.parse(entity.getContent());
            httpclient.getConnectionManager().shutdown();
            return doc;
        } catch (Throwable e) {
            u.p(e);
            this.error = e;
        }
        return null;
    }


    @Override
    protected void onEnd(Doc document) {
        try {
            done = true;
            if(error != null && errorCallback != null) {
                errorCallback.call(error);
            } else {
                if(callback == null) return;
                callback.call(document);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean isDone() {
        return done;
    }

}
