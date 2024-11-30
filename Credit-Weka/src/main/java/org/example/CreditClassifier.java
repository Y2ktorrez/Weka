package org.example;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.DenseInstance;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;

public class CreditClassifier {
    private Classifier model;
    private Instances dataStructure;

    public CreditClassifier(String modelPath, String arffPath) throws Exception {
        ObjectInputStream ois = new ObjectInputStream(getClass().getClassLoader().getResourceAsStream(modelPath));
        model = (Classifier) ois.readObject();
        ois.close();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream(arffPath))
        );
        dataStructure = new Instances(reader);
        reader.close();

        dataStructure.setClassIndex(dataStructure.numAttributes() - 1);
    }

    public ClassificationResult classifyWithProbability(double[] attributeValues) throws Exception {
        Instance newInstance = new DenseInstance(dataStructure.numAttributes());
        newInstance.setDataset(dataStructure);

        for (int i = 0; i < attributeValues.length; i++) {
            newInstance.setValue(i, attributeValues[i]);
        }

        double[] distribution = model.distributionForInstance(newInstance);
        double goodProbability = distribution[dataStructure.classAttribute().indexOfValue("good")];
        double badProbability = distribution[dataStructure.classAttribute().indexOfValue("bad")];

        String result = goodProbability >= 0.51 ? "good" : "bad";
        double percentage = goodProbability * 100;

        return new ClassificationResult(result, percentage);
    }

    public String getClassificationPath(double[] attributeValues) throws Exception {
        StringBuilder path = new StringBuilder();
        path.append("Ruta tomada por el modelo:\n");

        Instance newInstance = new DenseInstance(dataStructure.numAttributes());
        newInstance.setDataset(dataStructure);
        for (int i = 0; i < attributeValues.length; i++) {
            newInstance.setValue(i, attributeValues[i]);
        }

        // Explorar el árbol (asume que es un modelo J48)
        if (model instanceof weka.classifiers.trees.J48) {
            weka.classifiers.trees.J48 j48Tree = (weka.classifiers.trees.J48) model;
            path.append(j48Tree.graph());
        } else {
            path.append("El modelo no admite rastreo de decisiones.");
        }
        return path.toString();
    }

    public static class ClassificationResult {
        private String result;
        private double percentage;

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
