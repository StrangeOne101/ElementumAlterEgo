# ElementumAlterEgo
A discord bot used to relay chat from a Spigot Minecraft Server. Designed for Elementum, hence the name.

The bot supports a few basic commands and the ability to bar users from using the bot. Ops must be defined in the config file that appears in `/plugins/ElementumAlterEgo/config.yml`

## Commands
The bot comes with a few limited commands

### User Commands
- `!list` - Lists all users in game
- `!listd` - Lists all users in game with their displaynames (nicknames)
- `!help` - Shows all commands the user can run

### Op Commands
- `!bar @user` - Bars a user from running commands (the bot will ignore them from now on)
- `!unbar @user` - Unbars a user
- `!barlist` - List all barred users
- `!reload` - Reloads the bot config
- `!relay <on/off>` - Disable or enable the relay
### Hidden Commands
- `!execute <command` - Executes a command on the Spigot server

## Config
This is the default config that generates. Obviously, change it to suit your needs.
```
Token: '***insert token here***'
Ops:
- '145436402107154433'
BarredUsers: []
RelayChannelID: '000000000000000'
RelayEnabled: true
```
**Token** - The token for the bot

**Ops** - A list of user IDs that can run OP commands 

**BarredUsers** - A list of all users the bot will ignore

**RelayChannelID** - The ID of the channel that the server will relay to

**RelayEnabled** - Allows the relay to be toggled off and on again
