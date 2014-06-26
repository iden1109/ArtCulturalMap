package me.synology.iden1109.map.model;

import java.util.NoSuchElementException;

public class NoSuchUName extends NoSuchElementException {
	
	private final String name;
	private final String type;

	public NoSuchUName(String type, String name) {
		super("No such UName for '" + type + "': '" + name + "'");
		this.type = type;
		this.name = name;
	}
}
