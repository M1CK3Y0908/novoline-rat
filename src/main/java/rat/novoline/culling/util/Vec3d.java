package rat.novoline.culling.util;

public class Vec3d {
   public double x;
   public double y;
   public double z;

   public Vec3d(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public void set(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public void setAdd(Vec3d other, double x, double y, double z) {
      this.x = other.x + x;
      this.y = other.y + y;
      this.z = other.z + z;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Vec3d)) return false;
      Vec3d vec3d = (Vec3d) o;
      return Double.compare(vec3d.x, this.x) == 0 &&
             Double.compare(vec3d.y, this.y) == 0 &&
             Double.compare(vec3d.z, this.z) == 0;
   }

   @Override
   public int hashCode() {
      long temp = Double.doubleToLongBits(this.x);
      int result = (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(this.y);
      result = 31 * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(this.z);
      result = 31 * result + (int) (temp ^ (temp >>> 32));
      return result;
   }

   @Override
   public String toString() {
      return "(" + this.x + ", " + this.y + ", " + this.z + ")";
   }
}
