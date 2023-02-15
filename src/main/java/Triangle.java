public class Triangle {
    /**
     * 判断输入三角形是否合法
     * @param a,b,c 三条边
     */
    public void test(float a, float b, float c) {
        float mx = Math.max(a, Math.max(b, c));  // 最长边
        float mn = Math.min(a, Math.min(b, c));  // 最短边
        float mi = a + b + c - mx - mn;  // 中间边
        String prefix = String.format("%f %f %f ", a, b, c);
        if (mn + mi > mx && mx - mi < mn) {
            System.out.println(prefix + "It is a triangle.");
        } else {
            System.out.println(prefix + "It is not a triangle.");
        }
    }

    static public void main(String[] args) {
        System.out.println("Run Main");
        Triangle t = new Triangle();
        t.test(1, 2, 3);
        t.test(3, 4, 5);
    }
}
