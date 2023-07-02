package com.example.opsc7311_poe

//import Services.ExportService
import ProjectForm.ProjectForm
import Services.ExportService
import Services.HoursService
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import data.ProjectViewModel
import data.graphData

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var db: FirebaseFirestore = Firebase.firestore

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val btnExcel = view.findViewById<CardView>(R.id.btnExport)
        val btnViewProjects = view.findViewById<CardView>(R.id.btnViewProjects)
        auth = Firebase.auth
        val uid = auth.uid!!
        val projectList: MutableList<ProjectViewModel> = mutableListOf()

        val docRef =  db.collection("users").document(uid).collection("projects")
        docRef.get().addOnCompleteListener() {
            if (it.isSuccessful) {
                var projects = it.result.documents
                for (p in projects) {
                    var project = p.toObject<ProjectViewModel>()
                    projectList.add(project!!)
                }
            }
        }

        btnViewProjects.setOnClickListener {
            if (projectList.size == 0) {
                Snackbar.make(requireView(), "You have no projects.", Snackbar.LENGTH_LONG).show()
            }
            else {
                val viewProject = ViewProject()
                requireActivity().supportFragmentManager.commit {
                    setCustomAnimations(
                        R.anim.fade_in,
                        R.anim.fade_out
                    )
                    replace(R.id.flNavigation, viewProject)
                    addToBackStack(null)
                }
            }
        }

        val btnAddProject = view.findViewById<CardView>(R.id.btnHomeAddProject)
        btnAddProject.setOnClickListener {
            val projectForm = ProjectForm()
            requireActivity().supportFragmentManager.commit {
                setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out
                )
                replace(R.id.flNavigation, projectForm)
                addToBackStack(null)
            }
        }

        val btnLogout = view.findViewById<CardView>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
        }

        val getTasks = HoursService()
        val tasks = getTasks.getTasks(projectList)

        btnExcel.setOnClickListener(){
           val service = ExportService(requireContext())

            service.createFile(tasks)
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}