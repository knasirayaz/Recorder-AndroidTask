package com.knasirayaz.recorder.ui.create_podcast

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.knasirayaz.recorder.R
import com.knasirayaz.recorder.data.models.Podcast
import com.knasirayaz.recorder.databinding.ItemPodcastsBinding
import com.knasirayaz.recorder.utils.DateTimeHelpers
import com.knasirayaz.recorder.utils.DateTimeHelpers.formateMilliSeccond
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


class PodcastsAdapter(var mList: ArrayList<Podcast>, callback: () -> Unit) :
    RecyclerView.Adapter<PodcastsAdapter.ViewHolder?>() {

    val mMediaPlayer = MediaPlayer()
    var lastPlayingPos = -1

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        return ViewHolder(
            ItemPodcastsBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(position)
    }


    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(private val viewBinding: ItemPodcastsBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(position: Int) {
            val mModel = mList[position]
            viewBinding.mModel = mModel

            if (mModel.isPlaying) {
                viewBinding.btnPlay.setIconResource(R.drawable.ic_stop)
            } else {
                viewBinding.btnPlay.setIconResource(R.drawable.ic_play)
            }

            viewBinding.btnPlay.setOnClickListener {
                stopPreviousPodcast()
                mediaPlayerConfiguration(mModel, position)

                mMediaPlayer.setOnPreparedListener {
                    it.start()
                    viewBinding.btnPlay.setIconResource(R.drawable.ic_stop)
                }

                mMediaPlayer.setOnCompletionListener {
                    viewBinding.btnPlay.setIconResource(R.drawable.ic_play)
                }
            }

            viewBinding.executePendingBindings()
        }
    }

    private fun mediaPlayerConfiguration(
        mModel: Podcast,
        position: Int
    ) {
        if (mMediaPlayer.isPlaying) {
            //If media player is playing reset it, if its same position stop track otherwise start.
            mMediaPlayer.reset()
            if (lastPlayingPos != position) {
                lastPlayingPos = position
                mModel.isPlaying = true
                mMediaPlayer.setDataSource(mModel.filePath)
                try {
                    mMediaPlayer.prepare()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } else {
            lastPlayingPos = position
            mModel.isPlaying = true
            try {
                mMediaPlayer.setDataSource(mModel.filePath)
                mMediaPlayer.prepare()
            } catch (e: Exception) {
                mMediaPlayer.reset()
                e.printStackTrace()
            }
        }


    }

    fun resetMediaPlayer() {
        stopPreviousPodcast()
        mMediaPlayer.reset()
    }

    private fun stopPreviousPodcast() {
        for (all in mList.indices) {
            if (mList[all].isPlaying) {
                mList[all].isPlaying = false
                notifyItemChanged(all)
            }
        }
    }


}