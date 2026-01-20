package com.luzdorefugio.tests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AdminLoginPage {
    private WebDriver driver;

    private By emailField = By.cssSelector("input[type='email']");
    private By passField = By.cssSelector("input[type='password']");
    private By loginButton = By.tagName("button");

    public AdminLoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public void login(String email, String password) {
        driver.findElement(emailField).sendKeys(email);
        driver.findElement(passField).sendKeys(password);
        driver.findElement(loginButton).click();
    }
}