package org.joshy.gfx.util.localization;

import com.joshondesign.xml.Doc;
import com.joshondesign.xml.Elem;
import com.joshondesign.xml.XMLParser;
import org.joshy.gfx.util.u;

import java.net.URL;
import java.util.*;

public class Localization {
    private static String masterLocaleName;
    private static HashMap<String, KeyString> translations;

    public static void setCurrentLocale(String locale) {
        masterLocaleName = locale;
    }

    public static void init(URL translationStore, String localeName) throws Exception {
        masterLocaleName = localeName;
        translations = new HashMap<String,KeyString>();
        Doc doc = XMLParser.parse(translationStore.openStream());
        for(Elem set : doc.xpath("//set")) {
            String prefix = set.attr("name");
            for(Elem key : set.xpath("key")) {
                String keyName = key.attr("name");
                KeyString keyString = new KeyString(prefix,keyName);
                for(Elem value : key.xpath("value")) {
                    String language = value.attr("language");
                    String translationValue = value.text().trim();
                    if(language.length() <= 0) {
                        language = "DEFAULT";
                    }
                    keyString.addTranslation(language,translationValue);
                }
                translations.put(prefix+"."+keyName,keyString);
            }
        }
        
    }

    public static CharSequence getString(String key) {
        if(!translations.containsKey(key)) {
            u.p("ERROR!!  key '" + key + "' not found!");
            return "--";
        }
        KeyString v = translations.get(key);
        return v;
    }



    public static Set<String> getAllKeys() {
        return translations.keySet();
    }

    public static KeyString getKeyString(String key) {
        return translations.get(key);
    }

    public static List<String> getSupportedLocales() {
        List<String> list = new ArrayList<String>();
        final Set<String> keys = Localization.getAllKeys();
        for(String dsKey : keys) {
            Localization.KeyString ks = Localization.getKeyString(dsKey);
            for(String lang : ks.translations.keySet()) {
                if(!list.contains(lang)) {
                    list.add(lang);
                }
            }
        }
        return list;
    }

    public static class KeyString implements CharSequence {
        //map of locale to value
        public Map<String,String> translations = new HashMap<String, String>();
        private String prefix;
        private String keyName;

        public KeyString(String prefix, String keyName) {
            this.prefix = prefix;
            this.keyName = keyName;
        }

        public void addTranslation(String language, String translationValue) {
            translations.put(language,translationValue);
        }

        public void setTranslation(String lang, String value) {
            translations.put(lang,value);
        }

        @Override
        public int length() {
            return getLocalizedValue().length();
        }

        private String getLocalizedValue() {
            //u.p("getting: "+ Localization.masterLocaleName + " from " + translations);
            if(translations.containsKey(Localization.masterLocaleName)) {
                return translations.get(Localization.masterLocaleName);
            }
            return translations.get("DEFAULT");
        }

        @Override
        public char charAt(int i) {
            return getLocalizedValue().charAt(i);
        }

        @Override
        public CharSequence subSequence(int i, int i1) {
            return getLocalizedValue().subSequence(i,i1);
        }

        public String getPrefix() {
            return prefix;
        }

        public String getKeyname() {
            return keyName;
        }

        public String toString() {
            return getLocalizedValue();
        }

    }
    
    public static class DynamicString implements CharSequence {
        private String key;
        private String prefix;
        private String locale;
        private String value;

        public DynamicString(String prefix, String key, String locale, String value) {
            this.prefix = prefix;
            this.key = key;
            this.locale = locale;
            this.value = value;
        }

        public int length() {
            return getValue().length();
        }

        public char charAt(int i) {
            return getValue().charAt(i);
        }

        public CharSequence subSequence(int i, int i1) {
            return getValue().subSequence(i,i1);
        }

        @Override
        public String toString() {
            return getValue();
        }


        public String getValue() {
            return value;
        }

        public String getPrefix() {
            return prefix;
        }

        public String getKey() {
            return key;
        }

        public String getLang() {
            return this.locale;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
