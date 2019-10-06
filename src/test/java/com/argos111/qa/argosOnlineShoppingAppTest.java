package com.argos111.qa;

import com.google.common.collect.Ordering;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class argosOnlineShoppingAppTest {
    public static WebDriver driver;

    @Before
    public void launchApp() {
        //setup the Chrome browser
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        //get the url
        driver.get("https://www.argos.co.uk/");
        //maximize the window
        driver.manage().window().maximize();
        //implicit wait for web elements
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        //page load time out for loading the page
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        //delete all cookies
        driver.manage().deleteAllCookies();

    }

    @After
    public void closeApp() {
        //close app
        driver.quit();
    }

    @Test
    public void getUrlTest() {
        //get the current url
        String actualUrl = getCurrentUrl();
        assertThat("different Home page " + actualUrl, Matchers.endsWith("co.uk/"));
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();

    }

    //login test
    @Test
    public void logInTest() throws InterruptedException {
        logIn("pundarikakshareddy555@gmail.com", "Sandeep5");
        //  String actual = logInsuccess();
        //assertThat("user not able to see user name :", actual, Matchers.startsWith("Hi Sandeep"));

    }

    //do search test
    @Test
    public void serchTest() {
        doSearchProductTest("nike");
        String actual = selectedItemHeader();
        assertThat("user not getting correct header :", actual, Matchers.equalToIgnoringCase("nike"));

    }

    //creating login method
    public void logIn(String email, String password) {
        //finding web elements for loginpage
        driver.findElement(By.linkText("Account")).click();
        driver.findElement(By.id("email-address")).sendKeys(email);
        driver.findElement(By.id("current-password")).sendKeys(password);
        Actions actions = new Actions(driver);
        actions.moveToElement(driver.findElement(By.cssSelector(".sign-in-form"))).build().perform();
        driver.findElement(By.cssSelector(".sign-in-form")).click();
    }

    //creating method for login assertion
    public String logInsuccess() throws InterruptedException {
        Thread.sleep(3000);
        return driver.findElement(By.cssSelector("._2WxI4")).getText();
    }

    //assertion method for search
    public String selectedItemHeader() {
        return driver.findElement(By.cssSelector(".search-title__term")).getText();
    }

    //filter test for rating
    @Test
    public void filterRatingTest() throws InterruptedException {
        doSearchProductTest("nike");
        selectARating("4 or more");
        List<Double> actualList = getAllRatingsOnFilteredProduct();
        assertThat("List is storing wrong value or filter broken. ", actualList, everyItem(greaterThanOrEqualTo(2.0)));

    }

    //method to search a product
    public void doSearchProductTest(String customerSelectedProduct) {
        driver.findElement(By.id("searchTerm")).sendKeys(customerSelectedProduct);
        driver.findElement(By.cssSelector("._2mKaC")).click();
    }

    //filter test for price
    @Test
    public void FilterPriceTest() throws InterruptedException {
        doSearchProductTest("nike");
        selectCustomerPrice("£15 - £20");
        List<Double> actualList = getAllPricesOnFilterProduct();
        assertThat("List is sorting wrong value .", actualList, everyItem(greaterThanOrEqualTo(15.00)));
        assertThat("List is sorting wrong value.", actualList, everyItem(lessThanOrEqualTo(20.0)));
    }

    //sort by test
    @Test
    public void sortByTest() throws InterruptedException {
        doSearchProductTest("nike");
        customerSortedProduct("Price: Low - High");
        List<Double> actual = getAllSortedProductOnPrice();
        boolean sorted = Ordering.natural().isOrdered(actual);
        assertThat("Price is not sorted. ", sorted, is(equalTo(true)));

    }

    //method for rating
    public void selectARating(String customerSelectedRating) throws InterruptedException {
        Thread.sleep(3000);
        List<WebElement> customerRatings = driver.findElements(By.cssSelector(".ac-facet__label .ac-facet__label--rating"));
        for (WebElement ratingWebElement : customerRatings) {
            if (ratingWebElement.getText().equalsIgnoreCase(customerSelectedRating)) {
                new WebDriverWait(driver, 20)
                        .until(ExpectedConditions.elementToBeClickable(ratingWebElement));
                ratingWebElement.click();
                break;

            }
        }
    }

    //assertion method for star rating
    public List<Double> getAllRatingsOnFilteredProduct() {

        List<Double> collectedRating = new ArrayList<>();

        List<WebElement> ratingWebElements = driver.findElements(By.cssSelector(".ac-star-rating"));
        for (WebElement ratingWedelement : ratingWebElements) {
            String ratingInSting = ratingWedelement.getAttribute("data-star-rating");
            double ratingInDouble = Double.parseDouble(ratingInSting);
            System.out.println("Collected rating :" + collectedRating);
            collectedRating.add(ratingInDouble);

        }
        return collectedRating;
    }

    //method for price filter test
    public void selectCustomerPrice(String customerSelectedPrice) throws InterruptedException {
        //finding the list wedElements for price
        List<WebElement> priceWebElements = driver.findElements(By.cssSelector(".ac-facet__filters .ac-facet__label--default"));
        for (WebElement priceWebElement : priceWebElements) {
            if (priceWebElement.getText().equalsIgnoreCase(customerSelectedPrice)) {
                Thread.sleep(3000);
                priceWebElement.click();
                break;
            }
        }
    }

    //assertion method for price test
    public List<Double> getAllPricesOnFilterProduct() throws InterruptedException {
        Thread.sleep(3000);
        List<Double> collectedPrice = new ArrayList<>();
        List<WebElement> priceWebelements = driver.findElements(By.cssSelector(".ac-product-price__amount"));

        for (WebElement priceWebelement : priceWebelements) {
            //get the text of price webelement
            String priceInString = priceWebelement.getText().replace("£", "");
            System.out.println(priceInString);
            //converting the variable string to double
            double priceInDouble = Double.parseDouble(priceInString);
            collectedPrice.add(priceInDouble);
        }
        return collectedPrice;
    }

    //method for sort by test
    public void customerSortedProduct(String customerSelectedpSort) throws InterruptedException {
        WebElement sortSelect = driver.findElement(By.cssSelector(".sort-select"));
        sortSelect.click();
        Select select = new Select(sortSelect);
        Thread.sleep(3000);
        select.selectByVisibleText(customerSelectedpSort);

    }

    //assertion for sort by
    public List<Double> getAllSortedProductOnPrice() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Double> collectedSort = new ArrayList<>();
        List<WebElement> priceAmount = driver.findElements(By.cssSelector(".ac-product-price__amount"));
        for (WebElement sortByWebElement : priceAmount) {
            String sortInString = sortByWebElement.getText();
            double sortInDouble = Double.parseDouble(sortInString);
            System.out.println(collectedSort);
            collectedSort.add(sortInDouble);
        }
        return collectedSort;

    }

}









