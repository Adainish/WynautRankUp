# WynautRankUp

WynautRankUp is a Java-based mod for managing ranked battles, ELO ratings, and match history in a competitive environment (e.g., Minecraft servers). It provides automated ELO calculation, repeat opponent detection, configurable settings, and persistent match result storage.

## Features

WynautRankUp provides robust tools for managing ranked battles, ELO ratings, and match history in a competitive environment.

### Feature List

| Feature                      | Description                                                                                 |
|------------------------------|---------------------------------------------------------------------------------------------|
| Ranked Battle Management     | Record and track ranked battles between players.                                            |
| ELO System                  | Automatic ELO calculation and adjustment for winners and losers.                            |
| Repeat Opponent Detection   | Detects if players have faced each other multiple times recently and applies an ELO modifier.|
| Persistent Match History    | Stores match results in a SQL database for auditing and repeat detection.                   |
| Configurable Settings       | Easily adjust ELO modifiers and teleport locations via a JSON config.                       |

### Feature Details

- **Ranked Battle Management**  
  Tracks and records all ranked matches, ensuring accurate player statistics.

- **ELO System**  
  Calculates and updates player ELO ratings automatically after each match.

- **Repeat Opponent Detection**  
  Identifies repeat matchups within a configurable timeframe and applies a custom ELO modifier.

- **Persistent Match History**  
  Saves all match results in a SQL database for long-term storage and review.

- **Configurable Settings**  
  Allows customization of ELO modifiers and teleport locations through a JSON configuration file.

## Configuration

The main configuration file is located at `config/WynautRankup/general_config.json`.

### Example Configuration

```json
{
  "repeatOpponentEloModifier": 0.5,
  "tpBackLocation": {
    "world": "minecraft:overworld",
    "x": 0,
    "y": 64,
    "z": 0,
    "yaw": 0,
    "pitch": 0
  }
}
```

- tpBackLocation:
  The default location to teleport players after a ranked battle.
- repeatOpponentEloModifier:
  Multiplier for ELO gain/loss when players are repeat opponents (e.g., 0.5 for 50%).

## Admin Commands

Admin commands let server administrators manage and configure WynautRankUp directly in-game.

### Admin Command List

| Command                | Description                                   |
|------------------------|-----------------------------------------------|
| `/wradmin help`        | Shows all admin commands and their usage.     |
| `/wradmin reload`      | Reloads the mod configuration files.       |

> **Note:** Admin commands require the `wynautrankup.admin` permission node.

### Command Details

- **/wradmin help**  
  Displays a help message listing all available admin commands.

- **/wradmin reload**  
  Reloads the mod’s configuration files without restarting the server.

Use these commands to quickly manage mod settings and troubleshoot issues as an administrator.


## Player Commands

Player commands allow users to interact with the ranked system, view stats, manage their queue status, and access GUIs for banned Pokémon and leaderboards.

### Player Command List

| Command                   | Description                                                                 |
|---------------------------|-----------------------------------------------------------------------------|
| `/ranked help`            | Displays a list of all ranked commands and their descriptions.              |
| `/ranked banned`          | Opens a GUI showing information about banned Pokémon.                       |
| `/ranked stats`           | Shows your current ELO, rank, and opens the extended info menu.             |
| `/ranked leaderboard`     | Opens a GUI displaying the top 10 players by ELO.                           |
| `/ranked queue`           | Shows your current queue status and players in your ELO range.              |
| `/ranked queue join`      | Adds you to the ranked matchmaking queue (requires a legal team of 6).      |
| `/ranked queue leave`     | Removes you from the ranked matchmaking queue.                              |

> **Note:** All `/ranked` commands must be run by a player in-game.

### Command Details

- **/ranked help**  
  Lists all available player commands and their usage.

- **/ranked banned**  
  Opens a GUI with details about Pokémon that are banned from ranked battles.

- **/ranked stats**  
  Displays your current ELO, rank, and opens a menu with extended season information.

- **/ranked leaderboard**  
  Opens a GUI showing the top 10 players for the current season.

- **/ranked queue**  
  Shows your queue status and the number of players in your ELO range.

- **/ranked queue join**  
  Adds you to the ranked queue if you have a legal team and are not already in a battle.

- **/ranked queue leave**  
  Removes you from the ranked queue.

Use these commands to participate in ranked battles, track your progress, and view important information about the ranked system.

## Arenas

Arenas are customizable battle locations where ranked matches take place. Each arena can have multiple player spawn positions and is managed via in-game commands and a configuration file.

### Arena Features

