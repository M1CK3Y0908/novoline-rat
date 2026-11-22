package client.m1ck3y.novoline.utils.cosmetic;

import com.google.gson.annotations.SerializedName;
import client.m1ck3y.novoline.enums.CapeType;

public class CapeCosmetic extends Cosmetic {
   public CapeCosmetic() {
      super((CapeType) null, (String) null);
   }

   @SerializedName("texHash")
   private String texHash;

   public String getTexHash() {
      return this.texHash;
   }
}
