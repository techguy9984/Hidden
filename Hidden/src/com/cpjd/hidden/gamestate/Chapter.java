package com.cpjd.hidden.gamestate;

import java.awt.Graphics2D;
import java.awt.Point;

import com.cpjd.hidden.entities.Player;
import com.cpjd.hidden.files.GameSave;
import com.cpjd.hidden.map.Map;
import com.cpjd.hidden.toolbox.Console;
import com.cpjd.hidden.ui.hud.HUD;

public class Chapter extends GameState {

	protected Map tileMap;
	protected boolean finishedGen;
	
	protected Player player;

	protected GameSave save;
	
	public Chapter(GameStateManager gsm, Console console) {
		super(gsm, console);

		save = gsm.getGameSave();
		hud = new HUD(save);
		
		tileMap = new Map(16);
	}
	
	@Override
	public void update() {
		if(player == null || !finishedGen) return;
		hud.update();
		tileMap.setCameraPosition(player.getX(),player.getY());
		
		if(hud.isOpen()) return;
		player.update();
	}
	@Override
	public GameSave getSave(GameSave prior) {
		GameSave save = prior;
		//save.setMap(tileMap.getMap());
		save.setPlayerLocation(new Point((int)player.getX(), (int)player.getY()));
		save.setInventory(hud.getInventory());
		save.setClothing(hud.getClothing());
		save.setHotbar(hud.getHotbar());
		return save;
	}
	@Override
	public void draw(Graphics2D g) {
		if(!finishedGen) return;
		
		tileMap.draw(g);
		player.draw(g);
		if(!gsm.isPaused()) hud.draw(g);
	}

	@Override
	public void keyPressed(int k) {
		if(player != null) player.keyPressed(k);
		hud.keyPressed(k);
	}

	@Override
	public void keyReleased(int k) {
		if(player != null) player.keyReleased(k);
	}
	
	@Override
	public void mousePressed(int x, int y) {
		hud.mousePressed(x, y);
	}

	@Override
	public void mouseReleased(int x, int y) {}

	@Override
	public void mouseMoved(int x, int y) {
		hud.mouseMoved(x, y);
	}
	
	@Override
	public void mouseWheelMoved(int k) {
		hud.mouseWheelMoved(k);
	}
}