- Create, list, and remove arenas.
- Add or edit player spawn positions for each arena.
- Teleport to arenas or specific positions for setup or testing.
- View arena information and positions interactively.
- Persistent storage in a JSON configuration file.

### Arena Command List

| Command                                 | Description                                                        |
|------------------------------------------|--------------------------------------------------------------------|
| `/wynautarena help`                      | Shows all arena-related commands and their usage.                  |
| `/wynautarena list`                      | Lists all available arenas.                                        |
| `/wynautarena create <name> <world>`     | Creates a new arena in the specified world.                        |
| `/wynautarena addpos <arena>`            | Adds your current position as a spawn point in the arena.          |
| `/wynautarena editpos <arena> <index>`   | Edits a specific player position in the arena.                     |
| `/wynautarena info <name>`               | Shows detailed info about an arena.                                |
| `/wynautarena tp <name>`                 | Teleports to the first position of the arena.                      |
| `/wynautarena arenapos <arena>`          | Shows clickable teleport positions for the arena.                  |
| `/wynautarena tppos <arena> <index>`     | Teleports to a specific position in the arena.                     |
| `/wynautarena reload`                    | Reloads all arena configurations.                                  |

> **Note:** Most arena commands require admin permissions.

### Arena Configuration

Arenas are stored in `config/WynautRankup/arenas.json`. Each arena entry includes its name, world, and a list of player positions.

#### Example Arena Configuration

```json
{
  "arenas": {
    "default": {
      "name": "default",
      "world": "minecraft:overworld",
      "positions": [
        {
          "world": "minecraft:overworld",
          "x": 0,
          "y": 64,
          "z": 0,
          "yaw": 0,
          "pitch": 0
        },
        {
          "world": "minecraft:overworld",
          "x": 10,
          "y": 64,
          "z": 0,
          "yaw": 0,
          "pitch": 0
        }
      ]
    }
  }
}
```
## Season Commands

Season commands allow administrators to manage competitive seasons, set the current season, and configure season rewards.

### Season Command List

| Command                                         | Description                                                                 |
|-------------------------------------------------|-----------------------------------------------------------------------------|
| `/season`                                      | Shows the current season and time until it ends.                            |
| `/season list`                                 | Lists all configured seasons.                                               |
| `/season reload`                               | Reloads all season configurations.                                          |
| `/season set <seasonName>`                     | Sets the current season by name.                                            |
| `/season create <seasonName> <displayName>`    | Creates a new season with the given name and display name.                  |

> **Note:** All `/season` subcommands except `/season` require the `wynautrankup.admin` permission node.

### Command Details

- **/season**
  Displays the current season’s display name and the time remaining until the season ends.

- **/season list**
  Lists all available seasons with their internal and display names.

- **/season reload**
  Reloads all season data from configuration files.

- **/season set <seasonName>**
  Sets the specified season as the current active season.

- **/season create <seasonName> <displayName>**
  Creates a new season with the provided internal name and display name. The new season will have default reward criteria and dates.

Use these commands to manage competitive seasons, rotate rewards, and keep your ranked system fresh for players.

## Shop Commands

Shop commands allow players to access the ranked shop, manage their shop balance, and for admins to add, remove, or edit shop items.

### Shop Command List

| Command                                              | Description                                                                 |
|------------------------------------------------------|-----------------------------------------------------------------------------|
| `/rankedshop`                                       | Opens the ranked shop GUI.                                                  |
| `/rankedshop balance`                               | Displays your current shop balance.                                         |
| `/rankedshop add <id> <price>`                      | Adds the item in your hand to the shop with the specified ID and price.     |
| `/rankedshop remove <id>`                           | Removes the shop item with the given ID.                                    |
| `/rankedshop edit <id> <field> <value>`             | Edits a field (`price` or `displayname`) of the shop item with the given ID.|
| `/rankedshop setbalance <player> <amount>`          | Sets the specified player's shop balance.                                   |
| `/rankedshop addbalance <player> <amount>`          | Adds to the specified player's shop balance.                                |
| `/rankedshop help`                                  | Lists all shop commands and their usage.                                    |

> **Note:** Most shop management commands require the `wynautrankup.admin` permission node.

### Command Details

- **/rankedshop**
  Opens the shop GUI for the player, allowing them to purchase items.

- **/rankedshop balance**
  Shows your current shop balance.

- **/rankedshop add <id> <price>**
  Adds the item currently in your main hand to the shop with the specified ID and price.

- **/rankedshop remove <id>**
  Removes the shop item with the given ID.

