package me.brokenearthdev.manhuntplugin.main;

import me.brokenearthdev.manhuntplugin.core.commands.ManhuntCommand;
import java.util.List;
import java.util.Arrays;

//Date generated: Tue May 25 00:31:25 AST 2021
class Generated$AutoRegisteredCommands {
  private final List<String> foundCmds = Arrays.asList(
          "me.brokenearthdev.manhuntplugin.commands.AdminCommand",
          "me.brokenearthdev.manhuntplugin.commands.game.GamesCommand",
          "me.brokenearthdev.manhuntplugin.commands.game.GameStartCommand",
          "me.brokenearthdev.manhuntplugin.commands.game.GameStopCommand",
          "me.brokenearthdev.manhuntplugin.commands.GiveTrackerCommand",
          "me.brokenearthdev.manhuntplugin.commands.PlayerProfileCommand",
          "me.brokenearthdev.manhuntplugin.commands.TestCounterCommand",
          "me.brokenearthdev.manhuntplugin.commands.TestStartCommand",
          "me.brokenearthdev.manhuntplugin.commands.TryKitCommand"
  );

  void register() throws Exception {
      for (String cmd : foundCmds) {
          ManhuntCommand command = (ManhuntCommand) Class.forName(cmd).newInstance();
          CommandRegistryManager.INST.registerCommand(command, false);
      }
  }
}