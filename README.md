# TrimCraft

TrimCraft adds active abilities to Minecraft armor trims. A matching armor set unlocks two abilities, activated with sneak + left click or sneak + right click.

## Requirements

- Minecraft 1.21 or newer
- Java 21
- Spigot or Paper
- No external plugin dependencies

SQLite is used by default. MySQL is available for servers that want to share cooldown persistence through an external database.

## Build

~~~shell
mvn --batch-mode clean verify
~~~

The shaded plugin is written to:

~~~text
target/TrimCraft-3.1.0.jar
~~~

GitHub Actions runs the same Maven verification on pushes and pull requests. Download the **TrimCraft-JAR** artifact from a successful workflow run to get the compiled plugin and its SHA-256 checksum.

## Install

1. Stop the server.
2. Place the JAR in the server's plugins directory.
3. Start the server once to generate plugins/TrimCraft/config.yml.
4. Adjust the configuration and restart the server.

## Using abilities

Wear four armor pieces with the same armor trim pattern. Sneak and left-click to use the first ability, or sneak and right-click to use the second.

The require-full-set setting is available for each individual ability. Trim activation can also be controlled with trimcraft.use and trimcraft.trim.<trim> permissions.

## Commands

| Command | Description |
| --- | --- |
| /trimcraft | Show command help |
| /trimcraft list | List registered trims |
| /trimcraft info <trim> | Show a trim's abilities and cooldowns |
| /trimcraft reload | Reload the configuration |
| /triminfo | Show the abilities and cooldowns for your equipped trim |

Administrative commands require trimcraft.admin. The ability information command requires trimcraft.use.

## Persistence

SQLite is the default and requires no setup. To use MySQL, set database.type to mysql and fill in the connection details under database.mysql. Keep database credentials private and do not commit production passwords to source control.

## Included trims

Silence, Wild, Coast, Dune, Eye, Host, Raiser, Sentry, Shaper, Snout, Spire, Tide, Vex, Ward, Wayfinder, Bolt, Flow, and Rib.
