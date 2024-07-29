package com.mike.TelegramRummikub.Game;

public class Tile {
	private Color color;
	private Number number;
	
	public Tile(Color color, Number number) {
		this.color = color;
		this.number = number;
	}
	
	public Tile() {
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public Number getNumber() {
		return number;
	}
	
	public void setNumber(Number number) {
		this.number = number;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Tile tile = (Tile)o;
		return color == tile.color && number == tile.number;
	}
	
	@Override
	public int hashCode() {
		return color.name().hashCode() * 31 + number.name().hashCode();
	}
}