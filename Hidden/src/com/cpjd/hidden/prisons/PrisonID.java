package com.cpjd.hidden.prisons;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.Random;

import com.cpjd.hidden.gamestate.GameStateManager;
import com.cpjd.hidden.toolbox.MessageLog;

// Stores information about a particular tier of prison
public class PrisonID {
	public int x, y;
	public String name;
	public String tier;
	public String hostage;
	public int reward;
	public int timeLimit;
	
	private final int RANGE = 200;
	
	private final int rectWidth = 225;
	private final int halfWidth = rectWidth / 2;
	private final int rectHeight = 115;
	
	//these are displayed in front of the corresponding information, except for money name-which is after
	private final String tierText = "Tier: ";
	private final String hostageText = "Hostage: ";
	private final String timeLimitText = "Time: ";
	private final String rewardText = "Reward: ";
	private final String moneyName = "Credits";
	
	private enum StatusEnum {CLOSED, OPENING, OPEN, CLOSING};
	private StatusEnum status = StatusEnum.CLOSED;
	private int width;
	private int height;
	private final int growSpeed = 10;
	
	public static final String RANDOM_NAME = "random_name";
	
	private final String[] randomNames = {"Daniel's Prison", "Bob's Prison", "Joe's Prison"};
	
	private boolean inRange;
	
	public PrisonID(int x, int y, String name, String tier, String hostage, int reward, int timeLimit) {
		this.x = x;
		this.y = y;
		this.tier = tier;
		this.hostage = hostage;
		this.reward = reward;
		this.timeLimit = timeLimit;
		
		if(name.equals(RANDOM_NAME)){
			
			Random r = new Random();
			int rand = r.nextInt(randomNames.length);//gives 0, 1, or 2 if length is 3
			this.name = randomNames[rand];
			
		}else this.name = name;
	}

	private void updateInRange(double playerX, double playerY){
		double xDiff = x - playerX, yDiff = y - playerY;
		
		double distanceSquared = xDiff * xDiff + yDiff * yDiff;
		
		inRange = false;
		
		if(distanceSquared < RANGE * RANGE){
			inRange = true;
		}
	}
	
	public void update(double playerX, double playerY){
		
		if(GameStateManager.ticks % 5 == 0){
			updateInRange(playerX, playerY);
		}
		
		switch(status){
		
		case CLOSED:
			if(inRange){
				status = StatusEnum.OPENING;
			}
			break;
			
		case CLOSING:
			
			if(inRange){
				status = StatusEnum.OPENING;
				break;
			}
			
			width -= growSpeed;
			height -= growSpeed;
				
			if(width < 0){
				width = 0;
			}
			if(height < 0){
				height = 0;
			}
				
			if(width == 0 && height == 0){
				status = StatusEnum.CLOSED;
			}
			break;
			
		case OPEN:
			if(!inRange){
				status = StatusEnum.CLOSING;
			}
			break;
			
		case OPENING:
			
			if(!inRange){
				status = StatusEnum.CLOSING;
				break;
			}
			
			width += growSpeed;
			height += growSpeed;
				
			if(width > rectWidth){
				width = rectWidth;
			}
			if(height > rectHeight){
				height = rectHeight;
			}
				
			if(width == rectWidth && height == rectHeight){
				status = StatusEnum.OPEN;
			}
			break;
			
		default:
			MessageLog.log("Unrecognized PrisonID status");
			System.err.println("Unrecognized PrisonID status");
			break;
		
		}
		
		
		
	}
	
	public void draw(Graphics2D g, double xOffset, double yOffset){
		
		if(status == StatusEnum.CLOSED) return;
		
		int drawX = (int) (x - width / 2 - xOffset);
		int drawY = (int) (y - height / 2 - yOffset);
		
		g.setColor(Color.white);
		g.fillRoundRect(drawX, drawY, width, height, 10, 10);
		
		if(status == StatusEnum.OPEN){
		
			g.setColor(Color.black);
			g.setFont(GameStateManager.font.deriveFont(18f));
			
			FontMetrics metrics = g.getFontMetrics();
			
			g.drawString(name, drawX + halfWidth - metrics.stringWidth(name) / 2, drawY + metrics.getHeight());
			g.drawString(tierText + tier, drawX + halfWidth - metrics.stringWidth(tierText + tier) / 2, drawY + 2 * metrics.getHeight());
			g.drawString(hostageText + hostage, drawX + halfWidth - metrics.stringWidth(hostageText + hostage) / 2, drawY + 3 * metrics.getHeight());
			g.drawString(rewardText + reward + " " + moneyName, drawX + halfWidth - metrics.stringWidth(rewardText + reward + " " + moneyName) / 2, drawY + 4 * metrics.getHeight());
			g.drawString(timeLimitText + timeLimit, drawX + halfWidth - metrics.stringWidth(timeLimitText + timeLimit) / 2, drawY + 5 * metrics.getHeight());
			
		}
	}
}
