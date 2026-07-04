package tic;

public class Tic {
	String[][] board;
	int rows;
	int cols;
	String turn;
	public Tic(int row, int col) {
		board = new String[row][col];
		rows = row;
		cols = col;
		turn = "X";
		for(int i = 0; i<row; i++) {
			for(int j = 0; j<col; j++) {
				board[i][j] = "_";
			}
		}
	}

	public String cellAt(int row, int col) {
		return board[row][col];
	}

	public String currentPlayer() {
		return turn;
	}

	public void place(int row, int col) {
		if (!board[row][col].equals("_")) {
			throw new IllegalStateException("cell (" + row + "," + col + ") is already taken");
		}
		board[row][col] = turn;
		turn = turn.equals("X") ? "O" : "X";
	}

	public String winner() {
		for (int i = 0; i < rows; i++) {
			String rowWinner = winnerOfRow(i);
			if (!rowWinner.equals("_")) {
				return rowWinner;
			}
		}
		return "_";
	}

	// returns the mark filling row i, or "_" if the row is empty/mixed
	private String winnerOfRow(int i) {
		String first = board[i][0];
		if (first.equals("_")) {
			return "_";
		}
		for (int j = 1; j < cols; j++) {
			if (!board[i][j].equals(first)) {
				return "_";
			}
		}
		return first;
	}
}
