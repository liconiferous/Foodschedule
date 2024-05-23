package foodblocko;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ScheduleWindowTest {

    private ScheduleWindow scheduleWindow;
    private DatabaseConnection mockDB;

    @BeforeEach
    public void setUp() {
        mockDB = mock(DatabaseConnection.class);
        scheduleWindow = new ScheduleWindow("testUser");
        scheduleWindow.DB = mockDB;
    }

    @Test
    public void testGetWeekStartEnd() {
        int year = 2024;
        ArrayList<String> weekList = scheduleWindow.getWeekStartEnd(year);
        assertEquals(52, weekList.size()); // Assuming 2024 has 52 weeks
    }

    @Test
    public void testUpdateTableCell() {
        scheduleWindow.updateTableCell("Lunch", "Salad", 1, 1);
        assertEquals("Salad (Lunch)", scheduleWindow.model.getValueAt(1, 1).toString());
    }

    @Test
    public void testUpdateTable() {
        String dateInfo = "2024-05-20 to 2024-05-26";
        scheduleWindow.dateInfo = dateInfo;

        List<ScheduleInfo> mockScheduleInfoList = new ArrayList<>();
        ScheduleInfo scheduleInfo = new ScheduleInfo();
        scheduleInfo.setMealtype("Lunch");
        scheduleInfo.setMealname("Salad");
        scheduleInfo.setRow(1); // Assuming the row index for testing
        scheduleInfo.setCol(1); // Assuming the column index for testing
        mockScheduleInfoList.add(scheduleInfo);

        when(mockDB.loadMealSchedule(dateInfo, "testUser")).thenReturn(mockScheduleInfoList);

        scheduleWindow.updateTable();

        verify(mockDB).loadMealSchedule(dateInfo, "testUser");
        assertEquals("Salad (Lunch)", scheduleWindow.model.getValueAt(1, 1).toString());
    }

    @Test
    public void testMealCellRenderer() {
        ScheduleWindow.MealCellRenderer renderer = scheduleWindow.new MealCellRenderer();
        JLabel label = (JLabel) renderer.getTableCellRendererComponent(
                scheduleWindow.table, "Lunch (Lunch)", false, false, 0, 1);

        // Assuming Lunch (Lunch) should be rendered with GREEN background
        assertEquals(Color.GREEN, label.getBackground());
    }

    @Test
    public void testItemStateChanged() {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.addItem("2024-05-20 to 2024-05-26");
        comboBox.addItemListener(scheduleWindow.timeComboBox.getItemListeners()[0]);

        comboBox.setSelectedItem("2024-05-20 to 2024-05-26");

        assertEquals("2024-05-20 to 2024-05-26", scheduleWindow.dateInfo);
    }

    // Additional tests can be added here to cover more scenarios
}
