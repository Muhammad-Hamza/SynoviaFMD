package com.scanapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.scanapp.R
import com.scanapp.databinding.ActivityFixBinding
import com.scanapp.util.launchActivity

class FixActivity : AppCompatActivity()
{
    private lateinit var binding:ActivityFixBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fix)

        binding.destroyPack.setOnClickListener{
            launchActivity<SupplyProductActivity> {
                putExtra("DP","Destroy Pack")
            }

        }
        binding.llStolenPack.setOnClickListener{
            launchActivity<SupplyProductActivity> {
                putExtra("FROM_FIX","YES")

                putExtra("State","Stolen")
            }}

        binding.llReactivate.setOnClickListener{
            launchActivity<SupplyProductActivity> {
                putExtra("FROM_FIX","YES")

                putExtra("State","Active")
            }}
        binding.llReactivatePack.setOnClickListener{
            launchActivity<SupplyProductActivity> {
                putExtra("FROM_FIX","YES")

                putExtra("State","Sample")
            }}
        binding.llLockPack.setOnClickListener{
            launchActivity<SupplyProductActivity> {
                putExtra("FROM_FIX","YES")
                putExtra("State","Locked")
            }}
        binding.llUnlockPack.setOnClickListener{
            launchActivity<SupplyProductActivity> {
                putExtra("FROM_FIX","YES")
                putExtra("State","EXPORTED")
            }}
        binding.destroyPack.setOnClickListener{
            launchActivity<SupplyProductActivity> {
                putExtra("FROM_FIX","YES")
                putExtra("State","Destroyed")
            }}
    }
}

