package com.kodego.app.istak_inventory.fragments.add

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.kodego.app.istak_inventory.R
import com.kodego.app.istak_inventory.converters.Converters
import com.kodego.app.istak_inventory.databinding.FragmentAddItemBinding
import com.kodego.app.istak_inventory.db.IstakDatabase
import com.kodego.app.istak_inventory.db.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddItemFragment : Fragment() {
    lateinit var binding: FragmentAddItemBinding
    lateinit var istakDB : IstakDatabase
    lateinit var capturedImage : Bitmap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddItemBinding.inflate(layoutInflater)

        istakDB = IstakDatabase.invoke(requireContext())
        //initialize the capturedBitmap : Bitmap if the item is without photo
        capturedImage = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565)

        binding.btnSave.setOnClickListener(){
            //checked if one or all of the fields is empty return false
            if(binding.etItemName.text?.isEmpty() == true || binding.etDescription.text?.isEmpty() == true
                || binding.etStock.text?.isEmpty() == true ||capturedImage == null){
                Toast.makeText(requireContext(), "Fill out all fields!", Toast.LENGTH_SHORT).show()
            }else{
                try {
                    var itemName: String = binding.etItemName.text.toString()
                    var description: String = binding.etDescription.text.toString()
                    var stocks: Int = binding.etStock.text.toString().toInt()
                    var imageItem: Bitmap = capturedImage
                    val item = Item(itemName, description, stocks, imageItem)
                    //save to database
                    save(item)

                    Toast.makeText(requireContext(), "Successfully saved!", Toast.LENGTH_SHORT).show()
                    //return to ItemListFragment
                    Navigation.findNavController(requireView()).navigateUp()

                }catch (e: Exception){
                    Toast.makeText(requireContext(), "$e", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.btnCancel.setOnClickListener(){
            Navigation.findNavController(requireView()).navigateUp()
        }

        binding.btnAddPhoto.setOnClickListener(){
            showImageSourceDialog()
        }

        return binding.root
    }

    private fun showImageSourceDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Select Image Source")
            .setPositiveButton("Camera"){ dialog, item ->
                showCamera()
            }.show()
//            .setNegativeButton("Gallery"){ dialog, item ->
//                showGallery()
//            }.show()
    }

    //show the gallery app also the permission to use the gallery
    private fun showGallery() {
        Dexter.withContext(requireContext()).withPermission(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object: PermissionListener{
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                //if the user granted the permission to open gallery app...
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                //...the gallery app will launch
                galleryLauncher.launch(galleryIntent)
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(requireContext(), "Camera Permission Denied!", Toast.LENGTH_SHORT).show()
                goToSettingsDialog()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?,
                p1: PermissionToken?
            ) {
                p1?.continuePermissionRequest()
            }

        }).onSameThread().check()
    }

    //show the camera app also the permission to use the camera
    private fun showCamera() {
        Dexter.withContext(requireContext()).withPermission(
            android.Manifest.permission.CAMERA
        ).withListener(object: PermissionListener{
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                //if the user granted the permission to use camera app...
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                //...the camera app will launched
                cameraLauncher.launch(cameraIntent)
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(requireContext(), "Camera Permission Denied!", Toast.LENGTH_SHORT).show()
                goToSettingsDialog()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?,
                p1: PermissionToken?
            ) {
                p1?.continuePermissionRequest()
            }

        }).onSameThread().check()
    }

    private fun save(item: Item){
        GlobalScope.launch(Dispatchers.IO) {
            istakDB.getItems().addItem(item)
        }
    }

    //dialog phone settings show up if user denied the permission of gallery or camera
    private fun goToSettingsDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Go To Settings")
            .setMessage("It seems your permission has been denied. Go to settings to enable the camera permission.")
            .setPositiveButton("Go to Settings"){ _, _ ->

                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                var uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel"){ dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    //the camera app will launched
    val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            result.data?.extras.let{
                capturedImage = result.data?.extras?.get("data") as Bitmap
                binding.ivAddProductPhoto.setImageBitmap(capturedImage)
            }
        }
    }
    //the gallery app will launched
    val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            result.data?.let{
//                 capturedImage = result.data?.data as Uri
//                capturedImage = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selectedImage)
//                binding.ivAddProductPhoto.setImageURI(capturedImage)
            }
        }
    }
}