# Security policy

Please report vulnerabilities privately through GitHub's security advisory
workflow for this repository. Do not include server addresses, credentials,
world data, player data, or unredacted logs in a public issue.

No build is published or supported while this repository remains an
unreleased local implementation. After publication, only the latest
prerelease will be supported unless a release notice says otherwise.
Compatibility bugs and visual differences without security impact may be
reported as ordinary issues.

Exact artifact/schema activation and collision checks are safety boundaries,
not security sandboxes. The add-on runs in BlueMap's JVM with BlueMap's
permissions; install only reviewed release artifacts.
