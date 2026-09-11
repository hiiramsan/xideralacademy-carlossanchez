package pages;

import base.BasePage;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;


public class HomePage extends BasePage {

    private static final By PERSONAS = By.id("firstLevel-mainItem-0-menu-button");

    private static final By EMPRESAS = By.id("firstLevel-mainItem-1-menu-button");

    private static final By PYMES = By.id("firstLevel-mainItem-2-menu-button");

    private static final By BANCA_PRIVADA = By.id("firstLevel-mainItem-3-menu-button");

    private static final By ACERCA_DEL_BANCO = By.id("firstLevel-mainItem-4-menu-button");

    private static final By PERSONAS_MENU = By.cssSelector("a[href*='/personas/']");

    private static final By EMPRESAS_MENU = By.cssSelector("a[href*='/bei/']");

    private static final By PYMES_MENU = By.cssSelector("a[href*='/pyme']");

    private static final By BANCA_PRIVADA_LINK = By.cssSelector("a[href*='/bp/home']");

    private static final By ACERCA_MENU = By.cssSelector("a[href*='/fundacion-santander']");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public PersonasPage openPersonasNavbar() {
        click(PERSONAS);

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(PERSONAS_MENU)
        );

        return new PersonasPage(driver);
    }

    public EmpresasPage openEmpresasNavbar() {
        click(EMPRESAS);

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(EMPRESAS_MENU)
        );

        return new EmpresasPage(driver);
    }

    public PyMesPage openPyMesNavbar() {
        clickLinkOrButton(PYMES_MENU, PYMES);

        wait.until(
                ExpectedConditions.or(
                        ExpectedConditions.visibilityOfElementLocated(PYMES_MENU),
                        ExpectedConditions.urlContains("/pyme")
                )
        );

        return new PyMesPage(driver);
    }

    public BancaPrivadaPage openBancaPrivadaNavbar() {
        clickLinkOrButton(BANCA_PRIVADA_LINK, BANCA_PRIVADA);

        wait.until(
                ExpectedConditions.urlContains("/bp/home")
        );

        return new BancaPrivadaPage(driver);
    }

    public AcercaDelBancoPage openAcercaDelBancoNavbar() {
        click(ACERCA_DEL_BANCO);

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(ACERCA_MENU)
        );

        return new AcercaDelBancoPage(driver);
    }

    public void waitForHomePage(String homeUrl) {
        wait.until(ExpectedConditions.urlToBe(homeUrl));
    }

    private void clickLinkOrButton(By link, By button) {
        List<WebElement> links = driver.findElements(link);
        boolean anyVisible = links.stream().anyMatch(WebElement::isDisplayed);
        if (anyVisible) {
            click(link);
        } else {
            click(button);
        }
    }
}