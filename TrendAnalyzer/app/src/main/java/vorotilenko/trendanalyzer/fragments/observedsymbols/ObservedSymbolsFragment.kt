package vorotilenko.trendanalyzer.fragments.observedsymbols

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import vorotilenko.trendanalyzer.viewmodel.AppViewModel
import vorotilenko.trendanalyzer.ObservedSymbol
import vorotilenko.trendanalyzer.R

/**
 * Displays the list of symbols observed by user.
 */
class ObservedSymbolsFragment : Fragment() {

    /**
     * Adapter for the RecyclerView.
     */
    private lateinit var adapter: ObservedAdapter

    /**
     * View model of the application.
     */
    private val appViewModel: AppViewModel by activityViewModels()

    /**
     * [View model factory][ViewModelProvider.Factory] for the fragment's view model.
     */
    private lateinit var vmFactory: ObservedSymbolsVMFactory

    /**
     * View model of this fragment.
     */
    private val viewModel: ObservedSymbolsViewModel by viewModels { vmFactory }

    /**
     * Visibility of TextView with text "No symbols are currently observed" on
     * [ObservedSymbolsFragment].
     */
    private val tvNothingObservedVisibility
        get() = if (viewModel.observedSymbols.isEmpty()) View.VISIBLE
        else View.INVISIBLE

    /**
     * Visibility of RecyclerView with symbols list on [ObservedSymbolsFragment].
     */
    private val rvVisibility
        get() = if (viewModel.observedSymbols.isNotEmpty()) View.VISIBLE
        else View.INVISIBLE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vmFactory = ObservedSymbolsVMFactory(appViewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_observed_symbols, container, false)

    /**
     * Called from [adapter] when the item was deleted by user.
     */
    private fun handleItemDeleted(position: Int, item: ObservedSymbol?) {
        if (viewModel.observedSymbols.isEmpty()) {
            view?.findViewById<RecyclerView>(R.id.rvObservedSymbols)?.visibility = View.INVISIBLE
            view?.findViewById<TextView>(R.id.tvNothingObserved)?.visibility = View.VISIBLE
        }
    }

    /**
     * Called from [adapter] when the item was returned by user.
     */
    private fun handleItemReturned(position: Int, item: ObservedSymbol?) {
        if (viewModel.observedSymbols.size == 1) {
            view?.findViewById<RecyclerView>(R.id.rvObservedSymbols)?.visibility = View.VISIBLE
            view?.findViewById<TextView>(R.id.tvNothingObserved)?.visibility = View.INVISIBLE
        }
    }

    /**
     * Sets up recycler view with the list of observed symbols.
     */
    private fun setupRecyclerView(fragmentView: View) {
        adapter = ObservedAdapter(
            fragmentView.context,
            viewModel,
            ::handleItemDeleted,
            ::handleItemReturned
        )
        val rvObservedSymbols =
            fragmentView.findViewById<RecyclerView>(R.id.rvObservedSymbols).also {
                it.visibility = rvVisibility
                it.adapter = adapter
            }
        ItemTouchHelper(SwipeToDeleteCallback(adapter)).attachToRecyclerView(rvObservedSymbols)
    }

    /**
     * Sets up toolbar.
     */
    private fun setupToolbar(fragmentView: View) {
        val navController = findNavController()
        fragmentView.findViewById<MaterialToolbar>(R.id.observed_toolbar).apply {
            setupWithNavController(navController)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_add_symbol -> {
                        appViewModel.symbolToAdd = ObservedSymbol()
                        val action = ObservedSymbolsFragmentDirections.actionToSelectExchange()
                        navController.navigate(action)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView(view)
        view.findViewById<TextView>(R.id.tvNothingObserved).visibility = tvNothingObservedVisibility
        setupToolbar(view)
    }
}