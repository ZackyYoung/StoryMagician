package com.bytedance.storymagician

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceCreator {
    private const val BASE_URL = "http://14.103.19.244:7000"

    // 创建一个 OkHttpClient 实例，并配置日志拦截器和超时
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                // 打印请求体和响应体
                level = HttpLoggingInterceptor.Level.BODY
            }
        )
        .connectTimeout(15, TimeUnit.SECONDS) // 连接超时
        .readTimeout(20, TimeUnit.SECONDS)    // 读取超时
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient) // 设置自定义的 OkHttpClient
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    // 借助泛型实化，我们可以进一步优化上述代码
    // 因为泛型 T 的类型信息在运行时依旧保留，在函数内部，我们可以直接访问 T 的 Class 对象 (T::class.java)，无需手动传入 Class 对象。
    inline fun <reified T> create(): T = create(T::class.java)
}

