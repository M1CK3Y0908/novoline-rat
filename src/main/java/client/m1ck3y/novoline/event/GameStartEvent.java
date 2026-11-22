package client.m1ck3y.novoline.event;

import client.m1ck3y.novoline.enums.HypixelGameType;

public class GameStartEvent extends Event {
   public final HypixelGameType hypixelGameType;

   public GameStartEvent(HypixelGameType hypixelGameType2) {
      this.hypixelGameType = hypixelGameType2;
   }
}
