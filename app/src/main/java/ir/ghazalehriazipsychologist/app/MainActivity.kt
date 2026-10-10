package ir.ghazalehriazipsychologist.app

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * خانه اپلیکیشن — برخلاف نسخه قبلی، این صفحه دیگر خودِ سایت را عیناً بارگذاری نمی‌کند؛
 * یک صفحه کاملاً بومی اندروید است (بدون نیاز به اینترنت برای نمایش خودش) با منوی
 * آیکونی و نوار پایین که هرکدام کاربر را به صفحه مربوطه از سایت زنده (WebViewActivity) می‌برند.
 *
 * محتوای هر صفحه (مقاله، تست، خدمات و...) همچنان مستقیم از سایت خوانده می‌شود، پس با
 * آپدیت سایت خودش را به‌روز نگه می‌دارد؛ فقط چیدمان/منو/آیکون‌ها بومی و اختصاصی اپ هستند.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val menuGrid = findViewById<RecyclerView>(R.id.menuGrid)
        menuGrid.layoutManager = GridLayoutManager(this, 3)
        menuGrid.adapter = MenuGridAdapter(AppMenu.homeGrid) { item ->
            openContent(item.title, item.path)
        }

        findViewById<TextView>(R.id.adminLoginLink).setOnClickListener {
            openContent("ورود مدیر", "login.php")
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> true
                R.id.nav_tests -> { openContent("تست‌های روان‌شناسی", "tests.php"); false }
                R.id.nav_booking -> { openContent("رزرو نوبت", "booking.php"); false }
                R.id.nav_articles -> { openContent("مقالات", "articles.php"); false }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // وقتی کاربر از یک صفحه WebView با دکمه بازگشت به خانه برمی‌گردد،
        // نوار پایین باید دوباره روی «خانه» نمایش داده شود
        findViewById<BottomNavigationView>(R.id.bottomNav).selectedItemId = R.id.nav_home
    }

    private fun openContent(title: String, path: String) {
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra(WebViewActivity.EXTRA_TITLE, title)
        intent.putExtra(WebViewActivity.EXTRA_PATH, path)
        startActivity(intent)
    }
}
