package com.cpjd.hidden.toolbox;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import com.cpjd.hidden.entities.Player;
import com.cpjd.hidden.files.IO;
import com.cpjd.hidden.gamestate.GameStateManager;
import com.cpjd.hidden.main.GamePanel;
import com.cpjd.hidden.map.Map;
import com.cpjd.hidden.ui.hud.HUD;
import com.cpjd.smartui.SmartField;
import com.cpjd.tools.Layout;
import com.cpjd.tools.Usage;

public class Console {
	
	// Passed in stuff
	private GameStateManager gsm;
	private Player player;
	
	private boolean open;
	private SmartField field;
	
	private String lastCommand;
	
	private ArrayList<String> output;
	
	private MessageLog messageLog;
	private boolean messageLogOpen = false;
	
	private HUD hud;
	
	private final String PAGEID = "#";
	//maximum ten lines per page, top and third should be empty
	private final String[] HELP = {
			
		"",
		"Command, Description, Usage - Page 1",
		"",
		"Help - displays page 1 of help menu",
		"Help <Page> - displays specified page of help menu",
		"debug - toggles debug mode",
		"stop - force close the program",
		"reload - reload the current gamestate",
		"clear - remove all messages from the console",
		
		PAGEID + "2",
		
		"",
		"Command, Description, Usage - Page 2",
		"",
		"clear log - remove all messages from the message log",
		"log - toggles visibility of message log",
		"log <String> - adds the String to the message log",
		"load <GameState> - loads the GameState specified",
		"delete - deletes save file",
		"scale <size> - scales game to size"
		
	};
	
	
	public Console(GameStateManager gsm) {
		this.gsm = gsm;
		
		Rectangle rect = new Rectangle(0, 200, 450, 30);
		
		field = new SmartField(new Font("Arial", Font.PLAIN, 15), rect, 100);
		field.setBlinkSpeed(40);
		
		output = new ArrayList<String>();
		
		messageLog = new MessageLog();
	}
	
	public void update() {
		if(!open) return;
		
		field.onFocus();

		field.update();
		
		if(field.isEnterPressed()) {
			try {
				processCommand(field.getText());
			} catch(Exception e) {
				e.printStackTrace();
				output.add("Incorrect command syntax. Use help for usage.");
			}
			field.setEnterPressed(false);
		}
	}
	
	public void processCommand(String s) throws Exception {
		String[] tokens = s.split("\\s+");
		
		lastCommand = s;
		
		// Basic commands
		if (tokens[0].toLowerCase().equals("stop")){
			output.add("Force stopping game");
			System.exit(0);
			return;
		}
		else if (tokens[0].toLowerCase().equals("r") || tokens[0].toLowerCase().equals("reload")){
			gsm.setState(gsm.getState());
			output.add("Gamestate reloaded.");
			open = false;
			return;
		}
		else if(tokens[0].toLowerCase().equals("scale")) {
			Map.SCALE = Byte.parseByte(tokens[1]);
			output.add("Map scale changed to "+tokens[1]+". Reload for changes to take effect.");
			return;
		}
		else if(tokens[0].toLowerCase().equals("debug")) {
			GamePanel.DEBUG = !GamePanel.DEBUG;
			output.add("DEBUG toggled.");
			return;
		}
		else if(tokens[0].toLowerCase().equals("delete")) {
			IO.deleteGameSave();
			output.add("Deleted game save file.");
			return;
		}
		else if(tokens[0].toLowerCase().equals("tp")) {
			player.setPosition(Integer.parseInt(tokens[1].split(",")[0]) * 64, Integer.parseInt(tokens[1].split(",")[1]) * 64);
			output.add("Player teleported.");
		}
		else if (tokens[0].toLowerCase().equals("clear")){
			if(tokens.length > 1){
				
				if(tokens[1].equalsIgnoreCase("log")){
					messageLog.clear();
					output.add("Message log cleared.");
				}
				
			}else output.clear();
			return;
		}
		else if (tokens[0].toLowerCase().equals("help")){
			
			if(tokens.length > 1){
					
				boolean display = false;
				int page = 1;
					
				for(int i = 0; i < HELP.length; i++) {
					
					if(page == Integer.parseInt(tokens[1])){
						display = true;
					}
					if(HELP[i].startsWith(PAGEID)){
						page++;
							
						if(display) {
							break;
						}
						continue;
					}
					
					if(display) {
							
						output.add(HELP[i]);
					}
				}
				if(!display){
					output.add("Unrecognized help page.");
				}
			}
			else{
				for(int i = 0; i < HELP.length; i++) {
					
					if(HELP[i].startsWith(PAGEID)) break;
					output.add(HELP[i]);
				}
			}
			return;
		}
		else if (tokens[0].toLowerCase().equals("log")){
			
			if(tokens.length > 1){
				
				tokens[0] = "";
				
				String text = Util.getString(tokens, true);
				MessageLog.log(text);
				output.add("Logged \"" + text + "\".");
			}else{
			
				messageLogOpen = !messageLogOpen;
				output.add("Message log toggled.");
			}
			return;
		}
		else if (tokens[0].toLowerCase().equals("load")){
			
			if(tokens.length > 1){
				
				lastCommand = tokens.toString();
				
				if(loadStateFromConsole(tokens[1])){
					
					output.add("Loaded " + tokens[1]);
				}else{
					output.add("Load failed");
				}
			}
			return;
		}
		else if(tokens[0].toLowerCase().equals("give")) {
			hud.addItem(hud.getItems().getItem(Integer.parseInt(tokens[1])));
			return;
		}
		else if(tokens[0].toLowerCase().equals("clearinv")) {
			hud.clearInventory();
			return;
		}
		else{
			output.add("Unrecognized command. Type help for list of commands");
		}
	}
	
