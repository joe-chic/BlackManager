package jonathan.humphreys.blackmanager.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import jonathan.humphreys.blackmanager.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Asignar la Toolbar como ActionBar (si quieres que muestre título o Up)
        val toolbar: MaterialToolbar = findViewById(R.id.top_toolbar)
        setSupportActionBar(toolbar)

        // 2. Obtener el NavController desde tu NavHostFragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                    as NavHostFragment
        val navController = navHostFragment.navController

        // 3) Define tus top‑level destinations
        val appBarConfig = AppBarConfiguration(
            setOf(
                R.id.nav_tasks,
                R.id.nav_settings
            )
        )

        // 3. Hacer que la Toolbar sincronice su título y Up con el NavController
        setupActionBarWithNavController(navController, appBarConfig)

        // 4. Conectar el BottomNavigationView con el NavController
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNav.setupWithNavController(navController)
    }

    // Opcional: manejar el botón “Up” de la Toolbar
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}