- **/rankedshop edit <id> <field> <value>**
  Edits the specified field (`price` or `displayname`) of the shop item with the given ID.

- **/rankedshop setbalance <player> <amount>**
  Sets the shop balance of the specified player.

- **/rankedshop addbalance <player> <amount>**
  Adds the specified amount to the player's shop balance.

- **/rankedshop help**
  Lists all available shop commands and their usage.

Use these commands to manage the ranked shop, player balances, and shop inventory for your server.

## Illegal Pokémon System

The Illegal Pokémon System enforces custom rules to restrict certain Pokémon, forms, moves, abilities, or held items in ranked battles. Rules are defined in a configuration file and validated automatically before matches.

### Configuration

Rules are stored in `config/WynautRankup/illegal_pokemon.json` as a list of objects. Each rule can specify:

| Field      | Description                                              |
|------------|----------------------------------------------------------|
| `species`  | Required. Internal name of the banned Pokémon species.   |
| `form`     | Optional. Specific form to ban for the species.          |
| `moves`    | Optional. List of banned moves for this species/form.    |
| `ability`  | Optional. Banned ability for this species/form.          |
| `heldItem` | Optional. Banned held item for this species/form.        |

#### Example Rule

```json
[
  "species:pikachu;form:cosplay;ability:static;held_items:cobblemon:leftovers;moves:thunderbolt",
  "species:charizard;form:mega-x;ability:tough-claws;held_items:cobblemon:charizardite_x;moves:flareblitz",
  "species:mewtwo;form:mega-y;ability:insomnia;held_items:cobblemon:mewtwonite_y;moves:psystrike",
  "species:garchomp;ability:roughskin;held_items:cobblemon:lifeorb;moves:earthquake",
  "species:greninja;ability:protean;held_items:cobblemon:choice-scarf;moves:hydropump",
  "species:dragonite;ability:multiscale;held_items:cobblemon:assault-vest;moves:outrage",
  "species:blaziken;ability:speedboost;held_items:cobblemon:blazikenite;moves:flareblitz",
  "species:lucario;ability:adaptability;held_items:cobblemon:lucarionite;moves:aura-sphere",
  "species:gengar;form:mega;ability:levitate;held_items:cobblemon:gengarite;moves:shadowball",
  "species:alakazam;form:mega;ability:trace;held_items:cobblemon:alakazite;moves:psychic",
  "species:tyranitar;form:mega;ability:sandstream;held_items:cobblemon:tyranitarite;moves:stoneedge",
  "species:metagross;form:mega;ability:toughclaws;held_items:cobblemon:metagrossite;moves:meteormash",
  "species:salamence;form:mega;ability:aerilate;held_items:cobblemon:salamencite;moves:return",
  "species:scizor;form:mega;ability:toughclaws;held_items:cobblemon:scizorite;moves:bulletpunch",
  "species:aegislash;ability:stancechange;held_items:cobblemon:leftovers;moves:kingsshield",
  "species:hydreigon;ability:levitate;held_items:;moves:darkpulse",
  "species:arceus",
  "species:volcarona;ability:flamebody;held_items:cobblemon:sitrusberry;moves:morningsun"
]
```

### Validation

- The `TeamValidator` checks each Pokémon on a team against all rules in the config.
- If a Pokémon matches a rule (by species, form, move, ability, or held item), it is flagged as illegal and cannot be used in ranked matches.
- Players receive clear error messages explaining why a Pokémon is banned.

#### Admin Usage

- Edit `illegal_pokemon.json` to add or remove rules.
- Reload the config in-game or on server restart to apply changes.

This system ensures fair play by automatically blocking banned Pokémon, forms, moves, abilities, and items as defined by server staff.

## Database Configuration

WynautRankup uses a SQL database to store persistent data such as match history and player statistics. The database connection is configured via the `config/WynautRankup/database_config.json` file.

### Configuration File

The `database_config.json` file contains the database connection URL. On first run, a default file is generated if it does not exist.

#### Example `database_config.json`

```json
{
  "databaseUrl": "jdbc:mysql://host:port/dbname"
}
```

- **databaseUrl**: The JDBC URL for your SQL database (e.g., MySQL, MariaDB).

### Setup Steps

1. Start the server/mod once to generate the config file.
2. Edit `config/WynautRankup/database_config.json` and set the correct JDBC URL for your database.
3. Restart the server/mod to apply changes.

> **Note:** Ensure your database is accessible and the user has appropriate permissions.  
> The mod will automatically create the configuration file if it does not exist and prompt you to edit it.
