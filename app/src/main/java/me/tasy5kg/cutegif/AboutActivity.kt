package me.tasy5kg.cutegif

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import me.tasy5kg.cutegif.Constants.URL_BROWSE_HELP_DOCUMENTATION_ZH_CN_KDOCS
import me.tasy5kg.cutegif.Constants.URL_GET_LATEST_VERSION_GITHUB
import me.tasy5kg.cutegif.Constants.URL_GET_LATEST_VERSION_HU60
import me.tasy5kg.cutegif.Constants.URL_OPEN_SOURCE_REPO_GITHUB
import me.tasy5kg.cutegif.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFinishOnTouchOutside(!Toolbox.isFirstStart())
        binding.mbStart.apply {
            if (!Toolbox.isFirstStart()) {
                text = getString(R.string.done)
            }
            setOnClickListener {
                Toolbox.setFirstStart(false)
                finish()
            }
        }
        binding.llcVisibleForNotFirstStart.visibility = if (Toolbox.isFirstStart()) {
            GONE
        } else {
            VISIBLE
        }
        binding.mtvVersionInfo.text = getString(R.string.version_X_date, BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME)
        binding.cmivJoinQqGroup.setUpWithLambda {
            val intent = Intent().apply {
                data = Uri.parse(Constants.URI_JOIN_QQ_GROUP)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toolbox.copyToClipboard(this@AboutActivity, Constants.QQ_GROUP_ID.toString(), getString(R.string.join_qq_group_toast))
            }
        }
        binding.cmivGetLatestVersion.setUpWithLambda {
            Toolbox.openLink(
                this@AboutActivity,
                if (Toolbox.localeEqualsZhOrCn()) {
                    URL_GET_LATEST_VERSION_HU60
                } else {
                    URL_GET_LATEST_VERSION_GITHUB
                }
            )
        }
        binding.cmivCnBrowseHelpDocumentationOrSendEmailFeedback.setUpWithLambda {

            if (Toolbox.localeEqualsZhOrCn()) {
                Toolbox.openLink(this@AboutActivity, URL_BROWSE_HELP_DOCUMENTATION_ZH_CN_KDOCS)
            } else {
                Toolbox.openLink(this@AboutActivity, URL_BROWSE_HELP_DOCUMENTATION_ZH_CN_KDOCS)
                // sendFeedbackEmail()
            }

        }
        binding.cmivMoreOptions.apply {
            setUpWithLambda {
                this.visibility = GONE
                binding.llcMoreOptionsGroup.visibility = VISIBLE
            }
        }
        binding.mtvDebugInfo.setOnClickListener {
            val popupView = LayoutInflater.from(this@AboutActivity).inflate(R.layout.view_popup_debug_info, null)
            val popupWindow by lazy {
                PopupWindow(popupView, binding.root.width, LinearLayout.LayoutParams.WRAP_CONTENT, true).apply {
                    elevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Constants.POPUP_WINDOW_ELEVATION, resources.displayMetrics)
                }
            }
            binding.root.alpha = 0.5f
            popupWindow.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
            popupWindow.setOnDismissListener { binding.root.alpha = 1f }
            popupView.findViewById<MaterialTextView>(R.id.mtv_debug_info_content).text = Toolbox.debugInfo()
            popupView.findViewById<MaterialButton>(R.id.mb_copy_and_close).setOnClickListener {
                Toolbox.copyToClipboard(this@AboutActivity, Toolbox.debugInfo(), getString(R.string.debug_info_copied_to_clipboard))
                popupWindow.dismiss()
            }
        }
        binding.mtvOpenSourceRepo.setOnClickListener {
            Toolbox.openLink(this@AboutActivity, URL_OPEN_SOURCE_REPO_GITHUB)
        }
        binding.mtvOpenSourceLicense.setOnClickListener {
            val popupView = LayoutInflater.from(this@AboutActivity).inflate(R.layout.view_popup_open_source_licenses, null)
            val popupWindow by lazy {
                PopupWindow(popupView, binding.root.width, LinearLayout.LayoutParams.WRAP_CONTENT, true).apply {
                    elevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Constants.POPUP_WINDOW_ELEVATION, resources.displayMetrics)
                }
            }
            popupView.findViewById<CustomMenuItemView>(R.id.cmiv_view_3rd_party_oss_licenses).setUpWithLambda {
                Toolbox.view3rdPartyOSSLicenses(this@AboutActivity)
            }
            binding.root.alpha = 0.5f
            popupWindow.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
            popupWindow.setOnDismissListener { binding.root.alpha = 1f }
            popupView.findViewById<MaterialButton>(R.id.mb_close).setOnClickListener {
                popupWindow.dismiss()
            }
        }
        binding.mtvDeveloperEmailAddress.apply {
            setOnClickListener {
                copyDeveloperEmailAddress()
            }
            setOnLongClickListener {
                copyDeveloperEmailAddress()
                return@setOnLongClickListener true
            }
        }

    }

    /*
    private fun sendFeedbackEmail() {
        try {
            startActivity(Intent.createChooser(Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(URI_EMAIL_TO_TASY5KG)
                putExtra(Intent.EXTRA_SUBJECT, "My email's subject")    //TODO
                putExtra(Intent.EXTRA_TEXT, "My email's body")  //TODO
            }, null))
        } catch (e: Exception) {
            copyDeveloperEmailAddress()
        }
    }
     */

    private fun copyDeveloperEmailAddress() {
        Toolbox.copyToClipboard(this@AboutActivity, R.string.email_address_tasy5kg, R.string.developer_email_address_copied)
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, AboutActivity::class.java))
        }
    }
}