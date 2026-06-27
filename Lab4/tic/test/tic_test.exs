defmodule TicTest do
  use ExUnit.Case
  doctest Tic

  def clean(expected), do: String.trim(expected)

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
end
