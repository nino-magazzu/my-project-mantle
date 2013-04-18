package com.project.mantle_v1;

public class MantleImage {
	private String link;
	private String type;
	private int height;
	private int width;

	public MantleImage(String link, String type, int height, int width) {
		super();
		this.link = link;
		this.type = type;
		this.height = height;
		this.width = width;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
}