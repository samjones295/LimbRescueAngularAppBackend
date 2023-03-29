package com.limbrescue.limbrescueangularappbackend;

import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;
import org.springframework.boot.test.context.SpringBootTest;
import com.limbrescue.limbrescueangularappbackend.model.Reading;
import com.limbrescue.limbrescueangularappbackend.controller.ReadingDAO;

@SpringBootTest
class LimbRescueAngularAppBackendApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void TestReadingNa() {
        ReadingDAO test = new ReadingDAO();
        Reading r = test.getReading(-1);
        assertNull(r);
    }

}
