package com.viacosta;

import com.viacosta.view.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Arrancador principal único del Sistema VIACOSTA (Java Swing - NetBeans GUI).
 * Entra a este archivo y dale a "Run" en NetBeans para iniciar la aplicación.
 */
public class Main {

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
            System.out.println("[Main] Sistema VIACOSTA (Java Swing) iniciado con exito.");
        });
    }
}
