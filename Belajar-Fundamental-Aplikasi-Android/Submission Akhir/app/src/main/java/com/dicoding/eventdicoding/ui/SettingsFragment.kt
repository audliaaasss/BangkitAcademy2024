package com.dicoding.eventdicoding.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.dicoding.eventdicoding.R
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsFragment : Fragment() {

    private lateinit var settingViewModel: SettingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        val switchTheme = view.findViewById<SwitchMaterial>(R.id.switch_theme)

        // Setup DataStore and ViewModel
        val pref = SettingPreferences.getInstance(requireContext().applicationContext.dataStore)
        settingViewModel = ViewModelProvider(this, SettingViewModelFactory(pref))[SettingViewModel::class.java]

        // Observe theme settings
        settingViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            switchTheme.isChecked = isDarkModeActive
        }

        // Save theme settings on switch toggle
        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            settingViewModel.saveThemeSetting(isChecked)
        }

        return view
    }
}
