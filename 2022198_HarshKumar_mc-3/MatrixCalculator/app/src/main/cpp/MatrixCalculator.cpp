#include <jni.h>
#include <string>
#include <vector>
#include "Eigen/Dense"

using namespace Eigen;

// Java array to Eigen Matrix
MatrixXd arrayToMatrix(JNIEnv *env, jobjectArray array, int rows, int cols) {
    MatrixXd mat(rows, cols);
    for (int i = 0; i < rows; ++i) {
        jdoubleArray row = (jdoubleArray) env->GetObjectArrayElement(array, i);
        jdouble* rowElements = env->GetDoubleArrayElements(row, 0);
        for (int j = 0; j < cols; ++j) {
            mat(i, j) = rowElements[j];
        }
        env->ReleaseDoubleArrayElements(row, rowElements, 0);
    }
    return mat;
}

// Eigen Matrix to Java array
jobjectArray matrixToArray(JNIEnv *env, const MatrixXd& mat) {
    jobjectArray result = env->NewObjectArray(mat.rows(), env->FindClass("[D"), nullptr);
    for (int i = 0; i < mat.rows(); ++i) {
        jdoubleArray row = env->NewDoubleArray(mat.cols());
        env->SetDoubleArrayRegion(row, 0, mat.cols(), mat.row(i).data());
        env->SetObjectArrayElement(result, i, row);
        env->DeleteLocalRef(row);
    }
    return result;
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_example_matrixcalculator_MainActivity_addMatrices(JNIEnv *env, jobject /*this*/,
                                                           jobjectArray mat1,
                                                           jobjectArray mat2,
                                                           jint rows1, jint cols1,
                                                           jint rows2, jint cols2) {
    // Matrix addition requires same dimensions
    if (rows1 != rows2 || cols1 != cols2) {
        return nullptr;
    }
    
    MatrixXd A = arrayToMatrix(env, mat1, rows1, cols1);
    MatrixXd B = arrayToMatrix(env, mat2, rows2, cols2);
    MatrixXd C = A + B;
    return matrixToArray(env, C);
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_example_matrixcalculator_MainActivity_subtractMatrices(JNIEnv *env, jobject /*this*/,
                                                                jobjectArray mat1,
                                                                jobjectArray mat2,
                                                                jint rows1, jint cols1,
                                                                jint rows2, jint cols2) {
    // Matrix subtraction requires same dimensions
    if (rows1 != rows2 || cols1 != cols2) {
        return nullptr;
    }
    
    MatrixXd A = arrayToMatrix(env, mat1, rows1, cols1);
    MatrixXd B = arrayToMatrix(env, mat2, rows2, cols2);
    MatrixXd C = A - B;
    return matrixToArray(env, C);
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_example_matrixcalculator_MainActivity_multiplyMatrices(JNIEnv *env, jobject /*this*/,
                                                                jobjectArray mat1,
                                                                jobjectArray mat2,
                                                                jint rows1, jint cols1,
                                                                jint rows2, jint cols2) {
    // Matrix multiplication requires cols1 == rows2
    if (cols1 != rows2) {
        return nullptr;
    }
    
    MatrixXd A = arrayToMatrix(env, mat1, rows1, cols1);
    MatrixXd B = arrayToMatrix(env, mat2, rows2, cols2);
    MatrixXd C = A * B;
    return matrixToArray(env, C);
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_example_matrixcalculator_MainActivity_divideMatrices(JNIEnv *env, jobject /*this*/,
                                                              jobjectArray mat1,
                                                              jobjectArray mat2,
                                                              jint rows1, jint cols1,
                                                              jint rows2, jint cols2) {
    // Element-wise division requires same dimensions
    if (rows1 != rows2 || cols1 != cols2) {
        return nullptr;
    }
    
    MatrixXd A = arrayToMatrix(env, mat1, rows1, cols1);
    MatrixXd B = arrayToMatrix(env, mat2, rows2, cols2);
    
    // Check for division by zero (any element in B is zero)
    for (int i = 0; i < B.rows(); ++i) {
        for (int j = 0; j < B.cols(); ++j) {
            if (std::abs(B(i, j)) < 1e-10) {
                return nullptr;
            }
        }
    }
    
    MatrixXd C = A.cwiseQuotient(B);
    return matrixToArray(env, C);
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_example_matrixcalculator_MainActivity_divideByInverseMatrices(JNIEnv *env, jobject /*this*/,
                                                                jobjectArray mat1,
                                                                jobjectArray mat2,
                                                                jint rows1, jint cols1,
                                                                jint rows2, jint cols2) {
    // For division using inverse: A / B = A * B^(-1)
    // second matrix must be square for inverse to exist
    if (rows2 != cols2) {
        return nullptr;
    }
    
    // For matrix multiplication after inversion: cols1 must equal rows2
    if (cols1 != rows2) {
        return nullptr;
    }
    
    MatrixXd A = arrayToMatrix(env, mat1, rows1, cols1);
    MatrixXd B = arrayToMatrix(env, mat2, rows2, cols2);
    
    // Check if B is invertible by calculating determinant
    double det = B.determinant();
    if (std::abs(det) < 1e-10) {
        return nullptr;
    }

    MatrixXd B_inv = B.inverse();
    MatrixXd C = A * B_inv;
    
    return matrixToArray(env, C);
}
