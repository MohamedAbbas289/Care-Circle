import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.example.carecircle.R
import com.example.carecircle.databinding.FragmentDocHomeBinding
import com.example.carecircle.model.Doctor
import com.example.carecircle.model.Token
import com.example.carecircle.ui.doctors.main.tabs.list.PatientsListFragment
import com.example.carecircle.ui.doctors.main.tabs.profile.myPatiens.PatientCardAdapter
import com.example.carecircle.ui.patients.main.tabs.home.TopDoctorsAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId

class DocHomeFragment : Fragment() {
    private lateinit var binding: FragmentDocHomeBinding
    private lateinit var adapter: PatientCardAdapter
    private lateinit var doctorsAdapter: TopDoctorsAdapter
    private var firebaseUser: FirebaseUser? = null

    private var doctorList: MutableList<Doctor> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDocHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the adapter here
        adapter = PatientCardAdapter(mutableListOf())
        binding.categoriesList.adapter = adapter
        doctorsAdapter = TopDoctorsAdapter(mutableListOf()) // Initialize with an empty list
        firebaseUser = FirebaseAuth.getInstance().currentUser
        binding.doctorsRecycler.adapter = doctorsAdapter
        underLineText()

        fetchDataFromDatabase()
        showDataFromFireBase()
        initProfileImage()

        fetchAcceptedAppointments()

        binding.seeAllTextView.setOnClickListener {
            navigateToCategoryFragment()
        }
        updateToken(FirebaseInstanceId.getInstance().token)
    }

    private fun fetchAcceptedAppointments() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            val appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments")

            val query = appointmentsRef
                .orderByChild("status")
                .equalTo("accepted")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val acceptedPatients: MutableList<String> = mutableListOf()

                        for (appointmentSnapshot in dataSnapshot.children) {
                            val doctorId =
                                appointmentSnapshot.child("doctorId").getValue(String::class.java)
                            val patientId =
                                appointmentSnapshot.child("patientId").getValue(String::class.java)

                            // Check if the appointment is for the current doctor and has a valid patientId
                            if (doctorId == currentUserId && !patientId.isNullOrBlank()) {
                                acceptedPatients.add(patientId)
                            }
                        }

                        // After fetching accepted patients, retrieve patient details
                        fetchPatientsDetails(acceptedPatients)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle errors
                    }
                })
        }
    }

    private fun fetchPatientsDetails(patientIds: List<String>) {
        val patientsRef = FirebaseDatabase.getInstance().getReference("users")

        patientsRef.orderByChild("userId")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val patientsList: MutableList<User> = mutableListOf()

                    for (patientSnapshot in dataSnapshot.children) {
                        val userId = patientSnapshot.child("userId").getValue(String::class.java)

                        // Check if the patient ID is in the list of accepted patient IDs
                        if (userId != null && userId in patientIds) {
                            // Retrieve patient details
                            val userName =
                                patientSnapshot.child("userName").getValue(String::class.java)
                            val profileImage =
                                patientSnapshot.child("profileImage").getValue(String::class.java)

                            // Check for null values before creating a User object
                            if (userName != null) {
                                // Create a User object and add it to the list
                                val patient = User(
                                    userName = userName,
                                    userId = userId,
                                    profileImage = profileImage
                                )
                                patientsList.add(patient)
                            }
                        }
                    }

                    // Update the RecyclerView with the list of patients
                    adapter.patientsList.clear()
                    adapter.patientsList.addAll(patientsList)
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                }
            })
    }

    private fun initProfileImage() {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("users").child(Firebase.auth.uid!!)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Check if the fragment is added to its activity and the view is not destroyed
                if (!isAdded || activity == null || view == null) {
                    return
                }

                val profileImage = snapshot.child("profileImage").getValue(String::class.java)
                Glide.with(this@DocHomeFragment)
                    .load(profileImage)
                    .placeholder(R.drawable.profile_pic)
                    .into(binding.profileImage)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled
            }
        })
    }

    private fun underLineText() {
        // Your text
        val yourText = binding.seeAllTextView.text.toString()

        // Create a SpannableString with UnderlineSpan
        val spannableString = SpannableString(yourText)
        spannableString.setSpan(
            UnderlineSpan(),
            0,
            yourText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Set the SpannableString to the TextView
        binding.seeAllTextView.text = spannableString
    }

    private fun fetchDataFromDatabase() {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("users")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {


                for (userSnapshot in dataSnapshot.children) {
                    val userType = userSnapshot.child("userType").getValue(String::class.java)

                    // Check if the user type is "Doctor"
                    if (userType == "Doctor") {
                        // Retrieve other relevant data for doctors
                        val name = userSnapshot.child("userName").getValue(String::class.java)
                        val id = userSnapshot.child("userId").getValue(String::class.java)
                        val speciality = userSnapshot.child("category").getValue(String::class.java)
                        val rating = userSnapshot.child("rating").getValue(Float::class.java)
                        val profileImage =
                            userSnapshot.child("profileImage").getValue(String::class.java)

                        // Check for null values before creating a Doctor object
                        if (name != null && id != null && speciality != null && rating != null) {
                            // Create a Doctor object and add it to the list
                            val doctor = Doctor(name, id, speciality, rating, profileImage)
                            doctorList.add(doctor)
                        } else {
                            // Log a message or handle the case when data is missing
                            Log.e("HomeFragment", "Doctor data is null or incomplete")
                        }
                    }
                }
                val sortedDoctorList = doctorList.sortedByDescending { it.ratting }

                // Update the data in the adapter
                doctorsAdapter.doctorsList.clear()
                doctorsAdapter.doctorsList.addAll(sortedDoctorList)
                doctorsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                Log.e(
                    "HomeFragment",
                    "Error fetching data from the database: ${databaseError.message}"
                )
            }
        })
    }

    private fun showDataFromFireBase() {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("users").child((Firebase.auth.uid!!))

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("userName").getValue(String::class.java)
                binding.userName.text = name
            }

            override fun onCancelled(error: DatabaseError) {
                // show error
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun navigateToCategoryFragment() {
        val anotherFragment = PatientsListFragment()

        // Get the FragmentManager
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager

        // Begin the fragment transaction
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()

        // Replace the current Fragment with the new Fragment
        transaction.replace(R.id.fragment_container, anotherFragment)

        transaction.addToBackStack(null)
        // Commit the transaction
        transaction.commit()
    }

    private fun updateToken(token: String?) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)
    }


}
