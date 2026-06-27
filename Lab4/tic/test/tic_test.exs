defmodule TicTest do
  use ExUnit.Case
  doctest Tic

  def clean(expected), do: String.trim(expected)

  describe "new/2" do
    test "creates an empty board with X to play first" do
      board = Tic.new()

      assert board.rows == 3
      assert board.cols == 3
      assert board.cells == List.duplicate("_", 9)
      assert board.turn == "X"
    end

    test "creates a custom empty board" do
      board = Tic.new(2, 2)

      assert board.rows == 2
      assert board.cols == 2
      assert board.cells == List.duplicate("_", 4)
      assert Tic.to_string(board) == "_|_\n_|_"
    end

    test "rejects invalid dimensions" do
      assert_raise ArgumentError, "board dimensions must be positive integers", fn ->
        Tic.new(0, 3)
      end

      assert_raise ArgumentError, "board dimensions must be positive integers", fn ->
        Tic.new(3, 0)
      end
    end
  end

  describe "size/1" do
    test "default 3x3 = 9" do
      assert Tic.size(%Tic{}) == 9
    end

    test "set lxw" do
      assert Tic.size(%Tic{rows: 1, cols: 1}) == 1
      assert Tic.size(%Tic{rows: 2, cols: 3}) == 6
      assert Tic.size(%Tic{rows: 4, cols: 5}) == 20
    end
  end

  describe "to_string/1" do
    test "1x1" do
      assert Tic.to_string(%Tic{rows: 1, cols: 1}) == "_"
    end

    test "1xn" do
      assert Tic.to_string(%Tic{rows: 1, cols: 2}) == "_|_"
      assert Tic.to_string(%Tic{rows: 1, cols: 3}) == "_|_|_"
      assert Tic.to_string(%Tic{rows: 1, cols: 7}) == "_|_|_|_|_|_|_"
    end

    test "nx1" do
      expected = """
      _
      _
      """

      assert Tic.to_string(%Tic{rows: 2, cols: 1}) == clean(expected)

      expected = """
      _
      _
      _
      """

      assert Tic.to_string(%Tic{rows: 3, cols: 1}) == clean(expected)

      expected = """
      _
      _
      _
      _
      _
      _
      _
      """

      assert Tic.to_string(%Tic{rows: 7, cols: 1}) == clean(expected)
    end

    test "nxm" do
      expected = """
      _|_|_|_
      _|_|_|_
      _|_|_|_
      """

      assert Tic.to_string(%Tic{rows: 3, cols: 4}) == clean(expected)

      expected = """
      _|_|_
      _|_|_
      _|_|_
      _|_|_
      """

      assert Tic.to_string(%Tic{rows: 4, cols: 3}) == clean(expected)
    end

    test "default" do
      expected = """
      _|_|_
      _|_|_
      _|_|_
      """

      assert Tic.to_string(%Tic{}) == clean(expected)
    end

    test "show position" do
      expected = """
      0|1|2|3
      4|5|6|7
      8|9|10|11
      """

      assert Tic.to_string(%Tic{rows: 3, cols: 4}, show_index: true) == clean(expected)
    end
  end

  describe "place/2" do
    test "marks the current player and changes the turn" do
      board = Tic.new()

      board = Tic.place(board, 0)

      assert Enum.at(board.cells, 0) == "X"
      assert board.turn == "O"

      board = Tic.place(board, 4)

      assert Enum.at(board.cells, 4) == "O"
      assert board.turn == "X"
    end

    test "shows placed marks when rendering the board" do
      board =
        Tic.new()
        |> Tic.place(0)
        |> Tic.place(4)

      expected = """
      X|_|_
      _|O|_
      _|_|_
      """

      assert Tic.to_string(board) == clean(expected)
    end

    test "rejects moves outside the board" do
      board = Tic.new()

      assert_raise ArgumentError, "move is outside the board", fn ->
        Tic.place(board, -1)
      end

      assert_raise ArgumentError, "move is outside the board", fn ->
        Tic.place(board, 9)
      end
    end

    test "rejects moves on occupied cells" do
      board = Tic.new() |> Tic.place(0)

      assert_raise ArgumentError, "cell is already occupied", fn ->
        Tic.place(board, 0)
      end
    end
  end

  describe "game status" do
    test "detects a row winner" do
      board =
        Tic.new()
        |> Tic.place(0)
        |> Tic.place(3)
        |> Tic.place(1)
        |> Tic.place(4)
        |> Tic.place(2)

      assert Tic.winner(board) == "X"
      assert Tic.status(board) == {:winner, "X"}
    end

    test "detects a diagonal winner" do
      board =
        Tic.new()
        |> Tic.place(0)
        |> Tic.place(1)
        |> Tic.place(4)
        |> Tic.place(2)
        |> Tic.place(8)

      assert Tic.winner(board) == "X"
      assert Tic.status(board) == {:winner, "X"}
    end

    test "detects a draw" do
      board =
        Tic.new()
        |> Tic.place(0)
        |> Tic.place(1)
        |> Tic.place(2)
        |> Tic.place(4)
        |> Tic.place(3)
        |> Tic.place(5)
        |> Tic.place(7)
        |> Tic.place(6)
        |> Tic.place(8)

      assert Tic.winner(board) == nil
      assert Tic.draw?(board)
      assert Tic.status(board) == :draw
    end

    test "detects a game still in progress" do
      board = Tic.new() |> Tic.place(0)

      assert Tic.winner(board) == nil
      refute Tic.draw?(board)
      assert Tic.status(board) == :playing
    end
  end
end
