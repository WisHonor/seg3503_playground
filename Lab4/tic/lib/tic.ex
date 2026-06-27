defmodule Tic do
  @moduledoc """
  Implementing a tic tac toe game.
  """

  @empty "_"

  defstruct rows: 3, cols: 3, cells: nil, turn: "X"

  def new(rows \\ 3, cols \\ 3) do
    %Tic{
      rows: rows,
      cols: cols,
      cells: List.duplicate(@empty, rows * cols),
      turn: "X"
    }
  end

  def size(%Tic{rows: r, cols: w}), do: r * w

  def place(%Tic{} = board, index) do
    board = with_cells(board)

    %{
      board
      | cells: List.replace_at(board.cells, index, board.turn),
        turn: next_turn(board.turn)
    }
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

  defp with_cells(%Tic{cells: nil} = board), do: %{board | cells: empty_cells(board)}
  defp with_cells(%Tic{} = board), do: board

  defp empty_cells(%Tic{} = board), do: List.duplicate(@empty, size(board))

  defp next_turn("X"), do: "O"
  defp next_turn("O"), do: "X"
end
