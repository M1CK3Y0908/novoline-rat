package wtf.novoline.utils.render.blur;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class ShaderUtil {
   private final int programID;

   private static final String vertexShader = """
      #version 120

      void main() {
          gl_TexCoord[0] = gl_MultiTexCoord0;
          gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
      }
      """;

   private static final String kawaseUpBloom = """
      #version 120

      uniform sampler2D inTexture, textureToCheck;
      uniform vec2 halfpixel, offset, iResolution;
      uniform int check;

      void main() {
          vec2 uv = vec2(gl_FragCoord.xy / iResolution);

          vec4 sum = texture2D(inTexture, uv + vec2(-halfpixel.x * 2.0, 0.0) * offset);
          sum.rgb *= sum.a;
          vec4 smpl1 = texture2D(inTexture, uv + vec2(-halfpixel.x, halfpixel.y) * offset);
          smpl1.rgb *= smpl1.a;
          sum += smpl1 * 2.0;
          vec4 smp2 = texture2D(inTexture, uv + vec2(0.0, halfpixel.y * 2.0) * offset);
          smp2.rgb *= smp2.a;
          sum += smp2;
          vec4 smp3 = texture2D(inTexture, uv + vec2(halfpixel.x, halfpixel.y) * offset);
          smp3.rgb *= smp3.a;
          sum += smp3 * 2.0;
          vec4 smp4 = texture2D(inTexture, uv + vec2(halfpixel.x * 2.0, 0.0) * offset);
          smp4.rgb *= smp4.a;
          sum += smp4;
          vec4 smp5 = texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset);
          smp5.rgb *= smp5.a;
          sum += smp5 * 2.0;
          vec4 smp6 = texture2D(inTexture, uv + vec2(0.0, -halfpixel.y * 2.0) * offset);
          smp6.rgb *= smp6.a;
          sum += smp6;
          vec4 smp7 = texture2D(inTexture, uv + vec2(-halfpixel.x, -halfpixel.y) * offset);
          smp7.rgb *= smp7.a;
          sum += smp7 * 2.0;
          vec4 result = sum / 12.0;
          vec3 rgb = result.a > 0.0001 ? result.rgb / result.a : vec3(0.0);
          gl_FragColor = vec4(rgb, mix(result.a, result.a * (1.0 - texture2D(textureToCheck, gl_TexCoord[0].st).a), float(check)));
      }
      """;

   private static final String kawaseDownBloom = """
      #version 120

      uniform sampler2D inTexture;
      uniform vec2 offset, halfpixel, iResolution;

      void main() {
          vec2 uv = vec2(gl_FragCoord.xy / iResolution);
          vec4 sum = texture2D(inTexture, gl_TexCoord[0].st);
          sum.rgb *= sum.a;
          sum *= 4.0;
          vec4 smp1 = texture2D(inTexture, uv - halfpixel.xy * offset);
          smp1.rgb *= smp1.a;
          sum += smp1;
          vec4 smp2 = texture2D(inTexture, uv + halfpixel.xy * offset);
          smp2.rgb *= smp2.a;
          sum += smp2;
          vec4 smp3 = texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset);
          smp3.rgb *= smp3.a;
          sum += smp3;
          vec4 smp4 = texture2D(inTexture, uv - vec2(halfpixel.x, -halfpixel.y) * offset);
          smp4.rgb *= smp4.a;
          sum += smp4;
          vec4 result = sum / 8.0;
          vec3 rgb = result.a > 0.0001 ? result.rgb / result.a : vec3(0.0);
          gl_FragColor = vec4(rgb, result.a);
      }
      """;

   private static final String kawaseUp = """
      #version 120

      uniform sampler2D inTexture, textureToCheck;
      uniform vec2 halfpixel, offset, iResolution;
      uniform int check;

      void main() {
          vec2 uv = vec2(gl_FragCoord.xy / iResolution);
          vec4 sum = texture2D(inTexture, uv + vec2(-halfpixel.x * 2.0, 0.0) * offset);
          sum += texture2D(inTexture, uv + vec2(-halfpixel.x, halfpixel.y) * offset) * 2.0;
          sum += texture2D(inTexture, uv + vec2(0.0, halfpixel.y * 2.0) * offset);
          sum += texture2D(inTexture, uv + vec2(halfpixel.x, halfpixel.y) * offset) * 2.0;
          sum += texture2D(inTexture, uv + vec2(halfpixel.x * 2.0, 0.0) * offset);
          sum += texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset) * 2.0;
          sum += texture2D(inTexture, uv + vec2(0.0, -halfpixel.y * 2.0) * offset);
          sum += texture2D(inTexture, uv + vec2(-halfpixel.x, -halfpixel.y) * offset) * 2.0;

          gl_FragColor = vec4(sum.rgb / 12.0, mix(1.0, texture2D(textureToCheck, gl_TexCoord[0].st).a, float(check)));
      }
      """;

   private static final String kawaseDown = """
      #version 120

      uniform sampler2D inTexture;
      uniform vec2 offset, halfpixel, iResolution;

      void main() {
          vec2 uv = vec2(gl_FragCoord.xy / iResolution);
          vec4 sum = texture2D(inTexture, gl_TexCoord[0].st) * 4.0;
          sum += texture2D(inTexture, uv - halfpixel.xy * offset);
          sum += texture2D(inTexture, uv + halfpixel.xy * offset);
          sum += texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset);
          sum += texture2D(inTexture, uv - vec2(halfpixel.x, -halfpixel.y) * offset);
          gl_FragColor = vec4(sum.rgb * .125, 1.0);
      }
      """;

   public ShaderUtil(String fragmentShaderName) {
      int program = glCreateProgram();
      int fragmentShaderID = switch (fragmentShaderName) {
         case "kawaseUpBloom" -> createShader(kawaseUpBloom, GL_FRAGMENT_SHADER);
         case "kawaseDownBloom" -> createShader(kawaseDownBloom, GL_FRAGMENT_SHADER);
         case "kawaseUp" -> createShader(kawaseUp, GL_FRAGMENT_SHADER);
         case "kawaseDown" -> createShader(kawaseDown, GL_FRAGMENT_SHADER);
         default -> throw new IllegalArgumentException("Unknown shader: " + fragmentShaderName);
      };
      glAttachShader(program, fragmentShaderID);

      int vertexShaderID = createShader(vertexShader, GL_VERTEX_SHADER);
      glAttachShader(program, vertexShaderID);

      glLinkProgram(program);
      int status = glGetProgrami(program, GL_LINK_STATUS);
      if (status == 0) {
         throw new IllegalStateException("Shader failed to link!");
      }
      this.programID = program;
   }

   private int createShader(String shaderSource, int shaderType) {
      int shader = glCreateShader(shaderType);
      glShaderSource(shader, shaderSource);
      glCompileShader(shader);
      if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
         System.err.println(glGetShaderInfoLog(shader, 4096));
         throw new IllegalStateException(String.format("Shader (%s) failed to compile!", shaderType));
      }
      return shader;
   }

   public void init() {
      glUseProgram(this.programID);
   }

   public void unload() {
      glUseProgram(0);
   }

   public int getUniform(String name) {
      return glGetUniformLocation(this.programID, name);
   }

   public void setUniformf(String name, float... args) {
      int loc = glGetUniformLocation(this.programID, name);
      switch (args.length) {
         case 1 -> glUniform1f(loc, args[0]);
         case 2 -> glUniform2f(loc, args[0], args[1]);
         case 3 -> glUniform3f(loc, args[0], args[1], args[2]);
         case 4 -> glUniform4f(loc, args[0], args[1], args[2], args[3]);
      }
   }

   public void setUniformi(String name, int... args) {
      int loc = glGetUniformLocation(this.programID, name);
      if (args.length > 1) {
         glUniform2i(loc, args[0], args[1]);
      } else {
         glUniform1i(loc, args[0]);
      }
   }

   public static void drawQuads() {
      ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
      float width = (float) sr.getScaledWidth_double();
      float height = (float) sr.getScaledHeight_double();
      GlStateManager.enableTexture2D();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      glBegin(GL_QUADS);
      glTexCoord2f(0.0F, 1.0F);
      glVertex2f(0.0F, 0.0F);
      glTexCoord2f(0.0F, 0.0F);
      glVertex2f(0.0F, height);
      glTexCoord2f(1.0F, 0.0F);
      glVertex2f(width, height);
      glTexCoord2f(1.0F, 1.0F);
      glVertex2f(width, 0.0F);
      glEnd();
   }

   public static void drawQuads(float x, float y, float width, float height) {
      GlStateManager.enableTexture2D();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      glBegin(GL_QUADS);
      glTexCoord2f(0.0F, 0.0F);
      glVertex2f(x, y);
      glTexCoord2f(0.0F, 1.0F);
      glVertex2f(x, y + height);
      glTexCoord2f(1.0F, 1.0F);
      glVertex2f(x + width, y + height);
      glTexCoord2f(1.0F, 0.0F);
      glVertex2f(x + width, y);
      glEnd();
   }
}
