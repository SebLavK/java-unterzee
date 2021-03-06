package controller.master;

import commons.Clock;
import model.ExecutiveOfficer;
import model.Scenario;
import view.MainWindow;
import view.panels.GamePanel;
import view.panels.SidePanel;
import view.screens.BlankScreen;
import view.screens.DataScreen;
import view.screens.IntroScreen;
import view.screens.MapScreen;

import java.awt.*;

/**
*@author Sebas Lavigne
*/

public class Master implements Runnable {
	
	public static Master master;
	
	private MainWindow mainWindow;
	
	private Scenario scenario;
	private GamePanel gamePanel;
	private SidePanel sidePanel;
	private ExecutiveOfficer xo;
	
	private boolean running;
	
	public Master(MainWindow mainWindow, GamePanel gamePanel, SidePanel sidePanel) {
		this.gamePanel = gamePanel;
		this.sidePanel = sidePanel;
		master = this;
	}
	
	public void initializeMaster() {
		running = false;
		scenario = new Scenario();
		
		xo = new ExecutiveOfficer(this, scenario.getSub());
		Parser.setXo(xo);

		gamePanel.setCurrentScreen(new IntroScreen(this));
		gamePanel.getCurrentScreen().initializeScreen();
		sidePanel.setCurrentScreen(new BlankScreen(sidePanel));
	}
	
	public void startGame() {
		new Thread(this).start();
	}
	
	private void tick() {
		gamePanel.paintImmediately(0, 0, gamePanel.getWidth(), gamePanel.getHeight());
		sidePanel.repaint();
		Clock.tick();
		if (running) {
			scenario.tick();
		}
		Toolkit.getDefaultToolkit().sync();
	}

	@Override
	public void run() {
		while (true) {
			long tickStartTime = Clock.getCurrentTime();
			tick();
			long elapsedTime = Clock.timeSince(tickStartTime);
			long waitTimeMillis = (Clock.FRAME_NANO - elapsedTime) / 1000000 - 1;
			int waitTimeNanos = 1000000 - (int) (elapsedTime % 1000000);
			if (waitTimeMillis < 0) {
				waitTimeMillis = 0;
				waitTimeNanos = 0;
			}
			try {
				Thread.sleep(waitTimeMillis, waitTimeNanos);
//				Thread.sleep(Clock.FRAME_PERIOD);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return the scenario
	 */
	public Scenario getScenario() {
		return scenario;
	}

	
	/**
	 * @return the sidePanel
	 */
	public SidePanel getSidePanel() {
		return sidePanel;
	}

	/**
	 * @return the xo
	 */
	public ExecutiveOfficer getXo() {
		return xo;
	}
	
	public void startRunning() {
		gamePanel.removeListeners();
		running = true;
		gamePanel.setCurrentScreen(new MapScreen(this, gamePanel));
		sidePanel.setCurrentScreen(new DataScreen(this, sidePanel.getDataPanel()));
		Clock.setGameStartTime();
	}

	
}
