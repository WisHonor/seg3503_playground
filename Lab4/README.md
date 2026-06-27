# SEG3503 — Lab 4 : Test-Driven Development

| Outline | Value |
| --- | --- |
| Course | SEG 3503 |
| Date | Summer 2026 |
| Students | Wissam Elmasry, Alexandre Turgeon |
| Professor | Mouhcine Guennoun |
| TA | Mohamed Nefsi |

This lab practises the Red–Green–Refactor TDD cycle. Each commit group below
pairs a failing-test commit (RED) with the implementation commit that makes it
pass (GREEN), and where useful a REFACTOR commit that keeps the tests green.
Every commit is attributed to its author in git history.

> **Language note.** Wissam's groups are implemented in **Java / JUnit 5**
> (the language the assignment requires), driven against the provided
> `tic_java_sample_code` base. Alexandre's groups are implemented in
> **Elixir / ExUnit** (`fizzbuzz` and `tic` mix projects). If the assignment
> only counts Java/JUnit groups, confirm with the TA whether the Elixir groups
> are accepted.

---

## Wissam Elmasry — Java / JUnit (`Lab4/tic/tic_java_sample_code`)

Built test-first on the provided `Tic.java` sample. Tests run with the JUnit 5
console launcher:

```powershell
javac -cp lib/junit-platform-console-standalone-1.11.3.jar -d bin Tic.java TicTest.java
java -jar lib/junit-platform-console-standalone-1.11.3.jar execute --class-path bin --select-class tic.TicTest
```

Final result: **6 tests found, 6 successful, 0 failed.**

