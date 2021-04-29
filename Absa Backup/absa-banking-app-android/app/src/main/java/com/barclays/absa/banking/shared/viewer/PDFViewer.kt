/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 *
 */

package com.barclays.absa.banking.shared.viewer

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.utils.PermissionHelper
import kotlinx.android.synthetic.main.pdf_viewer.view.*
import java.io.IOException

/* Limitations:
* Docfusion pdf files are cached using AppCache. We hashing the full link and use that as key to save the download id which unique identifies the pdf files downloaded.
* This enables the file not to be redownloaded when the application process is alive. However, if the the application process is killed or restarted,the file
* will be re-downloaded. Future improvements, the download ids will need to be persited on permanent storage to avoid redownloads when the applications process is restarted
*/

class PDFViewer @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr), LifecycleObserver {

    private lateinit var pdfRenderer: PdfRenderer
    private lateinit var pdfViewerAdapter: PdfViewerAdapter
    private lateinit var downloadNotifier: DownloadNotifier
    private val layoutManager: LinearLayoutManager
    private var downloadManager: DownloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private val appCacheService = getServiceInterface(IAppCacheService::class.java)
    private var documentUri: Uri? = null

    init {
        inflate(context, R.layout.pdf_viewer, this)
        layoutManager = pdfPageView.layoutManager as LinearLayoutManager
        zoomOutImageButton.setOnClickListener {
            pdfPageView.zoomOut()
        }
        zoomInImageButton.setOnClickListener {
            pdfPageView.zoomIn()
        }
    }

    fun sharePdf() {
        documentUri?.let {
            Intent().apply {
                action = Intent.ACTION_SEND
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, it)
                context.startActivity(Intent.createChooser(this, context.getString(R.string.share)))
            }
        }
    }

    fun downloadAndViewPdf(url: String, cacheKey: String) {
        if (appCacheService.isDocumentCached(cacheKey)) {
            val downloadId = appCacheService.getDownloadId(cacheKey)
            documentUri = downloadManager.getUriForDownloadedFile(downloadId)
            openDownloadedPdf(downloadId)
        } else {
            PermissionHelper.requestExternalStorageWritePermission(context as Activity) {
                val request = DownloadManager.Request(Uri.parse(url))
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, URLUtil.guessFileName(url, null, null))
                val downloadId = downloadManager.enqueue(request)
                (context as BaseActivity).showProgressDialog()
                downloadNotifier = DownloadNotifier(cacheKey, downloadId)
                IntentFilter().apply {
                    addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                    context.registerReceiver(downloadNotifier, this)
                }
            }
        }
    }

    inner class DownloadNotifier(private val cacheKey: String, private val downloadId: Long) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            (context as BaseActivity).dismissProgressDialog()
            appCacheService.setDownloadId(cacheKey, downloadId)
            documentUri = downloadManager.getUriForDownloadedFile(downloadId)
            openDownloadedPdf(downloadId)
        }
    }

    private fun openDownloadedPdf(downloadId: Long) {
        val fileDescriptor = downloadManager.openDownloadedFile(downloadId)
        renderPdf(fileDescriptor)
    }

    fun viewPdf(encodedPdfContent: ByteArray, fileName: String) {
        if (encodedPdfContent.isNotEmpty()) {
            PermissionHelper.requestExternalStorageWritePermission(context as Activity) {
                documentUri = context.writeFile(fileName, encodedPdfContent)
                documentUri?.let { uri ->
                    val fileDescriptor = context.contentResolver.openFileDescriptor(uri, "r") as ParcelFileDescriptor
                    renderPdf(fileDescriptor)
                }
            }
        }
    }

    private fun updatePageNumbering() {
        val position = layoutManager.findLastVisibleItemPosition() + 1
        val pageCount = pdfRenderer.pageCount
        pageIndexTextView.text = context.getString(R.string.pdf_viewer_page_number, position, pageCount)
    }

    private fun renderPdf(fileDescriptor: ParcelFileDescriptor) {

        pdfRenderer = PdfRenderer(fileDescriptor)
        pdfViewerAdapter = PdfViewerAdapter()
        val margin = resources.getDimensionPixelSize(R.dimen.medium_space)
        pdfPageView.addItemDecoration(SpaceItemDecoration(margin, margin))
        updatePageNumbering()

        pdfPageView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                updatePageNumbering()
            }
        })
        pdfPageView.adapter = pdfViewerAdapter
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun release() {
        try {

            if (::downloadNotifier.isInitialized) {
                context.unregisterReceiver(downloadNotifier)
            }

            if (::pdfRenderer.isInitialized) {
                pdfRenderer.close()
            }
        } catch (ioException: IOException) {
            BMBLogger.e("Failed to close the pdf renderer")
        }
    }

    inner class PdfViewerAdapter : RecyclerView.Adapter<PdfViewerAdapter.PdfViewerItemViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfViewerItemViewHolder {
            return PdfViewerItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.pdf_viewer_item_view, parent, false) as ImageView)
        }

        override fun onBindViewHolder(holder: PdfViewerItemViewHolder, position: Int) =
            holder.onBind(position)

        override fun getItemCount(): Int = pdfRenderer.pageCount

        inner class PdfViewerItemViewHolder(private val pdfItemView: ImageView) : RecyclerView.ViewHolder(pdfItemView) {

            fun onBind(pageIndex: Int) {
                val page: PdfRenderer.Page = pdfRenderer.openPage(pageIndex)
                val pageBitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                page.render(pageBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                pdfItemView.setImageBitmap(pageBitmap)
                page.close()
            }
        }
    }

    class SpaceItemDecoration private constructor(private val leftMargin: Int, private val topMargin: Int, private val rightMargin: Int, private val bottomMargin: Int) : ItemDecoration() {

        constructor(verticalMargin: Int, horizontalMargin: Int) : this(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin)

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.apply {
                if (parent.getChildAdapterPosition(view) == 0) {
                    top = topMargin
                }
                bottom = bottomMargin
                left = leftMargin
                right = rightMargin
            }
        }
    }
}