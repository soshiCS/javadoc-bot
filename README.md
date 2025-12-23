# Javadoc Bot

Javadoc Bot is a Java 21 CLI tool that automatically generates missing Javadoc
for Java source code using AST analysis and an LLM.

It is designed to run locally or automatically on GitHub pull requests and
commit generated Javadoc back to the PR branch.

---

## What it does

- Detects changed `.java` files using Git diff
- Parses source code using JavaParser (BLEEDING_EDGE language level)
- Finds missing Javadoc on:
  - classes
  - interfaces
  - enums
  - methods
  - constructors
- Generates Javadoc using an LLM
- Ensures output is valid JavaDoc only
- Rewrites source safely via AST mutation
- Idempotent on re-runs (no duplicate changes)

---

## Requirements

- Java 21
- Git
- An OpenAI API key

---

## Environment setup

Set the OpenAI API key as an environment variable:

```bash
export OPENAI_API_KEY=sk-xxxxxxxx
```

---

## Running locally

From a Git repository with Java code:

```bash
java -jar javadoc-bot.jar --base HEAD~1
```

This generates Javadoc for Java files changed relative to the given Git base ref.

Examples:

```bash
--base HEAD~1
--base origin/main
--base origin/develop
```

---

## Using Javadoc Bot in another GitHub repository

### 1. Add the GitHub Actions workflow

Create this file in the target repository:

```
.github/workflows/javadoc-bot.yml
```

```yaml
name: Javadoc Bot

on:
  pull_request:
    types: [opened, synchronize, reopened]

permissions:
  contents: write
  pull-requests: write

jobs:
  javadoc:
    runs-on: ubuntu-latest

    if: github.actor != 'github-actions[bot]'

    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0
          ref: ${{ github.event.pull_request.head.ref }}

      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '21'

      - name: Fetch base branch
        run: |
          git fetch origin ${{ github.event.pull_request.base.ref }}

      - name: Download Javadoc Bot
        run: |
          curl -L -o javadoc-bot.jar             https://github.com/soshiCS/javadoc-bot/releases/download/v0.1.0/javadoc-bot-0.1.0.jar

      - name: Run Javadoc Bot
        env:
          OPENAI_API_KEY: ${{ secrets.OPENAI_API_KEY }}
        run: |
          java -jar javadoc-bot.jar             --base origin/${{ github.event.pull_request.base.ref }}

      - name: Commit generated Javadoc
        run: |
          if git diff --quiet; then
            exit 0
          fi

          git config user.name "javadoc-bot"
          git config user.email "javadoc-bot@users.noreply.github.com"

          git add .
          git commit -m "docs: add missing Javadoc (automated)"
          git push
```

---

### 2. Add the OpenAI API key to the repository

In the target repository:

- Go to **Settings → Secrets and variables → Actions**
- Add a new repository secret:
  - Name: `OPENAI_API_KEY`
  - Value: your OpenAI API key

---

## Behavior notes

- The bot runs automatically on pull request open and update
- Generated Javadoc is committed directly to the PR branch
- The bot avoids infinite loops by skipping bot-authored commits
- Forked PRs will run but cannot be pushed to (GitHub limitation)

---

## License

Internal / experimental tool. License can be added later.
