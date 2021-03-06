package com.cpjd.hidden.chapters;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import com.cpjd.hidden.entities.Player;
import com.cpjd.hidden.files.GameSave;
import com.cpjd.hidden.files.IO;
import com.cpjd.hidden.gamestate.Chapter;
import com.cpjd.hidden.gamestate.GameStateManager;
import com.cpjd.hidden.genworld2.Turbulence;
import com.cpjd.hidden.genworld2.TurbulenceListener;
import com.cpjd.hidden.prisons.PrisonID;
import com.cpjd.hidden.toolbox.Console;
import com.cpjd.hidden.ui.hud.PUD;
import com.cpjd.tools.Layout;

public class World extends Chapter implements TurbulenceListener {

	protected Turbulence world;
	
	private PUD pud;
	
	public World(GameStateManager gsm, Console console) {
		super(gsm, console);
		
		console.setHUD(hud);
		
		finishedGen = false;
		
		tileMap.loadTiles("/tiles/tileset.png");
		
		pud = new PUD();
		
		if(save == null || save.getMap() == null) {
			world = new Turbulence();
			world.addListener(this);
		} else {
			tileMap.setMap(save.getMap());
			player = new Player(tileMap);
			player.setPosition(save.getPlayerLocation().x, save.getPlayerLocation().y);
			tileMap.initCamera(player.getX(), player.getY());
			console.setPlayer(player);
			hud.setPlayer(player);
			finishedGen = true;
			
			//setIDs(save.getPrisonLocations());
		}
		
	}
	
	public void update() {
		if(finishedGen){
			super.update();
			pud.update(player.getX(), player.getY());
		}
	}
	
	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		
		pud.draw(g, tileMap.getXOffset(), tileMap.getYOffset());
		
		if(finishedGen) return;
		
		g.setColor(Color.WHITE);
		g.setFont(GameStateManager.font.deriveFont(35f));
		g.drawString("Generating terrain...", Layout.centerString("Generating terrain...", g), Layout.centerStringVert(g));
		g.setColor(Color.DARK_GRAY);
		g.fillRect(Layout.centerw(400), Layout.aligny(51), 400, 40);
		g.setColor(Color.WHITE);
		g.fillRect(Layout.centerw(400), Layout.aligny(51), (int)(world.getCollectiveProgress() * 400), 40);
		
	}

	private void setIDs(ArrayList<ArrayList<Point>> prisonLocations){
		for(int i = 0; i < prisonLocations.size(); i++){
			
			for(int j = 0; j < prisonLocations.get(i).size(); j++){
				
				PrisonID id = new PrisonID(prisonLocations.get(i).get(j).x * tileMap.getScaledTileSize(), prisonLocations.get(i).get(j).y * tileMap.getScaledTileSize(), PrisonID.RANDOM_NAME, Integer.toString(i + 1), "Daniel Peterson", 500, 0);
				pud.addPrisonID(id);
				
			}
		}
	}
	
	@Override
	public void worldGenerated(int[][][] map, ArrayList<Point> villageLocations, ArrayList<ArrayList<Point>> prisonLocations, Point spawn) {
		System.out.println("WORLD GENERATED");
		
		tileMap.setMap(world.getWorld());
		player = new Player(tileMap);
		player.setPosition(spawn.x * tileMap.getScaledTileSize(), spawn.y * tileMap.getScaledTileSize());
		player.setPosition(100, 100);
		tileMap.initCamera(player.getX(), player.getY());
		console.setPlayer(player);
		hud.setPlayer(player);
		finishedGen = true;
		
		GameSave save = new GameSave();
		save.setMap(world.getWorld());
		save.setPrisonLocations(prisonLocations);
		save.setPlayerLocation(new Point((int)player.getX(), (int)player.getY()));
		
		//setIDs(prisonLocations);
		
		IO.serializeGameSave(save);
		
	}
}
