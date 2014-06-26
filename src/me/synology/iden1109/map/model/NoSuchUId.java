package me.synology.iden1109.map.model;

import java.util.NoSuchElementException;

public class NoSuchUId extends NoSuchElementException {

	private final String id;
	private final String type;

	public NoSuchUId(String type, String id) {
		super("No such UID for '" + type + "': " + id);
		this.type = type;
		this.id = id;
	}
}
