package com.cpjd.hidden.gamestates;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import com.cpjd.hidden.gamestate.GameState;
import com.cpjd.hidden.gamestate.GameStateManager;
import com.cpjd.hidden.main.GamePanel;
import com.cpjd.hidden.toolbox.Layout;
import com.cpjd.hidden.ui.FragmentButton;

public class Pause extends GameState {

	private boolean open;

	// Technical
	private AlphaComposite composite;
	private AlphaComposite defaultComposite;
	private int xmov;

	// UI
	FragmentButton resume, options, exit;

	public Pause(GameStateManager gsm) {
		super(gsm);

		composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.70f);
		defaultComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);

		resume = new FragmentButton("Resume");
		options = new FragmentButton("Options");
		exit = new FragmentButton("Exit");
		resume.setFlip(true);
		options.setFlip(true);
		exit.setFlip(true);

		xmov = 100;
	}

	@Override
	public void update() {
		if(open) {
			xmov += 13;
			if(xmov > 400) xmov = 400;
		}

		if(resume.isClicked()) {
			xmov = 100;
			open = false;
			resume.setClicked(false);
		}
		
		if(exit.isClicked()) {
			xmov = 100;
			gsm.setState(GameStateManager.INTRO);
			open = false;
			exit.setClicked(false);
		}

	}

	@Override
	public void draw(Graphics2D g) {
		if(!open) return;
		g.setComposite(composite);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

		Font font = g.getFont();
		Font defaultFont = font;
		font = font.deriveFont(40f);
		g.setFont(font);
		g.setColor(Color.BLACK);
		g.drawString("P A U S E D", Layout.centerString("P A U S E D", g) - GamePanel.WIDTH / 2, Layout.aligny(15));
		
		g.setFont(defaultFont);
		
		g.setColor(Color.BLACK);
		g.fillRect(Layout.centerw(xmov), Layout.centerh(300), xmov, 300);

		resume.draw(g, 10 + Layout.centerw(xmov), Layout.centerh(300) + 50);
		options.draw(g, 10 + Layout.centerw(xmov), Layout.centerh(300) + 150);
		exit.draw(g, 10 + Layout.centerw(xmov), Layout.centerh(300) + 250);
		
		g.setComposite(defaultComposite);

	}

	public boolean isOpen() {
		return open;
	}

	@Override
	public void keyPressed(int k) {
		if(k == KeyEvent.VK_ESCAPE) {
			if(gsm.getState() > 0) open = !open;
		}

		if(!open) xmov = 100;

	}

	@Override
	public void keyReleased(int k) {

	}

	@Override
	public void mousePressed(int x, int y) {
		resume.mousePressed(x, y);
		options.mousePressed(x, y);
		exit.mousePressed(x, y);

	}

	@Override
	public void mouseReleased(int x, int y) {

	}

	@Override
	public void mouseMoved(int x, int y) {
		resume.mouseMoved(x, y);
		options.mouseMoved(x, y);
		exit.mouseMoved(x, y);

	}

	@Override
	public void mouseWheelMoved(int k) {

	}

}
