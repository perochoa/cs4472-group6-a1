# How testing works in this repository

All graded work reaches `main` through pull requests from `dev`, in two
phases (below).

## Branches

- `main` is protected. Everything arrives via a squash-merged pull request
  from `dev`, approved by every teammate except the author.
- `dev` is the integration branch. Work happens on feature branches that
  are merged into `dev` by pull request (one approval).

## CI

`.github/workflows/ci.yml` runs on pull requests into `main` and `dev`
(opened, synchronize, reopened, labeled, unlabeled). Pushes don't trigger
it, so `dev` only receives changes that already passed. Both `Lint` and
`Tests` are required checks on `main` and `dev`.

- **Lint**: `mvn -B checkstyle:check` against `checkstyle.xml` on Java 11.
- **Tests**: requires **exactly one** of the `phase:tests` / `phase:fix`
  labels and fails otherwise, then runs the suite in that phase's mode.
  It publishes the JUnit results and the JaCoCo coverage report as PR
  comments, and uploads the coverage HTML as a workflow artifact.

## The two phases

Every pull request, into `dev` or `main`, carries exactly one label:

- **`phase:tests`** - the PR adds new JUnit tests derived from the
  specification (Assignment 1) or from the code structure (Assignment 2).
  Tests are _expected_ to fail: the code under test contains faults, and
  finding them is the point. CI checks that the code compiles, that
  Checkstyle passes, and that the test suite runs to completion; it then
  posts the list of failures as a PR comment. Failing tests do **not**
  block merging in this phase.
- **`phase:fix`** - the PR fixes the faults your tests localized. Here the
  gate is strict: the **entire** test suite must pass (`mvn -B test`),
  plus Checkstyle. A red suite blocks merging.

## Reviews

A PR to `main` needs approval from **every teammate except the author**
(3 reviews). Approvals are dismissed when new commits are pushed, so review
again after fixes are pushed.

## The automated checks

| Check             | What it does                                                               | Blocks merging?       |
| ----------------- | -------------------------------------------------------------------------- | --------------------- |
| Lint              | Checkstyle (`checkstyle.xml`): unused imports, braces, line length, naming | Always                |
| Tests             | `mvn -B test`; failures ignored in `phase:tests`, fatal in `phase:fix`     | In `phase:fix`        |
| JUnit test report | Posts pass/fail per test as a PR comment                                   | Never (informational) |
| Coverage          | JaCoCo report posted as a PR comment                                       | Never (informational) |

## Running things locally

```bash
mvn -B test                 # run the suite (fails the build on test failure)
mvn -B checkstyle:check     # run the linter
```

Use Java 11. The JaCoCo HTML report lands in
`target/site/jacoco/index.html` after `mvn -B test`.

## Test doubles

`Mockito` is on the test classpath. When a white-box target calls
collaborators (`FeesCalculator`, `DBHandler`), stub them so the target is
tested in isolation. Note: Mockito's strict stubbing rejects back-to-back
`when(...)` stubbings of the same method; prefer
`doReturn(x).when(mock)...` in that case.
