package ch.rmy.android.http_shortcuts.activities

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import ch.rmy.android.http_shortcuts.Application
import ch.rmy.android.http_shortcuts.R
import ch.rmy.android.http_shortcuts.data.RealmFactory
import ch.rmy.android.http_shortcuts.dialogs.DialogBuilder
import ch.rmy.android.http_shortcuts.extensions.color
import ch.rmy.android.http_shortcuts.extensions.consume
import ch.rmy.android.http_shortcuts.extensions.drawable
import ch.rmy.android.http_shortcuts.extensions.setTintCompat
import ch.rmy.android.http_shortcuts.utils.Destroyer
import ch.rmy.android.http_shortcuts.utils.LocaleHelper
import ch.rmy.android.http_shortcuts.utils.ThemeHelper


abstract class BaseActivity : AppCompatActivity() {

    internal var toolbar: Toolbar? = null

    val destroyer = Destroyer()

    val themeHelper by lazy {
        ThemeHelper(context)
    }

    open val initializeWithTheme: Boolean
        get() = true

    val baseView: ViewGroup?
        get() = (findViewById<ViewGroup>(android.R.id.content))?.getChildAt(0) as ViewGroup?

    val isRealmAvailable: Boolean
        get() = (application as Application).isRealmAvailable

    override fun onCreate(savedInstanceState: Bundle?) {
        if (initializeWithTheme) {
            setTheme(themeHelper.theme)
        }
        super.onCreate(savedInstanceState)
        try {
            RealmFactory.init(applicationContext)
        } catch (e: RealmFactory.RealmNotFoundException) {
            if (this is Entrypoint) {
                showRealmError()
            } else {
                throw e
            }
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.applyLocale(base))
    }

    private fun showRealmError() {
        DialogBuilder(context)
            .title(R.string.dialog_title_error)
            .message(R.string.error_realm_unavailable, isHtml = true)
            .positive(R.string.dialog_ok)
            .dismissListener {
                finish()
            }
            .showIfPossible()
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        baseView?.setBackgroundColor(color(context, R.color.activity_background))
        toolbar = findViewById(R.id.toolbar) ?: return
        updateStatusBarColor()
        setSupportActionBar(toolbar)
        if (navigateUpIcon != 0) {
            enableNavigateUpButton(navigateUpIcon)
        }
    }

    val context: Context
        get() = this

    protected open val navigateUpIcon = R.drawable.up_arrow

    private fun enableNavigateUpButton(iconResource: Int) {
        val actionBar = supportActionBar ?: return
        actionBar.setDisplayHomeAsUpEnabled(true)
        val upArrow = drawable(context, iconResource) ?: return
        upArrow.setTintCompat(Color.WHITE)
        actionBar.setHomeAsUpIndicator(upArrow)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> consume { onBackPressed() }
        else -> super.onOptionsItemSelected(item)
    }

    private fun updateStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                statusBarColor = themeHelper.statusBarColor
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyer.destroy()
    }
}
