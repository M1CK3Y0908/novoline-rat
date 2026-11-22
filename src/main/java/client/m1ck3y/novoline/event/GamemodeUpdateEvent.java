package client.m1ck3y.novoline.event;

import client.m1ck3y.novoline.enums.HypixelGameType;

public class GamemodeUpdateEvent extends Event {
   public final HypixelGameType hypixelGameType;

   public GamemodeUpdateEvent(HypixelGameType hypixelGameType2) {
      this.hypixelGameType = hypixelGameType2;
   }
}
