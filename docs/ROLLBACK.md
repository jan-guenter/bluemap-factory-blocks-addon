# Removal and rollback

The add-on writes no world, player, or required configuration state. Remove
only its JAR from BlueMap's packs directory, restart the JVM, and rerender the
affected region to restore stock output.

An artifact, schema, texture, or registry mismatch keeps the complete 35-ID
route inactive. An individual failure atomically discards custom output and
uses BlueMap's original renderer. No relaxed or partial-version profile exists.

The current prototype is unreleased and is not an operational rollback
artifact.
