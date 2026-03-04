# Git Commit Instructions

Purpose: Deterministic, compact rules for generating high‑quality Conventional Commit messages with a single, most
specific emoji. Optimized for GPT-4.1 & GPT-5-mini.

Version: 2.4.15

---

## 1. Minimal Format

```
<type>[optional scope]: <emoji> <imperative subject ≤50 chars>

[body (what + why, wrapped ~72 chars)]
[optional footers: Closes #123 / BREAKING CHANGE: ... / Co-authored-by: ...]
```

---

## 2. Commit Type Decision

🚨 **CHECK FILE PATHS FIRST** 🚨

**IMPORTANT: Only analyze STAGED files (files that will be committed).**

- Do NOT analyze unstaged or untracked files.
- Do NOT analyze the entire working directory.
- ONLY look at the files that are staged for commit.

---

### PATH 1: Test Files → Use `test` Type

**Is this a test file change?**

A file is a **test file** if its path or name matches **ANY** of these:

- Path contains `/test/` → e.g., `src/test/`, `lib/test/`
- Path contains `/spec/` → e.g., `src/spec/`, `app/spec/`
- Path contains `/__tests__/` → e.g., `src/__tests__/`
- Filename contains `Test` → e.g., `UserTest.java`, `ServiceTest.groovy`
- Filename contains `Spec` → e.g., `UserSpec.groovy`, `service.spec.js`

**If ALL staged files are test files:**

```
✅ Use type: test
✅ Go directly to Section 3 (Emoji Selection)
❌ Do NOT evaluate what the change does
❌ Do NOT use feat, fix, refactor, etc.
```

**Examples:**

- ✅ `src/test/groovy/jobs/scripts/RepoCloneAndCheckoutBranchScript_Job.groovy` → **test**
- ✅ `src/test/java/com/example/UserServiceTest.java` → **test**
- ✅ `src/spec/services/AuthSpec.js` → **test**

**Correct messages for test file changes:**

- ✅ `test(script): 🔊 Add logging to test`
- ✅ `test: ♻️ Simplify test setup`
- ✅ `test: 🐛 Fix failing test`

**Wrong messages for test file changes:**

- ❌ `feat(script): ✨ Add logging to test`
- ❌ `refactor(test): ♻️ Simplify test setup`
- ❌ `fix(test): 🐛 Fix failing test`

---

### PATH 2: Non-Test Files → Evaluate Change Type

**If at least one staged file is NOT a test file, evaluate in this order (stop at first match):**

1. **docs** – Only docs / code comments (markdown & inline code comments)
2. **style** – Formatting / cosmetic (no behavior change)
3. **fix** – Bug / incorrect logic / defect / security patch
4. **perf** – Pure performance optimization (no new capability)
5. **feat** – New user-visible capability (incl. infra enabling feature)
6. **refactor** – Internal restructuring (no behavior change)
7. **ci** – Pipelines / workflow automation / deployment YAML
8. **build** – Build system / dependencies / Dockerfile / packaging
9. **chore** – Maintenance not covered above

**Special case: Mixed changes (production + test files)**

- Production code + new tests for feature → **feat** (production code determines type)
- Bug fix + new test → **fix** (production code determines type)
- New feature + new tests → **feat** (production code determines type)

---

## 3. Emoji Selection Priority (Pick ONE)

**Decision Steps (APPLY IN ORDER, FIRST MATCH WINS):**

1. **Special Override Cases:**
  - Revert commit → ⏪ (always, regardless of other rules)
  - Security patch → 🔒 (unless revert)
  - Deletion dominant (≥50% net lines removed) → 🔥 (unless security or revert)

2. **File-Type Emoji:**
  - **IMPORTANT: File-type emojis apply ONLY to production/configuration files, NOT test files.**
  - If the commit type is `test`, skip file-type emoji evaluation and go to Action/Content emoji.
  - If any production file matches a listed type (see below), use its emoji.

3. **Action/Content Emoji:**
  - If no file-type match, check for listed actions or content and use its emoji.
  - **Note:** Action emojis are SPECIFIC and take priority over generic fallbacks.

4. **Generic Fallback:**
  - If neither above applies, use the generic emoji for the commit type.

5. **Tie-Breakers (if multiple candidates remain):**
  - Multiple file-types: choose one reflecting primary intent.
  - Perf + new capability → feat (emoji from file-type if present).
  - Tests + prod code for fix: fix (emoji rule applies).

**Key Principle:** Specific action/content emojis (like 🔊 for "Add logs") beat generic type emojis (like ✅ for test).

File-Type Emojis:

