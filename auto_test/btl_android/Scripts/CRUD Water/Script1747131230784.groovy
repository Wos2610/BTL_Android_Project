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
Mobile.delay(1)

Mobile.tap(findTestObject('Object Repository/crud water/android.widget.ImageButton'), 0)

Mobile.waitForElementPresent(findTestObject('Object Repository/crud water/android.widget.TextView - Water'), 10)
Mobile.verifyElementExist(findTestObject('Object Repository/crud water/android.widget.TextView - Water'), 5)
Mobile.delay(1)

Mobile.tap(findTestObject('Object Repository/crud water/android.widget.TextView - Water'), 0)

Mobile.waitForElementPresent(findTestObject('Object Repository/crud water/android.widget.Button'), 10)
Mobile.tap(findTestObject('Object Repository/crud water/android.widget.Button'), 0)

Mobile.waitForElementPresent(findTestObject('Object Repository/crud water/android.widget.Button - Save'), 10)
Mobile.tap(findTestObject('Object Repository/crud water/android.widget.Button - Save'), 0)
Mobile.delay(1)

Mobile.waitForElementPresent(findTestObject('Object Repository/crud water/android.widget.ImageButton (1)'), 10)
Mobile.tap(findTestObject('Object Repository/crud water/android.widget.ImageButton (1)'), 0)

Mobile.waitForElementPresent(findTestObject('Object Repository/crud water/android.widget.TextView - Diary'), 10)
Mobile.tap(findTestObject('Object Repository/crud water/android.widget.TextView - Diary'), 0)

Mobile.waitForElementPresent(findTestObject('Object Repository/crud water/android.widget.TextView - 500 ml'), 10)
Mobile.verifyElementExist(findTestObject('Object Repository/crud water/android.widget.TextView - 500 ml'), 5)
Mobile.tapAndHold(findTestObject('Object Repository/crud water/android.widget.TextView - 500 ml'), 3, 4)

Mobile.waitForElementPresent(findTestObject('Object Repository/crud water/android.widget.TextView - Edit'), 10)
Mobile.tap(findTestObject('Object Repository/crud water/android.widget.TextView - Edit'), 0)

Mobile.waitForElementPresent(findTestObject('Object Repository/crud water/android.widget.EditText - 500'), 10)
Mobile.setText(findTestObject('Object Repository/crud water/android.widget.EditText - 500'), '450', 0)

Mobile.waitForElementPresent(findTestObject('Object Repository/crud water/android.widget.Button - SAVE (1)'), 10)
Mobile.tap(findTestObject('Object Repository/crud water/android.widget.Button - SAVE (1)'), 0)
Mobile.delay(1)

Mobile.waitForElementPresent(findTestObject('Object Repository/crud water/android.widget.TextView - 450 ml'), 10)
Mobile.tapAndHold(findTestObject('Object Repository/crud water/android.widget.TextView - 450 ml'), 3, 4)

Mobile.waitForElementPresent(findTestObject('Object Repository/crud water/android.widget.TextView - Delete'), 10)
Mobile.tap(findTestObject('Object Repository/crud water/android.widget.TextView - Delete'), 0)

Mobile.closeApplication()


