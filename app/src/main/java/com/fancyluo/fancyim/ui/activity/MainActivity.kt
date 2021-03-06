package com.fancyluo.fancyim.ui.activity

import android.content.Intent
import com.afollestad.materialdialogs.MaterialDialog
import com.fancyluo.fancyim.R
import com.fancyluo.fancyim.base.BaseActivity
import com.fancyluo.fancyim.ui.fragment.ContactsFragment
import com.fancyluo.fancyim.ui.fragment.ConversationFragment
import com.fancyluo.fancyim.ui.fragment.MineFragment
import com.hyphenate.EMError
import com.hyphenate.chat.EMClient
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : BaseActivity() {

    private var conversationFragment: ConversationFragment? = null
    private var contactFragment: ContactsFragment? = null
    private var mineFragment: MineFragment? = null

    override fun setupLayout(): Int = R.layout.activity_main

    override fun showBack(): Boolean = false

    override fun init() {
        super.init()
        initTab()
    }

    private fun initTab() {
        // 初始化第一个 Fragment
        tab_conversation.setCompoundDrawablesWithIntrinsicBounds(
                0, R.drawable.tab_conversation_press, 0, 0)
        tab_conversation.setTextColor(resources.getColor(R.color.colorPrimary))
        conversationFragment = ConversationFragment()
        supportFragmentManager.beginTransaction().add(R.id.container, conversationFragment).commit()

        tab_conversation.setOnClickListener {
            setTabPress(R.id.tab_conversation)
        }

        tab_contacts.setOnClickListener {
            setTabPress(R.id.tab_contacts)
        }

        tab_mine.setOnClickListener {
            setTabPress(R.id.tab_mine)
        }
    }

    private fun setTabPress(resId: Int) {
        clearTabState()
        hideAllFragment()
        val ft = supportFragmentManager.beginTransaction()
        when (resId) {
            R.id.tab_conversation -> {
                tab_conversation.setCompoundDrawablesWithIntrinsicBounds(
                        0, R.drawable.tab_conversation_press, 0, 0)
                tab_conversation.setTextColor(resources.getColor(R.color.colorPrimary))
                ft.show(conversationFragment)
            }
            R.id.tab_contacts -> {
                tab_contacts.setCompoundDrawablesWithIntrinsicBounds(
                        0, R.drawable.tab_contacts_press, 0, 0)
                tab_contacts.setTextColor(resources.getColor(R.color.colorPrimary))
                if (contactFragment == null) {
                    contactFragment = ContactsFragment()
                    ft.add(R.id.container, contactFragment)
                } else ft.show(contactFragment)
            }
            R.id.tab_mine -> {
                tab_mine.setCompoundDrawablesWithIntrinsicBounds(
                        0, R.drawable.tab_mine_press, 0, 0)
                tab_mine.setTextColor(resources.getColor(R.color.colorPrimary))
                if (mineFragment == null) {
                    mineFragment = MineFragment()
                    ft.add(R.id.container, mineFragment)
                } else ft.show(mineFragment)
            }
        }
        ft.commit()
    }

    private fun hideAllFragment() {
        val ft = supportFragmentManager.beginTransaction()
        if (conversationFragment != null) ft.hide(conversationFragment)
        if (contactFragment != null) ft.hide(contactFragment)
        if (mineFragment != null) ft.hide(mineFragment)
        ft.commit()
    }

    private fun clearTabState() {
        tab_conversation.setCompoundDrawablesWithIntrinsicBounds(
                0, R.drawable.tab_conversation_nomal, 0, 0)
        tab_conversation.setTextColor(resources.getColor(R.color.secondary_text))
        tab_contacts.setCompoundDrawablesWithIntrinsicBounds(
                0, R.drawable.tab_contacts_nomal, 0, 0)
        tab_contacts.setTextColor(resources.getColor(R.color.secondary_text))
        tab_mine.setCompoundDrawablesWithIntrinsicBounds(
                0, R.drawable.tab_mine_nomal, 0, 0)
        tab_mine.setTextColor(resources.getColor(R.color.secondary_text))
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        showErrorHintDialog(intent?.getIntExtra("error", -1))
    }

    private fun showErrorHintDialog(hxConnError: Int?) {
        if (hxConnError == null) return
        val hint = when (hxConnError) {
            EMError.USER_REMOVED -> "您的账号已被删除"
            EMError.USER_LOGIN_ANOTHER_DEVICE -> "您的账号已在其他设备登陆"
            else -> ""
        }
        MaterialDialog.Builder(this)
                .content(hint)
                .canceledOnTouchOutside(false)
                .positiveText("确定")
                .onPositive { dialog, _ ->
                    dialog.dismiss()
                    this.finish()
                    EMClient.getInstance().logout(true)
                    startActivity<LoginActivity>()
                }
                .show()
    }


}
