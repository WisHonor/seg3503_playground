defmodule Tic do
  @moduledoc """
  Implementing a tic tac toe game.
  """

  defstruct rows: 3, cols: 3

  def size(%Tic{rows: r, cols: w}), do: r * w

  @doc """
  Create a string to represent the board
  """
  def to_string(%Tic{rows: n, cols: m}, opts \\ []) do
    0..(n - 1)
    |> Enum.map(fn row_index -> to_string_column(row_index, m, opts[:show_index]) end)
    |> Enum.join("\n")
  end

  defp to_string_column(row_index, num_columns, show_index) do
    0..(num_columns - 1)
    |> Enum.map(fn col_index ->
      to_string_cell(row_index * num_columns + col_index, show_index)
    end)
    |> Enum.join("|")
  end

  defp to_string_cell(index, true), do: "#{index}"
  defp to_string_cell(_, _), do: "_"
end
