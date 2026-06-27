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

}
