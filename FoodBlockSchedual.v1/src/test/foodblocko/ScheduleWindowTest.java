package foodblocko;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ScheduleWindowTest {

    private ScheduleWindow scheduleWindow;
    private DatabaseConnection mockDB;

    @Before
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
        assertEquals("Salad (Lunch)", scheduleWindow.model.getValueAt(1, 1));
    }

    @Test
    public void testUpdateTable() {
        String dateInfo = "2024-05-20 至 2024-05-26";
        scheduleWindow.dateInfo = dateInfo;

        List<ScheduleInfo> mockScheduleInfoList = new ArrayList<>();
        mockScheduleInfoList.add(new ScheduleInfo());

        when(mockDB.loadMealSchedule(dateInfo, "testUser")).thenReturn(mockScheduleInfoList);

        scheduleWindow.updateTable();

        verify(mockDB).loadMealSchedule(dateInfo, "testUser");
        assertEquals("Salad (Lunch)", scheduleWindow.model.getValueAt(1, 1));
    }






    @Test
    public void testMealCellRenderer() {
        ScheduleWindow.MealCellRenderer renderer = scheduleWindow.new MealCellRenderer();
        JLabel label = (JLabel) renderer.getTableCellRendererComponent(
                scheduleWindow.table, "Lunch (Lunch)", false, false, 0, 1);

        assertEquals(Color.GREEN, label.getBackground());
    }

    @Test
    public void testItemStateChanged() {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.addItem("2024-05-20 至 2024-05-26");
        comboBox.addItemListener(scheduleWindow.timeComboBox.getItemListeners()[0]);

        comboBox.setSelectedItem("2024-05-20 至 2024-05-26");

        assertEquals("2024-05-20 至 2024-05-26", scheduleWindow.dateInfo);
    }
}
