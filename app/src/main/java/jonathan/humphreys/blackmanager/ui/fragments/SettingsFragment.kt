package jonathan.humphreys.blackmanager.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import jonathan.humphreys.blackmanager.R
import jonathan.humphreys.blackmanager.data.database.AppDatabase
import jonathan.humphreys.blackmanager.data.entity.Users
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsFragment : Fragment() {

    private lateinit var actvUsername: AutoCompleteTextView
    private lateinit var btnGuardarUser: Button
    private lateinit var btnIniciar: Button
    private lateinit var btnUnlink: Button
    private lateinit var prefs: SharedPreferences

    private val prefsName = "app_prefs"
    private val tokenKey = "notion_token"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        actvUsername = view.findViewById(R.id.actvUsername)
        btnGuardarUser = view.findViewById(R.id.btnGuardarUsuario)
        btnIniciar = view.findViewById(R.id.btnIniciarSesion)
        btnUnlink = view.findViewById(R.id.btnEliminarVinculacion)

        actvUsername.setHintTextColor(ColorStateList.valueOf(Color.WHITE))
        actvUsername.setTextColor(Color.WHITE)

        val db = AppDatabase.getInstance(requireContext())
        val userDao = db.userDao()

        lifecycleScope.launch {
            // --- 1) Ensure we have a current user in prefs ---
            var currentId = prefs.getInt("currentUserId", 0)
            var currentName = prefs.getString("currentUsername", null)

            if (currentId == 0 || currentName.isNullOrBlank()) {
                val defaultName = "Usuario de Android"
                val existing = withContext(Dispatchers.IO) {
                    userDao.getByUsername(defaultName)
                }
                val defaultId = existing?.idUser ?: withContext(Dispatchers.IO) {
                    userDao.insert(Users(0, defaultName)).toInt()
                }

                prefs.edit()
                    .putInt("currentUserId", defaultId)
                    .putString("currentUsername", defaultName)
                    .apply()

                currentId = defaultId
                currentName = defaultName
            }

            // --- 2) Load all usernames into dropdown ---
            val allNames = withContext(Dispatchers.IO) {
                userDao.getAllUsernames()
            }
            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.dropdown_item_white,
                allNames
            )
            actvUsername.setAdapter(adapter)

            // --- 3) Show currentUsername in the selector ---
            actvUsername.setText(currentName, false)

            // ---- Save Username button ----
            btnGuardarUser.setOnClickListener {
                val entered = actvUsername.text.toString().trim()
                if (entered.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Por favor ingresa o selecciona un usuario",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                lifecycleScope.launch {
                    val existing = withContext(Dispatchers.IO) {
                        userDao.getByUsername(entered)
                    }
                    val userId = existing?.idUser ?: withContext(Dispatchers.IO) {
                        userDao.insert(Users(0, entered)).toInt()
                    }

                    prefs.edit()
                        .putInt("currentUserId", userId)
                        .putString("currentUsername", entered)
                        .apply()

                    actvUsername.setText(entered, false)
                    Toast.makeText(
                        requireContext(),
                        if (existing == null)
                            "Usuario \"$entered\" creado"
                        else
                            "Bienvenido de nuevo, $entered",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            // ---- Notion Token flow ----
            updateTokenButtons()
        }

        btnIniciar.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_loginFragment)
        }

        btnUnlink.setOnClickListener {
            prefs.edit().remove(tokenKey).apply()
            Toast.makeText(requireContext(), "Vinculación eliminada", Toast.LENGTH_SHORT).show()
            updateTokenButtons()
        }
    }

    private fun updateTokenButtons() {
        val hasToken = prefs.contains(tokenKey)
        btnIniciar.visibility = if (hasToken) View.GONE else View.VISIBLE
        btnUnlink.visibility = if (hasToken) View.VISIBLE else View.GONE
    }
}