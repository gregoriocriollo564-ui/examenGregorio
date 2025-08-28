/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.frame;

import com.mycompany.model.User;
import com.mycompany.service.UserService;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Gregorio
 */
public class UserFrame extends JFrame{
    
    private JTextField searchField;
    private JTable resultTable;
    private JTextArea detailArea;
    private DefaultTableModel tableModel;
    
    public UserFrame() {
        setTitle("Buscador de Usuarios");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        initUI();
    }
    
    private void initUI() {
        JPanel topPanel = new JPanel();
        searchField = new JTextField(25);
        JButton searchButton = new JButton("Buscar");

        topPanel.add(new JLabel("Nombre:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);

        tableModel = new DefaultTableModel(new Object[]{"Nombre", "Email","Empresa"}, 0);
        resultTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(resultTable);

        detailArea = new JTextArea(8, 50);
        detailArea.setEditable(false);
        JScrollPane detailScroll = new JScrollPane(detailArea);

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
        add(detailScroll, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> searchUsers());

        resultTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int row = resultTable.getSelectedRow();
                    String name = (String) tableModel.getValueAt(row, 0);
                    showUserDetails(name);
                }
            }
        });
        
        List<User> cachedUsers = UserService.loadFromLocalFile();
        if (!cachedUsers.isEmpty()) {
            for (User user : cachedUsers) {
                tableModel.addRow(new Object[]{user.getName(), user.getEmail()});
            }
        }
    }
    
    private void searchUsers() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa un nombre para buscar.");
            return;
        }

        List<User> users = UserService.fetchUsersByName(searchTerm);
        tableModel.setRowCount(0);
        if (users.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontraron usuarios.");
            detailArea.setText("");
            return;
        }

        for (User user : users) {
            tableModel.addRow(new Object[]{user.getName(), user.getEmail(), user.getCompany().getName()});
        }

        detailArea.setText(""); // limpiar detalles al mostrar nueva búsqueda
    }
    
    private void showUserDetails(String name) {
        List<User> users = UserService.fetchUsersByName(name);
        if (!users.isEmpty()) {
            User user = users.get(0);
            StringBuilder sb = new StringBuilder();
            sb.append("Nombre: ").append(user.getName()).append("\n");
            sb.append("Email: ").append(user.getEmail()).append("\n");
            sb.append("Teléfono: ").append(user.getPhone()).append("\n");
            sb.append("Ciudad: ").append(user.getAddress().getCity()).append("\n");
            sb.append("Empresa: ").append(user.getCompany().getName()).append("\n");
            detailArea.setText(sb.toString());
        }
    }

}
