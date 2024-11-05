import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ShamirSecretSharing {

    public static void main(String[] args) {
        try {
            // Parse the JSON data from a file
            JSONObject jsonObject = new JSONObject(new JSONTokener(new FileInputStream("testcase1.json")));

            // Read 'n' and 'k' values
            JSONObject keys = jsonObject.getJSONObject("keys");
            int n = keys.getInt("n");
            int k = keys.getInt("k");

            // Prepare to store the decoded points
            List<Point> points = new ArrayList<>();

            // Loop through the root entries and decode each one
            for (String key : jsonObject.keySet()) {
                if (!key.equals("keys")) {
                    int x = Integer.parseInt(key);
                    JSONObject rootData = jsonObject.getJSONObject(key);

                    // Decode y value from the specified base
                    int base = rootData.getInt("base");
                    String value = rootData.getString("value");
                    BigInteger y = new BigInteger(value, base);

                    // Add the point to the list
                    points.add(new Point(x, y));
                }
            }

            // Calculate the constant term 'c' using Lagrange interpolation
            BigInteger constantTerm = calculateConstantTerm(points, k);
            System.out.println("The constant term (secret) is: " + constantTerm);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // A helper class to store points (x, y)
    static class Point {
        int x;
        BigInteger y;

        public Point(int x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Calculate the constant term of the polynomial using Lagrange interpolation.
     *
     * @param points The list of points (x, y) used in interpolation.
     * @param k The minimum number of points required.
     * @return The constant term 'c' of the polynomial.
     */
    public static BigInteger calculateConstantTerm(List<Point> points, int k) {
        BigInteger result = BigInteger.ZERO;

        // Perform Lagrange interpolation at x = 0 to find the constant term
        for (int i = 0; i < k; i++) {
            BigInteger term = points.get(i).y;
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    int xi = points.get(i).x;
                    int xj = points.get(j).x;

                    term = term.multiply(BigInteger.valueOf(-xj))
                                .divide(BigInteger.valueOf(xi - xj));
                }
            }
            result = result.add(term);
        }

        return result;
    }
}
