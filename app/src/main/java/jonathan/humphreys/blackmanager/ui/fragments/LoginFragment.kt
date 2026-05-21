package jonathan.humphreys.blackmanager.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.Toolbar
import jonathan.humphreys.blackmanager.ui.activities.MainActivity
import jonathan.humphreys.blackmanager.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

class LoginFragment : Fragment() {

    private lateinit var etIntegrationToken: EditText
    private lateinit var btnLogin: Button

    private val notionApiUrl = "https://api.notion.com/v1/users/me"
    private val notionVersion = "2022-06-28"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etIntegrationToken = view.findViewById(R.id.etIntegrationToken)
        btnLogin = view.findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val token = etIntegrationToken.text.toString().trim()

            if (token.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your Integration Token", Toast.LENGTH_SHORT).show()
            } else {
                validateNotionToken(token)
            }
        }
    }

    private fun validateNotionToken(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(notionApiUrl)
                    .addHeader("Authorization", "Bearer $token")
                    .addHeader("Notion-Version", notionVersion)
                    .build()

                val response = client.newCall(request).execute()
                val isSuccessful = response.isSuccessful
                response.close()

                launch(Dispatchers.Main) {
                    if (isSuccessful) {
                        Toast.makeText(requireContext(), "Token is valid! Logging in...", Toast.LENGTH_SHORT).show()

                        // store notion_token:
                        requireContext()
                            .getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                            .edit()
                            .putString("notion_token", token)
                            .apply()


                        // return to main activity
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    } else {
                        Toast.makeText(requireContext(), "Invalid token. Please check and try again.", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error verifying token: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
