package org.joshy.gfx.sidehatch;

import java.lang.instrument.Instrumentation;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

/*****************************************************************************
 * Used to inject sidehatch into an existing amino application.
 * To do this we use the Java Agent API.
 * 
 * http://download.oracle.com/javase/6/docs/api/java/lang/instrument/package-summary.html
 *
 * @author  Bernd Rosstauscher 
 ****************************************************************************/

public class Agent {
	
	private static final String VALUE_SEPARATOR = "=";
	private static final String ARGUMENT_SEPARATOR = ",";

	private static final String ARG_DELAY = "delay";
	
	private static int BOOT_DELAY_MILLIS = 10000; // 10 seconds default delay

	/*************************************************************************
	 * Needed for the java agent loading.
	 * Needs a entry in the jar manifest like this
	 * 
	 * Premain-Class:  org.joshy.gfx.sidehatch.Agent
	 * 
	 * @param agentArgs contains the command line options.
	 * @param inst the instrumentation to use
	 ************************************************************************/
	
	public static void premain(String agentArgs, Instrumentation inst) {
		parseOptions(agentArgs);
		startSideHatchDelayed();
	}
	
	/*************************************************************************
	 * Needed for the java agent loading to attach to an running application.
	 * Needs a entry in the jar manifest like this
	 * 
	 * Agent-Class: org.joshy.gfx.sidehatch.Agent
	 * @param agentArgs contains the command line options.
	 * @param inst the instrumentation to use
	 ************************************************************************/
	
	public static void agentmain(String agentArgs, Instrumentation inst) {
		parseOptions(agentArgs);
		startSideHatchDelayed();
	}
	
	/*************************************************************************
	 * @param agentArgs
	 * @throws NumberFormatException
	 ************************************************************************/
	
	
	private static void parseOptions(String agentArgs) {
		if (agentArgs == null) {
			return;
		}
		String[] options = agentArgs.split(ARGUMENT_SEPARATOR);
		for (String optionPair: options) {
			String[] pair = optionPair.split(VALUE_SEPARATOR);
			if (pair.length == 2) {
				String optionName = pair[0].trim();
				String optionValue = pair[1].trim();
				if (ARG_DELAY.equalsIgnoreCase(optionName)) {
					BOOT_DELAY_MILLIS = Integer.parseInt(optionValue);
				}
			}
		}
	}
	
	/*************************************************************************
	 * Start side hatch delayed to give the application some time to bring 
	 * up the UI and everything.
	 ************************************************************************/
	
	private static void startSideHatchDelayed() {
			Timer timer = new Timer(true);
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					SwingUtilities.invokeLater(					
					new Runnable() {
						@Override
						public void run() {
							launchSideHatchStage();
						}
					});
				}
			}, BOOT_DELAY_MILLIS);
	}

	/*************************************************************************
	 * Used to launch side hatch in the event thread.
	 ************************************************************************/
	
	private static void launchSideHatchStage() {
		try {
			SideHatch.launch();						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}


