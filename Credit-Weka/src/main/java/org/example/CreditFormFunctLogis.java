package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class CreditFormFunctLogis extends JFrame {
    private HashMap<String, JComponent> inputFields;
    private JLabel resultLabel;
    private CreditClassifier classifier;

    public CreditFormFunctLogis() {
        setTitle("Clasificador de Créditos Functions Logis");
        setSize(500, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(0, 2, 10, 10));

        // Crear margen alrededor de todo el panel
        JPanel contentPane = new JPanel(new GridLayout(0, 2, 10, 10));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        try {
            classifier = new CreditClassifier("funtionsLogis.model", "credit.g.arff");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar el modelo: " + e.getMessage());
            System.exit(1);
        }

        String[] attributeNames = {
                "Estado de la cuenta", "Duración (meses)", "Historial crediticio", "Propósito del crédito",
                "Monto del crédito", "Estado de ahorros", "Duración empleo", "Compromiso de pagos mensuales",
                "Estado civil", "Otros avales", "Años en residencia", "Propiedad",
                "Edad", "Otros planes de pago", "Tipo de vivienda", "Créditos existentes", "Ocupación",
                "Dependientes", "Teléfono propio", "Trabajador extranjero"
        };

        HashMap<String, String[][]> attributeValues = new HashMap<>();
        attributeValues.put("Estado de la cuenta", new String[][]{
                {"Saldo negativo", "<0"},
                {"0 a 200 unidades", "0<=X<200"},
                {"Más de 200 unidades", ">=200"},
                {"Sin cuenta", "no checking"}
        });
        attributeValues.put("Historial crediticio", new String[][]{
                {"Sin créditos/Todos pagados", "no credits/all paid"},
                {"Todos pagados", "all paid"},
                {"Pagos existentes", "existing paid"},
                {"Pagos retrasados", "delayed previously"},
                {"Crédito crítico", "critical/other existing credit"}
        });
        attributeValues.put("Propósito del crédito", new String[][]{
                {"Coche nuevo", "new car"},
                {"Coche usado", "used car"},
                {"Muebles/equipos", "furniture/equipment"},
                {"Radio/TV", "radio/tv"},
                {"Electrodomésticos", "domestic appliance"},
                {"Reparaciones", "repairs"},
                {"Educación", "education"},
                {"Vacaciones", "vacation"},
                {"Reentrenamiento", "retraining"},
                {"Negocio", "business"},
                {"Otro", "other"}
        });
        attributeValues.put("Estado de ahorros", new String[][]{
                {"Menos de 100", "<100"},
                {"100 a 500", "100<=X<500"},
                {"500 a 1000", "500<=X<1000"},
                {"Más de 1000", ">=1000"},
                {"Sin ahorros", "no known savings"}
        });
        attributeValues.put("Duración empleo", new String[][]{
                {"Desempleado", "unemployed"},
                {"Menos de 1 año", "<1"},
                {"1 a 4 años", "1<=X<4"},
                {"4 a 7 años", "4<=X<7"},
                {"Más de 7 años", ">=7"}
        });
        attributeValues.put("Estado civil", new String[][]{
                {"Hombre divorciado/separado", "male div/sep"},
                {"Mujer divorciada/dependiente/casada", "female div/dep/mar"},
                {"Hombre soltero", "male single"},
                {"Hombre casado/viudo", "male mar/wid"},
                {"Mujer soltera", "female single"}
        });
        attributeValues.put("Otros avales", new String[][]{
                {"Ninguno", "none"},
                {"Co-solicitante", "co applicant"},
                {"Garante", "guarantor"}
        });
        attributeValues.put("Propiedad", new String[][]{
                {"Inmueble", "real estate"},
                {"Seguro de vida", "life insurance"},
                {"Coche", "car"},
                {"Sin propiedad conocida", "no known property"}
        });
        attributeValues.put("Otros planes de pago", new String[][]{
                {"Ninguno", "none"},
                {"Banco", "bank"},
                {"Tiendas", "stores"}
        });
        attributeValues.put("Tipo de vivienda", new String[][]{
                {"Propia", "own"},
                {"Alquiler", "rent"},
                {"Sin costo", "for free"}
        });
        attributeValues.put("Ocupación", new String[][]{
                {"Desempleado/no cualificado no residente", "unemp/unskilled non res"},
                {"No cualificado residente", "unskilled resident"},
                {"Cualificado", "skilled"},
                {"Altamente cualificado/independiente/gestión", "high qualif/self emp/mgmt"}
        });
        attributeValues.put("Teléfono propio", new String[][]{
                {"Ninguno", "none"},
                {"Sí", "yes"}
        });
        attributeValues.put("Trabajador extranjero", new String[][]{
                {"Sí", "yes"},
                {"No", "no"}
        });

        inputFields = new HashMap<>();
        for (int i = 0; i < attributeNames.length; i++) {
            add(new JLabel(attributeNames[i]));

            String englishAttributeName = mapToEnglish(attributeNames[i]);
            if (attributeValues.containsKey(attributeNames[i])) {
                String[][] options = attributeValues.get(attributeNames[i]);
                JComboBox<String> comboBox = new JComboBox<>();
                for (String[] option : options) {
                    comboBox.addItem(option[0] + " (" + option[1] + ")");
                }
                inputFields.put(englishAttributeName, comboBox);
                add(comboBox);
            } else {
                JTextField textField = new JTextField();
                inputFields.put(englishAttributeName, textField);
                add(textField);
            }
        }



        JButton classifyButton = new JButton("Clasificar");
        classifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                classifyData(attributeNames, attributeValues);
            }
        });
        add(classifyButton);

        resultLabel = new JLabel("Resultado: ");
        add(resultLabel);
    }

    private String mapToEnglish(String spanishAttributeName) {
        switch (spanishAttributeName) {
            case "Estado de la cuenta": return "checking_status";
            case "Duración (meses)": return "duration";
            case "Historial crediticio": return "credit_history";
            case "Propósito del crédito": return "purpose";
            case "Monto del crédito": return "credit_amount";
            case "Estado de ahorros": return "savings_status";
            case "Duración empleo": return "employment";
            case "Compromiso de pagos mensuales": return "installment_commitment";
            case "Estado civil": return "personal_status";
            case "Otros avales": return "other_parties";
            case "Años en residencia": return "residence_since";
            case "Propiedad": return "property_magnitude";
            case "Edad": return "age";
            case "Otros planes de pago": return "other_payment_plans";
            case "Tipo de vivienda": return "housing";
            case "Créditos existentes": return "existing_credits";
            case "Ocupación": return "job";
            case "Dependientes": return "num_dependents";
            case "Teléfono propio": return "own_telephone";
            case "Trabajador extranjero": return "foreign_worker";
            default: return spanishAttributeName;
        }
    }


    private void classifyData(String[] attributeNames, HashMap<String, String[][]> attributeValues) {
        try {
            double[] attributeValuesArray = new double[attributeNames.length];

            StringBuilder summary = new StringBuilder("<html><h3>Detalles clave:</h3><ul>");
            String duration = ""; // Para almacenar la duración
            String purpose = "";
            String creditAmount = "";
            String creditHistory = "";
            String checkingStatus ="";
            String foreingWorker = "";
            String installmentCommit = "";

            for (int i = 0; i < attributeNames.length; i++) {
                String attributeName = mapToEnglish(attributeNames[i]);
                JComponent field = inputFields.get(attributeName);

                if (field instanceof JComboBox) {
                    String selectedValue = (String) ((JComboBox<?>) field).getSelectedItem();
                    String originalValue = extractOriginalValue(selectedValue);

                    String[][] options = attributeValues.get(attributeNames[i]);
                    attributeValuesArray[i] = indexOf(options, originalValue);

                    // Capturar valores específicos
                    if ("checking_status".equals(attributeName)) {
                        checkingStatus = selectedValue;
                    } else if ("purpose".equals(attributeName)) {
                        purpose = selectedValue;
                    } else if ("credit_history".equals(attributeName)) {
                        creditHistory = selectedValue;
                    }
                    else if ("foreign_worker".equals(attributeName)) {
                        foreingWorker = selectedValue;
                    }else if ("installment_commitment".equals(attributeName)){
                        installmentCommit = selectedValue;
                    }
                } else if (field instanceof JTextField) {
                    String textValue = ((JTextField) field).getText();
                    attributeValuesArray[i] = Double.parseDouble(textValue);

                    if ("credit_amount".equals(attributeName)) {
                        creditAmount = textValue;
                    }else if ("duration".equals(attributeName)){
                        duration = textValue;
                    } else if ("installment_commitment".equals(attributeName)){
                        installmentCommit = textValue;
                    }
                }
            }


//            summary.append("<li><b>Duración:</b> ").append(duration).append("</li>");
//            summary.append("<li><b>Propósito:</b> ").append(purpose).append("</li>");
//            summary.append("<li><b>Monto del crédito:</b> ").append(creditAmount).append("</li>");
//            summary.append("<li><b>Historial crediticio:</b> ").append(creditHistory).append("</li>");
//            summary.append("</ul></html>");

            CreditClassifier.ClassificationResult classificationResult = classifier.classifyWithProbability(attributeValuesArray);

            if(classificationResult.getPercentage() >= 51){
                summary.append("<li><b>Estado de la cuenta:</b> ").append(checkingStatus).append("</li>");
                summary.append("<li><b>Propósito:</b> ").append(purpose).append("</li>");
                summary.append("<li><b>Monto del crédito:</b> ").append(creditAmount).append("</li>");
                summary.append("<li><b>Trabajador extranjero:</b> ").append(foreingWorker).append("</li>");
                summary.append("</ul></html>");
            }else{
                summary.append("<li><b>Duración:</b> ").append(duration).append("</li>");
                summary.append("<li><b>Compromiso de pagos mensuales:</b> ").append(installmentCommit).append("</li>");
                summary.append("<li><b>Monto del crédito:</b> ").append(creditAmount).append("</li>");
                summary.append("<li><b>Historial crediticio:</b> ").append(creditHistory).append("</li>");
                summary.append("</ul></html>");
            }

            // Determinar color basado en probabilidad
            String color = classificationResult.getPercentage() >= 51 ? "green" : "red";

            // Crear el mensaje del resultado
            String resultMessage = String.format(
                    "<html><h2>Resultado de la Clasificación</h2>" +
                            "<p><b style='color:%s;'>Probabilidad : %.2f%%</b></p>%s</html>",
                    color, classificationResult.getPercentage(), summary.toString()
            );

            // Mostrar el mensaje en un JOptionPane
            JLabel messageLabel = new JLabel(resultMessage);
            messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            JOptionPane.showMessageDialog(this, messageLabel, "Resultado de la Clasificación", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al procesar los datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }



    private String extractOriginalValue(String combinedValue) {
        if (combinedValue.contains("(") && combinedValue.contains(")")) {
            return combinedValue.substring(combinedValue.indexOf("(") + 1, combinedValue.indexOf(")"));
        }
        return combinedValue;
    }

    private int indexOf(String[][] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i][1].equals(value)) {
                return i;
            }
        }
        return -1; 
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CreditFormFunctLogis form = new CreditFormFunctLogis();
            form.setVisible(true);
        });
    }
}
