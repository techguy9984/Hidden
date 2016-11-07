package com.cpjd.hidden.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.cpjd.tools.Hardware;

public class IO {
	private static File gameDir;
	
	/**
	 * Should be called whenever the application is started.
	 * This will locate the game save dirs / create them in accordance with the os
	 */
	public static void initDirs() {
		String osName = Hardware.osName.toLowerCase();
		
		if(osName.contains("win")) {
			gameDir = new File((System.getenv("APPDATA") + File.separator + "Hidden" + File.separator));
			if(!gameDir.exists()) gameDir.mkdir();
		}
		
	}
	
	public static boolean serializeGameSave(GameSave gameSave) {
		return serializeObject(gameSave, "gamesave.ser");
	}
	
	public static GameSave deserializeGameSave() {
		return (GameSave) deserializeObject("gamesave.ser");
	}
	
	private static boolean serializeObject(Object object, String fileName) {
		try {
			FileOutputStream fos = new FileOutputStream(gameDir + fileName);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(object);
			out.close();
			fos.close();
			return true;
		} catch(Exception e) {
			System.err.println("Couldn't save game.");
			return false;
		}
	}
	
	private static Object deserializeObject(String location) {
		try {
			FileInputStream fis = new FileInputStream(gameDir + location);
			ObjectInputStream in = new ObjectInputStream(fis);
			Object o = in.readObject();
			in.close();
			fis.close();
			return o;
		} catch(Exception e) {
			System.err.println("Couldn't read game save.");
			return null;
		}
	}
	
}