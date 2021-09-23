package vorotilenko.trendanalyzer.fragments.select.currency.second

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import vorotilenko.trendanalyzer.viewmodel.AppViewModel
import vorotilenko.trendanalyzer.Currencies
import vorotilenko.trendanalyzer.R
import vorotilenko.trendanalyzer.fragments.select.SearchBarWatcher
import vorotilenko.trendanalyzer.fragments.select.currency.CurrenciesAdapter

/**
 * Fragment for selecting the second currency from the list.
 */
class SelectSecondCurrencyFragment : Fragment() {

    /**
     * View model of the application.
     */
    private val appViewModel: AppViewModel by activityViewModels()

    /**
     * [View model factory][ViewModelProvider.Factory] for the fragment's view model.
     */
    private lateinit var vmFactory: SecondCurrencyVMFactory

    /**
     * Fragment's view model.
     */
    private val viewModel: SecondCurrencyViewModel by viewModels { vmFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appViewModel.symbolToAdd?.let {
            vmFactory = SecondCurrencyVMFactory(it, appViewModel.observedSymbols)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_select_second_currency, container, false)

    /**
     * Sets adapter to the RecyclerView.
     * @return Created adapter.
     */
    private fun setRVAdapter(view: View, navController: NavController): CurrenciesAdapter {
        val appContext = appViewModel.getApplication<Application>().applicationContext
        val adapter = CurrenciesAdapter(appContext, viewModel.shownCurrencies) { currency, _ ->
            appViewModel.symbolToAdd?.let {
                it.symbolName += " \\ ${currency.name}"
                it.symbolTicker += Currencies.getTicker(currency.name)
                appViewModel.addSymbol(it)
            }
            val action = SelectSecondCurrencyFragmentDirections.actionBackToChart()
            navController.navigate(action)
        }
        view.findViewById<RecyclerView>(R.id.rvCurrenciesList).adapter = adapter
        return adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val adapter = setRVAdapter(view, navController)
        view.findViewById<EditText>(R.id.etCurrencyName).apply {
            val textChangedListener =
                SearchBarWatcher(viewModel.shownCurrencies, viewModel.availableCurrencies, adapter)
            addTextChangedListener(textChangedListener)
        }
        view.findViewById<MaterialToolbar>(R.id.select_currency_toolbar).apply {
            setupWithNavController(navController)
        }
    }
}