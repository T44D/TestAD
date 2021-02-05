package project.taufan.keyta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import project.taufan.keyta.databinding.ActivityItemTersimpanBinding

class ItemTersimpanActivity : AppCompatActivity(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityItemTersimpanBinding
    private var listItem = ArrayList<Data>()
    private var person = ArrayList<String>()
    private var resultId: Int = 0
    private var resultName: String = ""

    companion object {
        const val EXTRA_DATA = "data"
        const val EXTRA_SELECTED_ID = "extra_selected_id"
        const val EXTRA_SELECTED_NAME = "extra_selected_name"
        const val RESULT_CODE = 110
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Daftar Nama Tersimpan"
        binding = ActivityItemTersimpanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listItem = intent.getSerializableExtra(EXTRA_DATA) as ArrayList<Data>
        for (i in 0 until listItem.size) {
            person.add(listItem[i].name.toString())
        }
        val spinner = binding.spinnnerNama
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, person)
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this
        binding.btnSimpan.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_simpan -> {
                if (resultId == 0) {
                    Toast.makeText(this@ItemTersimpanActivity, "Harus Memilih Nama", Toast.LENGTH_SHORT).show()
                } else {
                    val resultIntent = Intent()
                    resultIntent.putExtra(EXTRA_SELECTED_ID, resultId)
                    resultIntent.putExtra(EXTRA_SELECTED_NAME, resultName)
                    setResult(RESULT_CODE, resultIntent)
                    finish()
                }
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        resultId = pos
        resultName = parent.getItemAtPosition(pos).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>) {}
}