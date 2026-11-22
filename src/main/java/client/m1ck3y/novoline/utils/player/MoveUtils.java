package client.m1ck3y.novoline.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public final class MoveUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private MoveUtils() {
    }

    public enum MoveDirection {
        NONE(false, false, false, false, false),
        FORWARD(true, false, false, false, false),
        BACK(false, true, false, false, false),
        LEFT(false, false, true, false, false),
        RIGHT(false, false, false, true, false),
        FORWARD_LEFT(true, false, true, false, true),
        FORWARD_RIGHT(true, false, false, true, true),
        BACK_LEFT(false, true, true, false, true),
        BACK_RIGHT(false, true, false, true, true);

        public final boolean forward;
        public final boolean backward;
        public final boolean left;
        public final boolean right;
        public final boolean diagonal;

        MoveDirection(boolean forward, boolean backward, boolean left, boolean right, boolean diagonal) {
            this.forward = forward;
            this.backward = backward;
            this.left = left;
            this.right = right;
            this.diagonal = diagonal;
        }
    }

    public static float forwardInput() {
        float f = 0.0f;
        if (mc.gameSettings.keyBindForward.isKeyDown()) f += 1.0f;
        if (mc.gameSettings.keyBindBack.isKeyDown()) f -= 1.0f;
        return f;
    }

    public static float strafeInput() {
        float f = 0.0f;
        if (mc.gameSettings.keyBindLeft.isKeyDown()) f += 1.0f;
        if (mc.gameSettings.keyBindRight.isKeyDown()) f -= 1.0f;
        return f;
    }

    public static boolean isMoving() {
        if (mc.thePlayer == null || mc.thePlayer.movementInput == null) return false;
        return mc.thePlayer.movementInput.moveForward != 0.0f || mc.thePlayer.movementInput.moveStrafe != 0.0f;
    }

    public static boolean jumpKeyDown() {
        if (mc.gameSettings == null || mc.gameSettings.keyBindJump == null) return false;
        int code = mc.gameSettings.keyBindJump.getKeyCode();
        return mc.gameSettings.keyBindJump.isKeyDown()
                || (code < 0 ? Mouse.isCreated() && Mouse.isButtonDown(code + 100) : (code > 0 && Keyboard.isCreated() && Keyboard.isKeyDown(code)));
    }

    public static double inputAngle() {
        double d = Math.toDegrees(Math.atan2(strafeInput(), -forwardInput()));
        return (d + 360.0) % 360.0;
    }

    public static double inputAngleSigned() {
        double d = Math.toDegrees(Math.atan2(-strafeInput(), forwardInput()));
        return d == 0.0 ? 0.0 : d;
    }

    public static float movementAngle() {
        if (mc.thePlayer == null) return 0.0f;
        return mc.thePlayer.rotationYaw + (float) inputAngle();
    }

    public static MoveDirection direction(double forward, double strafe) {
        if (forward == 0.0) {
            if (strafe == 0.0) return MoveDirection.NONE;
            if (strafe > 0.0) return MoveDirection.LEFT;
            return MoveDirection.RIGHT;
        } else if (forward > 0.0) {
            if (strafe == 0.0) return MoveDirection.FORWARD;
            if (strafe > 0.0) return MoveDirection.FORWARD_LEFT;
            return MoveDirection.FORWARD_RIGHT;
        } else {
            if (strafe == 0.0) return MoveDirection.BACK;
            if (strafe > 0.0) return MoveDirection.BACK_LEFT;
            return MoveDirection.BACK_RIGHT;
        }
    }

    public static MoveDirection direction() {
        return direction(forwardInput(), strafeInput());
    }

    public static boolean diagonalYaw() {
        if (mc.thePlayer == null) return false;
        float f = Math.abs(mc.thePlayer.rotationYaw % 90.0f);
        return f > 10.0f && f < 80.0f;
    }

    public static boolean isDiagonal() {
        return diagonalYaw() || direction().diagonal;
    }

    public static float moveYaw(float yaw, float forward, float strafe) {
        float f4 = 1.0f;
        if (forward < 0.0f) {
            yaw += 180.0f;
            f4 = -0.5f;
        } else if (forward > 0.0f) {
            f4 = 0.5f;
        }
        if (strafe > 0.0f) {
            yaw -= 90.0f * f4;
        } else if (strafe < 0.0f) {
            yaw += 90.0f * f4;
        }
        return yaw;
    }

    public static float moveYaw(float yaw) {
        if (mc.thePlayer == null || mc.thePlayer.movementInput == null) return yaw;
        return moveYaw(yaw, mc.thePlayer.movementInput.moveForward, mc.thePlayer.movementInput.moveStrafe);
    }

    public static double[] movementVector() {
        if (!isMoving() || mc.thePlayer == null) return new double[]{0.0, 0.0};
        float yaw = moveYaw(mc.thePlayer.rotationYaw);
        double dx = -Math.sin(Math.toRadians(yaw));
        double dz = Math.cos(Math.toRadians(yaw));
        return new double[]{dx, dz};
    }

    public static void setSpeed(double speed, float yaw) {
        if (mc.thePlayer == null) return;
        if (speed == 0.0) {
            mc.thePlayer.motionZ = 0.0;
            mc.thePlayer.motionX = 0.0;
            return;
        }
        mc.thePlayer.motionX = -Math.sin(Math.toRadians(yaw)) * speed;
        mc.thePlayer.motionZ = Math.cos(Math.toRadians(yaw)) * speed;
    }

    public static void setSpeed(double speed) {
        if (mc.thePlayer == null) return;
        float yaw = mc.thePlayer.rotationYaw;
        if (mc.thePlayer.movementInput != null && mc.thePlayer.movementInput.moveForward < 0.0f) {
            yaw += 180.0f;
        }
        setSpeed(speed, yaw);
    }

    public static void strafe(double speed) {
        if (mc.thePlayer == null) return;
        setSpeed(speed, moveYaw(mc.thePlayer.rotationYaw));
    }

    public static void strafe(double speed, float yaw) {
        if (mc.thePlayer == null) return;
        setSpeed(speed, moveYaw(yaw));
    }

    public static double speed() {
        if (mc.thePlayer == null) return 0.0;
        return Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }

    public static int speedLevel() {
        if (mc.thePlayer == null) return 0;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            return mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1;
        }
        return 0;
    }

    public static double perSpeedLevel(double amount) {
        return (double) speedLevel() * amount;
    }

    public static int posYDecimals() {
        if (mc.thePlayer == null) return 0;
        return (int) Math.round(mc.thePlayer.posY % 1.0 * 10000.0);
    }

    public static int motionYDecimals() {
        if (mc.thePlayer == null) return 0;
        return (int) Math.round(mc.thePlayer.motionY % 10.0 * 10000.0);
    }

    public static boolean nearEdge(double margin) {
        if (mc.thePlayer == null || mc.theWorld == null) return false;
        int x = MathHelper.floor_double(mc.thePlayer.posX);
        int y = MathHelper.floor_double(mc.thePlayer.posY - 0.01);
        int z = MathHelper.floor_double(mc.thePlayer.posZ);
        BlockPos pos = new BlockPos(x, y, z);
        if (mc.theWorld.isAirBlock(pos)) {
            return true;
        }
        double fx = mc.thePlayer.posX - (double) x;
        double fz = mc.thePlayer.posZ - (double) z;
        return (fx < margin && mc.theWorld.isAirBlock(new BlockPos(x - 1, y, z)))
                || (fx > 1.0 - margin && mc.theWorld.isAirBlock(new BlockPos(x + 1, y, z)))
                || (fz < margin && mc.theWorld.isAirBlock(new BlockPos(x, y, z - 1)))
                || (fz > 1.0 - margin && mc.theWorld.isAirBlock(new BlockPos(x, y, z + 1)));
    }

    public static boolean isAirBelow(double x, double y, double z) {
        if (mc.theWorld == null) return false;
        BlockPos pos = new BlockPos(x, y - (double) (y % 1.0 == 0.0 ? 1 : 0), z);
        return mc.theWorld.isAirBlock(pos);
    }

    public static boolean isAirBelow() {
        if (mc.thePlayer == null) return false;
        return isAirBelow(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
    }

    public static void jump(double speed) {
        if (mc.thePlayer == null) return;
        strafe(speed);
        mc.thePlayer.jump();
    }

    public static void multiplyMotion(double mult) {
        if (mc.thePlayer == null) return;
        mc.thePlayer.motionX *= mult;
        mc.thePlayer.motionZ *= mult;
    }
}
