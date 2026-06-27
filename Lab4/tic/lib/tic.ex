defmodule Tic do
  @moduledoc """
  Implementing a tic tac toe game.
  """

  @empty "_"

  defstruct rows: 3, cols: 3, cells: nil, turn: "X"

  def new(rows \\ 3, cols \\ 3)

  def new(rows, cols) when is_integer(rows) and rows > 0 and is_integer(cols) and cols > 0 do
    %Tic{
      rows: rows,
      cols: cols,
      cells: List.duplicate(@empty, rows * cols),
      turn: "X"
    }
  end

  def new(_, _), do: raise(ArgumentError, "board dimensions must be positive integers")

  def size(%Tic{rows: r, cols: w}), do: r * w

  def place(%Tic{} = board, index) do
    board = with_cells(board)
    validate_move!(board, index)

    %{
      board
      | cells: mark_cell(board, index),
        turn: next_turn(board.turn)
    }
  end

  def winner(%Tic{} = board) do
    board
    |> with_cells()
    |> winning_lines()
    |> Enum.find_value(&winning_mark/1)
  end

  def draw?(%Tic{} = board) do
    board = with_cells(board)

    is_nil(winner(board)) and full?(board)
  end

  def status(%Tic{} = board) do
    board = with_cells(board)

    case winner(board) do
      nil ->
        if full?(board), do: :draw, else: :playing

      mark ->
        {:winner, mark}
    end
  end

  @doc """
  Create a string to represent the board
  """
  def to_string(%Tic{} = board, opts \\ []) do
    board = with_cells(board)

    0..(board.rows - 1)
    |> Enum.map(fn row_index -> to_string_row(board, row_index, opts[:show_index]) end)
    |> Enum.join("\n")
  end

  defp to_string_row(board, row_index, show_index) do
    0..(board.cols - 1)
    |> Enum.map(fn col_index ->
      to_string_cell(board, row_index * board.cols + col_index, show_index)
    end)
    |> Enum.join("|")
  end

  defp to_string_cell(board, index, true) do
    case cell_at(board, index) do
      @empty -> Integer.to_string(index)
      mark -> mark
    end
  end

  defp to_string_cell(board, index, _), do: cell_at(board, index)

  defp cell_at(board, index), do: Enum.at(board.cells, index)

  defp validate_move!(board, index) do
    cond do
      outside_board?(board, index) -> raise ArgumentError, "move is outside the board"
      occupied?(board, index) -> raise ArgumentError, "cell is already occupied"
      true -> :ok
    end
  end

  defp outside_board?(board, index) do
    not is_integer(index) or index < 0 or index >= size(board)
  end

  defp occupied?(board, index), do: cell_at(board, index) != @empty

  defp mark_cell(board, index), do: List.replace_at(board.cells, index, board.turn)

  defp winning_lines(board), do: row_lines(board) ++ column_lines(board) ++ diagonal_lines(board)

  defp row_lines(board) do
    0..(board.rows - 1)
    |> Enum.map(fn row_index ->
      0..(board.cols - 1)
      |> Enum.map(fn col_index -> cell_at(board, row_index * board.cols + col_index) end)
    end)
  end

  defp column_lines(board) do
    0..(board.cols - 1)
    |> Enum.map(fn col_index ->
      0..(board.rows - 1)
      |> Enum.map(fn row_index -> cell_at(board, row_index * board.cols + col_index) end)
    end)
  end

  defp diagonal_lines(%Tic{rows: size, cols: size} = board) do
    [
      Enum.map(0..(size - 1), fn index -> cell_at(board, index * size + index) end),
      Enum.map(0..(size - 1), fn index -> cell_at(board, index * size + (size - index - 1)) end)
    ]
  end

  defp diagonal_lines(_), do: []

  defp winning_mark(line) do
    case Enum.uniq(line) do
      [@empty] -> nil
      [mark] -> mark
      _ -> nil
    end
  end

  defp full?(board), do: Enum.all?(board.cells, fn cell -> cell != @empty end)

  defp with_cells(%Tic{cells: nil} = board), do: %{board | cells: empty_cells(board)}
  defp with_cells(%Tic{} = board), do: board

  defp empty_cells(%Tic{} = board), do: List.duplicate(@empty, size(board))

  defp next_turn("X"), do: "O"
  defp next_turn("O"), do: "X"
end
