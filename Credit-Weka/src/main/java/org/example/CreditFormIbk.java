package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class CreditFormIbk extends JFrame {

    private HashMap<String, JComponent> inputFields;
    private JLabel resultLabel;
    private CreditClassifierIbk classifier;

    public CreditFormIbk() {
        setTitle("Clasificador de Créditos - Lazy-IBk");
        setSize(600, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(0, 2, 10, 10));

        // Crear margen alrededor de todo el panel
        JPanel contentPane = new JPanel(new GridLayout(0, 2, 10, 10));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        try {
            classifier = new CreditClassifierIbk("credit.g.arff", "lazy.ibk");
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

        // Configuración de valores predefinidos para atributos específicos
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

        // Continúa agregando atributos predefinidos como en el formulario detallado
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

        // Crear campos dinámicamente
        inputFields = new HashMap<>();
        for (String attributeName : attributeNames) {
            add(new JLabel(attributeName));
            if (attributeValues.containsKey(attributeName)) {
                // Si el atributo tiene valores predefinidos, usa un JComboBox
                JComboBox<String> comboBox = new JComboBox<>();
                for (String[] option : attributeValues.get(attributeName)) {
                    comboBox.addItem(option[0] + " (" + option[1] + ")");
                }
                inputFields.put(attributeName, comboBox);
                add(comboBox);
            } else {
                // Si no tiene valores predefinidos, usa un JTextField
                JTextField textField = new JTextField();
                inputFields.put(attributeName, textField);
                add(textField);
            }
        }

        // Botón de Clasificar
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

    private void classifyData(String[] attributeNames, HashMap<String, String[][]> attributeValues) {
        try {
            double[] attributeValuesArray = new double[attributeNames.length];

            for (int i = 0; i < attributeNames.length; i++) {
                JComponent field = inputFields.get(attributeNames[i]);
                if (field instanceof JComboBox) {
                    String selectedValue = (String) ((JComboBox<?>) field).getSelectedItem();
                    String originalValue = extractOriginalValue(selectedValue);

                    String[][] options = attributeValues.get(attributeNames[i]);
                    attributeValuesArray[i] = indexOf(options, originalValue);
                } else if (field instanceof JTextField) {
                    attributeValuesArray[i] = Double.parseDouble(((JTextField) field).getText());
                }
            }

            CreditClassifierIbk.ClassificationResult result = classifier.classifyInstance(attributeValuesArray);

            if ("good".equalsIgnoreCase(result.getResult())) {
                resultLabel.setForeground(Color.GREEN);
            } else {
                resultLabel.setForeground(Color.RED);
            }

            resultLabel.setText(String.format("Resultado: %s (%.2f%%)", result.getResult(), result.getPercentage()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al procesar los datos: " + e.getMessage());
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
            CreditFormIbk form = new CreditFormIbk();
            form.setVisible(true);
        });
    }
}
