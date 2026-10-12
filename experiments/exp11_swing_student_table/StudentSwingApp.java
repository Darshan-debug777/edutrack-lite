package experiments.exp11_swing_student_table;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Experiment 11: Desktop Graphical User Interface (GUI) Application using
 * Java Swing: JFrame, JLabel, JTextField, JButton, JTable, DefaultTableModel,
 * and Event Listeners.
 */
public class StudentSwingApp extends JFrame {

    // Swing Components
    private JTextField txtId;
    private JTextField txtName;
    private JComboBox<String> cmbBranch;
    private JTextField txtMarks;
    private JButton btnAdd;
    private JButton btnClear;
    private JButton btnDelete;
    private JTable tblStudents;
    private DefaultTableModel tableModel;

    public StudentSwingApp() {
        // 1. Frame Configuration
        setTitle("EduTrack Lite — Experiment 11: Java Swing Desktop App");
        setSize(780, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setLayout(new BorderLayout(10, 10));

        // 2. Input Form Panel
        JPanel pnlForm = new JPanel(new GridLayout(5, 2, 8, 8));
        pnlForm.setBorder(BorderFactory.createTitledBorder("Enroll New Student Record"));

        pnlForm.add(new JLabel("Student ID:"));
        txtId = new JTextField();
        pnlForm.add(txtId);

        pnlForm.add(new JLabel("Full Name:"));
        txtName = new JTextField();
        pnlForm.add(txtName);

        pnlForm.add(new JLabel("Branch:"));
        String[] branches = { "Computer Science", "Information Technology", "Electronics", "Mechanical", "Civil" };
        cmbBranch = new JComboBox<>(branches);
        pnlForm.add(cmbBranch);

        pnlForm.add(new JLabel("Marks (0 - 100):"));
        txtMarks = new JTextField();
        pnlForm.add(txtMarks);

        // Buttons
        btnAdd = new JButton("Add Student to Table");
        btnAdd.setBackground(new Color(79, 70, 229));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);

        btnClear = new JButton("Clear Inputs");

        pnlForm.add(btnAdd);
        pnlForm.add(btnClear);

        add(pnlForm, BorderLayout.NORTH);

        // 3. JTable Configuration
        String[] columnNames = { "Student ID", "Full Name", "Branch", "Marks", "Status" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only table cells
            }
        };

        tblStudents = new JTable(tableModel);
        tblStudents.setRowHeight(24);
        tblStudents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tblStudents);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Student Database Records Table"));

        add(scrollPane, BorderLayout.CENTER);

        // Bottom Action Panel
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnDelete = new JButton("Delete Selected Student");
        btnDelete.setForeground(new Color(185, 28, 28));
        pnlBottom.add(btnDelete);
        add(pnlBottom, BorderLayout.SOUTH);

        // Seed Sample Records
        tableModel.addRow(new Object[]{ "STU101", "John Doe", "Computer Science", "88.5", "Distinction" });
        tableModel.addRow(new Object[]{ "STU102", "Jane Smith", "Information Technology", "92.0", "Distinction" });
        tableModel.addRow(new Object[]{ "STU103", "Robert Tables", "Computer Science", "74.0", "Passed" });

        // 4. Event Listeners
        // Add Student Button Listener
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = txtId.getText().trim();
                String name = txtName.getText().trim();
                String branch = (String) cmbBranch.getSelectedItem();
                String marksStr = txtMarks.getText().trim();

                // Validation
                if (id.isEmpty() || name.isEmpty() || marksStr.isEmpty()) {
                    JOptionPane.showMessageDialog(StudentSwingApp.this,
                            "Please fill in all required fields.", "Input Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double marks;
                try {
                    marks = Double.parseDouble(marksStr);
                    if (marks < 0 || marks > 100) {
                        JOptionPane.showMessageDialog(StudentSwingApp.this,
                                "Marks must be between 0 and 100.", "Invalid Range", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(StudentSwingApp.this,
                            "Marks must be a valid numeric value.", "Invalid Number", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String status = marks >= 75 ? "Distinction" : marks >= 50 ? "Passed" : "Needs Attention";

                // Add row to JTable DefaultTableModel
                tableModel.addRow(new Object[]{ id, name, branch, String.valueOf(marks), status });

                JOptionPane.showMessageDialog(StudentSwingApp.this,
                        "Student record " + id + " successfully added to table!", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Clear input fields
                clearInputs();
            }
        });

        // Clear Button Listener
        btnClear.addActionListener(e -> clearInputs());

        // Delete Button Listener
        btnDelete.addActionListener(e -> {
            int selectedRow = tblStudents.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(StudentSwingApp.this,
                        "Please select a student row from the table to delete.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String delId = (String) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(StudentSwingApp.this,
                    "Delete student " + delId + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                tableModel.removeRow(selectedRow);
            }
        });
    }

    private void clearInputs() {
        txtId.setText("");
        txtName.setText("");
        txtMarks.setText("");
        txtId.requestFocus();
    }

    public static void main(String[] args) {
        // Run on Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            StudentSwingApp app = new StudentSwingApp();
            app.setVisible(true);
        });
    }
}
