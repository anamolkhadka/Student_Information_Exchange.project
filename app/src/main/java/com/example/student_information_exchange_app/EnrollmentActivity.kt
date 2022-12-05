package com.example.student_information_exchange_app

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.student_information_exchange_app.adapters.TutoringEnrollAdapter
import com.example.student_information_exchange_app.adapters.TutoringSessionAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class EnrollmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enrollment)

        configureTopBar()
        configureViewPager()
    }
    //Sets up top bar with a name and back arrow
    private fun configureTopBar(){
        val display=supportActionBar
        display?.title="Manage Enrollments"
        display?.setDisplayHomeAsUpEnabled(true)
    }
    //Goes back to previous page when back arrow is pressed
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    //Sets up the viewpager and its elements
    private fun configureViewPager(){
        val tabLayout: TabLayout =findViewById(R.id.enroll_tabs)
        val viewPager: ViewPager2 =findViewById(R.id.et_pager)

        val adapter= TutoringEnrollAdapter(supportFragmentManager,lifecycle)
        viewPager.adapter=adapter
        TabLayoutMediator(tabLayout,viewPager){tab,position->
            when(position){
                0->{
                    tab.text="Join"
                }
                1->{
                    tab.text="Remove"
                }
            }
        }.attach()
    }
}
