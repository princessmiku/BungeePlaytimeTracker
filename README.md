# BungeePlaytimeTracker

BungeePlaytimeTracker is a BungeeCord plugin to track the playtime of players across the BungeeCord network.

## Requirements

- A BungeeCord server
- A MySQL or MariaDB server

## Installation

Put the plugin into your BungeeCord server's plugins folder. Start the server and the plugin will be loaded and generate
the ``config.yml``.
Then close it and edit the ``config.yml`` to your needs.

## Configuration

| Config key               | Description                                                                                                                                                             |
|--------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ``language``             | The language of the plugin, supportet languages below                                                                                                                   |
| ``print-session-update`` | `true` or `false` prints the message if it updates the playtime session in the database                                                                                 |
| ``exclude-servers``      | Select servers for exclude in playtime calculation.<br/>If you exclude a server later, then you need to reload the players for the change to take effect for all users. |
| ``reload``               | Reload players playtime on startup, if this enabled the plugin will take a while to load and will disable the setting afterward                                         |
| ``database.host``        | The host of the MySQL or MariaDB server                                                                                                                                 |
| ``database.port``        | The port of the MySQL or MariaDB server                                                                                                                                 |
| ``database.database``    | The name of the database                                                                                                                                                |
| ``database.username``    | The username of the database                                                                                                                                            |
| ``database.password``    | The password of the database                                                                                                                                            |

*Note: The servername is the name of the server how it registered in BungeeCord.*

### Multi-Language Support

You can change the language of the plugin in the config.yml under the key ``language``.

Supported languages:

- English ``en``
- German ``de``
- Spanish ``es``
- French ``fr``
- Italian ``it``
- Polish ``pl``
- Dutch ``nl``

AI translated the language for Spanish, French, Italian, Polish and Dutch.

## Usage

The plugin works by itself when it's enabled and configured.

## Commands

| Command                   | Description                                   |
|---------------------------|-----------------------------------------------|
| ``/playtime``             | Shows the playtime of the player.             |
| ``/playtime leaderboard`` | Shows the playtime of the top 10 best players |

## PlaceholderAPI Support

This plugin provides a PlaceholderAPI expansion.

To use the Placeholder API Support, you need to add this plugin to your Spigot server.
Remember to set the config. It uses the same config as the BungeeCord plugin, so
you can copy the config from your BungeeCord server.

Multi-Language Support is also available for PlaceholderAPI.

### PlaceholderAPI Placeholders

| Placeholder                  | do                                                               | Example                                     |
|------------------------------|------------------------------------------------------------------|---------------------------------------------|
| ``%playtimetracker%``        | Returns the Seconds                                              | 15343                                       |
| ``%playtimetracker_short%``  | Returns a Short Info                                             | 4 Hours, 15 Minutes                         |
| ``%playtimetracker_normal%`` | Returns the Normal Info that you find in `/playtime leaderboard` | 4 Hours and 15 Minutes                      |
| ``%playtimetracker_long%``   | Returns the detaild info from `/playtime`                        | 0 Days, 4 Hours, 15 Minutes and 43 secconds |

___

# FAQ

### How it works?
The plugin will create a session if a player joins Server inside the BungeeCord network.
This session will be stored in the database and updated all 30 seconds.
If the player leaves the BungeeCord network or switches the Server inside the BungeeCord network,
the plugin will stop the session and create a new one for the new Server. 
Every join/switch is a new session that will be stored in the database.

Why? Because I want to track the played sessions for later for providing more detailed and 
interesting information/statistics for the player and the server owner. So in the current state it's useless, but later it will be good.


### The Playtime in the Leaderboard is not correct

The plugin only updates the global playtime of a player if he uses the command `/playtime` or is leaving the BungeeCord network.

The cause of this is that this plugin stores every session of a server from a player separately.
The result of it is that performance issues can occur if a player has many sessions that he played on a server, and it
needs to recalculate the playtime of all sessions every time.

___

# License

This Project is licensed under the Apache License Version 2.0
