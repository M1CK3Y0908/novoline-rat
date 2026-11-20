package rat.novoline.script.adapter;

import net.minecraft.client.multiplayer.ServerData;
import rat.novoline.script.AbstractTypeAdapter;
import rat.novoline.script.ScriptValue;
import rat.novoline.script.TypeRegistry;

public final class ServerDataTypeAdapter extends AbstractTypeAdapter<ServerData> {
   public ScriptValue put(ScriptValue scriptValue, ServerData serverData) {
      scriptValue.put("serverIP", serverData.serverIP);
      scriptValue.put("serverName", serverData.serverName);
      scriptValue.put("serverMOTD", serverData.serverMOTD);
      scriptValue.put("pingToServer", serverData.pingToServer);
      scriptValue.put("populationInfo", serverData.populationInfo);
      scriptValue.put("version", serverData.version);
      scriptValue.put("gameVersion", serverData.gameVersion);
      scriptValue.put("playerList", serverData.playerList);
      scriptValue.put("lanServer", serverData.pinged);
      return scriptValue;
   }

   public ServerDataTypeAdapter(TypeRegistry typeRegistry) {
      super("ServerData", typeRegistry);
   }
}
