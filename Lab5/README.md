# SEG3503 - Lab 5 : Stubs, Mocks, and Architecture

| Outline | Value |
| --- | --- |
| Course | SEG 3503 |
| Date | Summer 2026 |
| Student | Alexandre Turgeon |
| Professor | Mouhcine Guennoun |
| TA | Mohamed Nefsi |

This lab uses the provided `grades` and `twitter` starter projects to practise
temporary stubs, mock collaborators, and small architectural choices that make
logic easier to test. The work is located in
`Teamwork/seg3503_playground/Lab5`.

## Setup Notes

The starter zips were extracted into:

```text
Lab5/
+-- grades/
+-- twitter/
```

The zip metadata directories/files (`__MACOSX`, `.DS_Store`) were removed, and
the redundant `twitter/lib/easymock-4.3-bundle.zip` was removed while keeping
`twitter/lib/easymock-4.3.jar`.

The `grades` project is a legacy Phoenix 1.5 / LiveView 0.15 project. On this
machine, `mix deps.get`, `mix compile`, `mix format --check-formatted`, and
`elixir calculator_check.exs` all stopped immediately with:

```text
Cannot find erlexec.dll
```

For that reason, the grade calculation was kept in the pure module
`Grades.Calculator`, with both a normal ExUnit test and a standalone
`calculator_check.exs` runner that can be executed on a working Elixir/Erlang
installation without booting Phoenix.

The `twitter` project has Windows launchers in addition to the original bash
scripts:

```powershell
cd Lab5/twitter
.\bin\test.ps1
```

```cmd
cd Lab5\twitter
bin\test.bat
```

Both Windows scripts use `;` classpath separators and pass
`--add-opens java.base/java.lang=ALL-UNNAMED`, which EasyMock 4.3 needs on
modern Java versions.

## Grades

### Stub

The temporary calculator stub returned fixed values:

```elixir
defmodule Grades.Calculator do
  # STUB - temporary constant values, ignores the grades map.
  def letter_grade(_grades), do: "A+"
  def numeric_grade(_grades), do: 10
  def percentage_grade(_grades), do: 95.0
end
```

Observation: with the stub, the LiveView can call the missing module, but every
input produces the same `A+`, `10`, and `95.0`. This is useful for unblocking UI
work, but it does not validate parsing, weighting, blanks, or grade boundaries.

### Real Calculation

The real calculator expects the map built by `GradesWeb.PageLive`:

```elixir
%{
  homework: ["...", "...", "...", "..."],
  labs: ["...", "...", "...", "...", "...", "..."],
  midterm: "...",
  final: "..."
}
```

List averages ignore blank fields. Nonblank invalid entries parse as `0.0`.
Blank or invalid midterm/final values also parse as `0.0`.

Formula:

```text
percentage =
  0.20 * avg(homework) +
  0.10 * avg(labs) +
  0.25 * midterm +
  0.45 * final
```

Scale:

| Percentage | Letter | Numeric |
| --- | --- | --- |
| 90+ | A+ | 10 |
| 85-89 | A | 9 |
| 80-84 | A- | 8 |
| 75-79 | B+ | 7 |
| 70-74 | B | 6 |
| 65-69 | C+ | 5 |
| 60-64 | C | 4 |
| 55-59 | D+ | 3 |
| 50-54 | D | 2 |
| 40-49 | E | 1 |
| below 40 | F | 0 |

Worked example from the tests:

| Component | Values | Result |
| --- | --- | --- |
| Homework | `80, 90, 100, blank` | average `90.0` |
| Labs | `60, 70, blank, 80, 90, 100` | average `80.0` |
| Midterm | `70` | `70.0` |
| Final | `85` | `85.0` |

```text
0.20*90 + 0.10*80 + 0.25*70 + 0.45*85 = 81.75
```

Expected result: `81.75`, `A-`, numeric grade `8`.

Verification files:

- `grades/test/grades/calculator_test.exs`
- `grades/calculator_check.exs`

Commands for a working Elixir installation:

```powershell
cd Lab5/grades
mix test
elixir calculator_check.exs
```

## Twitter

The Twitter starter has a slow/random `loadTweet()` collaborator. The mock tests
replace only that collaborator so `isMentionned` can be tested deterministically.
The extracted `actual_call()` test also depended on the random tweet and failed
on the first Windows-script run, so it was stabilized with the same partial-mock
helper before the red bug tests were recorded.

### Mock Tests

| Test | Mocked `loadTweet()` | Expected assertions |
| --- | --- | --- |
| `isMentionned_lookForAtSymbol` | `"hello @me"` twice | `me` true, `you` false |
| `isMentionned_dontReturnSubstringMatches` | `"hello @meat"` twice | `me` false, `meat` true |
| `isMentionned_superStringNotFound` | `"hello @me"` twice | `me` true, `meat` false |
| `isMentionned_handleNull` | `null` twice | `me` false, `meat` false |

### Results

| Stage | Command | Result |
| --- | --- | --- |
| Starter Windows script | `.\bin\test.ps1` | Script compiled and ran; random `actual_call()` failed this run with `expected true but was false` |
| Red mock tests | `.\bin\test.ps1` | 7 tests, 5 passed, 2 failed |
| Green fix | `.\bin\test.ps1` | 7 tests, 7 passed, 0 failed |

Red failures:

- `isMentionned_dontReturnSubstringMatches`: `@meat` matched `me` because the
  original code used `tweet.contains("@" + name)`.
- `isMentionned_handleNull`: `loadTweet()` could return `null`, causing a
  `NullPointerException` before any assertion could complete.

Fix:

```java
if (tweet == null || name == null) {
  return false;
}

Pattern mention = Pattern.compile("@" + Pattern.quote(name) + "(?![A-Za-z0-9_])");
return mention.matcher(tweet).find();
```

The null guard handles missing tweets. `Pattern.quote(name)` avoids treating a
name as regex syntax, and the negative lookahead prevents `@meat` from matching
the requested username `me`.

## Commit Map

| Step | Commit | Description |
| --- | --- | --- |
| 1 | `3d720a6` | Add Lab5 grades starter |
| 2 | `b326eb4` | Add grades calculator stub |
| 3 | `83eaa1d` | Implement grades calculator |
| 4 | `a9b6c42` | Add Lab5 twitter starter |
| 5 | `aee9ff2` | Add twitter mention mock tests |
| 6 | `1a43957` | Fix twitter mention matching |
| 7 | current README commit | Document setup, observations, results, and commit map |

## Submission Note

The repo is already shared through `WisHonor/seg3503_playground`. The
BrightSpace submission should reference that repository and this `Lab5`
directory.
