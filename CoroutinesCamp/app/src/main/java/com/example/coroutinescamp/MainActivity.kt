package com.example.coroutinescamp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.UiThread
import androidx.lifecycle.lifecycleScope
import com.hencoder.coroutinescamp.model.Repo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Thread.currentThread
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
//    private val disposable = CompositeDisposable()
    private val scope = MainScope()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        /*println("Coroutines camp main: ${Thread.currentThread().name}")
        GlobalScope.launch {
            println("Coroutines camp ${Thread.currentThread().name}")
        }

        Thread{
            println("Coroutines camp 2 ${Thread.currentThread().name}")
        }.start()

        thread {
            println("Coroutines camp 3 ${Thread.currentThread().name}")
        }*/

        /*thread {
            ioCode1()
            runOnUiThread {
                uiCode1()
                thread {
                    ioCode2()
                    runOnUiThread {
                        uiCode2()
                        thread {
                            ioCode3()
                            runOnUiThread {
                                uiCode3()
                            }
                        }
                    }
                }
            }
        }*/

        /*GlobalScope.launch(Dispatchers.Main) {
            ioCode1()
            uiCode1()
            ioCode2()
            uiCode2()
            ioCode3()
            uiCode3()
        }*/

        /*scope.launch(Dispatchers.Main) {
            ioCode1()
            uiCode1()
            ioCode2()
            uiCode2()
            ioCode3()
            uiCode3()
        }

        scope.launch(Dispatchers.Main) {
            ioCode1()
            uiCode1()
            ioCode2()
            uiCode2()
            ioCode3()
            uiCode3()
        }

        scope.launch(Dispatchers.Main) {
            ioCode1()
            uiCode1()
            ioCode2()
            uiCode2()
            ioCode3()
            uiCode3()
        }*/

        //android团队提供的Kotlin拓展，不需要在onFinish()回调那里取消协程中的耗时任务
        /*lifecycleScope.launch(Dispatchers.Main) {
            ioCode1()
            uiCode1()
            ioCode2()
            uiCode2()
            ioCode3()
            uiCode3()
        }

        lifecycleScope.launch(Dispatchers.Main) {
            ioCode1()
            uiCode1()
            ioCode2()
            uiCode2()
            ioCode3()
            uiCode3()
        }

        lifecycleScope.launch(Dispatchers.Main) {
            ioCode1()
            uiCode1()
            ioCode2()
            uiCode2()
            ioCode3()
            uiCode3()
        }*/

        /*thread {
            println("Coroutines camp classic 1:${Thread.currentThread().name}")
            classicIoCode1(block = ::uiCode1)
            */
        /**
         * 切走了，还能切回到上下文环境中的线程中；而上面定义的方法只能切到指定线程（例如主线程）
         *//*
            println("Coroutines camp classic 3:${Thread.currentThread().name}")
        }*/

        //作业-1
        /*GlobalScope.launch(Dispatchers.IO){
            println("current thread: ${Thread.currentThread().name}")
        }*/

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()

        val api = retrofit.create(API::class.java)
        api.listRepos("rengwuxian")
            .enqueue(object : Callback<List<Repo>>{
                override fun onFailure(call: Call<List<Repo>>, t: Throwable) {

                }

                override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                    text_repo.text = response.body()?.get(0)?.name
                }

            })


        /*GlobalScope.launch(Dispatchers.Main) {
            val response = api.listReposKt("xiaof14792")
            text_repo.text = response.get(0).name + "Kt"
        }*/

        /*api.listReposRx("rengwuxian")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<List<Repo>> {
                override fun onSuccess(t: List<Repo>?) {
                    text_repo.text = t?.get(0)?.name + "Rx"
                }

                override fun onError(e: Throwable?) {
                }

                override fun onSubscribe(d: Disposable?) {
                    disposable.add(d)
                }

            })*/

        /*Single.zip<List<Repo>, List<Repo>, Boolean>(api.listReposRx("rengwuxian"),
            api.listReposRx("rengwuxian"),
            BiFunction<List<Repo>, List<Repo>, Boolean> { t1, t2 ->
                t1?.get(0)?.name == t2?.get(
                    0
                )?.name
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Boolean> {
                override fun onSuccess(t: Boolean?) {
                    text_repo.text = t.toString()
                }

                override fun onSubscribe(d: Disposable?) {
                    disposable.add(d)
                }

                override fun onError(e: Throwable?) {
                }
            }
            )*/

        GlobalScope.launch(Dispatchers.Main) {
            val one = async { api.listReposKt("rengwuxian") }
            val two = async { api.listReposKt("rengwuxian") }

            val same = one.await()[0].name == two.await()[0].name //await()方法是阻塞式的，等待结果出来

            text_repo.text = "$same same"
        }

        //协程泄漏

        /*scope.launch {
            coroutineScope {
                //一定要等到「coroutineScope」里面的逻辑执行完后，才能执行后面的逻辑
                launch(Dispatchers.IO) {
                    a()
                }
                launch {
                    b()
                }
                c()
            }
        }*/


        /*lifecycleScope.launch {
            ioCode1() //开启后台线程
            uiCode1() //切回前台显示
        }

        //上下两个方法原理等价

        thread {
            Thread.sleep(1000)
            println("Coroutines camp io1:${Thread.currentThread().name}")
            runOnUiThread {
                text_repo.text = "heihei"
            }
        }

        text_repo.text = "haha"*/

        lifecycleScope.launch {
            delay(1000)
            text_repo.text = "1000kl"
        }

        //上下两个方法原理等价

        thread {
            Thread.sleep(1000)
            runOnUiThread {
                text_repo.text = "1000kl"
            }
        }
    }

    override fun onDestroy() {
//        disposable.dispose() //RxJava预防内存泄漏
//        scope.cancel() //协程预防协程泄漏「内存泄漏」
        super.onDestroy()
    }


    /**
     * 需要自己去调用函数，去切换线程
     */
    private suspend fun ioCode1() {
        withContext(Dispatchers.IO) {
            Thread.sleep(1000)
            println("Coroutines camp io1:${Thread.currentThread().name}")
        }
    }

    private suspend fun ioCode2() {
        withContext(Dispatchers.IO) {
            Thread.sleep(1000)
            println("Coroutines camp io2:${Thread.currentThread().name}")
        }
    }

    private suspend fun ioCode3() {
        withContext(Dispatchers.IO) {
            Thread.sleep(1000)
            println("Coroutines camp io3:${Thread.currentThread().name}")
        }
    }

    private fun uiCode1() {
        println("Coroutines camp ui1:${Thread.currentThread().name}")
        text_repo.text = "heihei"
    }

    private fun uiCode2() {
        println("Coroutines camp ui2:${Thread.currentThread().name}")
    }

    private fun uiCode3() {
        println("Coroutines camp ui3:${Thread.currentThread().name}")
    }

}


/*private fun classicIoCode1(toUiThread: Boolean = true, block: () -> Unit) {
    val executor = ThreadPoolExecutor(5, 20, 1, TimeUnit.MINUTES, LinkedBlockingQueue(1000))
    executor.execute {
        Thread.sleep(1000)
        println("Coroutines camp classic io1:${Thread.currentThread().name}")

        if (toUiThread) {
            runOnUiThread {
                block()
            }
        } else {
            block()
        }
    }
}*/