	public void draw(Graphics2D g) {
		
		if(open) {
			Stroke s = g.getStroke();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, 452, 200);
			
			field.draw(g);
			
			// Draw items
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.PLAIN, 15));
			for(int i = output.size() - 1, j = 0; i >= 0; i--, j++) {
				g.drawString(output.get(i), 5, Layout.aligny(19) - (j * 20));
			}
			g.setStroke(s);
		}
		
		if(messageLogOpen){
			messageLog.draw(g);
		}
		
		if(!GamePanel.DEBUG) return;
		
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.PLAIN, 30));
		
		if(player != null){
			int tempx = (int)player.getX();
			int tempy = (int)player.getY();
			g.drawString("XY: " + "(" + tempx + "," + tempy + ")"+" XY: ("+(tempx / 64)+","+(tempy / 64)+")", 5, Layout.HEIGHT - 15);
		}
		
		g.drawString(Usage.calcMemory(), 5, Layout.HEIGHT - 50);
		
	}
	
	public boolean keyPressed(int k) {
		if(k == KeyEvent.VK_BACK_QUOTE) open = !open;
		if(k == KeyEvent.VK_UP){
			
			if(lastCommand == null){
				output.add("No previous command");
				return true;
			}
			
			for(int i = 0; i < 50; i++) field.delete();
			
			lastCommand.toUpperCase();
			for(int i = 0; i < lastCommand.length(); i++){
				field.add(lastCommand.charAt(i));
			}
			return true;
		}
		if(!open) return false;
		field.keyPressed(k);
		return true;
		//returns whether key was used
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public void mousePressed(int x, int y) {
		//if(GamePanel.DEBUG && player != null) player.setPosition(player.getX() - (GamePanel.WIDTH / 2), player.getY() - (GamePanel.HEIGHT / 2) - y);
	}
	
	public void setPlayer(Player p) {
		this.player = p;
	}
	
	/**
	 * Loads a GameState from the console
	 * @param name The name of the GameState to load
	 * @return boolean returns true if new GameState loaded correctly, false if it failed
	 */
	private boolean loadStateFromConsole(String name){
			
		//java 6 doesn't support switch statements with Strings
		if(name.equalsIgnoreCase("INTRO")){
			gsm.setState(GameStateManager.INTRO);
			return true;
		}
		else if(name.equalsIgnoreCase("MENU")){
			gsm.setState(GameStateManager.MENU);
			return true;
		}
		else if(name.equalsIgnoreCase("WORLD")){
			gsm.setState(GameStateManager.WORLD);
			return true;
		}else if(name.equalsIgnoreCase("LVL1")){
			gsm.setState(GameStateManager.LVL_1);
			return true;
		}else{
			MessageLog.log("Attempted to load unrecognized GameState " + name);
			return false;
		}
	}
	
	public void setHUD(HUD hud) {
		this.hud = hud;
	}
}
