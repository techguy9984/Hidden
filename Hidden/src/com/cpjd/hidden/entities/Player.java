package com.cpjd.hidden.entities;

import java.awt.event.KeyEvent;

import com.cpjd.hidden.map.TileMap;

public class Player extends Sprite {

	
	public Player(TileMap tm) {
		super(tm);
		
		width = 50;
		height = 50;
		cwidth = 50;
		cheight = 50;
		maxSpeed = 10;
		
		moveSpeed = 2.4;
	}
	
	public void keyPressed(int k) {
		if(k == KeyEvent.VK_W) up = true;
		if(k == KeyEvent.VK_A) left = true;		
		if(k == KeyEvent.VK_S) down = true;
		if(k == KeyEvent.VK_D) right = true;
	}
	public void keyReleased(int k) {
		if(k == KeyEvent.VK_W) up = false;
		if(k == KeyEvent.VK_A) left = false;		
		if(k == KeyEvent.VK_S) down = false;
		if(k == KeyEvent.VK_D) right = false;
	}
}
