package foodblocko;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ScheduleWindow extends JFrame {
    final JTable table;
    final DefaultTableModel model;
    final Map<String, String> recipes = new HashMap<>(); // Store recipes
    public AbstractButton timeComboBox;
    DatabaseConnection DB = new DatabaseConnection();
    private String userName = "";
    String dateInfo = "";

    public ScheduleWindow(String name) {
        this.userName = name;
        setTitle("Meal Schedule");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        String[] columns = {"Time", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        String[][] data = new String[17][8];
        for (int i = 0; i < data.length; i++) {
            data[i][0] = String.format("%02d:00", i + 6); // Fill first column with time slots
        }

        model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));

        MealCellRenderer mealCellRenderer = new MealCellRenderer();
        for (int i = 1; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(mealCellRenderer);
        }

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    JTable target = (JTable) e.getSource();
                    int row = target.getSelectedRow();
                    int column = target.getSelectedColumn();
                    if (column != 0) {
                        String currentValue = (String) model.getValueAt(row, column);
                        if (currentValue == null || currentValue.isEmpty()) {
                            openMealInputDialog(row, column);
                        } else {
                            editMealInputDialog(currentValue, row, column);
                        }
                    }
                }
            }
        });

        JLabel nameLabel = new JLabel("Username:");
        JLabel nameItem = new JLabel(this.userName);
        JLabel timeLabel = new JLabel("Time:");
        JComboBox<String> timeComboBox = new JComboBox<>(getWeekStartEnd(2024).stream().toArray(String[]::new));
        JPanel headerPanel = new JPanel(new GridLayout(0, 4, 10, 10));
        timeComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    dateInfo = e.getItem().toString();
                    updateTable();
                }
            }
        });
        headerPanel.add(nameLabel);
        headerPanel.add(nameItem);
        headerPanel.add(timeLabel);
        headerPanel.add(timeComboBox);
        JScrollPane scrollPane = new JScrollPane(table);
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        timeComboBox.setSelectedItem(getWeekDate());
        setVisible(true);
    }

    ArrayList<String> getWeekStartEnd(int year) {
        ArrayList<String> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar yearCalendar = new GregorianCalendar();
        yearCalendar.set(Calendar.YEAR, year);
        int maxWeek = yearCalendar.getMaximum(Calendar.WEEK_OF_YEAR);
        for (int weekNumber = 1; weekNumber <= maxWeek; weekNumber++) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.WEEK_OF_YEAR, weekNumber);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date monday = calendar.getTime(); // Start time of Monday
            calendar.add(Calendar.DAY_OF_WEEK, 6);
            Date sunday = calendar.getTime(); // End time of sunday
            list.add(String.format("%s to %s", sdf.format(monday), sdf.format(sunday)));
        }
        return list;
    }

    void openMealInputDialog(int row, int column) {
        JComboBox<String> mealTypeComboBox = new JComboBox<>(new String[]{"Breakfast", "Lunch", "Dinner", "Snack"});
        JTextField mealNameTextField = new JTextField();
        JTextField recipeTextField = new JTextField();
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Select meal type:"));
        panel.add(mealTypeComboBox);
        panel.add(new JLabel("Enter meal name: "));
        panel.add(mealNameTextField);
        panel.add(new JLabel("Recipe: "));
        panel.add(recipeTextField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Meal", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String mealType = (String) mealTypeComboBox.getSelectedItem();
            String mealName = mealNameTextField.getText();
            String recipe = recipeTextField.getText();
            updateTableCell(mealType, mealName, row, column);
            recipes.put(mealName, recipe);
            DB.saveOrUpdateMeal(dateInfo, userName, row, column, mealType, mealName, recipe);
        }
    }

    void editMealInputDialog(String currentValue, int row, int column) {
        String[] parts = currentValue.split(" \\(");
        String mealName = parts[0];
        String mealType = parts.length > 1 ? parts[1].replace(")", "") : "";

        JComboBox<String> mealTypeComboBox = new JComboBox<>(new String[]{"Breakfast", "Lunch", "Dinner", "Snack"});
        mealTypeComboBox.setSelectedItem(mealType);
        JTextField mealNameTextField = new JTextField(mealName);
        String currentRecipe = recipes.getOrDefault(mealName, "");
        JTextField recipeTextField = new JTextField(currentRecipe);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Select meal type:"));
        panel.add(mealTypeComboBox);
        panel.add(new JLabel("Enter meal name:"));
        panel.add(mealNameTextField);
        panel.add(new JLabel("Recipe:"));
        panel.add(recipeTextField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Meal", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String newMealType = (String) mealTypeComboBox.getSelectedItem();
            String newMealName = mealNameTextField.getText();
            String newRecipe = recipeTextField.getText(); // Save the recipe as needed
            updateTableCell(newMealType, newMealName, row, column);
            recipes.put(newMealName, newRecipe);
            DB.saveOrUpdateMeal(dateInfo, userName, row, column, newMealType, newMealName, newRecipe);
        }
    }

    void updateTableCell(String mealType, String mealName, int row, int column) {
        if (mealName != null && mealType != null && mealName.length() > 0 && mealType.length() > 0) {
            model.setValueAt(mealName + " (" + mealType + ")", row, column);
        } else {
            model.setValueAt("", row, column);
        }
        model.fireTableCellUpdated(row, column);
    }

    void updateTable() {
        for (int r = 0; r < table.getRowCount(); r++) {
            for (int i = 1; i < table.getColumnCount(); i++) {
                updateTableCell("", "", r, i);
            }
        }
        List<ScheduleInfo> list = DB.loadMealSchedule(this.dateInfo, this.userName);
        for (ScheduleInfo scheduleInfo : list) {
            updateTableCell(scheduleInfo.getMealtype(), scheduleInfo.getMealname(), scheduleInfo.getRow(), scheduleInfo.getCol());
            recipes.put(scheduleInfo.getMealname(), scheduleInfo.getRecipe());
        }
    }

    private Color getColorForMealType(String mealName) {
        if (mealName.contains("Breakfast")) return Color.YELLOW;
        else if (mealName.contains("Lunch")) return Color.GREEN;
        else if (mealName.contains("Dinner")) return Color.BLUE;
        else if (mealName.contains("Snack")) return Color.ORANGE;
        else return Color.WHITE;
    }

    public static String getWeekDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();

        cal.setFirstDayOfWeek(Calendar.MONDAY);

        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayWeek == 1) {
            dayWeek = 8;
        }
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - dayWeek);
        Date mondayDate = cal.getTime();
        String weekBegin = sdf.format(mondayDate);
        cal.add(Calendar.DATE, 4 + cal.getFirstDayOfWeek());
        Date sundayDate = cal.getTime();
        String weekEnd = sdf.format(sundayDate);
        return String.format("%s to %s", weekBegin, weekEnd);
    }


    class MealCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            String val = (value != null) ? value.toString() : "";
            component.setBackground(getColorForMealType(val));
            if (!val.isEmpty()) {
                setToolTipText(val);  // Set the tooltip text
            } else {
                setToolTipText(null);  // Clear tooltip text if the cell is empty
            }

            return component;  // Return the decorated component
        }
    }
}