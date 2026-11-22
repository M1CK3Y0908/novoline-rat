package client.m1ck3y.novoline.lang;

public class AccessRevokedException extends AuthException {
   public AccessRevokedException() {
      super("Access to the application was revoked");
   }
}
