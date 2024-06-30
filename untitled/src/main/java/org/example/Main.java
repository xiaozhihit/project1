package org.example;

import org.apache.commons.math.analysis.polynomials.PolynomialFunction;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Main {

    private static double evaluatePolynomial(double[] coefficients, double x) {
        PolynomialFunction polynomial = new PolynomialFunction(coefficients);
        return polynomial.value(x);
    }


    // 从文件加载模型
    private static double[] loadModel(String filename) {
        double[] coefficients = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            coefficients = (double[]) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return coefficients;
    }
    public static void main(String[] args) {
        String modelFile = "polynomial_regression_model.ser";
        double[] loadedCoefficients = loadModel(modelFile);
        double predictedSuccessRate = evaluatePolynomial(loadedCoefficients, 1.1);
        System.out.println(predictedSuccessRate);
    }
}
