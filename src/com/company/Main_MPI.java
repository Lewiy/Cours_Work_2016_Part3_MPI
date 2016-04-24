package com.company;
import mpi.MPI;
public class Main_MPI{

    private static int[][] MO, MT, MR;              // Операнди виразу
    public static int[] Z,C,B,A,arrayA;
    public static int[] VectorMR,VectorMO,VectorMT; //Матриці трансформовані
                                                    // для передачі між процесорами
    // Локальна память для передачі частин даних
    public static int[] BuffGetZ,BuffGetC,BuffGetB,BuffGetMO,BuffGetMT;
    // Проміжні змінні
    // N - Розмірність матриць, векторів
    public static int N,H,a,InputValueConstatnt_a,InputValueConstant_Matrix,P;

    public static void main(String[] args) {

        // Мета дані для створення топології
        int[] dims = {2,4};
        boolean[] periodic = {true,true};
        MPI.Init(args);

        // Визначення кількості процесів, їхніх рангів
        int rank = MPI.COMM_WORLD.Rank();
            P = MPI.COMM_WORLD.Size();

        // Значення введених даних
            InputValueConstant_Matrix = 1;
            InputValueConstatnt_a = 1;
            N = 16;
            H = N/P;
        //Створення топології
       // System.out.println(P+"- Processes");
        MPI.COMM_WORLD.Create_cart(dims,periodic,false);

        // Створення буферів памяті для передачі
        createMemory(N,H);

        //Введення даних у відповідні процеси згідно із завданням
        initData(rank);

        //Конвертування матриць у вектори
        VectorMR = convertMatrixToVector(MR);
        VectorMO = convertMatrixToVector(MO);
        VectorMT = convertMatrixToVector(MT);

        //Передача даних всім процесам
        MPI.COMM_WORLD.Bcast(Z,0,N,MPI.INT,0);
        MPI.COMM_WORLD.Bcast(C,0,N,MPI.INT,7);
        MPI.COMM_WORLD.Bcast(arrayA,0,1,MPI.INT,2);
        MPI.COMM_WORLD.Bcast(VectorMR,0,N*N,MPI.INT,2);
        MPI.COMM_WORLD.Scatter(B,0,H,MPI.INT,BuffGetB,0,H,MPI.INT,2);
        MPI.COMM_WORLD.Scatter(VectorMO,0,N*H,MPI.INT,BuffGetMO,0,N*H,MPI.INT,7);
        MPI.COMM_WORLD.Scatter(VectorMT,0,N*H,MPI.INT,BuffGetMT,0,N*H,MPI.INT,0);

        //Конвертування матриць-векторів назад у матриці
        //для виконання обчислень
        int[][] ConvertMT = converVectorToMatrixH(BuffGetMT);
        int [][] ConvertMR = converVectorToMatrix(VectorMR);
        int [][] ConvertMO = converVectorToMatrixH(BuffGetMO);

        // Обчислення виразу згідно із завданням
        int [][] MT_MR = MyAlgorithms.multMatrix(ConvertMT,ConvertMR);
        int [] Z_MT_MR = MyAlgorithms.MultipVectorToMatrix(Z,MT_MR);
        int [] C_MO = MyAlgorithms.MultipVectorToMatrix(C,ConvertMO);
        int [] B_C_MO = MyAlgorithms.addVector(BuffGetB,C_MO);
        int [] a_B_C_MO = MyAlgorithms.multiplyNumberVector(B_C_MO,arrayA[0]);
        int [] AH = MyAlgorithms.addVector(a_B_C_MO,Z_MT_MR);

        // Збір результатів зі всіх процесів в процес з номером 0
        int [] result = new int [N];
        MPI.COMM_WORLD.Gather(AH,0,H,MPI.INT,result,0,H,MPI.INT,0);

        // Вивід на екран результату в процесі 0
        if(rank==0) {
            MyAlgorithms.outputVector(result);
        }

        // Кіньць роботи бібліотеки MPI
        MPI.Finalize();

    }

    // Введення даних у відповідні процеси згідно із завданням
    public static void initData(int rank) {

        if(rank == 0){
            A = MyAlgorithms.inputVector(N,InputValueConstant_Matrix);
            Z = MyAlgorithms.inputVector(N,InputValueConstant_Matrix);
            MT = MyAlgorithms.inputMatrix(N,InputValueConstant_Matrix);
        }

        if(rank == 2){
            B = MyAlgorithms.inputVector(N,InputValueConstant_Matrix);
            MR = MyAlgorithms.inputMatrix(N,InputValueConstant_Matrix);
            a = MyAlgorithms.inputConstant(InputValueConstatnt_a);
            arrayA[0]=a;
        }

        if(rank == 7){
            C = MyAlgorithms.inputVector(N,InputValueConstant_Matrix);
            MO = MyAlgorithms.inputMatrix(N,InputValueConstant_Matrix);
        }

    }

    // Створення буферів памяті для передачі
    public static void createMemory(int n,int h){
        Z = new int[N];
        C = new int[N];
        B = new int[N];
        A = new int[N];
        MO = new int[n][n];
        MR = new int[n][n];
        MT = new int[n][n];
        BuffGetZ = new int [H];
        BuffGetC = new int [H];
        BuffGetB = new int [H];
        BuffGetMO = new int [N*H];
        BuffGetMT = new int [N*H];
        arrayA = new int[1];
    }

    // Конвертування матриць у вектори
    public static int[] convertMatrixToVector(int[][] param1){

        int[] result = new int[N*N];
        for (int i=0; i<N; i++) {
            for(int j=0; j<N; j++) {
                result[i*N+j] = param1[i][j];
            }
        }
        return result;
    }

    // Конвертування матриць-векторів назад у матриці
    //для виконання обчислень
    public static int[][] converVectorToMatrix(int [] param1){

        int[][] result = new int[N][N];
        int tmp=0;
        for (int i=0; i<N; i++) {
            for(int j=0; j<N; j++) {
                if(j==N-1)
                   tmp +=j;
                 result [i][j] = param1[j+tmp];
            }
        }
        return result;
    }

    //Конвертування матриць-векторів частин назад у матриці
    //для виконання обчислень
    public static int[][] converVectorToMatrixH(int [] param1){

        int[][] result = new int[H][N];
        int tmp=0;
        for (int i=0; i<H; i++) {
            for(int j=0; j<N; j++) {
                result [i][j] = param1[j+tmp];
                if(j==(N-1))
                    tmp +=j;
            }
        }
        return result;
    }
}
