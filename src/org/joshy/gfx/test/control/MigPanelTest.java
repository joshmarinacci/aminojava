package org.joshy.gfx.test.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.control.Label;
import org.joshy.gfx.node.control.ListView;
import org.joshy.gfx.node.control.ScrollPane;
import org.joshy.gfx.node.control.Textbox;
import org.joshy.gfx.node.layout.TabPanel;
import org.joshy.gfx.node.layout.mig.MigPanel;
import org.joshy.gfx.node.layout.mig.layout.AC;
import org.joshy.gfx.node.layout.mig.layout.CC;
import org.joshy.gfx.node.layout.mig.layout.LC;
import org.joshy.gfx.stage.Stage;

/*****************************************************************************
 * Test for the MigPanel
 * 
 * @author Bernd Rosstauscher (svg2java(@)rosstauscher.de)
 ****************************************************************************/

public class MigPanelTest implements Runnable {

	/*************************************************************************
	 * @param args
	 * @throws Exception
	 ************************************************************************/

	public static void main(String... args) throws Exception {
		Core.init();
		Core.getShared().defer(new MigPanelTest());
	}

	/*************************************************************************
	 * run
	 * 
	 * @see java.lang.Runnable#run()
	 ************************************************************************/
	@Override
	public void run() {
		initUI();
	}

	/*************************************************************************
	 * Setup the UI components and layout.
	 ************************************************************************/

	private void initUI() {
		Stage stage = Stage.createStage();

		Control formPanel = buildFormTestTab();

		Control cellPanel = buildCellTestTab();
		
		TabPanel tabbedPane = new TabPanel();
		tabbedPane.add("Form Test", formPanel);
		tabbedPane.add("Cell Layout Test", cellPanel);

		stage.setContent(tabbedPane);
	}

	/*************************************************************************
	 * @return
	 ************************************************************************/

	private MigPanel buildFormTestTab() {
		LC layC = new LC().fill().wrap();
		AC colC = new AC().align("right", 1).fill(2, 4).grow(100, 2, 4)
				.align("right", 3).gap("15", 2);
		AC rowC = new AC().align("top", 7).gap("15!", 6).grow(100, 8);

		MigPanel panel = new MigPanel(layC, colC, rowC);

		// References to text fields not stored to reduce code clutter.

		ScrollPane list2 = new ScrollPane(new ListView<String>());
		panel.nextCell().spanY().growY().minWidth("150").gapX(null, "10").content(list2);

		panel.add(new Label("Last Name"));
		panel.add(new Textbox());
		panel.add(new Label("First Name"));
		panel.add(new Textbox(), new CC().wrap().alignX("right"));
		panel.add(new Label("Phone"));
		panel.add(new Textbox());
		panel.add(new Label("Email"));
		panel.add(new Textbox());
		panel.add(new Label("Address 1"));
		panel.add(new Textbox(), new CC().spanX().growX());
		panel.add(new Label("Address 2"));
		panel.add(new Textbox(), new CC().spanX().growX());
		panel.add(new Label("City"));
		panel.add(new Textbox(), new CC().wrap());
		panel.add(new Label("State"));
		panel.add(new Textbox());
		panel.add(new Label("Postal Code"));
		panel.add(new Textbox("10"), new CC().spanX(2).growX(0));
		panel.add(new Label("Country"));
		panel.add(new Textbox(), new CC().wrap());

		panel.add(new Button("New"), new CC().spanX(5).split(5).tag("other"));
		panel.add(new Button("Delete"), new CC().tag("other"));
		panel.add(new Button("Edit"), new CC().tag("other"));
		panel.add(new Button("Save"), new CC().tag("other"));
		panel.add(new Button("Cancel"), new CC().tag("cancel"));
		return panel;
	}

	/*************************************************************************
	 * @return 
	 ************************************************************************/

	private Control buildCellTestTab() {
		// Horizontal
		MigPanel hPanel = new MigPanel("wrap",
				"[grow,left][grow,center][grow,right][grow,fill,center]",
				"[]unrel[][]");
		String[] sizes = new String[] { "", "growx", "growx 0", "left",
				"center", "right", "leading", "trailing" };
		hPanel.add(new Label("[left]"), "c");
		hPanel.add(new Label("[center]"), "c");
		hPanel.add(new Label("[right]"), "c");
		hPanel.add(new Label("[fill,center]"), "c, growx 0");

		for (int r = 0; r < sizes.length; r++) {
			for (int c = 0; c < 4; c++) {
				String text = sizes[r].length() > 0 ? sizes[r] : "default";
				hPanel.add(new Button(text), sizes[r]);
			}
		}

		// Vertical
		MigPanel vPanel = new MigPanel("wrap,flowy", "[right][]",
				"[grow,top][grow,center][grow,bottom][grow,fill,bottom][grow,fill,baseline]");
		String[] vSizes = new String[] { "", "growy", "growy 0", "top",
				"center", "bottom" };
		vPanel.add(new Label("[top]"), "center");
		vPanel.add(new Label("[center]"), "center");
		vPanel.add(new Label("[bottom]"), "center");
		vPanel.add(new Label("[fill, bottom]"), "center, growy 0");
		vPanel.add(new Label("[fill, baseline]"), "center");

		for (int c = 0; c < vSizes.length; c++) {
			for (int r = 0; r < 5; r++) {
				String text = vSizes[c].length() > 0 ? vSizes[c] : "default";
				Button b = new Button(text);
				// TODO rossi 19.12.2010 setFont not supported.
//				if (r == 4 && c <= 1)
//					b.setFont(new Font("sansserif", Font.PLAIN, 16 + c * 5));
				vPanel.add(b, vSizes[c]);
			}
		}

		TabPanel tabbedPane = new TabPanel();
		tabbedPane.add("Horizontal", hPanel);
		tabbedPane.add("Vertical", vPanel);
		return tabbedPane;
	}

}
