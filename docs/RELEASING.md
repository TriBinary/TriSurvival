# TriSurvival - Changelog & Releasing

This guide covers how to maintain [CHANGELOG.md](../CHANGELOG.md) during development and how to publish a release on
GitHub. The release itself is automated by [.github/workflows/release.yml](../.github/workflows/release.yml) — pushing
a version tag builds the plugin, extracts the matching changelog section, and creates the GitHub Release with the jar
attached. Every push and pull request is also built by [.github/workflows/build.yml](../.github/workflows/build.yml).

## Changelog format

The changelog follows the [SkyHanni](https://github.com/hannibal002/SkyHanni) style:

```markdown
# TriSurvival - Change Log

## Unreleased

## Version 1.1.0

### New Features

#### Skills

+ Added the Taming skill.
    + Gain XP by breeding and taming animals.

### Improvements

#### Stats

+ Showed stat symbols next to gear bonuses in item lore.

### Fixes

#### Fishing

+ Fixed sea creatures spawning on land.
```

Structure, top to bottom:

| Level  | Heading                        | Purpose                                                                               |
|--------|--------------------------------|---------------------------------------------------------------------------------------|
| `##`   | `Unreleased` / `Version X.Y.Z` | One section per release; `Unreleased` collects entries during development             |
| `###`  | Category                       | `New Features`, `Improvements`, `Fixes`, `Technical Details`, `Removed Features`      |
| `####` | Feature area                   | `Skills`, `Stats`, `Mobs`, `Fishing`, `Misc`, … — free-form, `Misc` is the catch-all  |
| `+`    | Entry                          | One change per bullet; indent `+` sub-bullets for details                             |

Rules of thumb:

- Add an entry under `## Unreleased` in the same commit (or PR) as the change itself, so the changelog never lags
  behind.
- Only include categories and feature areas that actually have entries — omit empty ones.
- Write entries for players and server owners, not developers: "Added the Taming skill", not
  "Refactored SkillManager". Developer-facing changes go under `Technical Details`.
- With outside contributors, append attribution like SkyHanni does:
  `+ Added X. - Name (https://github.com/Trilleo/TriSurvival/pull/123)`

## Versioning

`plugin_version` in [gradle.properties](../gradle.properties) is the single source of truth — it flows into the jar
filename and the `version` field of `plugin.yml` automatically. Follow semver: **patch** for bugfixes, **minor** for new
features, **major** for breaking changes (e.g. config or data resets).

## Release walkthrough

Example: releasing version `1.1.0`.

1. **Finalize the changelog** — in `CHANGELOG.md`, rename the `## Unreleased` heading to `## Version 1.1.0` and add a
   fresh empty `## Unreleased` above it:

   ```markdown
   ## Unreleased

   ## Version 1.1.0
   ...entries...
   ```

2. **Bump the version** — in `gradle.properties`:

   ```properties
   plugin_version=1.1.0
   ```

3. **Verify the build** (optional but recommended):

   ```
   ./gradlew build
   ```

4. **Commit and tag** — the tag must be the version prefixed with `v`:

   ```
   git commit -am "Update: Plugin version 1.1.0 release"
   git tag v1.1.0
   ```

5. **Push** — this is the publish step; the release workflow fires on the tag:

   ```
   git push && git push --tags
   ```

6. **Check the result** — the `release` workflow under the repo's *Actions* tab builds the jar and creates the GitHub
   Release. Verify the release page shows the changelog text and has `TriSurvival-1.1.0.jar` attached.

> **Never tag or push tags unless you have been explicitly asked to.** Pushing a tag publishes a release.

## How the automation matches things up

- The workflow strips the `v` from the tag (`v1.1.0` → `1.1.0`) and extracts everything between `## Version 1.1.0` and
  the next `## ` heading in `CHANGELOG.md`. That text becomes the release body.
- **If no matching section exists, the workflow fails** — a release cannot ship with an empty changelog. Fix the heading
  (exact match: `## Version 1.1.0`) and re-run the workflow, or delete and re-push the tag.
- The plugin jar from `build/libs/` is attached.

## Fixing a botched release

- **Wrong changelog / missing section**: fix `CHANGELOG.md`, commit, then move the tag and re-push it:

  ```
  git tag -f v1.1.0
  git push -f origin v1.1.0
  ```

  Delete the draft/failed release on GitHub first if one was created.

- **Wrong version in the jar**: you tagged before bumping `plugin_version`. Bump it, commit, move the tag as above.
