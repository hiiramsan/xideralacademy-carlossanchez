package tests;

import base.BaseTest;
import java.util.List;
import java.util.function.Consumer;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

import pages.AcercaDelBancoPage;
import pages.BancaPrivadaPage;
import pages.EmpresasPage;
import pages.HomePage;
import pages.PersonasPage;
import pages.PyMesPage;

public class AutomationLinksTest extends BaseTest {

    @Test
    public void shouldNavigateToPersonasLinks() {
        assertEachLinkNavigates(
                List.of(
                        PersonasPage.TARJETAS_DE_CREDITO,
                        PersonasPage.CREDITO_PERSONAL,
                        PersonasPage.CREDITO_HIPOTECARIO,
                        PersonasPage.SIMULADOR_HIPOTECA,
                        PersonasPage.CREDITO_AUTOMOTRIZ,
                        PersonasPage.BURO_DE_CREDITO,
                        PersonasPage.SANTANDER_DIGITAL,
                        PersonasPage.APP_SANTANDER,
                        PersonasPage.SANTANDER_WEB,
                        PersonasPage.CUENTAS,
                        PersonasPage.BASICA,
                        PersonasPage.NOMINA,
                        PersonasPage.PORTABILIDAD_NOMINA,
                        PersonasPage.INVERSIONES,
                        PersonasPage.SEGUROS
                ),
                link -> new HomePage(driver).openPersonasNavbar().navigateToLink(link)
        );
    }

    @Test
    public void shouldNavigateToEmpresasLinks() {
        assertEachLinkNavigates(
                List.of(
                        EmpresasPage.EMPRESAS_Y_GOBIERNO,
                        EmpresasPage.MULTINACIONALES
                ),
                link -> new HomePage(driver).openEmpresasNavbar().navigateToLink(link)
        );
    }

    @Test
    public void shouldNavigateToPyMesLinks() {
        assertEachLinkNavigates(
                List.of(
                        PyMesPage.SANTANDER_PYME,
                        PyMesPage.CUENTAS,
                        PyMesPage.PAQUETES_PYMES,
                        PyMesPage.SEGUROS,
                        PyMesPage.NEGOCIO_TRANSACCIONAL,
                        PyMesPage.INVERSIONES,
                        PyMesPage.DIVISAS_Y_COBERTURAS,
                        PyMesPage.NEGOCIO_INTERNACIONAL,
                        PyMesPage.CREDITOS,
                        PyMesPage.ALIANZAS,
                        PyMesPage.ECOSISTEMA_PYME
                ),
                link -> new HomePage(driver).openPyMesNavbar().navigateToLink(link)
        );
    }

@Test
    public void shouldNavigateToBancaPrivadaLinks() {
        HomePage homePage = new HomePage(driver);
        String homeUrl = driver.getCurrentUrl();

        driver.get(homeUrl);
        homePage.waitForHomePage(homeUrl);
        BancaPrivadaPage bpp = homePage.openBancaPrivadaNavbar();

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/bp/home"),
                "Banca Privada deberia estr en /bp/home, se obtuvo: " + driver.getCurrentUrl()
        );
    }

    @Test
    public void shouldNavigateToAcercaDelBancoLinks() {
        assertEachLinkNavigates(
                List.of(
                        AcercaDelBancoPage.FUNDACION_SANTANDER,
                        AcercaDelBancoPage.SOSTENIBILIDAD,
                        AcercaDelBancoPage.EDUCACION_FINANCIERA,
                        AcercaDelBancoPage.INVERSIONISTAS,
                        AcercaDelBancoPage.SALA_COMUNICACION,
                        AcercaDelBancoPage.BOLSA_TRABAJO,
                        AcercaDelBancoPage.BLOG
                ),
                link -> new HomePage(driver).openAcercaDelBancoNavbar().navigateToLink(link)
        );
    }

    private void assertEachLinkNavigates(List<By> linkLocators, Consumer<By> clickLink) {
        HomePage homePage = new HomePage(driver);
        String homeUrl = driver.getCurrentUrl();

        for (By link : linkLocators) {

            driver.get(homeUrl);
            homePage.waitForHomePage(homeUrl);

            clickLink.accept(link);

            String currentUrl = driver.getCurrentUrl();
            Assert.assertFalse(
                    currentUrl.equals(homeUrl),
                    "No se llegó a la pagina: " + link
            );
        }
    }
}