- Terraform (.tf*) → 🏗️
- Dockerfile → 🐳
- Kubernetes manifests (yaml with k8s kinds) → ☸
- DB schema/migration (sql, liquibase, flyway) → 🗃️
- Security/auth config → 🔏
- Ignore files (.gitignore, .dockerignore) → 🙈
- UI/CSS/styling → 💄
- App config (application*.properties/yml/yaml) → 🔧
- Copilot files (.github/*.md, AGENTS.md, CLAUDE.md, GEMINI.md) → 🤖

Action/Content Emojis:

- Error handling → 🥅
- WIP → 🚧
- Tests only → ✅
- Comments only → 💡
- Docs only → 📚
- Initial bootstrap → 🎉
- Security fix → 🔒
- Deletion dominant (≥50% net lines removed) → 🔥
- Upgrade dependency → ⬆️
- Downgrade dependency → ⬇️
- Add dependency → ➕
- Remove dependency → ➖
- Pure move/rename → ✈���
- Perf optimization → 🐎
- Deployment/release → 🚀
- **Add logs/logging** → 🔊
- **Remove logs/logging** → 🔇

Generic Fallbacks:

- feat ✨
- fix 🐛
- docs 📚
- style 🎨
- refactor ♻️
- perf 🐎
- test ✅
- build 🏗️
- ci 💚
- chore 🧹
- revert ⏪

**Apply these tie-breakers if more than one emoji candidate matches:**

1. **Specific beats generic:**
  - If both a specific action/content emoji and a generic type fallback apply, **always use the specific emoji**.
  - Example: Adding logging in a test file → 🔊 (specific action) NOT ✅ (generic test fallback)

2. **Security patch:**
  - If the commit is a security patch, use 🔒.
  - Exception: If the commit is a revert, use ⏪.

3. **Performance + new capability:**
  - If the commit improves performance and adds a new capability, use `feat` type and select the emoji from file-type
    if present.
  - If purely performance, use 🐎.

4. **Multiple file-type candidates:**
  - If more than one file-type emoji applies, choose the emoji that best reflects the main purpose of the commit (
    e.g., infra provisioning 🏗️ vs container tweak 🐳).

5. **Deletion dominant:**
  - If the commit’s main intent is deletion (≥50% net lines removed), use 🔥.
  - Exception: If it’s a security patch, use 🔒; if it’s a revert, use ⏪.

6. **Tests + production code for fix:**
  - If the commit fixes a bug and includes both tests and production code, use `fix` type and apply the emoji rule.
  - If only tests are changed, use ✅ or 📸.

---

**Example Application:**

- If a commit updates both a Dockerfile and a Kubernetes manifest, but the main change is to the Dockerfile, use 🐳.
- If a commit removes a deprecated module and also updates documentation, but the deletion is the main intent, use 🔥.
- **If a commit only modifies test files and adds logging statements, use `test` type with 🔊 emoji (action-specific
  beats generic).**
- **If a commit adds a new test file with no other changes, use `test` type with ✅ emoji (no more specific action
  applies).**

---

## 4. Subject Line Rules

All subject lines **must** follow the Minimal Format: `<type>[optional scope]: <emoji> <imperative subject ≤50 chars>`

- Start with an imperative verb: (e.g., Add, Fix, Update, Remove, Refactor, Optimize, Improve, Secure, Harden, Enforce,
  Document, Configure)
- Be specific and descriptive: Clearly state what was changed and where. Prefer "Update API error handling for user
  login" over "Update error handling."
- Include affected component, feature, or file if possible: E.g., "Refactor payment service validation logic"
- Summarize the main change: Focus on what is most important for someone scanning commit history.
- Highlight the intent or outcome within the subject, as space allows. E.g., "Fix login bug preventing user
  authentication"
- Mention user-facing impact if relevant, but only if it fits within the character limit and minimal format.
- ≤50 characters recommended (60 hard max); no trailing period
- Avoid redundancy: ("Add new feature" → "Add feature")
- Avoid vague terms: Do not use "misc", "various", "updates", or similar.
- Avoid jargon or abbreviations unless standard for the project.

**Examples (all fit the minimal format):**

- fix(auth): 🐛 Resolve token refresh bug in OAuth flow
- feat(api): ✨ Add pagination to /users endpoint
- refactor(db): ♻ Simplify query logic for order retrieval
- improve(upload): 💡 Enhance error messages for failed uploads
- remove(ui): 🔥 Delete deprecated UserProfile component
- feat(settings): ✨ Add dark mode toggle for user preferences

---

## 5. Body

A commit body should be included for all commits except the most trivial changes (such as typo fixes, formatting, or
single-line updates with self-explanatory subjects). The body provides valuable context for reviewers and future
maintainers.

- **Explain WHAT changed:** Describe the main modifications, listing key files, functions, or behaviors affected.
- **Explain WHY the change was needed:** Provide context, such as the problem being solved, motivation, or related
  discussion.
- **Describe IMPACT:** Note any effects on users, systems, or dependencies. Mention migration steps, breaking changes,
  or follow-up actions if relevant.
- **Avoid restating the subject verbatim:** Expand on the summary, adding details and rationale.
- **Use bullet points or paragraphs for clarity:** Structure information for easy scanning.
- **Reference related issues, tickets, or discussions in footers.**

**Include a commit body unless the change is extremely simple and self-explanatory.**

**Example:**

    This commit updates the error handling logic in the user login API.

    - Refactored the token refresh mechanism to prevent expired tokens from being accepted.
    - Improved logging for failed authentication attempts.
    - Updated unit tests to cover new error scenarios.

    Motivation: Users were intermittently unable to log in due to token expiration issues. This change ensures more reliable
    authentication and better traceability for failures.

    No database migration required. Related to #123.

---

## 6. Footers

Examples:

- Closes #123
- BREAKING CHANGE: reason + migration notes
- Co-authored-by: Name <email>

Breaking Change Syntax: Either type or scope (e.g., feat(api):) plus BREAKING CHANGE footer.
