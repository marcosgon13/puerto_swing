//Marcos Gonzalez Palazon
package PaqB11;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
public class Puerto {
    private Hub hub;
    private FormularioContenedores formulario;
    private JTable tablaContenedores;
    private JPanel panelCuadricula = new JPanel(new GridLayout(10, 12));
    private int pesoTotal;

    public Puerto() {
        // Crear el hub con una capacidad de 10 filas y 12 columnas
        Contenedor[][] contenedores = new Contenedor[10][12];
        int[] ocupacion = new int[3]; // 0: prioridad 1, 1: prioridad 2, 2: prioridad 3
        hub = new Hub(contenedores, ocupacion);

        // Crear el formulario de contenedores
        formulario = new FormularioContenedores();

        // Agregar el logo de UCLM
        ImageIcon logo = new ImageIcon("UCLM-Logo (1) (1).png");
        JLabel logoLabel = new JLabel(logo);
        logoLabel.setPreferredSize(new Dimension(150, 128));
        formulario.add(logoLabel, BorderLayout.NORTH);

        // Agregar el estado del hub
        JTable hubTable = new JTable(10, 12);
        hubTable.setEnabled(false);
        JScrollPane hubScrollPane = new JScrollPane(hubTable);
        formulario.add(hubScrollPane, BorderLayout.EAST);

        // Obtener la tabla de contenedores del formulario de gestión de contenedores
        FormularioContenedores gestionContenedores = new FormularioContenedores();
        tablaContenedores = gestionContenedores.getTable1();

        // Agregar la tabla de contenedores al formulario de Puerto
        JScrollPane tableScrollPane = new JScrollPane(tablaContenedores);
        formulario.add(tableScrollPane, BorderLayout.WEST);

        // Agregar un panel para las operaciones
        JPanel operacionesPanel = new JPanel();
        formulario.add(operacionesPanel, BorderLayout.CENTER);

        // Agregar un JTextArea para introducir el número de columna
        JTextArea columnaTextArea = new JTextArea(1, 10);
        operacionesPanel.add(columnaTextArea);

        // Agregar un JTextArea para mostrar los datos del contenedor
        JTextArea datosTextArea = new JTextArea(10, 20);
        datosTextArea.setEditable(false);
        JScrollPane datosScrollPane = new JScrollPane(datosTextArea);
        operacionesPanel.add(datosScrollPane);

        // Agregar un JTextArea para introducir el ID del contenedor
        JTextArea idTextArea = new JTextArea(1, 10);
        operacionesPanel.add(idTextArea);

        // Agregar un JLabel y un JTextArea para introducir el país de procedencia
        JLabel paisLabel = new JLabel("País de procedencia:");
        JTextArea paisTextArea = new JTextArea(1, 10);
        operacionesPanel.add(paisLabel);
        operacionesPanel.add(paisTextArea);

        // Agregar los botones de apilar y desapilar
        JButton apilarButton = new JButton("Apilar");
        JButton desapilarButton = new JButton("Desapilar");
        operacionesPanel.add(apilarButton);
        operacionesPanel.add(desapilarButton);

        // Crear boton con el texto "peso total del hub
        JButton pesoTotalButton = new JButton("Peso total del hub");
        operacionesPanel.add(pesoTotalButton);

        // Crear la cuadrícula de 10x12
        for (int i = 1; i <= 120; i++) {
            JPanel panelContenedor = new JPanel();
            panelContenedor.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            panelCuadricula.add(panelContenedor);
        }

        // Configurar el botón de apilar contenedor en el formulario
        apilarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtener los datos del formulario
                int id = Integer.parseInt(idTextArea.getText());
                int peso = Integer.parseInt(formulario.getTextField2().getText());
                String paisProcedencia = (String) formulario.getComboBox1().getSelectedItem();
                boolean inspeccionado = formulario.getCheckBox1().isSelected();
                int prioridad = 0;
                if (formulario.getA1B().isSelected()) {
                    prioridad = 1;
                } else if (formulario.getA2B().isSelected()) {
                    prioridad = 2;
                } else if (formulario.getA3B().isSelected()) {
                    prioridad = 3;
                }
                String descripcion = formulario.getTextArea1().getText();
                String empresaEnvio = formulario.getTextField4().getText();
                String empresaRecepcion = formulario.getTextField5().getText();
                // Crear el contenedor con los datos obtenidos
                Contenedor contenedor = new Contenedor(id, peso, paisProcedencia, inspeccionado, prioridad, descripcion, empresaEnvio, empresaRecepcion);
                // Obtener la columna donde se quiere apilar el contenedor
                int columna = Integer.parseInt(columnaTextArea.getText());
                // Apilar el contenedor en el hub
                boolean apilado = hub.apilarContenedor(contenedor, columna);
                // Actualizar la tabla de estado del hub
                for (int fila = 0; fila < 10; fila++) {
                    hubTable.setValueAt(contenedores[fila][columna] != null, fila, columna);
                }
                // Actualizar la tabla de contenedores en el formulario
                if (apilado) {
                    Object[] row = {contenedor.getId(), contenedor.getPeso(), contenedor.getPaisProcedencia(),
                            contenedor.isInspeccionado(), contenedor.getPrioridad(), contenedor.getDescripcion(),
                            contenedor.getEmpresaEnvio(), contenedor.getEmpresaRecepcion()};
                    DefaultTableModel model = (DefaultTableModel) tablaContenedores.getModel();
                    model.addRow(row);
                }
            }
        });

        // Configurar el botón de desapilar contenedor en el formulario
        desapilarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtener la columna donde se quiere desapilar el contenedor
                int columna = Integer.parseInt(columnaTextArea.getText());
                // Desapilar el contenedor del hub
                Contenedor contenedor = hub.desapilarContenedor(columna);
                // Actualizar la tabla de estado del hub
                for (int fila = 9; fila >= 0; fila--) {
                    if (contenedores[fila][columna] != null) {
                        hubTable.setValueAt(false, fila, columna);
                        break;
                    }
                }
                // Actualizar la tabla de contenedores en el formulario
                if (contenedor != null) {
                    DefaultTableModel model = (DefaultTableModel) tablaContenedores.getModel();
                    for (int i = 0; i < model.getRowCount(); i++) {
                        if (model.getValueAt(i, 0).equals(contenedor.getId())) {
                            model.removeRow(i);
                            break;
                        }
                    }
                }
                // Mostrar los datos del contenedor desapilado
                if (contenedor != null) {
                    datosTextArea.setText("ID: " + contenedor.getId() + "\n" +
                            "Peso: " + contenedor.getPeso() + "\n" +
                            "País de procedencia: " + contenedor.getPaisProcedencia() + "\n" +
                            "Inspeccionado: " + contenedor.isInspeccionado() + "\n" +
                            "Prioridad: " + contenedor.getPrioridad() + "\n" +
                            "Descripción: " + contenedor.getDescripcion() + "\n" +
                            "Empresa de envío: " + contenedor.getEmpresaEnvio() + "\n" +
                            "Empresa de recepción: " + contenedor.getEmpresaRecepcion());
                } else {
                    datosTextArea.setText("");
                }
            }
        });

        pesoTotalButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Creare una matriz que vaya recorriendo el hub y sumando los pesos de cada contenedor
                    int peso = 0;
                    for (int fila = 0; fila < 10; fila++) {
                        for (int columna = 0; columna < 12; columna++) {
                            if (contenedores[fila][columna] != null){
                                int pesoPorContenedor = hub.getContenedor(fila,columna);
                                peso = peso + pesoPorContenedor;
                            }
                        }
                    }
                }
        });

        // Configurar el JTextArea de la columna para que solo acepte números
        columnaTextArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                    e.consume();
                }
            }
        });

        // Configurar el JTextArea del ID del contenedor para que solo acepte números
        idTextArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                    e.consume();
                }
            }
        });

        // Mostrar el formulario
        formulario.pack();
        formulario.setVisible(true);
    }
    public static void main(String[] args) {
        Puerto puerto = new Puerto();
    }
}
/*para el estado en tiempo real

private JPanel panelCuadricula = new JPanel(new GridLayout(10, 12));

//Crear la cuadrícula de 10x12
        for (int i = 1; i <= 120; i++) {
            JPanel panelContenedor = new JPanel();
            panelContenedor.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            panelCuadricula.add(panelContenedor);
        }

//Boton agregar
        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.addActionListener(event -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                int tipo = cmbTipo.getSelectedIndex() + 1;
                int capacidad = Integer.parseInt(txtCapacidad.getText());
                Contenedor contenedor = new Contenedor(id, tipo, capacidad);
                contenedores.add(contenedor);
                actualizarCuadricula();
                txtId.setText("");
                cmbTipo.setSelectedIndex(-1);
                txtCapacidad.setText("");
                lblEstado.setText("Contenedor agregado");
            } catch (NumberFormatException e) {
                lblEstado.setText("Error: los campos ID y Capacidad deben ser números enteros");
            }
        });


//Crear el panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.add(panelCuadricula, BorderLayout.CENTER);

private void actualizarCuadricula() {
        for (int i = 0; i < panelCuadricula.getComponentCount(); i++) {
            JPanel panelContenedor = (JPanel) panelCuadricula.getComponent(i);
            panelContenedor.removeAll();
            panelContenedor.revalidate();
            panelContenedor.repaint();
        }
        for (Contenedor c : contenedores) {
            int fila = (c.getId() - 1) / 12;
            int columna = (c.getId() - 1) % 12;
            JPanel panelContenedor = (JPanel) panelCuadricula.getComponent(fila * 12 + columna);
            JLabel lblContenedor = new JLabel(Integer.toString(c.getId()));
            lblContenedor.setHorizontalAlignment(SwingConstants.CENTER);
            panelContenedor.add(lblContenedor);
        }
    }

 */