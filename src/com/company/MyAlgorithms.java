package com.company;

/**
 * Created by Worck on 18.04.2016.
 */
public class MyAlgorithms {

    // Функція для введення константи
    public static int inputConstant(int a) {
        return a;
    }

    //Функція для введення вектора
    public static int[] inputVector(int n,int value) {
        int[] result = new int[n];
        int start = 4;
        for (int i = 0; i < result.length; i++) {
            result[i] = value;
        }
        return result;
    }

    //Функція для введення матриці
    public static int[][] inputMatrix(int n,int value) {
        int[][] result = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                result[i][j] = value;
            }
        return result;
    }

    // Функція для виводу матриці на екран
    public static  void outputMatrix(int[][] param1) {

        for (int i = 0; i < param1.length ; i++) {
            for (int j = 0; j < param1[0].length; j++) {
                System.out.print(param1[i][j]);
            }
            System.out.println();
        }
    }

    // Функція для виводу вектора на екран
    public static void outputVector(int[] param1) {

        for (int i = 0; i < param1.length; i++)
                System.out.print("|"+param1[i]);
    }

    // Функція для множення вектора на матрицю
    public  static int [] MultipVectorToMatrix(int [] param1,int[][] param2) {

        int[] result = new int[param1.length];

        for (int i = 0; i < param2.length; i++) {
            for (int j = 0; j < param1.length; j++) {
                result[i] = result[i]+param2[i][j] * param1[j];
            }
        }
        return result;
        }

    // Функція для множення матриць
    public static int[][] multMatrix(int[][] param1, int[][] param2) {
        if (param1[0].length != param2.length) {
            System.out.println("Exaptation Null pointer Mozgiv programizda");
            return null;
        }
        int[][] result = new int[param1.length][param1[0].length];
        for (int k = 0; k < param1.length; k++) {
            for (int i = 0; i < param1[0].length; i++) {
                for (int j = 0; j < param2.length; j++) {
                    result[k][i] += param1[k][j] * param2[j][i];
                }
            }
        }
        return result;
    }

    // Функція для додавання векторів
    public static int [] addVector(int [] param1, int [] param2 ){
        int[] result = new int[param1.length];
        for (int i=0; i<param1.length; i++){
            result[i] = param1[i] + param2[i];
        }
        return result;
    }

      // Функція для меоження  константи на вектор
      public static int [] multiplyNumberVector(int [] param1, int param2){

          int[] result = new int[param1.length];

        for (int i=0; i<param1.length; i++){
           result[i] = param1[i] * param2;
        }
        return result;
    }

}
