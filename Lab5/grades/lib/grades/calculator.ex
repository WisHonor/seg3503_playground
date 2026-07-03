defmodule Grades.Calculator do
  @scale [
    {90, "A+", 10},
    {85, "A", 9},
    {80, "A-", 8},
    {75, "B+", 7},
    {70, "B", 6},
    {65, "C+", 5},
    {60, "C", 4},
    {55, "D+", 3},
    {50, "D", 2},
    {40, "E", 1}
  ]

  def percentage_grade(grades) do
    0.20 * average(grades[:homework]) +
      0.10 * average(grades[:labs]) +
      0.25 * parse_grade(grades[:midterm]) +
      0.45 * parse_grade(grades[:final])
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

  defp average(grades) when is_list(grades) do
    parsed_grades =
      grades
      |> Enum.reject(&blank?/1)
      |> Enum.map(&parse_grade/1)

    case parsed_grades do
      [] -> 0.0
      parsed_grades -> Enum.sum(parsed_grades) / length(parsed_grades)
    end
  end

  defp average(_grades), do: 0.0

  defp grade_scale(percentage) do
    case Enum.find(@scale, fn {minimum, _letter, _numeric} -> percentage >= minimum end) do
      {_minimum, letter, numeric} -> {letter, numeric}
      nil -> {"F", 0}
    end
  end

  defp parse_grade(value) when is_number(value), do: value * 1.0

  defp parse_grade(value) do
    parsed_value =
      value
      |> to_string()
      |> String.trim()
      |> Float.parse()

    case parsed_value do
      {grade, ""} -> grade
      _other -> 0.0
    end
  end

  defp blank?(nil), do: true

  defp blank?(value) do
    value
    |> to_string()
    |> String.trim()
    |> Kernel.==("")
  end
end
