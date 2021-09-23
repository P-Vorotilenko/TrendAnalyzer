package vorotilenko.trendanalyzer.fragments.select.exchange

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import vorotilenko.trendanalyzer.viewmodel.AppViewModel
import vorotilenko.trendanalyzer.R
import vorotilenko.trendanalyzer.fragments.select.SearchBarWatcher

/**
 * Fragment for selecting an exchange from the list.
 */
class SelectExchangeFragment : Fragment() {

    /**
     * View model of the application.
     */
    private val appViewModel: AppViewModel by activityViewModels()

    /**
     * [View model factory][ViewModelProvider.Factory] for the fragment's view model.
     */
    private lateinit var vmFactory: SelectExchangeVMFactory

    /**
     * View model of this fragment.
     */
    private val viewModel: SelectExchangeViewModel by viewModels { vmFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appViewModel.symbolToAdd?.let {
            vmFactory = SelectExchangeVMFactory(appViewModel.observedSymbols)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_select_exchange, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appContext = appViewModel.getApplication<Application>().applicationContext
        val shownExchanges = viewModel.shownExchanges
        val navController = findNavController()
        val exchangesAdapter = ExchangesAdapter(appContext, shownExchanges) { exchange, _ ->
            appViewModel.symbolToAdd?.exchangeName = exchange.name
            val action = SelectExchangeFragmentDirections.actionToSelectFirstCurrency()
            navController.navigate(action)
        }

        view.findViewById<RecyclerView>(R.id.rvExchangesList).apply { adapter = exchangesAdapter }
        view.findViewById<EditText>(R.id.etExchangeName).apply {
            val watcher =
                SearchBarWatcher(shownExchanges, viewModel.availableExchanges, exchangesAdapter)
            addTextChangedListener(watcher)
        }
        view.findViewById<MaterialToolbar>(R.id.select_exchange_toolbar).apply {
            setupWithNavController(navController)
        }
    }
}