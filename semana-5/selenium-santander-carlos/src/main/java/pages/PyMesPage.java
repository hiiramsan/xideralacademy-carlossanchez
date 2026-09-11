package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PyMesPage extends BasePage {

    public static final By SANTANDER_PYME = By.cssSelector("a[href*='/pyme/']");

    public static final By CUENTAS = By.cssSelector("a[href*='/pyme/cuentas']");

    public static final By PAQUETES_PYMES = By.cssSelector("a[href*='paquetes-pymes']");

    public static final By SEGUROS = By.cssSelector("a[href*='/pyme/seguros']");

    public static final By NEGOCIO_TRANSACCIONAL = By.cssSelector("a[href*='negocio-transaccional']");

    public static final By INVERSIONES = By.cssSelector("a[href*='/pyme/inversiones']");

    public static final By DIVISAS_Y_COBERTURAS = By.cssSelector("a[href*='coberturas-y-cambios']");

    public static final By NEGOCIO_INTERNACIONAL = By.cssSelector("a[href*='negocio-internacional']");

    public static final By CREDITOS = By.cssSelector("a[href*='/pyme/creditos']");

    public static final By ALIANZAS = By.cssSelector("a[href*='/pyme/alianzas']");

    public static final By ECOSISTEMA_PYME = By.cssSelector("a[href*='ecosistemas-pyme']");

    public PyMesPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToLink(By locator) {
        click(locator);
    }
}