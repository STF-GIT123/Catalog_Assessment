import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ShamirSecretSharing {

    static class Point {
        int x;
        BigDecimal y;

        Point(int x, BigDecimal y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) {
        String filePath = "testcase.json";
        try {
            JSONObject jsonObject = new JSONObject(new String(Files.readAllBytes(Paths.get(filePath))));
            int n = jsonObject.getJSONObject("keys").getInt("n");
            int k = jsonObject.getJSONObject("keys").getInt("k");

            // Parse points
            List<Point> points = parsePoints(jsonObject);

            // Ensure we have enough points
            if (points.size() < k) {
                throw new IllegalArgumentException("Not enough points to solve the polynomial");
            }

            // Calculate the constant term (f(0)) using Lagrange Interpolation
            BigDecimal secretConstant = lagrangeInterpolation(points.subList(0, k), BigDecimal.ZERO);
            System.out.println("The secret constant term is: " + secretConstant);

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    // Parse points from JSON data
    private static List<Point> parsePoints(JSONObject jsonObject) {
        List<Point> points = new ArrayList<>();
        for (String key : jsonObject.keySet()) {
            if (!key.equals("keys")) {
                int x = Integer.parseInt(key);
                JSONObject pointData = jsonObject.getJSONObject(key);
                int base = pointData.getInt("base");
                String value = pointData.getString("value");

                // Decode y value from specified base
                BigDecimal y = new BigDecimal(Long.parseLong(value, base));
                points.add(new Point(x, y));
            }
        }
        return points;
    }

    // Lagrange Interpolation method
    private static BigDecimal lagrangeInterpolation(List<Point> points, BigDecimal xValue) {
        BigDecimal result = BigDecimal.ZERO;

        for (int j = 0; j < points.size(); j++) {
            Point pj = points.get(j);
            BigDecimal lj = pj.y;

            for (int m = 0; m < points.size(); m++) {
                if (m != j) {
                    Point pm = points.get(m);
                    BigDecimal numerator = xValue.subtract(BigDecimal.valueOf(pm.x));
                    BigDecimal denominator = BigDecimal.valueOf(pj.x).subtract(BigDecimal.valueOf(pm.x));
                    lj = lj.multiply(numerator.divide(denominator, BigDecimal.ROUND_HALF_UP));
                }
            }
            result = result.add(lj);
        }
        return result;
    }
}