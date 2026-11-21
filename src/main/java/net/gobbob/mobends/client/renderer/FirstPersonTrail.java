package net.gobbob.mobends.client.renderer;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.gobbob.mobends.settings.SettingsBoolean;
import net.gobbob.mobends.settings.SettingsNode;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import rat.novoline.module.ModuleManager;

public class FirstPersonTrail {
    private static class TrailPoint {
        final float x, y, z;
        final long time;

        TrailPoint(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.time = System.currentTimeMillis();
        }
    }

    private static final List<TrailPoint> points = new ArrayList<>();
    private static final FloatBuffer matrixBuf = GLAllocation.createDirectFloatBuffer(16);

    public static void recordFist(float tipOffset) {
        boolean trailEnabled = false;
        if (ModuleManager.chineseKungfu != null && ModuleManager.chineseKungfu.isEnabled() && ModuleManager.chineseKungfu.firstPersonEffects.isEnabled()) {
            trailEnabled = true;
        } else if (ModuleManager.moBends != null && ModuleManager.moBends.isEnabled() && ModuleManager.moBends.swordTrail.isEnabled()) {
            trailEnabled = true;
        } else {
            SettingsBoolean setting = (SettingsBoolean)SettingsNode.getSetting("swordTrail");
            if (setting != null && setting.data) {
                trailEnabled = true;
            }
        }
        if (!trailEnabled) {
            points.clear();
            return;
        }

        matrixBuf.rewind();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, matrixBuf);
        float x = matrixBuf.get(12) + matrixBuf.get(4) * tipOffset;
        float y = matrixBuf.get(13) + matrixBuf.get(5) * tipOffset;
        float z = matrixBuf.get(14) + matrixBuf.get(6) * tipOffset;

        if (!points.isEmpty()) {
            TrailPoint last = points.get(points.size() - 1);
            float dx = x - last.x;
            float dy = y - last.y;
            float dz = z - last.z;
            if (dx * dx + dy * dy + dz * dz < 0.0001f) {
                return;
            }
        }
        points.add(new TrailPoint(x, y, z));
    }

    public static void render() {
        long now = System.currentTimeMillis();
        Iterator<TrailPoint> it = points.iterator();
        while (it.hasNext()) {
            if (now - it.next().time > 180) {
                it.remove();
            }
        }
        if (points.size() < 2) {
            return;
        }

        GL11.glPushMatrix();
        GL11.glLoadIdentity();

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GL11.glShadeModel(GL11.GL_SMOOTH);

        GL11.glBegin(GL11.GL_QUAD_STRIP);
        for (int i = 0; i < points.size(); i++) {
            TrailPoint p = points.get(i);
            float age = (float)(now - p.time) / 180.0f;
            float alpha = Math.max(0.0f, Math.min(1.0f, 1.0f - age)) * 0.75f;

            int colorRgb = ModuleManager.themeColor != null
                ? ModuleManager.themeColor.getColor(i * 100)
                : 0xFFFFFF;
            float r = ((colorRgb >> 16) & 0xFF) / 255.0f;
            float g = ((colorRgb >> 8) & 0xFF) / 255.0f;
            float b = (colorRgb & 0xFF) / 255.0f;

            GL11.glColor4f(r, g, b, alpha);
            GL11.glVertex3f(p.x, p.y + 0.08f, p.z);
            GL11.glColor4f(r, g, b, 0.0f);
            GL11.glVertex3f(p.x, p.y - 0.08f, p.z);
        }
        GL11.glEnd();

        GL11.glShadeModel(GL11.GL_FLAT);
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        GL11.glPopMatrix();
    }
}
