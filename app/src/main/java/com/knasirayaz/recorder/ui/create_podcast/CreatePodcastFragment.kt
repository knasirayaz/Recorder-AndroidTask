package com.knasirayaz.recorder.ui.create_podcast

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.filepickersample.utils.FileUtil
import com.knasirayaz.recorder.R
import com.knasirayaz.recorder.base.BaseFragment
import com.knasirayaz.recorder.data.models.Podcast
import com.knasirayaz.recorder.databinding.FragmentCreatepodcastBinding
import com.knasirayaz.recorder.ui.MainActivity
import com.knasirayaz.recorder.utils.DateTimeHelpers
import com.knasirayaz.recorder.utils.FilePickerHelper
import com.knasirayaz.recorder.utils.invisible
import com.knasirayaz.recorder.utils.show
import java.io.File
import java.util.concurrent.TimeUnit

class CreatePodcastFragment :
    BaseFragment<FragmentCreatepodcastBinding>(R.layout.fragment_createpodcast) {
    val viewModel by viewModels<CreatePodcastViewModel>()

    var mAdapter: PodcastsAdapter? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getString(R.string.create_podcast))

        listener()
        getMyRecordings()
        setupRecycler()

    }

    private fun setupRecycler() {
        mAdapter = PodcastsAdapter(viewModel.mFilesArrayList) {}
        viewBinding.rvPodcasts.adapter = mAdapter

        if (viewModel.mFilesArrayList.isEmpty()) {
            viewBinding.ivMic.show()
        } else {
            viewBinding.ivMic.invisible()
        }
    }

    private fun getMyRecordings() {
        viewModel.mFilesArrayList.clear()
        File(FileUtil.getAudioDirectory(requireContext()).path).walkTopDown().forEach {
            if (it.path.contains(".wav") || it.path.contains(".mp3") || it.path.contains(".opus")) {
                viewModel.mFilesArrayList.add(addPodcast(it.path))
            }
        }
    }

    fun getDuration(context: Context, filePath: String?): Long {
        val metaRetriever = MediaMetadataRetriever()
        metaRetriever.setDataSource(context, Uri.parse(filePath))
        val duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val dur = duration!!.toLong()
        val seconds = (dur % 60000 / 1000).toString()
        metaRetriever.release()
        return TimeUnit.SECONDS.toMillis(seconds.toLong())
    }

    private fun addPodcast(path: String): Podcast {
        return Podcast(
            path,
            File(path).nameWithoutExtension,
            DateTimeHelpers.formateMilliSeccond(
                getDuration(
                    requireContext(),
                    path
                )
            ).toString(),
            DateTimeHelpers.getTimeAgo(File(path).lastModified()), false
        )

    }

    private fun listener() {
        viewBinding.btnRecord.setOnClickListener {
            findNavController().navigate(CreatePodcastFragmentDirections.actionCreatePodCastFragmentToRecordPodcastFragment())
        }

        viewBinding.btnImport.setOnClickListener {
            FilePickerHelper.getAudio(
                requireContext(),
                childFragmentManager
            ) { success, media, message ->
                if (success) {
                    viewModel.mFilesArrayList.add(addPodcast(media.url))
                    mAdapter?.notifyItemInserted(viewModel.mFilesArrayList.size - 1)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mAdapter?.resetMediaPlayer()
    }

}