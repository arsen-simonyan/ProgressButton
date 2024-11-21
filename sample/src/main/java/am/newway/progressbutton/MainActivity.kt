package am.newway.progressbutton

import am.newway.progressbutton.databinding.ActivityMainBinding
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        startProgressButton()
    }

    private fun startProgressButton() {
        with(binding.content) {
            progressButton1.startProgress()
            progressButton2.startProgress()
            progressButton3.startProgress()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun ProgressButton.startProgress() {
        GlobalScope.launch {
            repeat(10) {
                for (i in 0..100) {
                    setProgress(i)
                    delay(100L)
                }
                for (i in 100 downTo 0) {
                    setProgress(i)
                    delay(100L)
                }
            }
        }
    }
}