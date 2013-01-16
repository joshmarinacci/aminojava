package org.joshy.gfx.util;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Jun 23, 2010
 * Time: 8:06:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class OSUtil {
    private OSUtil() {
    }

    public static boolean isMac() {
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Mac OS")) {
            return true;
        }
        return false;
    }

    public static boolean isJava6() {
        return (System.getProperty("java.version").startsWith("1.6"));
    }

    public static String getClipboardAsString() {
      String result = "";
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      //odd: the Object param of getContents is not currently used
      Transferable contents = clipboard.getContents(null);
      boolean hasTransferableText =
        (contents != null) &&
        contents.isDataFlavorSupported(DataFlavor.stringFlavor)
      ;
      if ( hasTransferableText ) {
        try {
          result = (String)contents.getTransferData(DataFlavor.stringFlavor);
        }
        catch (UnsupportedFlavorException ex){
          //highly unlikely since we are using a standard DataFlavor
          System.out.println(ex);
          ex.printStackTrace();
        }
        catch (IOException ex) {
          System.out.println(ex);
          ex.printStackTrace();
        }
      }
      return result;
    }

    public static void setStringToClipboard(String selectedText) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection content = new StringSelection(selectedText);
        clipboard.setContents(content, new ClipboardOwner(){
            @Override
            public void lostOwnership(Clipboard clipboard, Transferable transferable) {
                u.p("clipboard lost ownership");
            }
        });
    }

    public static File getJavaWSExecutable() {
        if(isMac()) {
            File javaws6 = new File("/System/Library/Frameworks/JavaVM.framework/Versions/1.6/Home/bin/javaws");
            if(javaws6.exists()) {
                return javaws6;
            }
            return new File("/System/Library/Frameworks/JavaVM.framework/Versions/1.5/Home/bin/javaws");
        }
        return new File(System.getProperty("java.home"),"bin/javaws");
    }


    // launching code from http://www.centerkey.com/java/browser/
    public static void openBrowser(String url) {
        String os = System.getProperty("os.name");
        String osName = System.getProperty("os.name");
        try {
            if (osName.startsWith("Mac OS")) {
                Class fileMgr = Class.forName("com.apple.eio.FileManager");
                Method openURL = fileMgr.getDeclaredMethod("openURL",
                        new Class[]{String.class});
                openURL.invoke(null, new Object[]{url});
            } else if (osName.startsWith("Windows")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else { //assume Unix or Linux
                String[] browsers = {
                    "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"};
                String browser = null;
                for (int count = 0; count < browsers.length && browser == null; count++) {
                    if (Runtime.getRuntime().exec(
                            new String[]{"which", browsers[count]}).waitFor() == 0) {
                        browser = browsers[count];
                    }
                }
                if (browser == null) {
                    throw new Exception("Could not find web browser");
                } else {
                    Runtime.getRuntime().exec(new String[]{browser, url});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getBaseStorageDir(String appName) {
        String os = System.getProperty("os.name").toLowerCase();
        StringBuffer filepath = new StringBuffer(System.getProperty("user.home"));
        if(os.indexOf("windows xp") != -1) {
            filepath.append(File.separator);
            filepath.append("Local Settings");
            filepath.append(File.separator);
            filepath.append("Application Data");
            filepath.append(File.separator);
            filepath.append(appName);
            filepath.append(File.separator);
        } else if (os.indexOf("vista") != -1) {
            filepath.append(File.separator);
            filepath.append("appdata");
            filepath.append(File.separator);
            filepath.append("locallow");
            filepath.append(File.separator);
            filepath.append(appName);
            filepath.append(File.separator);
        } else if (os.startsWith("mac")) {
            filepath.append(File.separator);
            filepath.append("Library");
            filepath.append(File.separator);
            filepath.append("Preferences");
            filepath.append(File.separator);
            filepath.append(appName);
            filepath.append(File.separator);
        } else {
            //if we don't know what OS it is then just use user.home followed by a .
            filepath.append(File.separator);
            filepath.append(".");
            filepath.append(appName);
            filepath.append(File.separator);
        }
        System.out.println("final base storage dir = " + filepath.toString());
      return filepath.toString();
    }
}