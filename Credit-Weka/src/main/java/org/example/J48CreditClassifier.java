package org.example;

import javax.swing.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class J48CreditClassifier {

    public static void main(String[] args) {
        try {
            String result = evaluateDecisionTree();
            showResultMessage(result);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    private static String evaluateDecisionTree() {
        String checkingStatus = selectOption("checking_status", List.of("<0", "0<=X<200", ">=200", "no checking"));
        if (checkingStatus.equals("<0")) {
            String foreignWorker = selectOption("foreign_worker", List.of("yes", "no"));
            if (foreignWorker.equals("yes")) {
                double duration = getNumericInput("duration");
                if (duration <= 11) {
                    double existingCredits = getNumericInput("existing_credits");
                    if (existingCredits <= 1) {
                        String propertyMagnitude = selectOption("property_magnitude", List.of("real estate", "life insurance", "car", "no known property"));
                        switch (propertyMagnitude) {
                            case "real estate":
                                return formatResult("good", 8.0, 1.0);
                            case "life insurance":
                                String ownTelephone = selectOption("own_telephone", List.of("none", "yes"));
                                return ownTelephone.equals("none")
                                        ? formatResult("bad", 2.0, 0.0)
                                        : formatResult("good", 4.0, 0.0);
                            case "car":
                                return formatResult("good", 2.0, 1.0);
                            case "no known property":
                                return formatResult("bad", 3.0, 0.0);
                        }
                    } else {
                        return formatResult("good", 14.0, 0.0);
                    }
                } else {
                    String job = selectOption("job", List.of("unemp/unskilled non res", "unskilled resident", "skilled", "high qualif/self emp/mgmt"));
                    switch (job) {
                        case "unemp/unskilled non res":
                            return formatResult("bad", 5.0, 1.0);
                        case "unskilled resident":
                            String purpose = selectOption("purpose", List.of("new car", "used car", "furniture/equipment", "radio/tv", "domestic appliance",
                                    "repairs", "education", "vacation", "retraining", "business", "other"));
                            if (purpose.equals("new car")) {
                                String ownTelephone = selectOption("own_telephone", List.of("none", "yes"));
                                return ownTelephone.equals("none")
                                        ? formatResult("bad", 10.0, 2.0)
                                        : formatResult("good", 2.0, 0.0);
                            } else if (purpose.equals("used car")) {
                                return formatResult("bad", 1.0, 0.0);
                            } else if (purpose.equals("furniture/equipment")) {
                                String employment = selectOption("employment", List.of("unemployed", "<1", "1<=X<4", "4<=X<7", ">=7"));
                                switch (employment) {
                                    case "unemployed":
                                        return formatResult("good", 0.0, 0.0);
                                    case "<1":
                                        return formatResult("bad", 3.0, 0.0);
                                    default:
                                        return formatResult("good", 4.0, 0.0);
                                }
                            }
                            return formatResult("bad", 0.0, 0.0); // Valor por defecto si no se encuentra hoja
                        case "skilled":
                            return formatResult("good", 30.0, 8.0);
                        case "high qualif/self emp/mgmt":
                            return formatResult("good", 30.0, 8.0);
                    }
                }
            } else {
                return formatResult("good", 15.0, 2.0);
            }
        } else if (checkingStatus.equals("0<=X<200")) {
            double creditAmount = getNumericInput("credit_amount");
            if (creditAmount <= 9857) {
                String savingsStatus = selectOption("savings_status", List.of("<100", "100<=X<500", "500<=X<1000", ">=1000", "no known savings"));
                if (savingsStatus.equals("<100")) {
                    String otherParties = selectOption("other_parties", List.of("none", "co applicant", "guarantor"));
                    if (otherParties.equals("none")) {
                        double duration = getNumericInput("duration");
                        if (duration <= 42) {
                            String personalStatus = selectOption("personal_status", List.of("male div/sep", "female div/dep/mar", "male single", "male mar/wid", "female single"));
                            if (personalStatus.equals("male div/sep")) {
                                return formatResult("bad", 8.0, 2.0);
                            }
                            return formatResult("good", 52.0, 15.0);
                        } else {
                            return formatResult("bad", 7.0, 0.0);
                        }
                    } else {
                        return formatResult("good", 2.0, 0.0);
                    }
                }
            } else {
                return formatResult("bad", 20.0, 3.0);
            }
        } else if (checkingStatus.equals(">=200")) {
            return formatResult("good", 63.0, 14.0);
        } else if (checkingStatus.equals("no checking")) {
            return formatResult("good", 394.0, 46.0);
        }
        return formatResult("unknown", 0.0, 0.0);
    }

    private static String formatResult(String classification, double correct, double incorrect) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#.##", symbols);

        double percentage = correct + incorrect == 0 ? 0 : (correct / (correct + incorrect)) * 100;

        // Limitar porcentaje a 50% para clasificación "bad"
        if (classification.equals("bad") && percentage > 50) {
            percentage = 50;
        }

        return String.format("%s:%s", classification, decimalFormat.format(percentage));
    }

    private static String selectOption(String attribute, List<String> options) {
        String selectedValue = (String) JOptionPane.showInputDialog(null,
                "Seleccione el valor para: " + attribute,
                "Entrada de datos",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options.toArray(),
                options.get(0));
        if (selectedValue == null) {
            JOptionPane.showMessageDialog(null, "Operación cancelada.");
            System.exit(0);
        }
        return selectedValue;
    }

    private static double getNumericInput(String attribute) {
        while (true) {
            try {
                String inputValue = JOptionPane.showInputDialog("Introduzca el valor para: " + attribute);
                if (inputValue == null) {
                    JOptionPane.showMessageDialog(null, "Operación cancelada.");
                    System.exit(0);
                }
                return Double.parseDouble(inputValue);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Entrada inválida. Intente de nuevo.");
            }
        }
    }

    private static void showResultMessage(String result) {
        String[] parts = result.split(":");
        String classification = parts[0];
        double confidence = Double.parseDouble(parts[1]);

        String message;
        String color;

        if (classification.equals("good")) {
            message = "La persona es adecuada para adquirir el crédito.";
            color = "green";
        } else if (classification.equals("bad")) {
            message = "La persona no es apta para adquirir el crédito.";
            color = "red";
        } else {
            message = "Clasificación desconocida.";
            color = "black";
        }

        String htmlMessage = String.format("<html><body style='color:%s; font-size:14px;'>%s<br>Confianza: %.2f%%</body></html>",
                color, message, confidence);

        JOptionPane.showMessageDialog(null, htmlMessage, "Resultado de Clasificación", JOptionPane.INFORMATION_MESSAGE);
    }
}
