package tic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TicTest {

	@Test
	void freshBoardCellsAreEmpty() {
		Tic board = new Tic(3, 3);
		// every cell of a new board should read as the empty marker "_"
		assertEquals("_", board.cellAt(0, 0));
		assertEquals("_", board.cellAt(2, 2));
	}

	@Test
	void placingMarkFillsCell() {
		Tic board = new Tic(3, 3);
		board.place(1, 1);
		// X moves first, so the placed cell should now hold "X"
		assertEquals("X", board.cellAt(1, 1));
	}

	@Test
	void turnAlternatesBetweenPlayers() {
		Tic board = new Tic(3, 3);
		assertEquals("X", board.currentPlayer());
		board.place(0, 0);
		assertEquals("O", board.currentPlayer());
		board.place(0, 1);
		assertEquals("X", board.currentPlayer());
	}

	@Test
	void placingOnOccupiedCellIsRejected() {
		Tic board = new Tic(3, 3);
		board.place(0, 0); // X takes (0,0)
		// O must not be able to overwrite an occupied cell
		assertThrows(IllegalStateException.class, () -> board.place(0, 0));
		// the rejected move must not consume O's turn
		assertEquals("O", board.currentPlayer());
	}

}
