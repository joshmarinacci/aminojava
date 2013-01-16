package org.joshy.gfx.sidehatch;

import com.joshondesign.xml.XMLWriter;
import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.ActionEvent;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.SelectionEvent;
import org.joshy.gfx.node.control.*;
import org.joshy.gfx.node.layout.HFlexBox;
import org.joshy.gfx.node.layout.VFlexBox;
import org.joshy.gfx.util.ArrayListModel;
import org.joshy.gfx.util.control.StandardDialogs;
import org.joshy.gfx.util.localization.Localization;
import org.joshy.gfx.util.u;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TranslationEditor<E> extends VFlexBox {
    private ArrayListModel<Prefix> prefixList;
    private HashMap<String, Prefix> prefixes;
    private ArrayListModel<String> currentLocaleModel;
    
    private ListView<Key> keyView = new ListView<Key>();
    private ListView<Lang> langView = new ListView<Lang>();
    private Textbox editBox = new Textbox();
    private PopupMenuButton<String> currentLocalePopup;

    public TranslationEditor() {

        editBox.setEnabled(false);
        editBox.setPrefWidth(200);

        processKeys();
        
        final ListView<Prefix> prefixView = new ListView<Prefix>();
        prefixView.setModel(ListUtil.toAlphaListModel(prefixList));
        keyView.setModel(new ArrayListModel<Key>());
        langView.setModel(new ArrayListModel<Lang>());

        langView.setRenderer(new ListView.ItemRenderer<Lang>() {

            @Override
            public void draw(GFX gfx, ListView listView, Lang item, int index, double x, double y, double width, double height) {
                if(item != null) {
                    boolean selected = listView.getSelectedIndex() == index;
                    gfx.setPaint(FlatColor.WHITE);
                    if(selected) {
                        gfx.setPaint(FlatColor.BLUE);
                    }
                    if(!item.isReal) {
                        gfx.setPaint(FlatColor.WHITE);
                        if(selected) {
                            gfx.setPaint(new FlatColor(0xff8888));
                        }
                    }
                    gfx.fillRect(x,y,width,height);


                    gfx.setPaint(FlatColor.BLACK);
                    if(selected) {
                        gfx.setPaint(FlatColor.WHITE);
                    }
                    if(!item.isReal) {
                        gfx.setPaint(FlatColor.RED);
                        if(selected) {
                            gfx.setPaint(FlatColor.BLACK);
                        }
                    }
                    gfx.drawText(item.realLang, Font.DEFAULT, x+5, y+12);
                }
            }
        });


        currentLocalePopup = new PopupMenuButton<String>()
                .setModel(ListUtil.toAlphaListModel(currentLocaleModel));

        EventBus.getSystem().addListener(SelectionEvent.Changed, new Callback<SelectionEvent>(){
            public void call(SelectionEvent selectionEvent) throws Exception {
                if(selectionEvent.getView() instanceof ListView) {
                    if(selectionEvent.getView() == prefixView) {
                        Prefix pf = prefixView.getModel().get(selectionEvent.getView().getSelectedIndex());
                        keyView.setModel(ListUtil.toAlphaListModel(pf.keys));
                        keyView.setSelectedIndex(-1);
                        langView.setSelectedIndex(-1);
                        editBox.setText("");
                        editBox.setEnabled(false);
                    }
                    if(selectionEvent.getView() == keyView) {
                        if(keyView.getSelectedIndex() <0) return;
                        Key key = keyView.getModel().get(keyView.getSelectedIndex());
                        ArrayListModel<Lang> m = new ArrayListModel<Lang>();
                        for(String l : key.keyString.translations.keySet()) {
                            m.add(new Lang(l,true));
                        }
                        //include stubs for the missing locales
                        for(String locale : currentLocaleModel) {
                            boolean found = false;
                            for(Lang l : m) {
                                if(l.realLang.equals(locale)) {
                                    found = true;
                                    break;
                                }
                            }
                            if(!found) {
                                m.add(new Lang(locale,false));
                            }
                        }
                        langView.setModel(ListUtil.toAlphaListModel(m));
                        langView.setSelectedIndex(-1);
                        editBox.setText("");
                        editBox.setEnabled(false);
                    }
                    if(selectionEvent.getView() == langView) {
                        if(keyView.getSelectedIndex() < 0) return;
                        Key key = keyView.getModel().get(keyView.getSelectedIndex());
                        if(langView.getSelectedIndex() < 0) return;
                        Lang lang = langView.getModel().get(langView.getSelectedIndex());
                        String val = key.keyString.translations.get(lang.realLang);
                        u.p("val = " + val);
                        if(val != null) {
                            editBox.setText(val);
                        } else {
                            editBox.setText(key.keyString.translations.get("DEFAULT"));
                        }
                        editBox.setEnabled(true);
                        editBox.selectAll();

                    }
                }
                if(selectionEvent.getView() == currentLocalePopup) {
                    String locale = currentLocalePopup.getModel().get(currentLocalePopup.getSelectedIndex());
                    Localization.setCurrentLocale(locale);
                    Core.getShared().reloadSkins();
                }
            }
        });

        this.setBoxAlign(Align.Stretch);
        this.add(new HFlexBox()
                .add(new Label("Current Locale"))
                .add(currentLocalePopup)
                .add(new Button("Add Locale").onClicked(addLocale))
            ,0);
        this.add(new HFlexBox()
                .setBoxAlign(Align.Stretch)
                .add(new VFlexBox()
                        .add(new Label("Category"),0)
                        .add(new ScrollPane(prefixView)
                                .setHorizontalVisiblePolicy(ScrollPane.VisiblePolicy.Never)
                                .setPrefWidth(150),1)
                )
                .add(new VFlexBox()
                        .add(new Label("Key"),0)
                        .add(new ScrollPane(keyView)
                                .setHorizontalVisiblePolicy(ScrollPane.VisiblePolicy.Never)
                                .setPrefWidth(150),1)
                )
                .add(new VFlexBox()
                    .add(new Label("Locale"))
                    .add(new ScrollPane(langView)
                            .setHorizontalVisiblePolicy(ScrollPane.VisiblePolicy.Never)
                            .setPrefWidth(100)
                            .setPrefHeight(100),1)
                    //.add(new Button("Add Lang").onClicked(addLangAction))
                    //.add(new Button("Del Lang"))
                )
                .add(new VFlexBox()
                    .add(new Label("Translation"))
                    .add(editBox.onAction(setString))
                    .add(new Button("Set").onClicked(setString))
                )
            ,1);

        Button exportButton = new Button("export").onClicked(exportTranslation);

        this.add(new HFlexBox()
            .add(exportButton));
    }

    Callback<ActionEvent> addLocale = new Callback<ActionEvent>() {

        @Override
        public void call(ActionEvent event) throws Exception {
            String locale = StandardDialogs.showEditText("New Locale Name","xz-XZ");
            u.p("locale = " + locale);
            if(locale != null) {
                currentLocaleModel.add(locale);
                ListModel<String> mod = ListUtil.toAlphaListModel(currentLocaleModel);
                currentLocalePopup.setModel(mod);
                for(int i=0; i<mod.size(); i++) {
                    if(mod.get(i) == locale) {
                        currentLocalePopup.setSelectedIndex(i);
                    }
                }
            }
        }
    };

    Callback<ActionEvent> exportTranslation = new Callback<ActionEvent>() {
        @Override
        public void call(ActionEvent event) throws Exception {
            File file = new File("test.xml");
            u.p("writing to file: " + file.getAbsolutePath());
            XMLWriter xml = new XMLWriter(file);
            xml.header();
            xml.start("sets");
            for(Prefix prefix : ListUtil.toAlphaCollection(prefixes.values())) {
                xml.start("set","name",prefix.prefix);
                for(Key key : ListUtil.toAlphaCollection(prefix.keys)) {
                    xml.start("key","name",key.keyString.getKeyname());
                    for(String lang : ListUtil.toAlphaCollection(key.keyString.translations.keySet())) {
                        xml.start("value")
                                .attr("language",lang)
                                .text(key.keyString.translations.get(lang))
                                .end();
                    }
                    xml.end();
                }
                xml.end();
            }
            xml.end();
            xml.close();
            StandardDialogs.showAlert("Translation has been exported to \n " + file.getAbsolutePath());
        }
    };

    Callback<ActionEvent> setString = new Callback<ActionEvent>() {
        public void call(ActionEvent actionEvent) throws Exception {
            Key key = keyView.getModel().get(keyView.getSelectedIndex());
            Lang lang = langView.getModel().get(langView.getSelectedIndex());
            String value = editBox.getText();
            key.keyString.setTranslation(lang.realLang,value);
            lang.isReal = true;
            Core.getShared().reloadSkins();
        }
    };


    Callback<ActionEvent> addLangAction = new Callback<ActionEvent>(){
        @Override
        public void call(ActionEvent event) throws Exception {
            String newLang = StandardDialogs.showEditText("New Locale","en-US");
            Key key = keyView.getModel().get(keyView.getSelectedIndex());
            key.keyString.addTranslation(newLang,"---");
            ArrayListModel<String> m = new ArrayListModel<String>();
            m.addAll(key.keyString.translations.keySet());
            //langView.setModel(ListUtil.toAlphaListModel(m));
        }
    };


    private void processKeys() {
        //process all keys in the existing translation file
        final Set<String> keys = Localization.getAllKeys();
        prefixes = new HashMap<String, Prefix>();
        currentLocaleModel = new ArrayListModel<String>();
        for(String dsKey : keys) {
            Localization.KeyString ks = Localization.getKeyString(dsKey);
            if(!prefixes.containsKey(ks.getPrefix())) {
                prefixes.put(ks.getPrefix(),new Prefix(ks.getPrefix()));
            }
            Prefix pf = prefixes.get(ks.getPrefix());
            if(!pf.keyMap.containsKey(ks.getKeyname())) {
                Key k = new Key(ks.getKeyname());
                pf.keyMap.put(ks.getKeyname(),k);
                pf.keys.add(k);
            }
            Key key = pf.keyMap.get(ks.getKeyname());
            key.keyString = ks;
            for(String lang : key.keyString.translations.keySet()) {
                if(!currentLocaleModel.contains(lang)) {
                    currentLocaleModel.add(lang);
                }
            }
        }
        prefixList = new ArrayListModel<Prefix>();
        prefixList.addAll(prefixes.values());


    }

    class Prefix implements Comparable<Prefix> {
        ArrayListModel<Key> keys = new ArrayListModel<Key>();
        private String prefix;
        public Map<String,Key> keyMap = new HashMap<String,Key>();

        public Prefix(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public String toString() {
            return prefix;
        }

        @Override
        public int compareTo(Prefix prefix) {
            return this.prefix.compareTo(prefix.prefix);
        }
    }

    class Key implements Comparable<Key>{
        private String key;
        public Localization.KeyString keyString;

        public Key(String key) {
            this.key = key;
        }
        public String toString() {
            return key;
        }

        @Override
        public int compareTo(Key key) {
            return this.key.compareTo(key.key);
        }
    }
    class Lang implements Comparable<Lang> {
        String realLang;
        boolean isReal;

        public Lang(String locale, boolean b) {
            this.realLang = locale;
            this.isReal = b;
        }

        @Override
        public int compareTo(Lang lang) {
            return this.realLang.compareTo(lang.realLang);
        }
    }
}
