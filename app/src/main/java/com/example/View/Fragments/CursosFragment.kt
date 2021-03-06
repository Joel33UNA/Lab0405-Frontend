package com.example.View.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.Data.CursoApi
import com.example.View.Adapter.CursoAdapter
import com.example.View.R
import com.example.lab04_frontend.Logica.Curso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CursosFragment : Fragment() {

    private var listaCursos = ArrayList<Curso>()
    private lateinit var cursosLiveData: MutableLiveData<List<Curso>>
    private lateinit var cursoAdapter: CursoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.cursosLiveData = MutableLiveData<List<Curso>>()
        this.cursoAdapter = CursoAdapter(listaCursos, context!!)

        initCursos()
        observer()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cursos, container, false)
    }

    private fun observer() {
        val curso: Observer<List<Curso>> = object : Observer<List<Curso>> {
            @Override
            override fun onChanged(@Nullable cursos: List<Curso>?) {
                listaCursos = cursos as ArrayList<Curso>
                if(listaCursos.isNotEmpty()) updateView()
            }
        }
        cursosLiveData!!.observe(this, curso)
    }

    private fun initCursos(){
        val retrofit = Retrofit.Builder().baseUrl("http://192.168.0.16:8088/GestionAcademica/api/")
            .addConverterFactory(GsonConverterFactory.create()).build()

        CoroutineScope(Dispatchers.IO).launch {
            val call = retrofit.create(CursoApi::class.java)
            val request = call.getCursosAll()
            val response = request.body() as ArrayList<Curso>
            if(request.isSuccessful){
                withContext(Dispatchers.Main) {
                    cursosLiveData!!.value = response
                }
            }else{
                Toast.makeText(context!!, "Error al mostrar los cursos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateView(){
        this.cursoAdapter = CursoAdapter(listaCursos, context!!)
        val recyclerView = view!!.findViewById<RecyclerView>(R.id.recyclerCursos)
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(LinearLayoutManager(context!!))
        recyclerView.adapter = cursoAdapter
    }
}