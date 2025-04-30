package asset.ledger.asset_ledger_android

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import asset.ledger.asset_ledger_android.fragment.MainActivityAssetFragment
import asset.ledger.asset_ledger_android.fragment.MainActivityLedgerFragment
import asset.ledger.asset_ledger_android.fragment.MainActivitySettingFragment
import asset.ledger.asset_ledger_android.fragment.MainActivityStatisticFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.activity_main_bottom_navigation)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001 // requestCode
                )
            }
        }

        // 바텀 네비게이션 아이템 선택 리스너 설정
        bottomNavigation.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment = when (item.itemId) {
                R.id.bottom_navigation_menu_ledger -> MainActivityLedgerFragment()
                R.id.bottom_navigation_menu_statistic -> MainActivityStatisticFragment()
                R.id.bottom_navigation_menu_asset -> MainActivityAssetFragment()
                R.id.bottom_navigation_menu_setting -> MainActivitySettingFragment()
                else -> MainActivityLedgerFragment()
            }

            // 프래그먼트 전환
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_activity_fragment_container, selectedFragment)
                .commit()

            true
        }

        // 첫 화면 설정 (기본값: HomeFragment)
        if (savedInstanceState == null) {
            bottomNavigation.selectedItemId = R.id.bottom_navigation_menu_ledger
        }
    }

}