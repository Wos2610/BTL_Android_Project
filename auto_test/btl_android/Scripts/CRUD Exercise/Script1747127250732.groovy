import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

Mobile.startApplication('D:\\PTIT\\IV.2\\IV.2_Mobile\\app-debug.apk', false)

Mobile.waitForElementPresent(findTestObject('Object Repository/log weight/android.widget.ImageButton'), 60)
Mobile.verifyElementExist(findTestObject('Object Repository/log weight/android.widget.ImageButton'), 5)

Mobile.tap(findTestObject('Object Repository/crud exercise/android.widget.ImageButton'), 0)

Mobile.waitForElementPresent(findTestObject('Object Repository/crud exercise/android.widget.TextView - Exercise'), 10)
Mobile.verifyElementExist(findTestObject('Object Repository/crud exercise/android.widget.TextView - Exercise'), 5)
Mobile.tap(findTestObject('Object Repository/crud exercise/android.widget.TextView - Exercise'), 0)

Mobile.waitForElementPresent(findTestObject('Object Repository/crud exercise/android.widget.Button - Create an Exercise'), 10)
Mobile.tap(findTestObject('Object Repository/crud exercise/android.widget.Button - Create an Exercise'), 0)

Mobile.setText(findTestObject('Object Repository/crud exercise/android.widget.EditText - Description'), 'VietNam', 0)
Mobile.setText(findTestObject('Object Repository/crud exercise/android.widget.EditText - Required'), '123', 0)
Mobile.setText(findTestObject('Object Repository/crud exercise/android.widget.EditText - Required (1)'), '123', 0)

Mobile.tap(findTestObject('Object Repository/crud exercise/android.widget.Button - Create an Exercise (1)'), 0)

Mobile.waitForElementPresent(findTestObject('Object Repository/crud exercise/android.widget.TextView - VietNam'), 10)
Mobile.verifyElementExist(findTestObject('Object Repository/crud exercise/android.widget.TextView - VietNam'), 5)
Mobile.tap(findTestObject('Object Repository/crud exercise/android.widget.TextView - VietNam'), 0)

Mobile.setText(findTestObject('Object Repository/crud exercise/android.widget.EditText - 123'), '125', 0)

Mobile.waitForElementPresent(findTestObject('Object Repository/crud exercise/android.widget.Button - SAVE'), 10)
Mobile.tap(findTestObject('Object Repository/crud exercise/android.widget.Button - SAVE'), 0)

Mobile.waitForElementPresent(findTestObject('Object Repository/crud exercise/android.widget.TextView - 125 minutes - 123.0 calories'), 10)
Mobile.tap(findTestObject('Object Repository/crud exercise/android.widget.TextView - 125 minutes - 123.0 calories'), 0)

Mobile.waitForElementPresent(findTestObject('Object Repository/crud exercise/android.widget.Button - DELETE'), 10)
Mobile.tap(findTestObject('Object Repository/crud exercise/android.widget.Button - DELETE'), 0)

Mobile.closeApplication()


