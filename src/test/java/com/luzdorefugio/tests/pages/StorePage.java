package com.luzdorefugio.tests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class StorePage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Seletores (Melhor prática: usar IDs ou data-testids no HTML, mas vamos usar classes por agora)
    private By productCards = By.cssSelector("app-product-card");
    private By detailsButtons = By.tagName("a"); // Os botões "VER PACK"

    public StorePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void navigateTo(String baseUrl) {
        driver.get(baseUrl + "/loja");
        wait.until(ExpectedConditions.presenceOfElementLocated(productCards));
    }

    public int getProductCount() {
        return driver.findElements(productCards).size();
    }

    public void clickFirstProduct() {
        List<WebElement> buttons = driver.findElements(detailsButtons);
        // Filtra para encontrar o primeiro botão visível que diz "VER PACK"
        // (Lógica simplificada para exemplo)
        if(!buttons.isEmpty()) {
            buttons.getFirst().click();
        }
    }
}