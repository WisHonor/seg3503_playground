# SEG3503 - Lab 4 : Test-Driven Development

| Outline | Value |
| --- | --- |
| Course | SEG 3503 |
| Date | Summer 2026 |
| Student | Alexandre Turgeon |
| Professor | Mouhcine Guennoun |
| TA | Mohamed Nefsi |

This lab uses the provided `fizzbuzz` and `tic` Elixir projects to practise the
TDD cycle: RED, GREEN, and REFACTOR. The work is located in
`Teamwork/seg3503_playground/Lab4`.

## Test Results

Commands executed from each project directory:

```powershell
cd Lab4/fizzbuzz
mix test
mix format --check-formatted

cd ../tic
mix test
mix format --check-formatted
```

Verified results:

- `fizzbuzz`: 11 tests, 0 failures
- `tic`: 19 tests, 0 failures
- `mix format --check-formatted`: passes for both projects

## TDD Commit Groups

Each group identifies a failing test commit, the implementation commit that makes
it pass, and a refactor commit where the tests still pass.

| Group | RED | GREEN | REFACTOR |
| --- | --- | --- | --- |
| 1 - FizzBuzz rejects non-positive values | `497fed7` | `f4e0309` | `7438826` |
| 2 - FizzBuzz validates ranges | `b2079f0` | `9de7732` | `137a43b` |
| 3 - Tic creates boards and places marks | `c47e566` | `4e745a0` | `812d7ca` |
| 4 - Tic rejects invalid moves | `f1cc245` | `41ccf13` | `8164e31` |
| 5 - Tic detects winners and draws | `7d21f13` | `4dab612` | `22afadf` |

Support commits:

- `4cb3a83` restored the corrupted Tic `mix.exs` file.
- `ec569a8` restored the corrupted Tic test helper and normalized Windows
  line endings in expected strings.
- `c578e5d` formatted the Elixir projects.
