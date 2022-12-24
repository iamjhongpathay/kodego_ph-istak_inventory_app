package com.kodego.app.istak_inventory.fragments.display

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.kodego.app.istak_inventory.R
import com.kodego.app.istak_inventory.databinding.FragmentViewItemBinding

class ViewItemFragment : Fragment() {

    private val args by navArgs<ViewItemFragmentArgs>()
    lateinit var binding : FragmentViewItemBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentViewItemBinding.inflate(layoutInflater)

        binding.tvViewItemName.text = args.currentItem.name
        binding.tvViewItemStock.text = args.currentItem.stock.toString()
        binding.tvViewItemDescription.text = args.currentItem.description
        binding.ivViewItemPhoto.setImageBitmap(args.currentItem.imageItem)

        return binding.root
    }
}