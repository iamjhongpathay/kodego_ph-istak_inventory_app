package com.kodego.app.istak_inventory.fragments.update

import android.app.Activity
import android.content.ClipData.Item
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavArgs
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.kodego.app.istak_inventory.R
import com.kodego.app.istak_inventory.databinding.FragmentUpdateItemBinding
import com.kodego.app.istak_inventory.db.IstakDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UpdateItemFragment : Fragment() {

    private val args by navArgs<UpdateItemFragmentArgs>()
    lateinit var binding : FragmentUpdateItemBinding
    lateinit var istakDB : IstakDatabase
    lateinit var capturedImage : Bitmap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUpdateItemBinding.inflate(layoutInflater)

        istakDB = IstakDatabase.invoke(requireContext())
        //initialize the capturedBitmap : Bitmap if the item is without photo
        capturedImage = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565)

        //passed the data from clicked item in recyclerview into Update fragments components
        binding.etUpdateItemName.setText(args.currentItem.name)
        binding.etUpdateDescription.setText(args.currentItem.description)
        binding.etUpdateStock.setText(args.currentItem.stock.toString())
        binding.ivUpdateProductPhoto.setImageBitmap(args.currentItem.imageItem)

        binding.btnUpdate.setOnClickListener(){
            try{
                if(binding.etUpdateItemName.text?.isEmpty() == true || binding.etUpdateDescription.text?.isEmpty() == true || binding.etUpdateStock.text?.isEmpty() == true){
                    Toast.makeText(requireContext(), "Fill out all fields!", Toast.LENGTH_SHORT).show()

                }else {
                    var itemName: String = binding.etUpdateItemName.text.toString()
                    var description: String = binding.etUpdateDescription.text.toString()
                    var stock: Int = binding.etUpdateStock.text.toString().toInt()
                    var imageItem: Bitmap = capturedImage
                    //The data will updated
                    GlobalScope.launch(Dispatchers.IO) {
                        istakDB.getItems().updateItem(args.currentItem.id, itemName, description, stock, imageItem)
                    }
                    Toast.makeText(requireContext(), "Successfully Updated!", Toast.LENGTH_SHORT).show()

                    //return to ItemListFragment
                    Navigation.findNavController(requireView()).navigateUp()
                }
            }catch (e: Exception){
                Toast.makeText(requireContext(), "$e", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnUpdateDiscard.setOnClickListener(){
            showDiscardDialog()
        }

        binding.btnUpdateAddPhoto.setOnClickListener(){
            showImageSourceDialog()
        }

        //Add delete menu on app bar
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.delete_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                if(menuItem.itemId == R.id.btnDeleteIcon){
                    deleteItem()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    private fun showImageSourceDialog() {
        android.app.AlertDialog.Builder(requireContext())
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
        ).withListener(object: PermissionListener {
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

    private fun goToSettingsDialog() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Go To Settings")
            .setMessage("It seems your permission has been denied. Go to settings to enable the camera permission.")
            .setPositiveButton("Go to Settings"){ _, _ ->

                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                var uri = Uri.fromParts("package",requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel"){ dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun showDiscardDialog(){
        AlertDialog.Builder(requireContext())
            .setTitle("Discard!")
            .setMessage("Discard changes?")
            .setPositiveButton("Discard"){ dialog, item2 ->
                Navigation.findNavController(requireView()).navigateUp()
            }
            .setNegativeButton("Cancel"){dialog, item ->
            }.show()
    }

    private fun deleteItem() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete!")
            .setMessage("Are you sure you want to delete this item? \n'${args.currentItem.name}'")
            .setPositiveButton("Delete"){ dialog, item2 ->
                try {
                    GlobalScope.launch(Dispatchers.IO) {
                        istakDB.getItems().deleteItem(args.currentItem.id)
                    }
                    Toast.makeText(requireContext(), "Successfully removed: ${args.currentItem.name}", Toast.LENGTH_SHORT).show()
                    //return to ItemListFragment
                    Navigation.findNavController(requireView()).navigateUp()

                }catch (e: Exception){
                    Toast.makeText(requireContext(), "$e", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Cancel"){dialog, item ->
            }.show()
    }

    //the camera app will launched
    val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            result.data?.extras.let{
                capturedImage = result.data?.extras?.get("data") as Bitmap
                binding.ivUpdateProductPhoto.setImageBitmap(capturedImage)
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
