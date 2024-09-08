package co.edu.uniquindio.poo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener; 

public class Prisioneros extends JFrame{

     private int n; private int m;
     private int prisioners, missingPrisioners;
     private JButton[][] jailButtons;
     private int[][] jail;
     private JPanel jailPanel;

     public Prisioneros(){
         setTitle("Prisioner Finder");
         setSize(600, 600);
         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         setLayout(new BorderLayout());
 
         // Panel para ingresar el tamaño del laberinto
         JPanel inputPanel = new JPanel();
         JLabel label = new JLabel("Tamaño del Laberinto NxM:");
         JTextField heightField = new JTextField(4);
         JTextField widthField = new JTextField(4);
         JButton generateButton = new JButton("Generar Cárcel");
         inputPanel.add(label);
         inputPanel.add(heightField);
         inputPanel.add(widthField);
         inputPanel.add(generateButton);
         add(inputPanel, BorderLayout.NORTH);

         //Inicializar cantidad de prisioneros
         this.prisioners = 0; 
         this.missingPrisioners = 0;

         // Panel para el laberinto
         jailPanel = new JPanel();
         add(jailPanel, BorderLayout.CENTER);
 
         // Botón para encontrar los prisioneros
         JButton findButton = new JButton("Encontrar Prisioneros");
         add(findButton, BorderLayout.SOUTH);
 
         // Acción al presionar "Generar"
         generateButton.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 n = Integer.parseInt(heightField.getText());
                 m = Integer.parseInt(widthField.getText());
                 jail = new int[n][m];
                 jailButtons = new JButton[n][m];
                 jailPanel.removeAll();
                 jailPanel.setLayout(new GridLayout(n, m));
                 for (int i = 0; i < n; i++) {
                     for (int j = 0; j < m; j++) {
                         jailButtons[i][j] = new JButton();
                         jailButtons[i][j].setBackground(Color.WHITE);
                         jailButtons[i][j].addActionListener(new ButtonClickListener(i, j));
                         jailPanel.add(jailButtons[i][j]);
                     }
                 }
                 jailPanel.revalidate();
                 jailPanel.repaint();
             }
         });
 
         // Acción al presionar "Encontrar Solución"
         findButton.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 int[][] route = new int[n][m];
                 if (findPrisoners(jail, route, 0, 0)) {
                     for (int i = 0; i < n; i++) {
                         for (int j = 0; j < m; j++) {
                             if(jailButtons[i][j].getBackground() == Color.BLUE){
                                 jailButtons[i][j].setBackground(Color.WHITE);
                             }
                             if (route[i][j] == 1) {
                                 jailButtons[i][j].setBackground(Color.BLUE);
                             }
                         }
                     }
                     JOptionPane.showMessageDialog(null, "Se encontraron " + prisioners + " prisioneros.");
                     if(missingPrisioners == 0 && prisioners == 26) {
                         JOptionPane.showMessageDialog(null, "No se escaparon prisioneros");
                     }else{
                         JOptionPane.showMessageDialog(null, "Se escaparon " + missingPrisioners + " prisioneros.");
                     }
                 } else {
                     JOptionPane.showMessageDialog(null, "No se encontró un camino.");
                 }
             }
         });
     }
 
     private class ButtonClickListener implements ActionListener {
         private int x, y;
 
         public ButtonClickListener(int x, int y) {
             this.x = x;
             this.y = y;
         }
 
         @Override
         public void actionPerformed(ActionEvent e) {
             JButton button = jailButtons[x][y];
             if (jail[x][y] == 0) {
                 jail[x][y] = 1;
                 button.setText("P");
                 button.setBackground(Color.LIGHT_GRAY);
             } else {
                 if(jail[x][y] == 1){
                     jail[x][y] = 2;
                     button.setText("X");
                 } else{
                     jail[x][y] = 0;
                    button.setText("");
                    button.setBackground(Color.WHITE);
                 }
             }
         }
     }
 
     public boolean findPrisoners(int[][] jail, int[][] route, int x, int y) {
         if (x == n - 2 && y == m - 2 && jail[x][y] == 0 ) { //&& AislesClear(jail)
             route[x][y] = 1;
             return true;
         }
 
         if (isAisle(jail, x, y, route)) {
             route[x][y] = 1;

             checkPrisonerAllDirections(jail, x, y, route);
             
                 //Moverse hacia la derecha
                 if (findPrisoners(jail, route, x, y + 1))
                     return true;

                 // Moverse hacia abajo
                 if (findPrisoners(jail, route, x + 1, y))
                     return true;

                 //Moverse hacia arriba
                 if (findPrisoners(jail, route, x - 1, y)) 
                     return true;

                 // Moverse hacia la izquierda
                 if (findPrisoners(jail, route, x, y - 1))
                     return true;

             route[x][y] = 0;
             return false;
            }
         
         return false;
     }

     //Método que verifica si aún quedan casillas por donde no se ha pasado.
    private boolean AislesClear(int[][] jail) {
        boolean clear = true;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if(jail[i][j] == 0){
                    clear = false;
                    break;
                }
            }
        }
        return clear;
    }

    //Método que revisa si hay prisioneros a su alrededor
    private void checkPrisonerAllDirections(int[][] jail, int x, int y, int[][] route) {
         //Revisar hacia la derecha
         checkPrisoner(jail, x, y + 1);
         //Revisar hacia la izquierda
         checkPrisoner(jail, x, y - 1);
         //Revisar hacia arriba
         checkPrisoner(jail, x + 1, y);
         //Revisar hacia abajo
         checkPrisoner(jail, x - 1, y);
         
         //Revisar esquina superior derecha
         checkPrisoner(jail, x + 1, y + 1);
         //Revisar esquina inferior derecha
         checkPrisoner(jail, x - 1, y + 1);
         //Revisar esquina inferior izquierda
         checkPrisoner(jail, x - 1, y - 1);
         //Revisar esquina superior derecha
         checkPrisoner(jail, x + 1, y - 1);  
    }

    //Método que cuenta los prisioneros que hayan en la casilla especificada 
    private void checkPrisoner(int[][] jail, int x, int y) {
         if(isJail(x, y)){
             if(jail[x][y] == 1 ){
                 prisioners++;
                 jail[x][y] = 3; //El estado de la casilla número 3 se refiere a un estado donde la casilla ya ha sido contada
             }else{
                 if(jail[x][y] == 2){
                     missingPrisioners++;
                     jail[x][y] = 3;
                 }
             }
        }
    }

    //Verificar si la casilla es un pasillo para seguir la ruta
    public boolean isAisle(int[][] jail, int x, int y, int[][] route) {
         return (isJail(x, y) && jail[x][y] == 0 && route[x][y] != 1);
     }

    //Verificar si la casilla se sale de la matriz 
    public boolean isJail(int x, int y){
        return x >= 0 && x < n && y >= 0 && y < m;
    }
 
    public static void main(String[] args) {
         SwingUtilities.invokeLater(() -> {
             Prisioneros gui = new Prisioneros();
             gui.setVisible(true);
         });    }
}
