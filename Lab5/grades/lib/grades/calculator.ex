defmodule Grades.Calculator do
  # University of Ottawa style grading scale.
  # Each tuple is {minimum_percentage, letter, numeric}.
  @scale [
    {90, "A+", 10},
    {85, "A", 10},
    {80, "A-", 9},
    {75, "B+", 8},
    {70, "B", 7},
    {65, "B-", 6},
    {60, "C+", 5},
    {55, "C", 4},
    {50, "D", 3}
  ]

  # Weights: homework 20% (5% each of 4), labs 10% (avg), midterm 30%, final 40%.
  def percentage_grade(grades) do
    0.20 * average(grades[:homework]) +
      0.10 * average(grades[:labs]) +
      0.30 * parse_grade(grades[:midterm]) +
      0.40 * parse_grade(grades[:final])
  end

  def letter_grade(grades) do
    grades
    |> percentage_grade()
    |> grade_scale()
    |> elem(0)
  end

  def numeric_grade(grades) do
    grades
    |> percentage_grade()
    |> grade_scale()
    |> elem(1)
  end

  # Every slot counts: blank/nil entries are treated as 0 and still divide
  # into the average, so "5% each" holds for the 4 homeworks (0.20 * sum / 4).
  defp average(grades) when is_list(grades) and grades != [] do
    grades
    |> Enum.map(&parse_grade/1)
    |> Enum.sum()
    |> Kernel./(length(grades))
  end

  defp average(_grades), do: 0.0

  defp grade_scale(percentage) do
    case Enum.find(@scale, fn {minimum, _letter, _numeric} -> percentage >= minimum end) do
      {_minimum, letter, numeric} -> {letter, numeric}
      nil -> {"F", 0}
    end
  end

  defp parse_grade(value) when is_number(value), do: value * 1.0

  # Empty strings, nil, and unparseable values all become 0.0.
  defp parse_grade(value) do
    case value |> to_string() |> String.trim() |> Float.parse() do
      {grade, ""} -> grade
      _other -> 0.0
    end
  end
end
