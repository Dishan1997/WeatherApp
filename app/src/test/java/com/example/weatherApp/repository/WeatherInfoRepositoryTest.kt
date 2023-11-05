package com.example.weatherApp.repository

import com.example.weatherApp.ConstantKeys
import com.example.weatherApp.WeatherInfo
import com.example.weatherApp.dao.WeatherInfoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import okhttp3.Response
import org.mockito.MockitoAnnotations
import java.net.HttpURLConnection
import okhttp3.ResponseBody
import org.mockito.Mockito
import org.mockito.Mockito.mock
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL


class WeatherInfoRepositoryTest {

    val dispatcher = TestCoroutineDispatcher()
    lateinit var repository: WeatherInfoRepository

    @Mock
     lateinit var mockHttpURLConnection: HttpURLConnection

    @Mock
    lateinit var dao: WeatherInfoDao

    @Mock
    var mockUrl1 =  URL("${ConstantKeys.BASE_URL}data/2.5/weather?lat=$0.0&lon=$0.0&appid=${ConstantKeys.APP_ID}")
    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        MockitoAnnotations.openMocks(this)
        repository = WeatherInfoRepository(dao)
    }

    @Test
    fun testFetchWeatherInfo_Success() {
        Mockito.`when`(mockHttpURLConnection.responseCode).thenReturn(HttpURLConnection.HTTP_OK)
        val mockUrl = mock(URL::class.java)
        val mockInputReader = mock(InputStream::class.java)
        Mockito.`when`(mockHttpURLConnection.inputStream).thenReturn(mockInputReader)
        Mockito.`when`(mockUrl.openConnection()).thenReturn(mockHttpURLConnection)
        repository.fetchWeatherInfo(0.0, 0.0){
           p1, p2 ->
         Assert.assertNull(p2)
            Assert.assertNotNull(p1)
       }
    }

    @Test
    fun testFetchWeatherInfo_Error() {
        Mockito.`when`(mockHttpURLConnection.responseCode).thenReturn(HttpURLConnection.HTTP_NOT_FOUND)
        val mockUrl = mock(URL::class.java)
        val mockInputReader = mock(InputStream::class.java)
        Mockito.`when`(mockHttpURLConnection.inputStream).thenReturn(mockInputReader)
        Mockito.`when`(mockUrl.openConnection()).thenAnswer{mockHttpURLConnection}
        repository.fetchWeatherInfo(0.0, 0.0){
                p1, p2 ->
             Assert.assertNull(p1)
            Assert.assertNotNull(p2)
            Assert.assertEquals(404, mockHttpURLConnection.responseCode)
        }
    }
    @Test
    fun testFetchHourlyWeatherInfo_Success() {
        Mockito.`when`(mockHttpURLConnection.responseCode).thenReturn(HttpURLConnection.HTTP_OK)
        val mockUrl = mock(URL::class.java)
        val mockInputReader = mock(InputStream::class.java)
        Mockito.`when`(mockHttpURLConnection.inputStream).thenReturn(mockInputReader)
        Mockito.`when`(mockUrl.openConnection()).thenReturn(mockHttpURLConnection)
        repository.fetchWeatherInfoHourly(0.0, 0.0){
                p1, p2 ->
            Assert.assertNull(p2)
            Assert.assertNotNull(p1)
        }
    }

    @Test
    fun testFetchHourlyWeatherInfo_Error() {
        Mockito.`when`(mockHttpURLConnection.responseCode).thenReturn(HttpURLConnection.HTTP_NOT_FOUND)
        val mockUrl = mock(URL::class.java)
        val mockInputReader = mock(InputStream::class.java)
        Mockito.`when`(mockHttpURLConnection.inputStream).thenReturn(mockInputReader)
        Mockito.`when`(mockUrl.openConnection()).thenReturn(mockHttpURLConnection)
        repository.fetchWeatherInfoHourly(0.0, 0.0){
                p1, p2 ->
            Assert.assertNull(p1)
            Assert.assertNotNull(p2)
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

}

