package com.mike.TelegramRummikub.Game;

public class Table {
	private Tile[][][] fourBlocks;
	private Tile[][] thirteenBlocks;
	
	public Table(Tile[][][] fourBlocks, Tile[][] thirteenBlocks) {
		this.fourBlocks = fourBlocks;
		this.thirteenBlocks = thirteenBlocks;
	}
	
	public Table() {
		fourBlocks = new Tile[3][8][4];
		thirteenBlocks = new Tile[8][13];
	}
	
	public Tile[][] getThirteenBlocks() {
		return thirteenBlocks;
	}
	
	public void setThirteenBlocks(Tile[][] thirteenBlocks) {
		this.thirteenBlocks = thirteenBlocks;
	}
	
	public Tile[][][] getFourBlocks() {
		return fourBlocks;
	}
	
	public void setFourBlocks(Tile[][][] fourBlocks) {
		this.fourBlocks = fourBlocks;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Table table = (Table)o;
		// check four blocks
		for (int i = 0; i < fourBlocks.length; i++) {
			for (int j = 0; j < fourBlocks[i].length; j++) {
				for (int k = 0; k < fourBlocks[i][j].length; k++) {
					if (!table.fourBlocks[i][j][k].equals(fourBlocks[i][j][k])) return false;
				}
			}
		}
		// check thirteen blocks
		for (int i = 0; i < thirteenBlocks.length; i++) {
			for (int j = 0; j < thirteenBlocks[i].length; j++) {
				if (!table.thirteenBlocks[i][j].equals(thirteenBlocks[i][j])) return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		int result = 0;
		for (Tile[][] fourBlock : fourBlocks) {
			for (Tile[] tiles : fourBlock) {
				for (Tile tile : tiles) {
					result = 31 * result + tile.hashCode();
				}
			}
		}
		for (Tile[] thirteenBlock : thirteenBlocks) {
			for (Tile tile : thirteenBlock) {
				result = 31 * result + tile.hashCode();
			}
		}
		return result;
	}
	
	public boolean checkRules() {
		//check four blocks
		for (Tile[][] fourBlock : fourBlocks) {
			for (Tile[] tiles : fourBlock) {
				int tileCountInRow = 0;
				Number tileNumberInRow = null;
				for (Tile tile : tiles) {
					if (tile != null) {
						if (tileNumberInRow == null) {
							tileNumberInRow = tile.getNumber();
							tileCountInRow++;
							continue;
						}
						if (tileNumberInRow == Number.JOKER) {
							tileNumberInRow = tile.getNumber();
							tileCountInRow++;
							continue;
						}
						if (tile.getNumber() != tileNumberInRow && tile.getNumber() != Number.JOKER) {
							return false;
						} else {
							tileCountInRow++;
						}
					}
				}
				if (tileCountInRow != 0 && tileCountInRow < 3) return false;
			}
		}
		
		//check thirteen blocks
		for (int i = 0; i < thirteenBlocks.length; i++) {
			int rowTilesCounter = 0;
			for (int j = 0; j < thirteenBlocks[i].length; j++) {
				if (thirteenBlocks[i][j] == null) {
					if (rowTilesCounter == 0) continue;
					if (rowTilesCounter < 3) {
						return false;
					} else {
						rowTilesCounter = 0;
					}
				} else {
					rowTilesCounter++;
				}
			}
		}
		return true;
	}
}