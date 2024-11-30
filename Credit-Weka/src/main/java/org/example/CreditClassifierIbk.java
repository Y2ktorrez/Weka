package org.example;

import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.DistanceFunction;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.DenseInstance;
import weka.core.neighboursearch.LinearNNSearch;
import java.io.FileNotFoundException;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CreditClassifierIbk {
    private IBk model;
    private Instances dataStructure;


    // Constructor que carga los datos y configura el clasificador
    public CreditClassifierIbk(String arffPath, String configPath) throws Exception {

        // Crear una instancia del clasificador IBk
        model = new IBk(); // Inicializamos sin parámetros

        // Cargar la configuración desde el archivo lazy.ibk
        loadConfiguration(configPath);

        // Cargar el archivo ARFF y crear el conjunto de datos
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream(arffPath))
        );
        if (reader == null) {
            throw new FileNotFoundException("Archivo ARFF no encontrado: " + arffPath);
        }

        dataStructure = new Instances(reader);
        reader.close();

        // Establecer el atributo de clase como la última columna
        dataStructure.setClassIndex(dataStructure.numAttributes() - 1);

        // Entrenar el modelo con los datos cargados
        model.buildClassifier(dataStructure);
    }

    // Método para cargar configuraciones desde el archivo lazy.ibk
    private void loadConfiguration(String configPath) throws Exception {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(configPath)))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("Scheme:")) {
                    // Detectar el número de vecinos (-K)
                    if (line.contains("-K")) {
                        String[] parts = line.split("-K");
                        int k = Integer.parseInt(parts[1].trim().split(" ")[0]);
                        model.setKNN(k); // Configurar K en el modelo
                    }
                }
            }
        }

        // Configurar el algoritmo para usar distancia euclidiana
        LinearNNSearch search = new LinearNNSearch();
        DistanceFunction distanceFunction = new EuclideanDistance();
        search.setDistanceFunction(distanceFunction);
        model.setNearestNeighbourSearchAlgorithm(search);
    }

    // Evaluación del modelo
    public void evaluateModel() throws Exception {
        Evaluation evaluation = new Evaluation(dataStructure);
        evaluation.crossValidateModel(model, dataStructure, 10, new java.util.Random(1));
        System.out.println("=== Summary ===");
        System.out.println(evaluation.toSummaryString());
        System.out.println("=== Detailed Accuracy By Class ===");
        System.out.println(evaluation.toClassDetailsString());
        System.out.println("=== Confusion Matrix ===");
        System.out.println(evaluation.toMatrixString());
    }

    // Clasificar una nueva instancia
    public ClassificationResult classifyInstance(double[] attributeValues) throws Exception {
        Instance newInstance = new DenseInstance(dataStructure.numAttributes());
        newInstance.setDataset(dataStructure);

        for (int i = 0; i < attributeValues.length; i++) {
            newInstance.setValue(i, attributeValues[i]);
        }

        double[] distribution = model.distributionForInstance(newInstance);
        double goodProbability = distribution[dataStructure.classAttribute().indexOfValue("good")];
        double badProbability = distribution[dataStructure.classAttribute().indexOfValue("bad")];

        String result = goodProbability >= 0.5 ? "good" : "bad";
        double percentage = goodProbability * 100;

        return new ClassificationResult(result, percentage);
    }

    // Clase interna para encapsular resultados
    public static class ClassificationResult {
        private final String result;
        private final double percentage;

        public ClassificationResult(String result, double percentage) {
            this.result = result;
            this.percentage = percentage;
        }

        public String getResult() {
            return result;
        }

        public double getPercentage() {
            return percentage;
        }
    }
}
