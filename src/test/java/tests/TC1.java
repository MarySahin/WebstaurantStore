package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import jdk.jfr.StackTrace;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TC1 {
/**
 * Test case:
 * 1.	Go to https://www.webstaurantstore.com/
 * 2.	Search for 'stainless work table'.
 * 3.	Check the search result ensuring every product has the word 'Table' in its title.
 * 4.	Add the last of found items to Cart.
 * 5.	Empty Cart.
 */

WebDriver webDriver;
String searchURL ;
String lastPageUrl;
int numberOfProductsOnLastPage=0;

@BeforeMethod
public void setup(){
    //Setting up the webdriver

    WebDriverManager.chromedriver().setup();
    webDriver = new ChromeDriver();
    webDriver.manage().window().maximize();
    webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

}

@AfterMethod
public void closeDriver(){
    webDriver.close();
}


@Test
public void test1() {

    //1.	Go to https://www.webstaurantstore.com/

    webDriver.get("https://www.webstaurantstore.com/");
    String expectedTitle = "WebstaurantStore: Restaurant Supplies & Foodservice Equipment";
    String actualTitle = webDriver.getTitle();
    Assert.assertEquals(actualTitle, expectedTitle);
}


    //2.	Search for 'stainless work table'.

    @Test
    public void test2() {
        webDriver.get("https://www.webstaurantstore.com/");
        WebElement searchInputBox = webDriver.findElement(By.id("searchval"));
        searchInputBox.sendKeys("stainless work table" + Keys.ENTER);
        searchURL = webDriver.getCurrentUrl();

        WebElement searhResultsKeyword = webDriver.findElement(By.xpath("//span[.='stainless work table']"));
        Assert.assertTrue(searhResultsKeyword.isDisplayed());

    }
    @Test
    public void test3() {
    webDriver.get(searchURL);

        WebElement nextPage = webDriver.findElement(By.xpath("(//*[@class='h-3 w-3'])[2]"));

        //the xpath below first find the container of all pages then find all </a> elements in that container. Then with size method we find number of pages to click
        int numberOfPages = webDriver.findElements(By.xpath("//ul[@class='rc-pagination rounded-md inline-block font-semibold h-7-1/2 list-none m-0 overflow-hidden w-auto']//a")).size() + 1;

        ArrayList<String> listOfnamesOfProducts = new ArrayList<>();

        for (int i = 0; i <= numberOfPages; i++) {
            int productNumber = 1;
            int pageNumber = i + 1;
            List<WebElement> ProductsWebElements = webDriver.findElements(By.xpath("//a[@data-testid='itemDescription']"));
            for (WebElement eachProduct : ProductsWebElements) {
                listOfnamesOfProducts.add(eachProduct.getText());

                //if you would like to print out the Product names make below active
                //System.out.println("Page:" + pageNumber + " " + "Product" + productNumber + " = " + eachProduct.getText());
                productNumber++;
                numberOfProductsOnLastPage = ProductsWebElements.size();

            }

            //refreshing the web element to avoid stale element exception;
             nextPage = webDriver.findElement(By.xpath("(//*[@class='h-3 w-3'])[2]"));
             nextPage.click();

        }
        //storing last pageURL to use at the next test
        lastPageUrl = webDriver.getCurrentUrl();

        //printing out the product items that does not include search item
        for (String eachProduct : listOfnamesOfProducts) {

            if (!(eachProduct.contains("Table"))) {
                System.out.println("The product does not contain 'Table' is/are: " + eachProduct);
            }

            Assert.assertTrue(eachProduct.contains("" + "Table"), "BUG Alert!!! There is product does NOT contain Searched Item");


        }


    }




@Test
public void test4_and_test5() throws InterruptedException {
    webDriver.get(lastPageUrl);
    String lastItemXpath = "(//a[@data-testid='itemDescription'])["+numberOfProductsOnLastPage+"]";
    webDriver.findElement(By.xpath(lastItemXpath)).click();
    WebElement addToCartButton = webDriver.findElement(By.id("buyButton"));
    addToCartButton.click();

    String numberOfItemInCart = webDriver.findElement(By.xpath("//span[@id='cartItemCountSpan']")).getText();
    Assert.assertEquals("1", numberOfItemInCart);

    webDriver.findElement(By.xpath("(//span[.='Cart'])[1]")).click();
    Thread.sleep(2000);

    webDriver.findElement(By.xpath("//button[.='Empty Cart']")).click();
    Thread.sleep(2000);

    webDriver.findElement(By.xpath("//button[@class='bg-origin-box-border bg-repeat-x border-solid border box-border cursor-pointer inline-block text-center no-underline hover:no-underline antialiased hover:bg-position-y-15 mr-2 rounded-normal text-base px-7 py-2-1/2 hover:bg-green-600 text-white text-shadow-black-60 bg-green-primary bg-linear-gradient-180-green border-black-25 shadow-inset-black-17 align-middle font-semibold']")).click();
    Thread.sleep(2000);

    numberOfItemInCart = webDriver.findElement(By.xpath("//span[@id='cartItemCountSpan']")).getText();

    Assert.assertEquals( numberOfItemInCart, "0");


}





}
