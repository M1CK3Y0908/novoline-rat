package rat.novoline.event;

import rat.novoline.enums.HypixelGameType;

public class GameStartEvent extends Event {
   public final HypixelGameType hypixelGameType;

   public GameStartEvent(HypixelGameType hypixelGameType2) {
      this.hypixelGameType = hypixelGameType2;
   }
}
