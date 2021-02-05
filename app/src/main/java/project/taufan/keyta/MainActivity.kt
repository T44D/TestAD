package project.taufan.keyta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.entity.StringEntity
import org.json.JSONArray
import org.json.JSONObject
import project.taufan.keyta.ItemTersimpanActivity.Companion.EXTRA_DATA
import project.taufan.keyta.ItemTersimpanActivity.Companion.EXTRA_SELECTED_ID
import project.taufan.keyta.ItemTersimpanActivity.Companion.EXTRA_SELECTED_NAME
import project.taufan.keyta.ItemTersimpanActivity.Companion.RESULT_CODE
import project.taufan.keyta.databinding.ActivityMainBinding
import java.lang.Exception

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private val listItem = ArrayList<Data>()
    private var resultId: Int? = 0
    private var resultName: String? = ""

    companion object {
        private const val REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Daftar Nama"
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getItem()
        binding.tvItemDipilih.visibility = View.INVISIBLE
        binding.btnSimpan.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_simpan -> {
                val simpan = Intent(this@MainActivity, ItemTersimpanActivity::class.java)
                simpan.putExtra(EXTRA_DATA, listItem)
                if (listItem.size == 0) {
                    Toast.makeText(this@MainActivity, "Tidak Ada Data", Toast.LENGTH_SHORT).show()
                } else {
                    startActivityForResult(simpan, REQUEST_CODE)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_CODE) {
                resultId = data?.getIntExtra(EXTRA_SELECTED_ID, 0)
                resultName = data?.getStringExtra(EXTRA_SELECTED_NAME)
                binding.tvNama.visibility = View.INVISIBLE
                binding.etNama.visibility = View.INVISIBLE
                binding.tvItemDipilih.visibility = View.VISIBLE
                binding.btnSimpan.text = "Kirim"
                binding.tvItemDipilih.text = "Nama Dipilih:\n\n$resultName"
                binding.btnSimpan.setOnClickListener {
                    sendItem(resultId, resultName)
                }
            }
        }
    }

    private fun sendItem(id: Int?, name: String?) {
        val client = AsyncHttpClient()
        val params = JSONObject()
        params.put("id", id)
        params.put("name", name)
        val entity = StringEntity(params.toString())
        val url = "https://test.keyta.id/api/v1/send_data/"
        client.post(this@MainActivity, url, entity, "application/json", object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>,
                responseBody: ByteArray
            ) {
                Toast.makeText(this@MainActivity, responseBody.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun getItem() {
        val client = AsyncHttpClient()
        val url = "https://test.keyta.id/api/v1/get_data/"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>,
                responseBody: ByteArray
            ) {
                val result = String(responseBody)
                var output = ""
                try {
                    val jsonArray = JSONArray(result)
                    listItem.add(Data(0, "Item dipilih:"))
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val id = jsonObject.getInt("id")
                        val name = jsonObject.getString("name")
                        listItem.add(Data(id, name))
                    }
                    if (listItem.size != 0) {
                        for (j in 0 until listItem.size) {
                            if (j != 0) {
                                output += if (j + 1 == listItem.size) {
                                    listItem[j].name
                                } else {
                                    listItem[j].name + ", "
                                }
                            }
                        }
                        binding.etNama.setText(output)
                    } else {
                        Toast.makeText(this@MainActivity, "Tidak Ada Data", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }
}