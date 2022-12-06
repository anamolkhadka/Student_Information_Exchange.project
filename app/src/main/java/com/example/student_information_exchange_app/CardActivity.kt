package com.example.student_information_exchange_app

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.exp

class CardActivity : AppCompatActivity(){
    private var db = Firebase.firestore
    private val collectionRef = db.collection("transaction_mediums")
    private lateinit var itemArrayList: ArrayList<CardData>
    private lateinit var indirList:ArrayList<Int>
    private val user = Firebase.auth.currentUser
    private val email = user?.email.toString()
    private lateinit var provider: String
    private lateinit var type: String
    private lateinit var num: String
    private lateinit var csv: String
    private lateinit var expiry: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        title="Add or Remove Cards"
        itemArrayList = arrayListOf()
        indirList= arrayListOf()
        configureSubmitButton()
    }
    //Sets up the submit button with firebase
    private fun configureSubmitButton(){
        val submit:Button=findViewById(R.id.ac_submit)
        submit.setOnClickListener {
            provider=findViewById<Spinner>(R.id.ac_provider).selectedItem.toString()
            type=findViewById<Spinner>(R.id.ac_type).selectedItem.toString()
            num=findViewById<TextView>(R.id.ac_num_text).text.toString()
            csv=findViewById<TextView>(R.id.ac_csv_text).text.toString()
            expiry=findViewById<TextView>(R.id.ac_expiry).text.toString()

            val regex="""(0[1-9]|1[0-2])/([0-9]{2})""".toRegex()
            val cn1=num.length!=16
            val cn2=csv.length!=3
            val cn3=!regex.matches(expiry)
            val incomplete=cn1||cn2||cn3
            if (incomplete){
                if(cn1){
                    Toast.makeText(applicationContext,"Please provide a valid 16-digit card number", Toast.LENGTH_LONG).show()
                }else if(cn2){
                    Toast.makeText(applicationContext,"Please provide a valid 3-digit csv number", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(applicationContext,"Please provide a valid date (mm/yy)", Toast.LENGTH_LONG).show()
                }
            }else{
                val user = Firebase.auth.currentUser
                uploadItem(user?.email.toString(),provider,type,num,csv,expiry)
                findViewById<TextView>(R.id.ac_num_text).text = ""
                findViewById<TextView>(R.id.ac_csv_text).text = ""
                findViewById<TextView>(R.id.ac_expiry).text = ""
            }
        }
    }
    private fun uploadItem(HOST:String,PROVIDER:String,TYPE:String,NUM:String,CSV:String,EXPIRY:String){
        val item = hashMapOf(
            "Host" to HOST,
            "Provider" to PROVIDER,
            "Type" to TYPE,
            "Num" to NUM,
            "Csv" to CSV,
            "Expiry" to EXPIRY
        )
        val formatter = SimpleDateFormat("yyyy_mm_dd_hh_mm_ss", Locale.getDefault())
        val currentTime = Date()
        val documentName = formatter.format(currentTime)

        val documentRef = collectionRef.document(documentName)
        documentRef.set(item)
            .addOnSuccessListener {
                Log.d("Document upload",":Success")

            }.addOnFailureListener{ e ->
                e.message?.let { Log.d("Document upload failed", it) }
            }
        Toast.makeText(applicationContext,"Card added", Toast.LENGTH_LONG).show()
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun eventChangeListener(v: View){
        val collectionRef = db.collection("transaction_mediums")
        collectionRef.addSnapshotListener{value,e ->

            if(e != null){
                Log.d("FireStore error",e.message.toString())
                return@addSnapshotListener

            }
            for(doc in value!!) {
                Log.d("Document","fetch succeed")
                Log.d("document",doc.toString())
                itemArrayList.add(doc.toObject<CardData>())
                Log.d("myItemList",itemArrayList.toString())
            }
            setUp()
        }
    }
    private fun setUp(){
        val group:RadioGroup=findViewById(R.id.rc_group)
        val remove:Button=findViewById(R.id.rc_remove)

        if(itemArrayList.size==0){
            Toast.makeText(applicationContext,"No sessions exist", Toast.LENGTH_LONG).show()
            remove.isEnabled=false
        }else{
            remove.isEnabled=true
            var index = 0
            var count = 0
            for (item in itemArrayList) {
                if(item.Host.equals(email)) {
                    val radioButton = RadioButton(this)
                    radioButton.text = item.Type
                    radioButton.id = count
                    count++
                    group.addView(radioButton)
                    indirList.add(index)
                }
                index++
            }
        }
    }
}