defmodule Fizzbuzz do
  @moduledoc """
  Caclulate FizzBuzz
  1
  2
  Fizz
  4
  Buzz
  Fizz
  7
  8
  Fizz
  Buzz
  11
  Fizz
  13
  14
  FizzBuzz
  """
  defguardp is_positive_integer(value) when is_integer(value) and value > 0

  defguardp is_valid_range(first, last)
            when is_integer(first) and first > 0 and is_integer(last) and last >= first

  def print_fizzbuzz() do
    Enum.each(fizzbuzz(), &IO.puts(&1))
  end

  def fizzbuzz(), do: fizzbuzz(1, 100)

  def fizzbuzz(n) when is_positive_integer(n) do
    cond do
      multiple_of?(n, 3) && multiple_of?(n, 5) -> "FizzBuzz"
      multiple_of?(n, 3) -> "Fizz"
      multiple_of?(n, 5) -> "Buzz"
      :else -> n
    end
  end

  def fizzbuzz(_), do: raise(ArgumentError, "number must be a positive integer")

  def fizzbuzz(n, m) when is_valid_range(n, m) do
    Enum.map(n..m, fn x -> fizzbuzz(x) end)
  end

  def fizzbuzz(_, _) do
    raise ArgumentError, "range must use positive integers with start <= finish"
  end

  defp multiple_of?(n, divisor), do: rem(n, divisor) == 0
end
