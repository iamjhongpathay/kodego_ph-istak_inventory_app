package com.kodego.app.istak_inventory.fragments.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kodego.app.istak_inventory.ItemAdapter
import com.kodego.app.istak_inventory.R
import com.kodego.app.istak_inventory.databinding.FragmentItemListBinding
import com.kodego.app.istak_inventory.db.IstakDatabase
import com.kodego.app.istak_inventory.db.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ItemListFragment : Fragment(), SearchView.OnQueryTextListener {

    lateinit var binding: FragmentItemListBinding
    lateinit var istakDB : IstakDatabase
    lateinit var itemAdapter : ItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentItemListBinding.inflate(layoutInflater)

        istakDB = IstakDatabase.invoke(requireContext())
        viewItem()

        binding.btnAddItem.setOnClickListener(){
            findNavController().navigate(R.id.action_itemListFragment_to_addItemFragment)
        }
        //swipe down to refresh the recyclerview
        val swipeRefresh : SwipeRefreshLayout = binding.swipeRefresh
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            viewItem()
            itemAdapter.notifyDataSetChanged()
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.search_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                if(menuItem.itemId == R.id.menu_search){

                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.search_menu, menu)
//
//        val search = menu?.findItem(R.id.menu_search)
//        val searchView = search?.actionView as? SearchView
//        searchView?.isSubmitButtonEnabled = true
//        searchView?.setOnQueryTextListener(this)
//    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query != null){
            searchItem(query)
        }
        return true
    }

    override fun onQueryTextChange(query : String?): Boolean {
        if(query != null){
            searchItem(query)
        }
        return true
    }

    private fun searchItem(query: String?){
        val searchQuery = "%$query%"
        lateinit var item: MutableList<Item>
        GlobalScope.launch(Dispatchers.IO) {
            item = istakDB.getItems().searchItemName(searchQuery)
            withContext(Dispatchers.Main){
                itemAdapter.setData(item)
            }
        }

    }


    private fun viewItem(){
        lateinit var item: MutableList<Item>
        GlobalScope.launch(Dispatchers.IO) {
            item = istakDB.getItems().getAllItems()

            withContext(Dispatchers.Main){
                itemAdapter = ItemAdapter(item)

                binding.recyclerViewProductList.adapter = itemAdapter
                binding.recyclerViewProductList.layoutManager = LinearLayoutManager(requireContext())
                itemAdapter.setData(item)

                if(item.isNotEmpty()){
                    binding.tvInventoryIsEmpty.visibility = View.GONE
                }else{
                    binding.recyclerViewProductList.visibility = View.GONE
                    binding.tvInventoryIsEmpty.visibility = View.VISIBLE
                }
                showDeleteDialog()
            }
        }
    }

    private fun deleteItem(item: Item){
        GlobalScope.launch(Dispatchers.IO) {
            istakDB.getItems().deleteItem(item.id)
            viewItem()
        }
    }

    private  fun showDeleteDialog(){
        itemAdapter.onItemDelete = { item: Item, position: Int ->
            AlertDialog.Builder(requireContext())
                .setTitle("Delete!")
                .setMessage("Are you sure you want to delete this item? \n'${item.name}'")
                .setPositiveButton("Delete"){ dialog, item2 ->
                    try {
                        deleteItem(item)
                        itemAdapter.itemModel.removeAt(position)
                        itemAdapter.notifyDataSetChanged()
                        Toast.makeText(requireContext(), "Successful removed: ${item.name}", Toast.LENGTH_SHORT).show()

                    }catch (e: Exception){
                        Toast.makeText(requireContext(), "$e", Toast.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton("Cancel"){dialog, item ->
                }.show()
        }
    }


}