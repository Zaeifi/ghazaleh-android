package ir.ghazalehriazipsychologist.app

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * صفحه نمایش محتوای زنده سایت داخل اپ.
 *
 * برخلاف نسخه قبلی اپ، اینجا سایت «عیناً» نمایش داده نمی‌شود: منو/هدر و فوتر خودِ سایت
 * (که برای مرورگر دسکتاپ طراحی شده‌اند) با تزریق CSS مخفی می‌شوند و به‌جایش یک نوار بالای
 * بومی اندروید (با دکمه بازگشت و عنوان صفحه) و منوی پایین بومی اپ نمایش داده می‌شود.
 * محتوای اصلی صفحه (متن، تصاویر، فرم‌ها) همچنان مستقیم و زنده از سایت خوانده می‌شود،
 * پس با آپدیت سایت، همین‌جا هم بدون نیاز به نسخه جدید اپ به‌روز می‌ماند.
 */
class WebViewActivity : AppCompatActivity() {

    companion object {
        private const val SITE_BASE = "https://ghazalehriazipsychologist.ir/"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_PATH = "extra_path"

        // این CSS همان کلاس‌های منو/فوتر دسکتاپِ سایت (nav.nav و footer) را مخفی می‌کند
        // تا داخل اپ فقط محتوای اصلی صفحه دیده شود، نه چیدمان مخصوص مرورگر.
        private const val HIDE_CHROME_CSS = """
            (function(){
                var style = document.createElement('style');
                style.innerHTML = 'nav.nav{display:none !important;} footer{display:none !important;} body{padding-top:0 !important;}';
                document.head.appendChild(style);
            })();
        """
    }

    private lateinit var webView: WebView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var offlineView: View

    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    private val fileChooserLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val callback = filePathCallback
            filePathCallback = null
            if (callback == null) return@registerForActivityResult
            val data = result.data
            if (result.resultCode != RESULT_OK || data == null) {
                callback.onReceiveValue(null)
                return@registerForActivityResult
            }
            callback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(result.resultCode, data))
        }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        val title = intent.getStringExtra(EXTRA_TITLE) ?: getString(R.string.app_name)
        val path = intent.getStringExtra(EXTRA_PATH) ?: ""

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = title
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        webView = findViewById(R.id.webView)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        progressBar = findViewById(R.id.progressBar)
        offlineView = findViewById(R.id.offlineView)

        setupWebView()

        swipeRefresh.setOnRefreshListener { webView.reload() }
        swipeRefresh.setColorSchemeResources(R.color.gold)

        findViewById<View>(R.id.retryButton).setOnClickListener {
            offlineView.visibility = View.GONE
            webView.visibility = View.VISIBLE
            webView.reload()
        }

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState)
        } else {
            webView.loadUrl(SITE_BASE + path)
        }

        onBackPressedDispatcher.addCallback(this) {
            if (webView.canGoBack()) {
                webView.goBack()
            } else {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true // لازم برای ذخیره پاسخ‌های تست در localStorage (test-take.php)
        settings.databaseEnabled = true
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        settings.setSupportZoom(false)
        settings.builtInZoomControls = false
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
        settings.mediaPlaybackRequiresUserGesture = false
        settings.userAgentString = settings.userAgentString + " GhazalehRiaziApp/1.0"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            settings.safeBrowsingEnabled = true
        }

        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val uri = request.url
                val scheme = uri.scheme ?: return false

                if (scheme == "tel" || scheme == "mailto" || scheme == "sms") {
                    return openExternally(uri)
                }
                if (scheme == "http" || scheme == "https") {
                    // شامل دامنه درگاه پرداخت زرین‌پال هم می‌شود — عمداً داخل همین WebView باز می‌ماند
                    // تا فرایند رزرو/پرداخت بدون خروج از اپ کامل شود
                    return false
                }
                return openExternally(uri)
            }

            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)
                view.evaluateJavascript(HIDE_CHROME_CSS, null)
                progressBar.visibility = View.GONE
                swipeRefresh.isRefreshing = false
            }

            override fun onPageStarted(view: WebView, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                super.onReceivedError(view, request, error)
                if (request.isForMainFrame) showOffline()
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progressBar.progress = newProgress
                if (newProgress >= 100) progressBar.visibility = View.GONE
            }

            override fun onShowFileChooser(
                webView: WebView,
                callback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                filePathCallback?.onReceiveValue(null)
                filePathCallback = callback
                return try {
                    fileChooserLauncher.launch(fileChooserParams.createIntent())
                    true
                } catch (e: ActivityNotFoundException) {
                    filePathCallback = null
                    false
                }
            }
        }

        webView.setDownloadListener { url, _, _, _, _ ->
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "امکان باز کردن این فایل وجود ندارد.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openExternally(uri: Uri): Boolean {
        return try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
            true
        } catch (e: ActivityNotFoundException) {
            false
        }
    }

    private fun showOffline() {
        webView.visibility = View.GONE
        offlineView.visibility = View.VISIBLE
        swipeRefresh.isRefreshing = false
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }

    override fun onDestroy() {
        (webView.parent as? ViewGroup)?.removeView(webView)
        webView.destroy()
        super.onDestroy()
    }
}
