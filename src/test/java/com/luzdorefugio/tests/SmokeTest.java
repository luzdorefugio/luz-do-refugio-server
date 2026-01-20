package com.luzdorefugio.tests;

import org.junit.jupiter.api.Test;
import com.luzdorefugio.tests.base.BaseTest;
import com.luzdorefugio.tests.pages.AdminLoginPage;
import com.luzdorefugio.tests.pages.StorePage;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SmokeTest extends BaseTest {

    @Test
    void testStoreLoadsProducts() {
        StorePage store = new StorePage(driver);
        store.navigateTo(BASE_URL);
        assertTrue(Objects.requireNonNull(driver.getTitle()).contains("Luz do Refúgio"));
        assertTrue(store.getProductCount() > 0, "A loja devia mostrar produtos!");
    }

    @Test
    void testAdminLoginFlow() throws InterruptedException {
        driver.get(BASE_URL + "/admin/");
        AdminLoginPage loginPage = new AdminLoginPage(driver);
        loginPage.login("admin@luzdorefugio.pt", "luz123");
        Thread.sleep(2000); 
        String currentUrl = driver.getCurrentUrl();
        assert currentUrl != null;
        assertTrue(currentUrl.contains("/dashboard"), "Devia ter entrado no dashboard");
    }
}