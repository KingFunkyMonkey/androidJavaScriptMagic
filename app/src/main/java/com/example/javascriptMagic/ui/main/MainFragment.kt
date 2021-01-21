package com.example.javascriptMagic.ui.main

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.squareup.duktape.Duktape


class MainFragment : Fragment() {
    private lateinit var mWebview: WebView

    companion object {
        fun newInstance() = MainFragment()
    }

    object AndroidJSInterface {

        @JavascriptInterface
        fun onClicked() {
            Log.d("javaScriptMagic", "Help button clicked")
        }
    }

    fun bindJavaScriptInterface() {
        mWebview.addJavascriptInterface(AndroidJSInterface, "Android")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return loadGoogle()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadGoogle()

        dukTape()
    }

    private fun loadGoogle(): WebView {
        mWebview = WebView(activity!!.applicationContext)
        mWebview.getSettings().javaScriptEnabled = true
        bindJavaScriptInterface()

        mWebview.setWebViewClient(object : WebViewClient() {
            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show()
            }

            @TargetApi(Build.VERSION_CODES.M)
            override fun onReceivedError(
                view: WebView,
                req: WebResourceRequest,
                rerr: WebResourceError
            ) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(
                    view,
                    rerr.errorCode,
                    rerr.description.toString(),
                    req.url.toString()
                )
            }

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                // log click event
                Log.i("javaScriptMagic", "redirect")
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageFinished(view: WebView, url: String) {
                Log.i("javaScriptMagic", "onScriptStarted()")
                view.loadUrl(
                    """javascript:(function f() {
                        var btns = document.getElementsByTagName('button');
                        for (var i = 0, n = btns.length; i < n; i++) {
                             btns[i].setAttribute('onclick', 'Android.onClicked()');
                        }
                        })()"""
                )
                Log.i("javaScriptMagic", "onScriptFinished()")
                Log.i("javaScriptMagic", "onPageFinished()")
            }
        })
        mWebview.loadUrl("https://www.facebook.com")
        return mWebview
    }

    private fun dukTape() {
        val duktape: Duktape = Duktape.create()
        try {
            Log.d("javaScriptMagic", duktape.evaluate("'hello world'.toUpperCase();").toString())
        } finally {
            duktape.close()
        }
    }
}