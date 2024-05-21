package foodblocko;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleInfoTest {

    private ScheduleInfo scheduleInfo;

    @BeforeEach
    void setUp() {
        scheduleInfo = new ScheduleInfo();
    }

    @Test
    void testGetSetId() {
        scheduleInfo.setId(1);
        assertEquals(1, scheduleInfo.getId());
    }

    @Test
    void testGetSetRow() {
        scheduleInfo.setRow(2);
        assertEquals(2, scheduleInfo.getRow());
    }

    @Test
    void testGetSetCol() {
        scheduleInfo.setCol(3);
        assertEquals(3, scheduleInfo.getCol());
    }

    @Test
    void testGetSetUsername() {
        String username = "testUser";
        scheduleInfo.setUsername(username);
        assertEquals(username, scheduleInfo.getUsername());
    }

    @Test
    void testGetSetDateinfo() {
        String dateinfo = "2023-01-01";
        scheduleInfo.setDateinfo(dateinfo);
        assertEquals(dateinfo, scheduleInfo.getDateinfo());
    }

    @Test
    void testGetSetMealtype() {
        String mealtype = "Lunch";
        scheduleInfo.setMealtype(mealtype);
        assertEquals(mealtype, scheduleInfo.getMealtype());
    }

    @Test
    void testGetSetMealname() {
        String mealname = "Pasta";
        scheduleInfo.setMealname(mealname);
        assertEquals(mealname, scheduleInfo.getMealname());
    }

    @Test
    void testGetSetRecipe() {
        String recipe = "Boil water, cook pasta for 10 minutes.";
        scheduleInfo.setRecipe(recipe);
        assertEquals(recipe, scheduleInfo.getRecipe());
    }
}
