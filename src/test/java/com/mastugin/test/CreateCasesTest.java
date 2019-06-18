package com.mastugin.test;

import com.mastugin.rule.DriverRule;
import com.mastugin.rule.DriverSetupRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.UUID;

public class CreateCasesTest {

    @ClassRule
    public static DriverSetupRule driverSetupRule = DriverSetupRule.getInstance();
    @Rule
    public DriverRule driverRule = DriverRule.getInstance();
    protected WebDriver driver;
    private String TEST_LINK_BASE_URI;
    private String USER_LOGIN;
    private String USER_PASSWORD;
    private String SUIT_NAME;
    private String TEST_CASE_NAME_1;
    private String TEST_CASE_NAME_2;

    @Before
    public void setUp() {
        driver = driverRule.getDriver();
        initTestData();
    }

    private void initTestData() {
        TEST_LINK_BASE_URI = "http://localhost:8080/testlink/";
        USER_LOGIN = "at-senior-tester";
        USER_PASSWORD = "12345678";
        SUIT_NAME = "OTUS_SUITE_" + UUID.randomUUID().toString();
        TEST_CASE_NAME_1 = "TEST_CASE_" + UUID.randomUUID().toString();
        TEST_CASE_NAME_2 = "TEST_CASE_" + UUID.randomUUID().toString();
    }

    @Test
    public void createSuitWithCases() throws InterruptedException {
        openPage(TEST_LINK_BASE_URI);
        signIn(USER_LOGIN, USER_PASSWORD);
        goToEditor();
        createSuite(SUIT_NAME, "The best suit!");
        goToSuit(SUIT_NAME);
        createTestCase(TEST_CASE_NAME_1, "SUPER COOL CASE", "FEEL GOOD", 3);
        goToSuit(SUIT_NAME);
        createTestCase(TEST_CASE_NAME_2, "SUPER COOL CASE", "FEEL GOOD", 3);
    }

    /**
     *  ОБЩИЕ МЕТОДЫ
     */
    private void openPage(String path) {
        driver.get(path);
    }

    private void signIn(String login, String pass) {
        driver.findElement(By.id("tl_login")).sendKeys(login);
        driver.findElement(By.id("tl_password")).sendKeys(pass);
        driver.findElement(By.cssSelector("input[type=submit]")).click();
    }

    private void goToEditor() {
        driver.switchTo().frame("mainframe");
        driver.findElement(By.linkText("Редактировать тесты")).click();
    }

    private void goToSuit(String name) throws InterruptedException {
        driver.switchTo().frame("treeframe");
        driver.findElement(By.xpath("//span[text()[contains(.,\'" + name + "\')]]/ancestor::div")).click();
        driver.switchTo().parentFrame();
        //явное ожидание очень плохо, нужно использовать Fluent ожидание,
        // но пока не разобрался как правильно это сделать
        Thread.sleep(500);
    }

    /**
     *  МЕТОДЫ ДЛЯ СЬЮТОВ
     */

    private void createSuite(String name, String description) {
        openNewSuitForm();
        fillSuitData(name, description);
        addSuit();
    }

    private void openNewSuitForm() {
        driver.switchTo().frame("workframe");
        driver.findElement(By.xpath("//img[@title='Действия']")).click();
        driver.findElement(By.id("new_testsuite")).click();
    }

    private void fillSuitData(String name, String description) {
        driver.findElement(By.id("name")).sendKeys(name);
        final WebElement iframe = driver.findElement(By.cssSelector("iframe.cke_wysiwyg_frame"));
        fillFrame(iframe, description);
    }

    private void addSuit() {
        driver.findElement(By.xpath("//form[@id='container_new']/div/div[1]/input[@name='add_testsuite_button']")).click();
        driver.switchTo().parentFrame();
    }

    /**
     *   МЕТОДЫ ДЛЯ ТЕСТКЕЙСОВ
     */
    private void createTestCase(String name, String description, String condition, int stepsCount) throws InterruptedException {
        openNewTestCaseForm();
        fillTestCase(name, description, condition);
        addTestCase();
        addSteps(stepsCount);
    }

    private void openNewTestCaseForm() {
        driver.switchTo().frame("workframe");
        driver.findElement(By.xpath("//img[@title='Действия']")).click();
        driver.findElement(By.cssSelector("#create_tc")).click();
    }

    private void fillTestCase(String name, String description, String condition) {
        driver.findElement(By.cssSelector("#testcase_name")).sendKeys(name);
        List<WebElement> iframes = driver.findElements(By.cssSelector("iframe.cke_wysiwyg_frame"));
        fillFrame(iframes.get(0), description);
        fillFrame(iframes.get(1), condition);
    }

    private void addTestCase() {
        driver.findElement(By.xpath("//*[@id='do_create_button']")).click();
    }

    private void addSteps(int count) throws InterruptedException {
        createStep();
        for (int i = 1; i <= count; i++) {
            fillStep();
            saveStep();
        }
        cancel();
    }

    private void createStep() {
        driver.findElement(By.cssSelector("input[name=create_step]")).click();
    }

    private void fillStep() {
        List<WebElement> iframes = driver.findElements(By.cssSelector("iframe.cke_wysiwyg_frame"));
        fillFrame(iframes.get(0), "STEP BY STEP");
        fillFrame(iframes.get(1), "IM WAITING YOU");
    }

    private void saveStep() throws InterruptedException {
        driver.findElement(By.cssSelector("input[name=do_update_step]")).click();
        //явное ожидание очень плохо, нужно использовать  Fluent ожидание,
        // но пока не разобрался как правильно это сделать
        Thread.sleep(500);
    }

    private void cancel() {
        driver.findElement(By.xpath("//input[@name='cancel']")).click();
        driver.switchTo().parentFrame();
    }

    /**
     * ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
     * */

    private void fillFrame(WebElement iframe, String text) {
        driver.switchTo().frame(iframe);
        driver.findElement(By.xpath("//body[@contenteditable='true']")).sendKeys(text);
        driver.switchTo().parentFrame();
    }
}