| Commit group / names | Commit number (hash) | Description |
| --- | --- | --- |
| Init empty board<br>red, green | RED `cada70d`<br>GREEN `23bf782` | <b>What:</b> A fresh 3×3 board reads the empty marker "_" at every cell, via a new <code>cellAt(row,col)</code> accessor.<br><br><b>RED — test written first:</b><pre>@Test<br>void freshBoardCellsAreEmpty() {<br>    Tic board = new Tic(3, 3);<br>    assertEquals("_", board.cellAt(0, 0));<br>    assertEquals("_", board.cellAt(2, 2));<br>}</pre><b>GREEN/REFACTOR — implementation:</b><pre>public String cellAt(int row, int col) {<br>    return board[row][col];<br>}</pre> |
| Place a mark<br>red, green | RED `67c2dbe`<br>GREEN `f212b04` | <b>What:</b> <code>place(r,c)</code> writes the current player’s mark (X moves first) into the chosen cell.<br><br><b>RED — test written first:</b><pre>@Test<br>void placingMarkFillsCell() {<br>    Tic board = new Tic(3, 3);<br>    board.place(1, 1);<br>    assertEquals("X", board.cellAt(1, 1));<br>}</pre><b>GREEN/REFACTOR — implementation:</b><pre>public void place(int row, int col) {<br>    board[row][col] = turn;<br>}</pre> |
| Turn alternates X/O<br>red, green | RED `84465da`<br>GREEN `b6d738d` | <b>What:</b> A new <code>currentPlayer()</code> reports whose turn it is, and <code>place()</code> flips X↔O after each move.<br><br><b>RED — test written first:</b><pre>@Test<br>void turnAlternatesBetweenPlayers() {<br>    Tic board = new Tic(3, 3);<br>    assertEquals("X", board.currentPlayer());<br>    board.place(0, 0);<br>    assertEquals("O", board.currentPlayer());<br>    board.place(0, 1);<br>    assertEquals("X", board.currentPlayer());<br>}</pre><b>GREEN/REFACTOR — implementation:</b><pre>public String currentPlayer() { return turn; }<br><br>public void place(int row, int col) {<br>    board[row][col] = turn;<br>    turn = turn.equals("X") ? "O" : "X";<br>}</pre> |
| Reject occupied move<br>red, green | RED `82471db`<br>GREEN `ac0b880` | <b>What:</b> Playing on a taken cell throws <code>IllegalStateException</code> without writing or consuming the turn.<br><br><b>RED — test written first:</b><pre>@Test<br>void placingOnOccupiedCellIsRejected() {<br>    Tic board = new Tic(3, 3);<br>    board.place(0, 0);<br>    assertThrows(IllegalStateException.class,<br>        () -> board.place(0, 0));<br>    assertEquals("O", board.currentPlayer());<br>}</pre><b>GREEN/REFACTOR — implementation:</b><pre>public void place(int row, int col) {<br>    if (!board[row][col].equals("_")) {<br>        throw new IllegalStateException(<br>            "cell (" + row + "," + col + ") is already taken");<br>    }<br>    board[row][col] = turn;<br>    turn = turn.equals("X") ? "O" : "X";<br>}</pre> |
| Detect a row win<br>red, green, refactor | RED `a3d3ffc`<br>GREEN `e4cc925`<br>REFACTOR `4429a13` | <b>What:</b> <code>winner()</code> returns the mark of any completed row (else "_"). Refactor extracted a <code>winnerOfRow(i)</code> helper, tests still green.<br><br><b>RED — test written first:</b><pre>@Test<br>void detectsRowWin() {<br>    Tic board = new Tic(3, 3);<br>    board.place(0,0); board.place(1,0);<br>    board.place(0,1); board.place(1,1);<br>    board.place(0,2);            // X tops the row<br>    assertEquals("X", board.winner());<br>}</pre><b>GREEN/REFACTOR — implementation:</b><pre>public String winner() {                 // after REFACTOR<br>    for (int i = 0; i &lt; rows; i++) {<br>        String w = winnerOfRow(i);<br>        if (!w.equals("_")) return w;<br>    }<br>    return "_";<br>}<br>private String winnerOfRow(int i) {<br>    String first = board[i][0];<br>    if (first.equals("_")) return "_";<br>    for (int j = 1; j &lt; cols; j++)<br>        if (!board[i][j].equals(first)) return "_";<br>    return first;<br>}</pre> |

---

## Alexandre Turgeon — Elixir / ExUnit (`Lab4/fizzbuzz`, `Lab4/tic`)

Verified with `mix test` and `mix format --check-formatted`:
**fizzbuzz** = 11 tests, 0 failures; **tic** = 19 tests, 0 failures.

| Commit group / names | Commit number (hash) | Description |
| --- | --- | --- |
| FizzBuzz rejects non-positive values<br>red, green, refactor | RED `497fed7`<br>GREEN `f4e0309`<br>REFACTOR `7438826` | <b>What:</b> <code>fizzbuzz/1</code> raises <code>ArgumentError</code> for 0 or negative input, guarded by <code>is_positive_integer</code>.<br><br><b>RED — test written first:</b><pre>test "raises when the number is not positive" do<br>  assert_raise ArgumentError,<br>    "number must be a positive integer", fn -><br>    Fizzbuzz.fizzbuzz(0)<br>  end<br>end</pre><b>GREEN/REFACTOR — implementation:</b><pre>defguardp is_positive_integer(value)<br>          when is_integer(value) and value > 0<br><br>def fizzbuzz(n) when is_positive_integer(n), do: ...<br>def fizzbuzz(_),<br>  do: raise(ArgumentError, "number must be a positive integer")</pre> |
| FizzBuzz validates ranges<br>red, green, refactor | RED `b2079f0`<br>GREEN `9de7732`<br>REFACTOR `137a43b` | <b>What:</b> <code>fizzbuzz/2</code> raises unless both bounds are positive integers with start ≤ finish.<br><br><b>RED — test written first:</b><pre>test "raises when the range bounds are invalid" do<br>  assert_raise ArgumentError,<br>    "range must use positive integers with start &lt;= finish",<br>    fn -> Fizzbuzz.fizzbuzz(5, 1) end<br>end</pre><b>GREEN/REFACTOR — implementation:</b><pre>defguardp is_valid_range(first, last)<br>  when is_integer(first) and first > 0<br>  and is_integer(last) and last >= first<br><br>def fizzbuzz(n, m) when is_valid_range(n, m),<br>  do: Enum.map(n..m, &amp;fizzbuzz/1)<br>def fizzbuzz(_, _), do: raise ArgumentError, ...</pre> |
| Tic creates boards &amp; places marks<br>red, green, refactor | RED `c47e566`<br>GREEN `4e745a0`<br>REFACTOR `812d7ca` | <b>What:</b> <code>Tic.new/2</code> builds an empty board (X first); <code>place/2</code> marks a cell and renders via <code>to_string</code>.<br><br><b>RED — test written first:</b><pre>test "marks the current player and changes the turn" do<br>  board = Tic.new() &#124;> Tic.place(0)<br>  assert Enum.at(board.cells, 0) == "X"<br>  assert board.turn == "O"<br>end</pre><b>GREEN/REFACTOR — implementation:</b><pre>def place(%Tic{} = board, index) do<br>  board = with_cells(board)<br>  validate_move!(board, index)<br>  %{board &#124; cells: mark_cell(board, index),<br>            turn: next_turn(board.turn)}<br>end</pre> |
| Tic rejects invalid moves<br>red, green, refactor | RED `f1cc245`<br>GREEN `41ccf13`<br>REFACTOR `8164e31` | <b>What:</b> <code>place/2</code> raises for out-of-board indices and for already-occupied cells.<br><br><b>RED — test written first:</b><pre>test "rejects moves on occupied cells" do<br>  board = Tic.new() &#124;> Tic.place(0)<br>  assert_raise ArgumentError,<br>    "cell is already occupied",<br>    fn -> Tic.place(board, 0) end<br>end</pre><b>GREEN/REFACTOR — implementation:</b><pre>defp validate_move!(board, index) do<br>  cond do<br>    outside_board?(board, index) -><br>      raise ArgumentError, "move is outside the board"<br>    occupied?(board, index) -><br>      raise ArgumentError, "cell is already occupied"<br>    true -> :ok<br>  end<br>end</pre> |
| Tic detects winners &amp; draws<br>red, green, refactor | RED `7d21f13`<br>GREEN `4dab612`<br>REFACTOR `22afadf` | <b>What:</b> <code>winner/1</code> scans rows, columns and diagonals; <code>status/1</code> reports {:winner, m}, :draw or :playing.<br><br><b>RED — test written first:</b><pre>test "detects a row winner" do<br>  board = Tic.new()<br>    &#124;> Tic.place(0) &#124;> Tic.place(3) &#124;> Tic.place(1)<br>    &#124;> Tic.place(4) &#124;> Tic.place(2)<br>  assert Tic.winner(board) == "X"<br>  assert Tic.status(board) == {:winner, "X"}<br>end</pre><b>GREEN/REFACTOR — implementation:</b><pre>def winner(%Tic{} = board) do<br>  board &#124;> with_cells() &#124;> winning_lines()<br>        &#124;> Enum.find_value(&amp;winning_mark/1)<br>end<br>defp winning_mark(line) do<br>  case Enum.uniq(line) do<br>    [@empty] -> nil<br>    [mark]   -> mark<br>    _        -> nil<br>  end<br>end</pre> |

---

### Support commits (Alexandre)

- `4cb3a83` restored the corrupted Tic `mix.exs`.
- `ec569a8` repaired the Tic test helper and normalized line endings.
- `c578e5d` formatted the Elixir projects.
- `1cb35cd` documented the TDD commit groups.

**Combined total: 10 commit groups (5 + 5).**
