# ElementumAlterEgo
A discord bot used to relay chat from a Spigot Minecraft Server. Designed for Elementum, hence the name.

The bot supports a few basic commands and the ability to bar users from using the bot. Ops must be defined in the config file that appears in `/plugins/ElementumAlterEgo/config.yml`

The bot is based off the [Javacord API](https://github.com/BtoBastian/Javacord). As such, the bot must be built with Maven and shade in Javacord API.

## Commands
The bot comes with a few limited commands

### User Commands
- `!list` - Lists all users in game
- `!listd` - Lists all users in game with their displaynames (nicknames)
- `!link <user>` - Link your MC account to your discord account
- `!unlink` - Unlink your MC account
- `!help` - Shows all commands the user can run

### Op Commands
- `!bar @user` - Bars a user from running commands (the bot will ignore them from now on)
- `!unbar @user` - Unbars a user
- `!barlist` - List all barred users
- `!reload` - Reloads the bot config
- `!relay <on/off>` - Disable or enable the relay
- `!say <message>` - Echo a message in game
- `!info` - Shows the bot version and gives a link to the source code (this page)
### Hidden Commands
- `!execute <command>` - Executes a command on the Spigot server

## Config
This is the default config that generates. Obviously, change it to suit your needs.
```
Token: '***insert token here***'
Ops:
- '145436402107154433'
BarredUsers: []
RelayChannelID: '000000000000000'
ReportChannelID: '000000000000000'
RelayEnabled: true
SayCommandFormat: '&7[Discord] <name>: &r<message>'
```
**Token** - The token for the bot

**Ops** - A list of user IDs that can run OP commands 

**BarredUsers** - A list of all users the bot will ignore

**RelayChannelID** - The ID of the channel that the server will relay to

**ReportChannelID** - The ID of the channel that the bot will report stuff to (failed linkings, etc)

**RelayEnabled** - Allows the relay to be toggled off and on again

**SayCommandFormat** - The format the `!say` command should broadast with
