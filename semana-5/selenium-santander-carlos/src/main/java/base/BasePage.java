package base;

import java.time.Duration;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final Actions actions;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        this.actions = new Actions(driver);
    }

    protected WebElement find(By locator) {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );
    }

    protected void click(By locator) {
        wait.until(driver -> {
            try {
                WebElement element = driver.findElement(locator);

                if (!element.isDisplayed() || !element.isEnabled()) {
                    return false;
                }

                element.click();
                return true;

            } catch (StaleElementReferenceException e) {
                return false;
            }
        });
    }

    protected void write(By locator, String value) {
        WebElement element = find(locator);
        element.clear();
        element.sendKeys(value);
    }

    protected String text(By locator) {
        return find(locator).getText();
    }

    protected void selectByText(By locator, String value) {
        new Select(find(locator)).selectByVisibleText(value);
    }

    protected boolean isSelected(By locator) {
        return find(locator).isSelected();
    }

    protected boolean exists(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    public void waitForUrl(String expectedUrl) {
        wait.until(ExpectedConditions.urlToBe(expectedUrl));
    }
}