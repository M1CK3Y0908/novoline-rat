package client.m1ck3y.novoline.util;

import client.m1ck3y.novoline.event.JumpEvent;
import client.m1ck3y.novoline.event.LoadWorldEvent;
import client.m1ck3y.novoline.event.MoveEntityEvent;
import client.m1ck3y.novoline.event.MoveEntityEvent2;
import client.m1ck3y.novoline.event.OnUpdateWalkingPlayerEvent;
import client.m1ck3y.novoline.event.RunTickEvent;
import client.m1ck3y.novoline.event.UpdateEvent;
import client.m1ck3y.novoline.script.TypeRegistry;
import client.m1ck3y.novoline.script.adapter.EventTypeAdapter12;

public class EventTypeAdapters {
   public static final class EventTypeAdapter12Child extends EventTypeAdapter12<LoadWorldEvent> {
      public EventTypeAdapter12Child(TypeRegistry typeRegistry) {
         super("LoadWorldEvent", typeRegistry);
      }
   }

   public static final class EventTypeAdapter12Child2 extends EventTypeAdapter12<JumpEvent> {
      public EventTypeAdapter12Child2(TypeRegistry typeRegistry) {
         super("PlayerJumpEvent", typeRegistry);
      }
   }

   public static final class EventTypeAdapter12Child3 extends EventTypeAdapter12<OnUpdateWalkingPlayerEvent> {
      public EventTypeAdapter12Child3(TypeRegistry typeRegistry) {
         super("PostUpdateEvent", typeRegistry);
      }
   }

   public static final class EventTypeAdapter12Child4 extends EventTypeAdapter12<UpdateEvent> {
      public EventTypeAdapter12Child4(TypeRegistry typeRegistry) {
         super("UpdateEvent", typeRegistry);
      }
   }

   public static final class EventTypeAdapter12Child5 extends EventTypeAdapter12<RunTickEvent> {
      public EventTypeAdapter12Child5(TypeRegistry typeRegistry) {
         super("TickUpdateEvent", typeRegistry);
      }
   }

   public static final class EventTypeAdapter12Child6 extends EventTypeAdapter12<MoveEntityEvent> {
      public EventTypeAdapter12Child6(TypeRegistry typeRegistry) {
         super("SafeWalkEvent", typeRegistry);
      }
   }

   public static final class EventTypeAdapter12Child7 extends EventTypeAdapter12<MoveEntityEvent2> {
      public EventTypeAdapter12Child7(TypeRegistry typeRegistry) {
         super("StepConfirmEvent", typeRegistry);
      }
   }
}
