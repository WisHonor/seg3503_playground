Code.require_file("lib/grades/calculator.ex")

ExUnit.start()

defmodule Grades.CalculatorCheck do
  use ExUnit.Case, async: true

  alias Grades.Calculator

  test "calculates percentage, letter, and numeric grades" do
    grades = %{
      homework: ["80", "90", "100", ""],
      labs: ["60", "70", "", "80", "90", "100"],
      midterm: "70",
      final: "85"
    }

    assert_in_delta Calculator.percentage_grade(grades), 75.1667, 0.001
    assert Calculator.letter_grade(grades) == "B+"
    assert Calculator.numeric_grade(grades) == 8
  end

  test "handles blank and invalid fields defensively" do
    grades = %{
      homework: ["", "bad", "100", ""],
      labs: ["", "", "", "", "", ""],
      midterm: "",
      final: "not a number"
    }

    assert_in_delta Calculator.percentage_grade(grades), 5.0, 0.001
    assert Calculator.letter_grade(grades) == "F"
    assert Calculator.numeric_grade(grades) == 0
  end
end
