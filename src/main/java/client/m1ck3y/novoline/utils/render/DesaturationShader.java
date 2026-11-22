package client.m1ck3y.novoline.utils.render;

import net.minecraft.util.ResourceLocation;

public class DesaturationShader extends ShaderProgram {
   public static DesaturationShader desaturationShader = new DesaturationShader();

   private DesaturationShader() {
      super(new ResourceLocation("novoline", "shaders/desaturation.fsh"));
   }
}
